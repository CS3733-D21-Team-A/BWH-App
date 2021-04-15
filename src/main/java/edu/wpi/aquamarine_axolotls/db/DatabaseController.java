package edu.wpi.aquamarine_axolotls.db;

import org.apache.derby.jdbc.EmbeddedDriver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * Controller class for working with the BWH database.
 */
public class DatabaseController implements AutoCloseable {
	final private Connection connection;
	final private Table nodeTable;
	final private Table edgeTable;

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
		nodeTable = tableFactory.getTable(DatabaseInfo.TABLES.NODES);
		edgeTable = tableFactory.getTable(DatabaseInfo.TABLES.EDGES);

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


	// ===== DATABASE CREATION =====


	/**
	 * Populate the tables of a new database (assumes database has been initialized but is empty)
	 * @throws SQLException Something went wrong
	 */
	private void createDB() throws SQLException {
		try (PreparedStatement smnt = connection.prepareStatement(DatabaseInfo.NODE_TABLE_SQL)) {
			smnt.execute();
		}

		try (PreparedStatement smnt = connection.prepareStatement(DatabaseInfo.EDGE_TABLE_SQL)) { //TODO: Make the column names available as static variables?
			smnt.execute();
		}
	}

	/**
	 * Populate the database with default nodes and edges (assumes database is fresh and empty)
	 * @throws URISyntaxException Something went wrong.
	 * @throws IOException Something went wrong.
	 * @throws SQLException Something went wrong.
	 */
	private void populateDB() throws URISyntaxException, IOException, SQLException {
		CSVHandler csvHandler = new CSVHandler(this);
		csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.nodeResourcePath), DatabaseInfo.TABLES.NODES);
		csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.edgeResourcePath), DatabaseInfo.TABLES.EDGES);
	}
}
