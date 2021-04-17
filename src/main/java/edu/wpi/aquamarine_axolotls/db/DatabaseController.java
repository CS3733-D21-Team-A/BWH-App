package edu.wpi.aquamarine_axolotls.db;

import org.apache.derby.jdbc.EmbeddedDriver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;

import edu.wpi.aquamarine_axolotls.db.DatabaseInfo.*;

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
	final private Map<TABLES.SERVICEREQUESTS,Table> requestsTables;


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
		for (TABLES.SERVICEREQUESTS table : TABLES.SERVICEREQUESTS.values()) {
			if (TABLES.SERVICEREQUESTS.SERVICEREQUESTS_SQL.get(table) != null) {
				requestsTables.put(table, tableFactory.getTable(table));
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
	}

	/**
	 * Get the full Nodes table as a List of Maps
	 * @return List of maps representing the full Nodes table.
	 * @throws SQLException Something went wrong.
	 */
	public List<Map<String,String>> getNodes() throws SQLException {
		return nodeTable.getEntries();
	}

	/**
	 * Query the Nodes table for an entry with the provided primary key.
	 * @param nodeID ID representing node to look for.
	 * @return Map representing the node to query for.
	 * @throws SQLException Something went wrong.
	 */
	public Map<String,String> getNode(String nodeID) throws SQLException {
		return nodeTable.getEntry(nodeID);
	}

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
	 * Delete a edge from the database (assumes node with provided ID exists).
	 * @param edgeID ID of node to delete.
	 * @throws SQLException Something went wrong (likely edge doesn't exist).
	 */
	public void deleteEdge(String edgeID) throws SQLException {
			edgeTable.deleteEntry(edgeID);
	}

	/**
	 * Get the full Edges table as a List of Maps
	 * @return List of maps representing the full Nodes table.
	 * @throws SQLException Something went wrong.
	 */
	public List<Map<String,String>> getEdges() throws SQLException  {
		return edgeTable.getEntries();
	}

	/**
	 * Query the Edges table for an entry with the provided primary key.
	 * @param edgeID ID representing node to look for.
	 * @return Map representing the node to query for.
	 * @throws SQLException Something went wrong.
	 */
	public Map<String,String> getEdge(String edgeID) throws SQLException {
		return edgeTable.getEntry(edgeID);
	}

	public List<Map<String,String>> getEdges(List<String> edgeIDs) throws SQLException {
		Map<String,List<String>> idMap = new HashMap<>();
		idMap.put("EDGEID", edgeIDs);

		return nodeTable.getEntriesByValues(idMap);
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

	public void addServiceRequest(TABLES.SERVICEREQUESTS requestType, Map<String,String> sharedValues, Map<String,String> requestValues) throws SQLException {
		Map<String, String> values = new HashMap<String, String>();
		values.putAll(sharedValues);
		values.putAll(requestValues);
		values.put("REQUESTTYPE", requestType.toString());
		serviceRequestsTable.addEntry(values);
		//TODO: IMPLEMENT THIS
	}

	public void changeStatus(String requestID, TABLES.SERVICEREQUESTS.STATUSES newStatus) throws SQLException {
		//TODO: IMPLEMENT THIS
		Map<String, String> values = new HashMap<String, String>();
		values = serviceRequestsTable.getEntry(requestID);
		values.replace("STATUS", newStatus.toString());
		serviceRequestsTable.editEntry(requestID, values);
	}

	public void changeEmployee(String requestID, String employeeID) throws SQLException {
		//TODO: IMPLEMENT THIS
		Map<String, String> values = new HashMap<String, String>();
		values = serviceRequestsTable.getEntry(requestID);
		values.replace("EMPLOYEE", employeeID);
		serviceRequestsTable.editEntry(requestID, values);
	}

	public void setEmployee(String requestID, String employeeID) throws SQLException {
		//TODO: does this need changing?
		changeEmployee(requestID, employeeID);
		changeStatus(requestID, TABLES.SERVICEREQUESTS.STATUSES.ASSIGNED);
	}

	public List<Map<String,String>> getServiceRequestsWithStatus(TABLES.SERVICEREQUESTS.STATUSES status) throws SQLException {
		return serviceRequestsTable.getEntriesByValue("STATUS", status.toString());
		//TODO: IMPLEMENT THIS
	}


	// ===== NODE / EDGE ATTRIBUTES =====

	private String idColumn(boolean isNode) {
		return isNode ? "NODEID" : "EDGEID";
	}

	public void deleteAttribute(String id, String attribute, boolean isNode) throws SQLException { //TODO: make attribute enum?
		Map<String,List<String>> filters = new HashMap<>();
		filters.put(idColumn(isNode), Collections.singletonList(id));
		filters.put("ATTRIBUTE", Collections.singletonList(attribute));

		for (Map<String,String> entry : attrTable.getEntriesByValues(filters)) { //TODO: this only ever returns one value, simplify?
			attrTable.deleteEntry(entry.get("ATTRID"));
		}
	}

	public void addAttribute(String id, String attribute, boolean isNode) throws SQLException { //TODO: make attribute enum?
		Map<String,String> values = new HashMap<>();
		values.put(idColumn(isNode), id);
		values.put("ATTRIBUTE", attribute);

		attrTable.addEntry(values);
	}

	public void clearAttributes(String id, boolean isNode) throws SQLException {
		List<Map<String,String>> entries = attrTable.getEntriesByValue(idColumn(isNode), id);
		for (Map<String,String> entry : entries) {
			nodeTable.deleteEntry(entry.get("ATTID"));
		}
		//TODO: IMPLEMENT THIS
	}

	public List<String> getAttributes(String id, boolean isNode) throws SQLException { //TODO: make attribute enum?
		List<String> attributes = new ArrayList<>();
		for (Map<String,String> attr : attrTable.getEntriesByValue(idColumn(isNode), id)) {
			attributes.add(attr.get("ATTRIBUTE"));
		}

		return attributes;
	}

	public boolean hasAttribute(String id, String attribute, boolean isNode) throws SQLException { //TODO: make attribute enum?
		Map<String,List<String>> filters = new HashMap<>();
		filters.put(idColumn(isNode), Collections.singletonList(id));
		filters.put("ATTRIBUTE", Collections.singletonList(attribute));

		return attrTable.getEntriesByValues(filters).size() > 0; //TODO: this only ever returns one value, simplify?
	}

	public List<String> getByAttribute(String attribute, boolean isNode) throws SQLException { //TODO: make attribute enum?
		Map<String,List<String>> filters = new HashMap<>();
		filters.put(idColumn(!isNode), Collections.singletonList(null));
		filters.put("ATTRIBUTE", Collections.singletonList(attribute));

		List<String> ids = new ArrayList<>();
		for (Map<String,String> entry : attrTable.getEntriesByValues(filters)) { //TODO: this only ever returns one value, simplify?
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
		for (String SQL : TABLES.TABLE_SQL.values()) {
			try (PreparedStatement smnt = connection.prepareStatement(SQL)) {
				smnt.execute();
			}
		}
		for (String SQL : TABLES.SERVICEREQUESTS.SERVICEREQUESTS_SQL.values()) {
			if (SQL != null) {
				try (PreparedStatement smnt = connection.prepareStatement(SQL)) {
					smnt.execute();
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
		csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.nodeResourcePath), TABLES.NODES, true);
		csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.edgeResourcePath), TABLES.EDGES, true);
	}
}
