package edu.wpi.aquamarine_axolotls.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Table {
	private Connection connection;
	private String tableName;
	private String primaryKey; //this isn't absolutely necessary, but may simplify things.
	private Map<String,Boolean> columns; //this isn't absolutely necessary, but may simplify things.

	/**
	 * Table contructor
	 * @param connection Database connection to use.
	 * @param tableName Name of table in database to represent.
	 */
	Table(Connection connection, String tableName) {
		//TODO: Implement this
	}

	/**
	 * get the columns of of this table.
	 * @return a dictionaary whose keys are the names of the columns and value.
	 * Value is a boolean indicating if they representing type (false = int,true = String).
	 */
	Map<String,Boolean> getColumns() {
		return this.columns; //TODO: Implement this
	}

	/**
	 * Add an entry to the database (assumes entry with provided primary key doesn't already exist).
	 * @param values Values to enter into the database. Key is column name, value is value to enter.
	 */
	void addEntry(Map<String,String> values) throws SQLException {
		try {
			//PreparedStatement smnt = connection.prepareStatement("INSERT INTO Nodes (nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName) VALUES (?,?,?,?,?,?,?,?)");
			PreparedStatement smnt = connection.prepareStatement("INSERT INTO Nodes (nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName) VALUES (values)");
			ResultSet rs = smnt.executeQuery();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Edit an existing entry in the database (assumines entry with provided primary key exists).
	 * @param key Primary key representing entry to edit.
	 * @param values Values to edit for the entry. Key is attribute to change, value is new value.
	 * @return Rows in database updated.
	 */
	int editEntry(String key, Map<String,String> values) throws SQLException{
		if(tableName.equals("NODES")){
			try {
				PreparedStatement smnt = connection.prepareStatement("UPDATE Nodes SET (nodeID, xcoord, ycoord, floor, building, nodeType, longName, shortName) WHERE NodeID = key ");
				smnt.setString(1,values.get("NODEID"));
				smnt.setInt(2, values.get("XCOORD");
				smnt.setInt(3, values.get("YCOORD");
				smnt.setString(4,values.get("FLOOR"));
				smnt.setString(5, values.get("BUILDING"));
				smnt.setString(6, values.get("NODETYPE"));
				smnt.setString(7, values.get("LONGNAME"));
				smnt.setString(8, values.get("SHORTNAME"));

				int updated = smnt.executeUpdate();
				if(updated == 0) System.out.println("Error: Invalid node ID. Exiting...");

				smnt.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		else{
			try{
				PreparedStatement snmt = connection.prepareStatement("UPDATE EDGES SET (EDGEID, STARTNDOE, ENDNODE) WHERE EDGEID == key ");
				snmt.setString(1,values.get("EDGEID"));
				snmt.setString(2, values.get("STARTNDOE"));
				snmt.setString(3, values.get("ENDNODE"));

				int updated = snmt.executeUpdate();
				if(updated == 0) System.out.println("Error: Invalid node ID. Exiting...");

				snmt.close();

			}
			catch(SQLException e){
				e.printStackTrace();
			}

		}

		return -1;
	}

	/**
	 * Deletes an in the database (assumes entry with provided primary key exists).
	 * @param entryID Primary key representing entry to delete.
	 * @return Rows in database updated.
	 */
	int deleteEntry(String entryID) throws SQLException{
		try {
			PreparedStatement smnt = connection.prepareStatement("DELETE FROM Node WHERE NodeID == entryID ");
			ResultSet rs = smnt.executeQuery();



		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the full SQL table as a ResultSet.
	 * @return List of maps representing the full table.
	 */
	List<Map<String,String>> getEntries() {
		return null; //TODO: Implement this
	}

	/**
	 * Query the SQL table for an entry with the provided primary key.
	 * @param entryID Primary key representing entry to look for.
	 * @return Map representing the entry to query for or null if entry not present.
	 */
	Map<String,String> getEntry(String entryID) {
		return null; //TODO: Implement this
	}

	/**
	 * Query the SQL table for entries with the provided value in the provided column.
	 * @param columnName Name of column to filter by.
	 * @param value Value to query for.
	 * @return List of maps containing the results of the query.
	 */
	List<Map<String,String>> getEntriesByValue(String columnName, String value) {
		return null; //TODO: Implement this
	}
}
