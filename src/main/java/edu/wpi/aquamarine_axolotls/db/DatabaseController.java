package edu.wpi.aquamarine_axolotls.db;

import org.apache.derby.jdbc.EmbeddedDriver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;

import edu.wpi.aquamarine_axolotls.db.DatabaseInfo.*;
import edu.wpi.aquamarine_axolotls.db.DatabaseInfo.TABLES.*;
import edu.wpi.aquamarine_axolotls.db.DatabaseInfo.TABLES.SERVICEREQUESTS.*;

/**
 * Controller class for working with the BWH database.
 */
public class DatabaseController implements AutoCloseable {
	final private Connection connection;
	final private Table nodeTable;
	final private Table edgeTable;
	final private Table attrTable;
	final private Table serviceRequestsTable;
	/*
	 * Map for getting service request tables.
	 * Key: Service Request enumeration.
	 * Value: Table for corresponding service request type.
	 */
	final private Map<SERVICEREQUESTS,RequestTable> requestsTables;


	/**
	 * DatabaseController constructor. Creates and populates new database if one is not found.
	 * @throws SQLException Something went wrong.
	 * @throws IOException Something went wrong.
	 * @throws URISyntaxException Something went wrong.
	 */
	public DatabaseController() throws SQLException, IOException, URISyntaxException {
		DriverManager.registerDriver(new EmbeddedDriver());

		boolean dbExists;
		try (Connection cTest = DriverManager.getConnection("jdbc:derby:BWH", "admin", "admin")) { //TODO: login credentials
			dbExists = true;
		} catch (SQLException e) {
			dbExists = false;
		}

		connection = DriverManager.getConnection("jdbc:derby:BWH;create=true", "admin", "admin"); //TODO: login credentials
		if (!dbExists) {
			System.out.println("No database found. Creating new one...");
			createDB();
		}

		TableFactory tableFactory = new TableFactory(connection);
		nodeTable = tableFactory.getTable(TABLES.NODES);
		edgeTable = tableFactory.getTable(TABLES.EDGES);
		attrTable = tableFactory.getTable(TABLES.ATTRIBUTES);
		serviceRequestsTable = tableFactory.getTable(TABLES.SERVICE_REQUESTS);

		requestsTables = new HashMap<>();
		for (SERVICEREQUESTS table : SERVICEREQUESTS.values()) {
			if (SERVICEREQUESTS.SERVICEREQUESTS_SQL.get(table) != null) {
				requestsTables.put(table, tableFactory.getRequestTable(table));
			}
		}

		if (!dbExists) {
			populateDB();
		}
	}

	@Override
	public void close() throws SQLException {
		if (!connection.isClosed()) {
			connection.close();
		}
	}

	@Override
	protected void finalize() throws SQLException {
		this.close();
	}

	/**
	 * Shuts down the database connection.
	 * Note: This will shut down the connection for all currently running DatabaseControllers!
	 * @return If shutdown was successful.
	 */
	public static boolean shutdownDB() {
		try (Connection shutdown = DriverManager.getConnection("jdbc:derby:BWH;shutdown=true", "admin", "admin")) {
			return false; // Shutting down a database should throw an exception. If it doesn't, something went wrong!
		} catch (SQLException e) {
			return true; // Shutting down a database throws an exception!
		}
	}

	// ===== NODES =====

	/**
	 * Get the columns of the node table.
	 * @return a dictionaary whose keys are the names of the columns and value.
	 * Value is a boolean indicating if they representing type (false = int,true = String).
	 */
	public Map<String,Boolean> getNodeColumns() {
		return nodeTable.getColumns();
	}

	/**
	 * Check if a node exists.
	 * @param nodeID ID of node to check.
	 * @return Boolean indicating presence of node in the database.
	 * @throws SQLException Something went wrong.
	 */
	public boolean nodeExists(String nodeID) throws SQLException {
		return nodeTable.getEntry(nodeID) != null;
	}

	/**
	 * Add a node to the database (assumes node with provided primary key doesn't already exist).
	 * @param values Map whose keys are the column names and values are the entry values.
	 * @throws SQLException Something went wrong (likely node already exists or malformed imput).
	 */
	public void addNode(Map<String,String> values) throws SQLException {
		nodeTable.addEntry(values);
	}

	/**
	 * Edit an existing node in the database (assumes node with provided ID exists).
	 * @param nodeID ID of node to edit.
	 * @param values Map whose keys are the column names and values are the new entry values.
	 * @throws SQLException Something went wrong (likely tried to edit node that doesn't exist or change a nodeID).
	 */
	public void editNode(String nodeID, Map<String,String> values) throws SQLException {
		nodeTable.editEntry(nodeID,values);
	}

