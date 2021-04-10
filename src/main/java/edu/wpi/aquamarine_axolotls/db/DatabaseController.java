package edu.wpi.aquamarine_axolotls.db;

import java.util.HashMap;
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
	 * ASSUMES valid node input
	 * @param edgeID
	 * @return
	 */
	public String edgeFlip(String edgeID) throws InvalidEdgeFormatException{
		int i = edgeID.indexOf("_");
		// length cannot possibly be valid, value is not found, more than one occurence of _ is found
		if(edgeID.length() <= 1 || i == -1 || edgeID.lastIndexOf("_") != i) throw new InvalidEdgeFormatException(edgeID);
		return edgeID.substring(i + 1, edgeID.length()) + "_" + edgeID.substring(0, i);

	}
	/**
	 * Check if an edge exists.
	 * @param edgeID ID of edge to check.
	 * @return Boolean indicating presence of edge in the database.
	 */
	public boolean edgeExists(String edgeID) throws InvalidEdgeFormatException{

		try{
			// checks edge and flipped edge ie Node1_Node2 and Node2_Node1
			// checks null here since edited edge may be empty
			if(edgeID == null || ( edgeTable.getEntry(edgeID) == null && edgeTable.getEntry(edgeFlip(edgeID)) == null) )
				return false;
		} catch (InvalidEdgeFormatException e ){
			throw e;
		}

		return true;
	}

	/**
	 * Add an edge to the database (assumes edge with provided primary key doesn't already exist).
	 * @param values Map whose keys are the column names and values are the entry values
	 */

	// DONE
	public void addEdge(Map<String,String> values) throws InvalidEdgeFormatException, EdgeAlreadyExistsException{

		try{
			// edge already exists
			if(edgeExists(values.get("edgeID"))) throw new EdgeAlreadyExistsException(values.get("edgeID"));
			else edgeTable.addEntry(values);
		}
		catch (InvalidEdgeFormatException e){
			throw e;
		}

	}

	/**
	 * Edit an existing edge in the database (assumes node with provided ID exists).
	 * @param edgeID ID of edge to edit.
	 * @param values Map whose keys are the column names and values are the new entry values
	 * @return Rows in database updated.
	 */

	public int editEdge(String edgeID, Map<String,String> values) throws InvalidEdgeFormatException, EdgeDoesNotExistException, EdgeAlreadyExistsException{
		try{
			// edge we are trying to edit does not exists
			if(!edgeExists(edgeID)) throw new EdgeDoesNotExistException(edgeID);
			String new_edgeID = values.get("edgeID");
			// edge we are trying to change to already exists
			if(new_edgeID != null && edgeExists(new_edgeID)) throw new EdgeAlreadyExistsException(new_edgeID);
			return edgeTable.editEntry(edgeID, values);
		}catch(InvalidEdgeFormatException e){
			throw e;

		}


	}

	/**
	 * Delete a edge from the database (assumes node with provided ID exists).
	 * @param edgeID ID of node to delete.
	 * @return Rows in database updated.
	 */
	public int deleteEdge(String edgeID) throws InvalidEdgeFormatException, EdgeDoesNotExistException{

		try{
			// edge does not exist
			if(!edgeExists(edgeID)) throw new EdgeDoesNotExistException(edgeID);
			return edgeTable.deleteEntry(edgeID);
		} catch (InvalidEdgeFormatException e) {
			throw e;
		}

	}

	/**
	 * Get the full Edges table as a List<Map<String,String>>
	 * @return List of maps representing the full Nodes table.
	 */
	public List<Map<String,String>> getEdges() {
		return edgeTable.getEntries();
	}

	/**
	 * Query the Edges table for an entry with the provided primary key.
	 * @param edgeID ID representing node to look for.
	 * @return Map representing the node to query for.
	 */
	public Map<String,String> getEdge(String edgeID) throws InvalidEdgeFormatException, EdgeDoesNotExistException {
		try{
			// edge does not exist
			if(!edgeExists(edgeID)) throw new EdgeDoesNotExistException(edgeID);
			return edgeTable.getEntry(edgeID);
		}catch(InvalidEdgeFormatException e){
			throw e;
		}
	}

	/**
	 * Get edges connected to the node with the provided ID
	 * @param nodeID ID of node to find edges connected to.
	 * @return List of maps of edges connected to the desired node.
	 */
	public List<Map<String,String>> getEdgesConnectedToNode(String nodeID) {
		if(!nodeExists(nodeID)) return null; // TODO throw error
		// gets all edges that has nodeID as a start node
		List<Map<String,String>> edges = edgeTable.getEntriesByValue("startNode", nodeID);
		// gets all edges that have the nodeID as a end node
		edges.addAll(edgeTable.getEntriesByValue("endNode", nodeID));

		return edges;
	}
}


