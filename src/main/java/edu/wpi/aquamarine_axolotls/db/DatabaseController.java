package edu.wpi.aquamarine_axolotls.db;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DatabaseController {
	final private Connection connection;
	final private Table nodeTable;
	final private Table edgeTable;

	public DatabaseController() throws SQLException, IOException, URISyntaxException {
		boolean dbExists;
		try {
			DriverManager.getConnection("jdbc:derby:BWH", "admin", "admin"); //TODO: login credentials
			dbExists = true;
		} catch (SQLException e) {
			dbExists = false;
		}

		connection = DriverManager.getConnection("jdbc:derby:BWH;create=true", "admin", "admin"); //TODO: login credentials
		if (!dbExists) {
			System.out.println("No database found. Creating new one...");
			createDB();
		} else System.out.println("Database found!");

		TableFactory tableFactory = new TableFactory(connection);
		nodeTable = tableFactory.getTable(DatabaseInfo.TABLES.NODES);
		edgeTable = tableFactory.getTable(DatabaseInfo.TABLES.EDGES);

		if (!dbExists) {
			populateDB();
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
	 */
	public boolean nodeExists(String nodeID) throws SQLException {
		return nodeTable.getEntry(nodeID) != null;
	}

	/**
	 * Add a node to the database (assumes node with provided primary key doesn't already exist).
	 * @param values Map whose keys are the column names and values are the entry values
	 */
	public void addNode(Map<String,String> values) throws SQLException {
		nodeTable.addEntry(values);
	}

	/**
	 * Edit an existing node in the database (assumes node with provided ID exists).
	 * @param nodeID ID of node to edit.
	 * @param values Map whose keys are the column names and values are the new entry values
	 */
	public void editNode(String nodeID, Map<String,String> values) throws SQLException {
		nodeTable.editEntry(nodeID,values);
	}

	/**
	 * Delete a node from the database (assumes node with provided ID exists).
	 * @param nodeID ID of node to delete.
	 */
	public void deleteNode(String nodeID) throws SQLException {
		nodeTable.deleteEntry(nodeID);
	}

	/**
	 * Get the full Nodes table as a List<Map<String,String>>
	 * @return List of maps representing the full Nodes table.
	 */
	public List<Map<String,String>> getNodes() throws SQLException {
		return nodeTable.getEntries();
	}

	/**
	 * Query the Nodes table for an entry with the provided primary key.
	 * @param nodeID ID representing node to look for.
	 * @return Map representing the node to query for.
	 */
	public Map<String,String> getNode(String nodeID) throws SQLException {
		return nodeTable.getEntry(nodeID);
	}

	/**
	 * Empties the node table by deleting all entries.
	 */
	void emptyNodeTable() throws SQLException {
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
	 */
	public boolean edgeExists(String edgeID) throws SQLException {
		return edgeTable.getEntry(edgeID) != null;
	}

	/**
	 * Add an edge to the database (assumes edge with provided primary key doesn't already exist).
	 * @param values Map whose keys are the column names and values are the entry values
	 */

	// DONE
	public void addEdge(Map<String,String> values) throws SQLException {
		edgeTable.addEntry(values);
	}

	/**
	 * Edit an existing edge in the database (assumes node with provided ID exists).
	 * @param edgeID ID of edge to edit.
	 * @param values Map whose keys are the column names and values are the new entry values
	 */
	public void editEdge(String edgeID, Map<String,String> values) throws SQLException {
		edgeTable.editEntry(edgeID, values);
	}

	/**
	 * Delete a edge from the database (assumes node with provided ID exists).
	 * @param edgeID ID of node to delete.
	 */
	public void deleteEdge(String edgeID) throws SQLException {
			edgeTable.deleteEntry(edgeID);
	}

	/**
	 * Get the full Edges table as a List<Map<String,String>>
	 * @return List of maps representing the full Nodes table.
	 */
	public List<Map<String,String>> getEdges() throws SQLException  {
		return edgeTable.getEntries();
	}

	/**
	 * Query the Edges table for an entry with the provided primary key.
	 * @param edgeID ID representing node to look for.
	 * @return Map representing the node to query for.
	 */
	public Map<String,String> getEdge(String edgeID) throws SQLException {
		return edgeTable.getEntry(edgeID);
	}

	/**
	 * Get edges connected to the node with the provided ID
	 * @param nodeID ID of node to find edges connected to.
	 * @return List of maps of edges connected to the desired node.
	 */
	public List<Map<String,String>> getEdgesConnectedToNode(String nodeID) throws SQLException {
		List<Map<String,String>> edges = edgeTable.getEntriesByValue("STARTNODE", nodeID); // gets all edges that has nodeID as a start node
		List<Map<String,String>> edges2 = edgeTable.getEntriesByValue("ENDNODE", nodeID); // gets all edges that have the nodeID as a end node
		if (edges == null) {
			edges = edges2;
		} else if (edges2 != null) {
			edges.addAll(edges2);
		}

		return edges;
	}

	/**
	 * Empties the edge table by deleting all entries.
	 */
	void emptyEdgeTable() throws SQLException {
		edgeTable.emptyTable();
	}


	// ===== DATABASE CREATION =====


	private void createDB() throws SQLException, IOException, URISyntaxException {
		PreparedStatement smnt = connection.prepareStatement(
			"CREATE TABLE NODES (" +
				"NODEID VARCHAR(25) PRIMARY KEY," +
				"XCOORD NUMERIC(5)," +
				"YCOORD NUMERIC(5)," +
				"FLOOR VARCHAR(3)," +
				"BUILDING VARCHAR(30)," +
				"NODETYPE VARCHAR(5)," +
				"LONGNAME VARCHAR(50)," +
				"SHORTNAME VARCHAR(30)" +
			")"
		);

		smnt.execute();

		smnt = connection.prepareStatement( //TODO: Make the column names available as static variables?
			"CREATE TABLE EDGES (" +
				"EDGEID VARCHAR(51) PRIMARY KEY," +
				"STARTNODE VARCHAR(25)," +
				"ENDNODE VARCHAR(25)," +
				"CONSTRAINT FK_STARTNODE FOREIGN KEY (STARTNODE) REFERENCES Nodes(NODEID) ON DELETE CASCADE ON UPDATE RESTRICT," +
				"CONSTRAINT FK_ENDNODE FOREIGN KEY (ENDNODE) REFERENCES Nodes(NODEID) ON DELETE CASCADE ON UPDATE RESTRICT" +
			")"
		);
		smnt.execute();
	}

	private void populateDB() throws URISyntaxException, IOException, SQLException {
		CSVHandler csvHandler = new CSVHandler(this);
		csvHandler.importCSV(DatabaseInfo.resourcePathToFile(DatabaseInfo.nodeResourcePath), DatabaseInfo.TABLES.NODES);
		csvHandler.importCSV(DatabaseInfo.resourcePathToFile(DatabaseInfo.edgeResourcePath), DatabaseInfo.TABLES.EDGES);
	}
}