	/**
	 * Delete a node from the database (assumes node with provided ID exists).
	 * @param nodeID ID of node to delete.
	 * @throws SQLException Something went wrong (likely node doesn't exist).
	 */
	public void deleteNode(String nodeID) throws SQLException {
		nodeTable.deleteEntry(nodeID);
		Map<String, List<String>> filters = new HashMap<>();
		filters.put("LOCATIONID", Collections.singletonList(nodeID));
		filters.put("STATUS", Arrays.asList(STATUS.UNASSIGNED.text, STATUS.ASSIGNED.text, STATUS.IN_PROGRESS.text));
		for(Map<String,String> entry :serviceRequestsTable.getEntriesByValues(filters)){
			changeStatus(entry.get("REQUESTID"),STATUS.CANCELED);
		}
	}

	/**
	 * Get the full NODES table as a List of Maps
	 * @return List of maps representing the full NODES table.
	 * @throws SQLException Something went wrong.
	 */
	public List<Map<String,String>> getNodes() throws SQLException {
		return nodeTable.getEntries();
	}

	/**
	 * Query the NODES table for an entry with the provided primary key.
	 * @param nodeID ID representing node to look for.
	 * @return Map representing the node to query for.
	 * @throws SQLException Something went wrong.
	 */
	public Map<String,String> getNode(String nodeID) throws SQLException {
		return nodeTable.getEntry(nodeID);
	}

	/**
	 * Query the NODES table for entries with the provided primary keys.
	 * @param nodeIDs ID representing nodes to look for.
	 * @return List of Maps representing the nodes found.
	 * @throws SQLException Something went wrong.
	 */
	public List<Map<String,String>> getNodes(List<String> nodeIDs) throws SQLException {
		Map<String,List<String>> idMap = new HashMap<>();
		idMap.put("NODEID", nodeIDs);

		return nodeTable.getEntriesByValues(idMap);
	}

	/**
	 * Empties the node table by deleting all entries.
	 * @throws SQLException Something went wrong.
	 */
	public void emptyNodeTable() throws SQLException {
		nodeTable.emptyTable();
	}

	// ===== EDGES =====

	/**
	 * Get the columns of the edge table.
	 * @return a dictionaary whose keys are the names of the columns and value.
	 * Value is a boolean indicating if they representing type (false = int,true = String).
	 */
	public Map<String,Boolean> getEdgeColumns() {
		return edgeTable.getColumns();
	}

	/**
	 * Check if an edge exists.
	 * @param edgeID ID of edge to check.
	 * @return Boolean indicating presence of edge in the database.
	 * @throws SQLException Something went wrong.
	 */
	public boolean edgeExists(String edgeID) throws SQLException {
		return edgeTable.getEntry(edgeID) != null;
	}

	/**
	 * Add an edge to the database (assumes edge with provided primary key doesn't already exist).
	 * @param values Map whose keys are the column names and values are the entry values.
	 * @throws SQLException Something went wrong (likely edge already exists or malformed input).
	 */

	// DONE
	public void addEdge(Map<String,String> values) throws SQLException {
		edgeTable.addEntry(values);
	}

	/**
	 * Edit an existing edge in the database (assumes node with provided ID exists).
	 * @param edgeID ID of edge to edit.
	 * @param values Map whose keys are the column names and values are the new entry values
	 * @throws SQLException Something went wrong (likely edge doesn't exist or tried to edit edge ID).
	 */
	public void editEdge(String edgeID, Map<String,String> values) throws SQLException {
		edgeTable.editEntry(edgeID, values);
	}

	/**
	 * Delete an edge from the database (assumes node with provided ID exists).
	 * @param edgeID ID of edge to delete.
	 * @throws SQLException Something went wrong (likely edge doesn't exist).
	 */
	public void deleteEdge(String edgeID) throws SQLException {
		edgeTable.deleteEntry(edgeID);
	}

	/**
	 * Get the full EDGES table as a List of Maps
	 * @return List of maps representing the full NODES table.
	 * @throws SQLException Something went wrong.
	 */
	public List<Map<String,String>> getEdges() throws SQLException  {
		return edgeTable.getEntries();
	}

	/**
	 * Query the EDGES table for an entry with the provided primary key.
	 * @param edgeID ID representing node to look for.
	 * @return Map representing the node to query for.
	 * @throws SQLException Something went wrong.
	 */
	public Map<String,String> getEdge(String edgeID) throws SQLException {
		return edgeTable.getEntry(edgeID);
	}

	/**
	 * Query the EDGES table for entries with the provided primary keys.
	 * @param edgeIDs ID representing edges to look for.
	 * @return List of Maps representing the edges found.
	 * @throws SQLException Something went wrong.
	 */
	public List<Map<String,String>> getEdges(List<String> edgeIDs) throws SQLException {
		Map<String,List<String>> idMap = new HashMap<>();
		idMap.put("EDGEID", edgeIDs);

		return edgeTable.getEntriesByValues(idMap);
	}

