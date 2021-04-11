package edu.wpi.aquamarine_axolotls.db;

import java.sql.*;
import java.util.*;

class Table {
	private Connection connection;
	private String tableName;
	private String primaryKey; //this isn't absolutely necessary, but may simplify things.
	private Map<String,Boolean> columns; //this isn't absolutely necessary, but may simplify things. //TODO: Make this use ints referencing java.sql.Types instead of a boolean

	/**
	 * Table contructor
	 * @param connection Database connection to use.
	 * @param tableName Name of table in database to represent.
	 */
	Table(Connection connection, String tableName) throws SQLException {
		this.connection = connection;
		this.tableName = tableName;

		DatabaseMetaData dbmd = connection.getMetaData();
		this.primaryKey = dbmd.getPrimaryKeys(null, null, tableName).getString("PK_NAME");

		this.columns = new HashMap<String,Boolean>();
		ResultSet rst = dbmd.getColumns(null, null, tableName, null);
		while (rst.next()) {
			columns.put(rst.getString("COLUMN_NAME"), rst.getInt("DATA_TYPE") == Types.VARCHAR); //TODO: Make this use ints referencing java.sql.Types instead of a boolean
		}
	}

	/**
	 * get the columns of of this table.
	 * @return a dictionaary whose keys are the names of the columns and value.
	 * Value is a boolean indicating if they representing type (false = int,true = String).
	 */
	Map<String,Boolean> getColumns() {
		return this.columns;
	}

	/**
	 * Add an entry to the database (assumes entry with provided primary key doesn't already exist).
	 * @param values Values to enter into the database. Key is column name, value is value to enter.
	 */
	void addEntry(Map<String,String> values) throws SQLException {
			//PreparedStatement smnt = connection.prepareStatement("INSERT INTO Nodes (nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName) VALUES (?,?,?,?,?,?,?,?)");
		// gets all columns
		Set<String> column_names = columns.keySet();

		// first part of statement ie all column names
		StringBuilder stringBuilder = new StringBuilder("INSERT INTO " + tableName + "(");
		// gets all values we are adding to each column name for the new node
		StringBuilder stringBuilder2 = new StringBuilder("VALUES (");
		// builds values
		for (String column : column_names) {
			stringBuilder.append(column);
			stringBuilder.append(", ");
			stringBuilder2.append(values.get(column) + ", ");
		}
		// remove extra , and space
		stringBuilder.delete(stringBuilder.length()-2, stringBuilder.length()-1);
		stringBuilder2.delete(stringBuilder2.length()-2, stringBuilder2.length()-1);
		// adds needed parens
		stringBuilder.append(") ");
		stringBuilder2.append(")");
		// appends two halves together
		stringBuilder.append(stringBuilder2);

		// executes statement
		PreparedStatement smnt = connection.prepareStatement(stringBuilder.toString());

		smnt.executeUpdate();
	}

	/**
	 * Edit an existing entry in the database (assumines entry with provided primary key exists).
	 * @param key Primary key representing entry to edit.
	 * @param values Values to edit for the entry. Key is attribute to change, value is new value.
	 * @return Rows in database updated.
	 */
	int editEntry(String key, Map<String,String> values) throws SQLException {

		Set<String> editColumns = values.keySet();

		StringBuilder stringBuilder = new StringBuilder("UPDATE " + tableName + " SET ");
		for (String column : editColumns) {
			stringBuilder.append(column);
			stringBuilder.append("=?, ");
		}
		stringBuilder.delete(stringBuilder.length()-2, stringBuilder.length()-1);
		stringBuilder.append("WHERE ");
		stringBuilder.append(primaryKey);
		stringBuilder.append("=?");

		PreparedStatement smnt = connection.prepareStatement(stringBuilder.toString());

		int i = 1;
		for (String column : editColumns) {
			if (columns.get(column)) {
				smnt.setString(i, values.get(column));
			} else {
				smnt.setInt(i, Integer.parseInt(values.get(column)));
			}
			i++;
		}

		return smnt.executeUpdate();
	}

	/**
	 * Deletes an in the database (assumes entry with provided primary key exists).
	 * @param entryID Primary key representing entry to delete.
	 * @return Rows in database updated.
	 */
	int deleteEntry(String entryID) throws SQLException{
		// orginal PreparedStatement smnt = connection.prepareStatement("DELETE FROM Node WHERE NodeID = entryID ");
		PreparedStatement smnt = connection.prepareStatement("DELETE FROM " + tableName + " WHERE " + primaryKey + " = " + entryID);
		return smnt.executeUpdate();
	}

	List<Map<String,String>> convertRS(ResultSet rs) {
		try{
			List<Map<String,String>> entries = new ArrayList<Map<String,String>>();
			while(rs != null){
				// new row to add
				Map<String, String> entry = new HashMap<String, String>();
				// for every column in the table
				for(String column : columns.keySet()){
					// put the value at that column into our new row vector
					entry.put(column, rs.getString(column));
				}
				// add this row to the list
				entries.add(entry);
				// increment the row
				rs.next();
			}
			return entries;

		}catch(SQLException e){
			e.printStackTrace();
		}

		return null;

	}
	/**
	 * Get the full SQL table as a ResultSet.
	 * @return List of maps representing the full table.
	 */
	List<Map<String,String>> getEntries() {

		// how to get around try catch for SQL statement?

		try {
			// gets everything from table
			PreparedStatement smnt = connection.prepareStatement("SELECT * FROM " + tableName);
			// gets sql result
			ResultSet rs = smnt.executeQuery();
			// convert rs to List of maps
			return convertRS(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * Query the SQL table for an entry with the provided primary key.
	 * @param entryID Primary key representing entry to look for.
	 * @return Map representing the entry to query for or null if entry not present.
	 */
	Map<String,String> getEntry(String entryID) {
		// how to get around try catch for SQL statement?
		PreparedStatement smnt;
		ResultSet rs;
		// will return null if SQL exception?
		Map<String, String> entry = new HashMap<String, String>();
		try {
			// gets row from table
			smnt = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE " + primaryKey + " = " + entryID);
			// gets sql result
			rs = smnt.executeQuery();
			// starting from first row in table iterate thru until the end
			for(String column : columns.keySet()){
				// put the value at that column into our new row vector
				entry.put(column, rs.getString(column));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return entry;
	}

	/**
	 * Query the SQL table for entries with the provided value in the provided column.
	 * @param columnName Name of column to filter by.
	 * @param value Value to query for.
	 * @return List of maps containing the results of the query.
	 */
	List<Map<String,String>> getEntriesByValue(String columnName, String value) {
		// how to get around try catch for SQL statement?
		PreparedStatement smnt;
		ResultSet rs;

		try {
			// gets row/rows that have column name with value
			smnt = connection.prepareStatement("SELECT " + columnName + " FROM " + tableName + " WHERE " + columnName + " = " + value);
			// gets sql result
			rs = smnt.executeQuery();
			// convert RS to List of maps
			return convertRS(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
}
