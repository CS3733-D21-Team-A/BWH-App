package edu.wpi.cs3733.d21.teamA.db;

import edu.wpi.cs3733.d21.teamA.db.enums.*;
import edu.wpi.cs3733.d21.teamA.extras.Security;
import javafx.util.Pair;
import org.apache.derby.jdbc.ClientDriver;
import org.apache.derby.jdbc.EmbeddedDriver;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static edu.wpi.cs3733.d21.teamA.Settings.DATABASE_HOSTNAME;
import static edu.wpi.cs3733.d21.teamA.Settings.PREFERENCES;
import static edu.wpi.cs3733.d21.teamA.db.DatabaseUtil.*;

/**
 * Controller class for working with the BWH database.
 */
public class DatabaseController {
	/**
	 * The connection to the database
	 */
	private Connection connection;

	final private Table nodeTable;
	final private Table edgeTable;
	final private Table userTable;
	final private Table serviceRequestsTable;
	final private Table favoriteNodesTable;
	final private Table covidSurveyTable;

	/**
	 * Map for getting service request tables.
	 * Key: Service Request enumeration.
	 * Value: Table for corresponding service request type.
	 */
	final private Map<SERVICEREQUEST, RequestTable> requestsTables;

	/**
	 * Whether the database controller is currently using the embedded database
	 */
	private boolean usingEmbedded;

	/**
	 * DatabaseController constructor. Creates and populates new database if one is not found.
	 *
	 * @throws SQLException Something went wrong.
	 * @throws IOException  Something went wrong.
	 */
	private DatabaseController() throws SQLException, IOException {
		boolean dbExists = connectToDB(PREFERENCES.get(DATABASE_HOSTNAME, null)).getKey();

		TableFactory tableFactory = new TableFactory(connection);
		nodeTable = tableFactory.getTable(TABLES.NODES);
		edgeTable = tableFactory.getTable(TABLES.EDGES);
		userTable = tableFactory.getTable(TABLES.USERS);
		serviceRequestsTable = tableFactory.getTable(TABLES.SERVICE_REQUESTS);
		favoriteNodesTable = tableFactory.getTable(TABLES.FAVORITE_NODES);
		covidSurveyTable = tableFactory.getTable(TABLES.COVID_SURVEY);

		requestsTables = new HashMap<>();
		for (SERVICEREQUEST table : SERVICEREQUEST.values()) {
			if (DatabaseInfo.SERVICEREQUEST_SQL.get(table) != null) {
				requestsTables.put(table, tableFactory.getRequestTable(table));
			}
		}

		if (!dbExists) {
			populateDB();
		}
	}

	/**
	 * Return if the database connection is closed
	 *
	 * @return if the database connection is closed
	 * @throws SQLException Something went wrong.
	 */
	private boolean connectionIsClosed() throws SQLException {
		return connection.isClosed();
	}

	/**
	 * Static nested class for databasecontrollerSingleton
	 */
	private static class DBControllerSingleton {
		private static DatabaseController instance;
	}