	/**
	 * Get edges connected to the node with the provided ID
	 * @param nodeID ID of node to find edges connected to.
	 * @return List of maps of edges connected to the desired node.
	 * @throws SQLException Something went wrong (likely node doesn't exist).
	 */
	public List<Map<String,String>> getEdgesConnectedToNode(String nodeID) throws SQLException {
		List<Map<String,String>> edges = edgeTable.getEntriesByValue("STARTNODE", nodeID); // gets all edges that has nodeID as a start node
		List<Map<String,String>> edges2 = edgeTable.getEntriesByValue("ENDNODE", nodeID); // gets all edges that have the nodeID as a end node
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
	public void emptyEdgeTable() throws SQLException {
		edgeTable.emptyTable();
	}


	// ===== SERVICE REQUESTS =====

	/**
	 * Insert a new service request into the databse.
	 * @param sharedValues Values shared among all service requests (goes into the SERVICE_REQUESTS table).
	 * @param requestValues Values specific to the particular type of service request.
	 * @throws SQLException Something went wrong.
	 */
	public void addServiceRequest(Map<String,String> sharedValues, Map<String,String> requestValues) throws SQLException {
		serviceRequestsTable.addEntry(sharedValues);
		requestsTables.get(SERVICEREQUESTS.stringToServiceRequest(sharedValues.get("REQUESTTYPE"))).addEntry(requestValues);
	}

	/**
	 * Change the status of a service request.
	 * @param requestID ID of the service request to change the status for.
	 * @param newStatus New status of the service request.
	 * @throws SQLException Something went wrong.
	 */
	public void changeStatus(String requestID, STATUS newStatus) throws SQLException {
		Map<String, String> values = serviceRequestsTable.getEntry(requestID);
		values.replace("STATUS", newStatus.text);
		serviceRequestsTable.editEntry(requestID, values);
	}

	/**
	 * Change the employee assigned to a service request.
	 * @param requestID ID of the service request to change the employee for.
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
	 * @param requestID ID of the service request to assign the employee to.
	 * @param employeeID ID of the employee to assign to the service request.
	 * @throws SQLException Something went wrong.
	 */
	public void assignEmployee(String requestID, String employeeID) throws SQLException {
		changeEmployee(requestID, employeeID);
		changeStatus(requestID, STATUS.ASSIGNED);
	}

	/**
	 * Get all service requests with the desired status.
	 * @param status Status of service requests to filter for.
	 * @return List of Maps representing the service requests.
	 * @throws SQLException Something went wrong.
	 */
	public List<Map<String,String>> getServiceRequestsWithStatus(STATUS status) throws SQLException {
		return serviceRequestsTable.getEntriesByValue("STATUS", status.text);
	}

	/**
	 * Get all service requests.
	 * @return List of Maps representing the service requests.
	 * @throws SQLException Something went wrong.
	 */
	public List<Map<String,String>> getServiceRequests() throws SQLException {
		return serviceRequestsTable.getEntries();
	}

	/**
	 * Get all service requests of the desired type.
	 * @param requestType Service request type to get.
	 * @return List of Maps representing the service requests.
	 * @throws SQLException Something went wrong.
	 */
	public List<Map<String,String>> getServiceRequestsByType(SERVICEREQUESTS requestType) throws SQLException {
		return requestsTables.get(requestType).getRequests();
	}

	/**
	 * Empties the service requests tables by deleting all entries.
	 */
	public void emptyServiceRequestsTable() throws SQLException {
		serviceRequestsTable.emptyTable();
		//Service requests cascade, so we don't need to clear request-specific tables
	}


	// ===== NODE / EDGE ATTRIBUTES =====

	/**
	 * Get the name of the primary key of the node or edge table.
	 * @param isNode Boolean indicating whether you want the NODES table or EDGES table primary key
	 * @return Column of primary key for desired table.
	 */
	private String idColumn(boolean isNode) {
		return isNode ? "NODEID" : "EDGEID"; //TODO: Make this pull from Table objects.
	}

	/**
	 * Remove an attribute from the provided entry.
	 * @param id ID of node or edge to remove attribute from.
	 * @param attribute Attribute to remove.
	 * @param isNode Boolean indicating whether attempting to remove an attribute of a node or an edge.
	 * @throws SQLException Something went wrong.
	 */
	public void deleteAttribute(String id, ATTRIBUTE attribute, boolean isNode) throws SQLException {
		Map<String,List<String>> filters = new HashMap<>();
		filters.put(idColumn(isNode), Collections.singletonList(id));
		filters.put("ATTRIBUTE", Collections.singletonList(attribute.text));

		for (Map<String, String> entry : attrTable.getEntriesByValues(filters)){
			attrTable.deleteEntry(entry.get("ATTRID"));
		}
	}

	/**
	 * Add an attribute to the provided entry.
	 * @param id ID of node or edge to add attribute to.
	 * @param attribute Attribute to add.
	 * @param isNode Boolean indicating whether attempting to add an attribute to a node or an edge.
	 * @return Boolean indicating if the addition was successful (false if the node already has the attribute)
	 * @throws SQLException Something went wrong.
	 */
	public boolean addAttribute(String id, ATTRIBUTE attribute, boolean isNode) throws SQLException {
		Map<String,String> values = new HashMap<>();
		values.put(idColumn(isNode), id);
		values.put("ATTRIBUTE", attribute.text);

		if(!(hasAttribute(id, attribute, isNode))){
			attrTable.addEntry(values);
			return true;
		}
		System.out.println("Entry already has attribute!");
		return false;
	}

	/**
	 * Removes all attributes from the provided entry.
	 * @param id ID of node or edge to clear attributes from.
	 * @param isNode Boolean indicating whether attempting to remove attributes of a node or an edge.
	 * @throws SQLException Something went wrong.
	 */
	public void clearAttributes(String id, boolean isNode) throws SQLException {
		List<Map<String,String>> entries = attrTable.getEntriesByValue(idColumn(isNode), id);
		for (Map<String,String> entry : entries) {
			attrTable.deleteEntry(entry.get("ATTRID"));
		}
	}

	/**
	 * Get all the attributes of the provided entry.
	 * @param id ID of node or edge to get attributes for.
	 * @param isNode Boolean indicating whether you're getting a node or an edge.
	 * @return List of attributes the entry has.
	 * @throws SQLException Something went wrong.
	 */
	public List<ATTRIBUTE> getAttributes(String id, boolean isNode) throws SQLException {
		List<ATTRIBUTE> attributes = new ArrayList<>();
		for (Map<String,String> attr : attrTable.getEntriesByValue(idColumn(isNode), id)) {
			attributes.add(ATTRIBUTE.stringToAttribute(attr.get("ATTRIBUTE")));
		}

		return attributes;
	}

	/**
	 * Check whether or not the desired entry has a certain attribute.
	 * @param id ID of node or edge to check.
	 * @param attribute Attribute to check for.
	 * @param isNode Boolean indicating whether attempting to look for a node or an edge.
	 * @return Boolean indicating if the desired entry has the specified attribute.
	 * @throws SQLException Something went wrong.
	 */
	public boolean hasAttribute(String id, ATTRIBUTE attribute, boolean isNode) throws SQLException {
		Map<String,List<String>> filters = new HashMap<>();
		filters.put(idColumn(isNode), Collections.singletonList(id));
		filters.put("ATTRIBUTE", Collections.singletonList(attribute.text));

		return attrTable.getEntriesByValues(filters).size() > 0;
	}

	/**
	 * Get all nodes or edges with the desired attribute.
	 * @param attribute Attribute to filter by.
	 * @param isNode Boolean indicating whether attempting to look for nodes or edges.
	 * @return List of IDs corresponding to the associated entries in the NODES or EDGES table.
	 * @throws SQLException Something went wrong.
	 */
	public List<String> getByAttribute(ATTRIBUTE attribute, boolean isNode) throws SQLException {
		Map<String,List<String>> filters = new HashMap<>();
		filters.put(idColumn(!isNode), Collections.singletonList(null));
		filters.put("ATTRIBUTE", Collections.singletonList(attribute.text));

		List<String> ids = new ArrayList<>();
		for (Map<String,String> entry : attrTable.getEntriesByValues(filters)) {
			ids.add(entry.get(idColumn(isNode)));
		}

		return ids;
	}


	// ===== DATABASE CREATION =====


	/**
	 * Populate the tables of a new database (assumes database has been initialized but is empty)
	 * @throws SQLException Something went wrong
	 */
	private void createDB() throws SQLException {
		for (String SQL : TABLES.TABLE_SQL.values()) { // for each primary table
			try (PreparedStatement smnt = connection.prepareStatement(SQL)) {
				smnt.execute(); // create the table
			}
		}
		for (String SQL : SERVICEREQUESTS.SERVICEREQUESTS_SQL.values()) { // for each service request table
			if (SQL != null) { // if we have written SQL code for it
				try (PreparedStatement smnt = connection.prepareStatement(SQL)) {
					smnt.execute(); // create the table
				}
			}
		}
	}

	/**
	 * Populate the database with default nodes and edges (assumes database is fresh and empty)
	 * @throws IOException Something went wrong.
	 * @throws SQLException Something went wrong.
	 */
	private void populateDB() throws IOException, SQLException {
		CSVHandler csvHandler = new CSVHandler(this);
		csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.NODE_RESOURCE_PATH), TABLES.NODES, true);
		csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.EDGE_RESOURCE_PATH), TABLES.EDGES, true);
	}

}
