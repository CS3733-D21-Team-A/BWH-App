package edu.wpi.aquamarine_axolotls.db;

import java.sql.*;
import java.util.Map;

public class DatabaseController {
	private PreparedStatement queuedChanges;
	private Table nodeTable;
	private Table edgeTable;

	public DatabaseController() {
		//TODO: Implement this
	}

	// ===== NODES =====

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
	 * Get the full Nodes table as a ResultSet
	 * @return ResultSet representing the full Nodes table.
	 */
	public ResultSet getNodes() {
		return null; //TODO: Implement this
	}

	/**
	 * Query the Nodes table for an entry with the provided primary key.
	 * @param nodeID ID representing node to look for.
	 * @return ResultSet representing the node to query for.
	 */
	public ResultSet getNode(String nodeID) {
		return null; //TODO: Implement this
	}

	// ===== EDGES =====

	/**
	 * Check if an edge exists.
	 * @param edgeID ID of edge to check.
	 * @return Boolean indicating presence of edge in the database.
	 */
	public boolean edgeExists(String edgeID) {
		return false;//TODO: Implement this
	}

	/**
	 * Add an edge to the database (assumes edge with provided primary key doesn't already exist).
	 * @param values Map whose keys are the column names and values are the entry values
	 */
	public void addEdge(Map<String,String> values) {
		//TODO: Implement this
	}

	/**
	 * Edit an existing edge in the database (assumes node with provided ID exists).
	 * @param nodeID ID of edge to edit.
	 * @param values Map whose keys are the column names and values are the new entry values
	 * @return Rows in database updated.
	 */
	public int editEdge(String nodeID, Map<String,String> values) {
		return -1; //TODO: Implement this
	}

	/**
	 * Delete a edge from the database (assumes node with provided ID exists).
	 * @param edgeID ID of node to delete.
	 * @return Rows in database updated.
	 */
	public int deleteEdge(String edgeID) {
		return -1; //TODO: Implement this
	}

	/**
	 * Get the full Edges table as a ResultSet
	 * @return ResultSet representing the full Nodes table.
	 */
	public ResultSet getEdge() {
		return null; //TODO: Implement this
	}

	/**
	 * Query the Edges table for an entry with the provided primary key.
	 * @param edgeID ID representing node to look for.
	 * @return ResultSet representing the node to query for.
	 */
	public ResultSet getEdge(String edgeID) {
		return null; //TODO: Implement this
	}

	/**
	 * Get edges connected to the node with the provided ID
	 * @param nodeID ID of node to find edges connected to.
	 * @return ResultSet of edges connected to the desired node.
	 */
	public ResultSet getEdgesConnectedToNode(String nodeID) {
		return null; //TODO: Implement this
	}

	// ===== CHANGES =====

	/**
	 * Save any currently unsaved table changes.
	 */
	public void saveChanges() {
		//TODO: Implement this
	}

	/**
	 * Drop any currently unsaved table changes.
	 */
	public void cancelChanges() {
		//TODO: Implement this
	}

	/**
	 * Return whether or not there are any unsaved table changes.
	 * @return Whether or not there are any unsaved table changes.
	 */
	public boolean unsavedChanges() {
		return false; //TODO: Implement this
	}
}