	/**
	 * getinstance of dbcontroller to then use
	 *
	 * @return Instance of dbcontroller, null if an error occurred
	 */
	public static DatabaseController getInstance() {
		try {
			if (DBControllerSingleton.instance == null || DBControllerSingleton.instance.connectionIsClosed()) {
				DBControllerSingleton.instance = new DatabaseController();
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			return null;
		}
		return DBControllerSingleton.instance;
	}

	/**
	 * Updates the connections associated with the database based on the current preference setting
	 * Note: Shuts down the previous connection
	 *
	 * @return boolean indicating if we are using the embedded database
	 * @throws SQLException Something went wrong.
	 * @throws IOException  Something went wrong.
	 */
	public boolean updateConnection() throws SQLException, IOException {
		shutdownDB();
		Pair<Boolean, Boolean> bools = connectToDB(PREFERENCES.get(DATABASE_HOSTNAME, null));
		boolean dbExists = bools.getKey();

		nodeTable.setConnection(connection);
		edgeTable.setConnection(connection);
		userTable.setConnection(connection);
		serviceRequestsTable.setConnection(connection);
		favoriteNodesTable.setConnection(connection);
		covidSurveyTable.setConnection(connection);

		for (RequestTable table : requestsTables.values()) {
			table.setConnection(connection);
		}

		if (!dbExists) {
			populateDB();
		}

		return bools.getValue();
	}

	/**
	 * Connects to the Derby database
	 * Note: This will fall back to the embedded database if unable to connect to the client-server database
	 * Note: Creates a database if one isn't present
	 *
	 * @param hostname hostname of server to conenct to. null if connecting to embedded database
	 * @return Pair whose key is if we need to construct a new database and value is whether we are using the embedded database
	 * @throws SQLException Something went wrong.
	 */
	private Pair<Boolean, Boolean> connectToDB(String hostname) throws SQLException {
		boolean remote = hostname != null;
		String connectionURL = "jdbc:derby:" + (remote ? "//" + hostname + ":1527/SERVER_BWH_DB" : "derby/EMBEDDED_BWH_DB");
		this.usingEmbedded = !remote;

		boolean dbExists = true;
		if (remote) {
			DriverManager.registerDriver(new ClientDriver());
			try {
				DriverManager.getConnection(connectionURL, "admin", "admin");
			} catch (SQLNonTransientConnectionException e) {
				if (e.getSQLState().equals("08001")) {
					System.out.println("Unable to establish Client-Server connection. Falling back to embedded database.");
					connectionURL = "jdbc:derby:derby/EMBEDDED_BWH_DB";
					usingEmbedded = true;
				} else dbExists = false;
			}
			if (!usingEmbedded) System.out.println("Client-Server database connection established!");
		}

		if (usingEmbedded) {
			DriverManager.registerDriver(new EmbeddedDriver());
			try {
				DriverManager.getConnection(connectionURL, "admin", "admin");
			} catch (SQLException e) {
				dbExists = false;
			}
		}

		this.connection = DriverManager.getConnection(connectionURL + ";create=true", "admin", "admin");
		if (!dbExists) {
			System.out.println("No database found. Creating new one...");
			createDB(connection);
		}

		return new Pair<>(dbExists, usingEmbedded);
	}

	/**
	 * Shuts down the database connection.
	 * Note: This will shut down the connection for all currently running DatabaseControllers!
	 *
	 * @return If shutdown was successful.
	 */
	boolean shutdownDB() {
		if (!usingEmbedded) {
			//System.out.println("Warning: Not allowed to shut down remote database. Action ignored");
			return false;
		}

		try {
			DriverManager.getConnection("jdbc:derby:derby/EMBEDDED_BWH_DB;shutdown=true", "admin", "admin");
			return false; // Shutting down a database should throw an exception. If it doesn't, something went wrong!
		} catch (SQLException e) {
			return true; // Shutting down a database throws an exception!
		}
	}

	// ===== NODES =====

	/**
	 * Check if a node exists.
	 *
	 * @param nodeID ID of node to check.
	 * @return Boolean indicating presence of node in the database.
	 * @throws SQLException Something went wrong.
	 */
	public boolean nodeExists(String nodeID) throws SQLException {
		return nodeTable.getEntry(nodeID) != null;
	}

	/**
	 * Add a node to the database (assumes node with provided primary key doesn't already exist).
	 *
	 * @param values Map whose keys are the column names and values are the entry values.
	 * @throws SQLException Something went wrong (likely node already exists or malformed imput).
	 */
	public void addNode(Map<String, String> values) throws SQLException {
		nodeTable.addEntry(values);
	}

	/**
	 * Edit an existing node in the database (assumes node with provided ID exists).
	 *
	 * @param nodeID ID of node to edit.
	 * @param values Map whose keys are the column names and values are the new entry values.
	 * @throws SQLException Something went wrong (likely tried to edit node that doesn't exist or change a nodeID).
	 */
	public void editNode(String nodeID, Map<String, String> values) throws SQLException {
		nodeTable.editEntry(nodeID, values);
	}

	/**
	 * Delete a node from the database (assumes node with provided ID exists).
	 *
	 * @param nodeID ID of node to delete.
	 * @throws SQLException Something went wrong (likely node doesn't exist).
	 */
	public void deleteNode(String nodeID) throws SQLException {
		nodeTable.deleteEntry(nodeID);
	}

	/**
	 * Get the full NODES table as a List of Maps
	 *
	 * @return List of maps representing the full NODES table.
	 * @throws SQLException Something went wrong.
	 */
	public List<Map<String, String>> getNodes() throws SQLException {
		return nodeTable.getEntries();
	}

	/**
	 * Query the NODES table for an entry with the provided primary key.
	 *
	 * @param nodeID ID representing node to look for.
	 * @return Map representing the node to query for.
	 * @throws SQLException Something went wrong.
	 */
	public Map<String, String> getNode(String nodeID) throws SQLException {
		return nodeTable.getEntry(nodeID);
	}

	/**
	 * Empties the node table by deleting all entries.
	 *
	 * @throws SQLException Something went wrong.
	 */
	void emptyNodeTable() throws SQLException {
		nodeTable.emptyTable();
	}

	/**
	 * Returns all nodes that contain the input value in the provided column
	 *
	 * @param column a column name for a given
	 * @param value  a value to query for in a given column
	 * @return all rows that contain value in the provided column .
	 * @throws SQLException
	 */
	public List<Map<String, String>> getNodesByValue(String column, String value) throws SQLException {
		return nodeTable.getEntriesByValue(column, value);
	}

	// ===== EDGES =====

	/**
	 * Check if an edge exists.
	 *
	 * @param edgeID ID of edge to check.
	 * @return Boolean indicating presence of edge in the database.
	 * @throws SQLException Something went wrong.
	 */
	public boolean edgeExists(String edgeID) throws SQLException {
		return edgeTable.getEntry(edgeID) != null;
	}

	/**
	 * Add an edge to the database (assumes edge with provided primary key doesn't already exist).
	 *
	 * @param values Map whose keys are the column names and values are the entry values.
	 * @throws SQLException Something went wrong (likely edge already exists or malformed input).
	 */
	public void addEdge(Map<String, String> values) throws SQLException {
		edgeTable.addEntry(values);
	}

	/**
	 * Delete an edge from the database (assumes node with provided ID exists).
	 *
	 * @param edgeID ID of edge to delete.
	 * @throws SQLException Something went wrong (likely edge doesn't exist).
	 */
	public void deleteEdge(String edgeID) throws SQLException {
		edgeTable.deleteEntry(edgeID);
	}

	/**
	 * Get the full EDGES table as a List of Maps
	 *
	 * @return List of maps representing the full NODES table.
	 * @throws SQLException Something went wrong.
	 */
	public List<Map<String, String>> getEdges() throws SQLException {
		return edgeTable.getEntries();
	}

	/**
	 * Query the EDGES table for an entry with the provided primary key.
	 *
	 * @param edgeID ID representing node to look for.
	 * @return Map representing the node to query for.
	 * @throws SQLException Something went wrong.
	 */
	public Map<String, String> getEdge(String edgeID) throws SQLException {
		return edgeTable.getEntry(edgeID);
	}

	/**
	 * Get edges connected to the node with the provided ID
	 *
	 * @param nodeID ID of node to find edges connected to.
	 * @return List of maps of edges connected to the desired node.
	 * @throws SQLException Something went wrong (likely node doesn't exist).
	 */
	public List<Map<String, String>> getEdgesConnectedToNode(String nodeID) throws SQLException {
		List<Map<String, String>> edges = edgeTable.getEntriesByValue("STARTNODE", nodeID); // gets all edges that has nodeID as a start node
		List<Map<String, String>> edges2 = edgeTable.getEntriesByValue("ENDNODE", nodeID); // gets all edges that have the nodeID as a end node
		if (edges == null) {
			edges = edges2; //replace edges with edges2 if edges is null. If edges2 is also null that's okay
		} else if (edges2 != null) {
			edges.addAll(edges2); //edges isn't null, so we add edges2 if it isn't also null
		}

		return edges;
	}

	/**
	 * Empties the edge table by deleting all entries.
	 */
	void emptyEdgeTable() throws SQLException {
		edgeTable.emptyTable();
	}


	// ===== SERVICE REQUESTS =====

	/**
	 * Insert a new service request into the databse.
	 *
	 * @param sharedValues  Values shared among all service requests (goes into the SERVICE_REQUESTS table).
	 * @param requestValues Values specific to the particular type of service request.
	 * @throws SQLException Something went wrong.
	 */
	public void addServiceRequest(Map<String, String> sharedValues, Map<String, String> requestValues) throws SQLException {
		serviceRequestsTable.addEntry(sharedValues);
		requestsTables.get(SERVICEREQUEST_NAMES.inverse().get(sharedValues.get("REQUESTTYPE"))).addEntry(requestValues);
	}

	/**
	 * Change the status of a service request.
	 *
	 * @param requestID ID of the service request to change the status for.
	 * @param newStatus New status of the service request.
	 * @throws SQLException Something went wrong.
	 */
	public void changeStatus(String requestID, STATUS newStatus) throws SQLException {
		Map<String, String> values = serviceRequestsTable.getEntry(requestID);
		values.replace("STATUS", STATUS_NAMES.get(newStatus));
		serviceRequestsTable.editEntry(requestID, values);
	}

	/**
	 * Change the employee assigned to a service request.
	 *
	 * @param requestID  ID of the service request to change the employee for.
	 * @param employeeID ID of the employee to assign to the service request.
	 * @throws SQLException Something went wrong.
	 */
	public void changeEmployee(String requestID, String employeeID) throws SQLException {
		Map<String, String> values = serviceRequestsTable.getEntry(requestID);
		values.replace("EMPLOYEEID", employeeID);
		serviceRequestsTable.editEntry(requestID, values);
	}

	/**
	 * Assign an employee to a service request and set its status to Assigned.
	 *
	 * @param requestID  ID of the service request to assign the employee to.
	 * @param employeeID ID of the employee to assign to the service request.
	 * @throws SQLException Something went wrong.
	 */
	public void assignEmployee(String requestID, String employeeID) throws SQLException {
		changeEmployee(requestID, employeeID);
		changeStatus(requestID, STATUS.ASSIGNED);
	}

	/**
	 * Get all service requests with the desired status.
	 *
	 * @param status Status of service requests to filter for.
	 * @return List of Maps representing the service requests.
	 * @throws SQLException Something went wrong.
	 */
	public List<Map<String, String>> getServiceRequestsWithStatus(STATUS status) throws SQLException {
		return serviceRequestsTable.getEntriesByValue("STATUS", STATUS_NAMES.get(status));
	}

	/**
	 * Get all service requests.
	 *
	 * @return List of Maps representing the service requests.
	 * @throws SQLException Something went wrong.
	 */
	public List<Map<String, String>> getServiceRequests() throws SQLException {
		return serviceRequestsTable.getEntries();
	}

	/**
	 * Get all service requests of the desired type.
	 *
	 * @param requestType Service request type to get.
	 * @return List of Maps representing the service requests.
	 * @throws SQLException Something went wrong.
	 */
	public List<Map<String, String>> getServiceRequestsByType(SERVICEREQUEST requestType) throws SQLException {
		return requestsTables.get(requestType).getRequests();
	}

	/**
	 * Empties the service requests tables by deleting all entries.
	 */
	void emptyServiceRequestsTable() throws SQLException {
		serviceRequestsTable.emptyTable();
		//Service requests cascade, so we don't need to clear request-specific tables
	}

	/**
	 * Gets all service requests made by given author
	 *
	 * @return List of Maps representing the service requests
	 */
	public List<Map<String, String>> getServiceRequestsByAuthor(String author) throws SQLException {
		if (userTable.getEntry(author) != null) {
			return serviceRequestsTable.getEntriesByValue("AUTHORID", author);
		} else {
			throw new SQLException();
		}
	}


	/**
	 * Gets the columns from the service request table named requestType
	 *
	 * @param requestType the name of a service request table
	 * @return a map whose keys are the names of the columns and value.
	 * 	Value is a boolean indicating if they representing type (false = int,true = String).
	 */
	public List<String> getServiceRequestColumns(SERVICEREQUEST requestType) {
		return new ArrayList<>(requestsTables.get(requestType).getColumns().keySet());
	}


	// ===== USERS ==========

	/**
	 * Adding a user to the database
	 *
	 * @param newUser
	 * @throws SQLException
	 */
	public void addUser(Map<String, String> newUser) throws SQLException {
		userTable.addEntry(newUser);
	}

	/**
	 * editing a user in the database
	 *
	 * @param user
	 * @throws SQLException
	 */
	public void editUser(String username, Map<String, String> user) throws SQLException {
		if (userTable.getEntry(username) != null) {
			userTable.editEntry(username, user);
		} else {
			throw new SQLException();
		}
	}

	/**
	 * editing a user in the database
	 *
	 * @param username
	 * @throws SQLException
	 */
	public void deleteUser(String username) throws SQLException {
		if (userTable.getEntry(username) != null) {
			userTable.deleteEntry(username);
		} else {
			throw new SQLException();
		}
	}

	// Emily

	/**
	 * Empties the user table by deleting all entries.
	 *
	 * @throws SQLException Something went wrong.
	 */ //this is needed for test
	void emptyUserTable() throws SQLException {
		userTable.emptyTable();
	}

	public List<Map<String, String>> getEmployees() throws SQLException {
		return userTable.getEntriesByValue("USERTYPE", USER_TYPE_NAMES.get(USERTYPE.EMPLOYEE));
	}


	/**
	 * Checks to see if a user has taken the COVID survey
	 *
	 * @param username The username of the user
	 * @return True if they have taken it, false otherwise
	 * @throws SQLException
	 */
	public boolean hasUserTakenCovidSurvey(String username) throws SQLException {
		return userTable.getEntry(username).get("TAKENSURVEY").equals("true");
	}

	/**
	 * checks database for the username to make sure it does not previously exist
	 *
	 * @param username
	 * @return true if a user with the desired username exists
	 * @throws SQLException
	 */
	public boolean checkUserExistsByUsername(String username) throws SQLException {
		return userTable.getEntry(username) != null;
	}

	// Emily

	/**
	 * get a user by their username
	 *
	 * @param username username of user to get
	 * @return map representing user information
	 * @throws SQLException something went wrong
	 */
	public Map<String, String> getUserByUsername(String username) throws SQLException {
		return userTable.getEntry(username);
	}

	/**
	 * checks if a user with the provided email already exists
	 *
	 * @param email email to check
	 * @return if a user exists with the provided email
	 * @throws SQLException something went wrong.
	 */
	public boolean checkUserExistsByEmail(String email) throws SQLException {
		return userTable.getEntriesByValue("EMAIL", email).size() > 0;
	}

	// ======= COVID SURVEY ==========

	/**
	 * Adds a survey that the user took to the database, and changes the users TAKENSURVEY
	 * field to YES
	 *
	 * @param survey The survey that the user has submitted
	 * @throws SQLException
	 */
	public void addSurvey(Map<String, String> survey) throws SQLException {
		if (covidSurveyTable.getEntry(survey.get("USERNAME")) != null) {
			covidSurveyTable.deleteEntry(survey.get("USERNAME")); //TODO: Talk to UI about having users resubmit a survey
		}
		//TODO: Make it so guests create a random ID for username
		covidSurveyTable.addEntry(survey);

		String username = covidSurveyTable.getEntry(survey.get("USERNAME")).get("USERNAME");
		Map<String, String> theUser = userTable.getEntry(username);
		theUser.replace("TAKENSURVEY", "true");

		userTable.editEntry(username, theUser);
	}

	/**
	 * Gets the survey from a specific user
	 *
	 * @param username The username of the user
	 * @return The survey of the user
	 * @throws SQLException
	 */
	public Map<String, String> getSurveyByUsername(String username) throws SQLException {
		if (covidSurveyTable.getEntry(username) != null) {
			return covidSurveyTable.getEntry(username);
		} else {
			throw new SQLException();
		}
	}

	/**
	 * Returns all of the surveys in the table
	 *
	 * @return The list of surveys
	 * @throws SQLException
	 */
	public List<Map<String, String>> getSurveys() throws SQLException {
		return covidSurveyTable.getEntries();
	}

	// ===== FAVORITE_NODES ======== Emily

	/**
	 * gets the automatically generated favid using username and node name
	 *
	 * @param username
	 * @param nodeName
	 * @return
	 */
	String getFAVID(String username, String nodeName) throws SQLException {
		return getFavoriteNodeByUserAndName(username, nodeName).get("FAVID");
	}

	/**
	 * Creates a new favorite id and adds it to fav node table
	 *
	 * @param username   userID that correlates to a user in the user table
	 * @param locationID nodeID that correlates to a node in the node table
	 * @param nodeName   the user given name for the node
	 * @return the auto-generated FAVID
	 */
	public String addFavoriteNodeToUser(String username, String locationID, String nodeName) throws SQLException {
		Map<String, String> takenValues = new HashMap<>();
		takenValues.put("USERID", username);
		takenValues.put("LOCATIONID", locationID);
		takenValues.put("NODENAME", nodeName);

		favoriteNodesTable.addEntry(takenValues);
		return getFAVID(username, nodeName);
	}

	/**
	 * Gets the favorite nodes for the user id inputted
	 *
	 * @param username
	 * @return list of map entries containing the favorite nodes, null if it doesn't exist
	 */
	public List<Map<String, String>> getFavoriteNodesForUser(String username) throws SQLException {
		return favoriteNodesTable.getEntriesByValue("USERID", username);
	}

	/**
	 * Gets a specific favorite node for the user id inputted
	 *
	 * @param username
	 * @param nodeName
	 * @return map of the fav node values or null if it doesn't exist
	 */
	public Map<String, String> getFavoriteNodeByUserAndName(String username, String nodeName) throws SQLException {
		Map<String, List<String>> filters = new HashMap<>();
		filters.put("USERID", Collections.singletonList(username));
		filters.put("NODENAME", Collections.singletonList(nodeName));

		List<Map<String, String>> userFaves = favoriteNodesTable.getEntriesByValues(filters);
		if (userFaves.size() == 0) return null;
		else return userFaves.get(0);
	}

	/**
	 * gets a favorite node from table using primary key
	 *
	 * @param favid
	 * @return the map of the favorite node associated with the favid
	 * @throws SQLException
	 */
	public Map<String, String> getFavoriteNodeByFAVID(String favid) throws SQLException {
		return favoriteNodesTable.getEntry(favid);
	}

	/**
	 * deletes a specific fav node from a user's account
	 *
	 * @param username
	 * @param nodeName
	 * @throws SQLException
	 */
	public void deleteFavoriteNodeFromUser(String username, String nodeName) throws SQLException {
		favoriteNodesTable.deleteEntry(getFAVID(username, nodeName));
	}

	/**
	 * empties favorite nodes table by calling empty table
	 *
	 * @throws SQLException
	 */ //needed for tests
	void emptyFavoriteNodesTable() throws SQLException {
		favoriteNodesTable.emptyTable();
	}


	// ===== DATABASE CREATION =====


	/**
	 * Populate the tables of a new database (assumes database has been initialized but is empty)
	 *
	 * @throws SQLException Something went wrong
	 */
	private void createDB(Connection connection) throws SQLException {
		for (String SQL : DatabaseInfo.TABLE_SQL.values()) { // for each primary table
			try (PreparedStatement smnt = connection.prepareStatement(SQL)) {
				smnt.execute(); // create the table
			}
		}
		for (String SQL : DatabaseInfo.SERVICEREQUEST_SQL.values()) { // for each service request table
			try (PreparedStatement smnt = connection.prepareStatement(SQL)) {
				smnt.execute(); // create the table
			}
		}
	}

	/**
	 * Populate the database with default nodes and edges (assumes database is fresh and empty)
	 *
	 * @throws IOException  Something went wrong.
	 * @throws SQLException Something went wrong.
	 */
	private void populateDB() throws IOException, SQLException {
		CSVHandler csvHandler = new CSVHandler(this);
		csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.DEFAULT_NODE_RESOURCE_PATH), TABLES.NODES, true);
		csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.DEFAULT_EDGE_RESOURCE_PATH), TABLES.EDGES, true);


		Map<String, String> adminAccount = new HashMap<String, String>() {{
			put("USERNAME", "admin");
			put("FIRSTNAME", "admin");
			put("LASTNAME", "admin");
			put("EMAIL", "admin");
			put("USERTYPE", USER_TYPE_NAMES.get(USERTYPE.ADMIN));
		}};
		Security.addHashedPassword(adminAccount, "admin");

		userTable.addEntry(adminAccount);


		Map<String, String> employeeAccount = new HashMap<String, String>() {{
			put("USERNAME", "employee");
			put("FIRSTNAME", "employee");
			put("LASTNAME", "employee");
			put("EMAIL", "employee");
			put("USERTYPE", USER_TYPE_NAMES.get(USERTYPE.EMPLOYEE));
		}};
		Security.addHashedPassword(employeeAccount, "employee");

		userTable.addEntry(employeeAccount);


		Map<String, String> patientAccount = new HashMap<String, String>() {{
			put("USERNAME", "patient");
			put("FIRSTNAME", "patient");
			put("LASTNAME", "patient");
			put("EMAIL", "patient");
			put("USERTYPE", USER_TYPE_NAMES.get(USERTYPE.PATIENT));
		}};
		Security.addHashedPassword(patientAccount, "patient");

		userTable.addEntry(patientAccount);
	}

}
