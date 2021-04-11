package edu.wpi.aquamarine_axolotls.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DatabaseController {
	private Table nodeTable;
	private Table edgeTable;

	public DatabaseController() {
		//TODO: Implement this
	}

	// ===== NODES =====

	/**
	 * Get the columns of the node table.
	 * @return a dictionaary whose keys are the names of the columns and value.
	 * Value is a boolean indicating if they representing type (false = int,true = String).
	 */
	public Map<String,Boolean> getNodeColumns() {
		return null; //TODO: Implement this
	}

	/**
	 * Check if a node exists.
	 * @param nodeID ID of node to check.
	 * @return Boolean indicating presence of node in the database.
	 */
	public boolean nodeExists(String nodeID) {
		return false;//TODO: Implement this
	}

	/**
	 * Add a node to the database (assumes node with provided primary key doesn't already exist).
	 * @param values Map whose keys are the column names and values are the entry values
	 */
	public void addNode(Map<String,String> values) {
		//TODO: Implement this
	}

	/**
	 * Edit an existing node in the database (assumes node with provided ID exists).
	 * @param nodeID ID of node to edit.
	 * @param values Map whose keys are the column names and values are the new entry values
	 * @return Rows in database updated.
	 */
	public int editNode(String nodeID, Map<String,String> values) {
		return -1; //TODO: Implement this
	}

	/**
	 * Delete a node from the database (assumes node with provided ID exists).
	 * @param nodeID ID of node to delete.
	 * @return Rows in database updated.
	 */
	public int deleteNode(String nodeID) {
		return -1; //TODO: Implement this
	}

	/**
	 * Get the full Nodes table as a List<Map<String,String>>
	 * @return List of maps representing the full Nodes table.
	 */
	public List<Map<String,String>> getNodes() {
		return null; //TODO: Implement this
	}

	/**
	 * Query the Nodes table for an entry with the provided primary key.
	 * @param nodeID ID representing node to look for.
	 * @return Map representing the node to query for.
	 */
	public Map<String,String> getNode(String nodeID) {
		return null; //TODO: Implement this
	}

	// ===== EDGES =====

	/**
	 * Get the columns of the edge table.
	 * @return a dictionaary whose keys are the names of the columns and value.
	 * Value is a boolean indicating if they representing type (false = int,true = String).
	 */
	public Map<String,Boolean> getEdgeColumns() {
		return null; //TODO: Implement this
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
	 * @return Rows in database updated.
	 */
	public int editEdge(String edgeID, Map<String,String> values) throws SQLException {
		return edgeTable.editEntry(edgeID, values);
	}

	/**
	 * Delete a edge from the database (assumes node with provided ID exists).
	 * @param edgeID ID of node to delete.
	 * @return Rows in database updated.
	 */
	public int deleteEdge(String edgeID) throws SQLException {
			return edgeTable.deleteEntry(edgeID);
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
		// gets all edges that has nodeID as a start node
		List<Map<String,String>> edges = edgeTable.getEntriesByValue("startNode", nodeID);
		// gets all edges that have the nodeID as a end node
		edges.addAll(edgeTable.getEntriesByValue("endNode", nodeID));

		return edges;
	}
}


