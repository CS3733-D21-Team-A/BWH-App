package edu.wpi.aquamarine_axolotls.db;

import java.sql.*;
import java.util.*;

class Table {
	final private Connection connection;
	final private String tableName;
	final private String primaryKey; //this isn't absolutely necessary, but may simplify things.
	final private Map<String,Boolean> columns; //this isn't absolutely necessary, but may simplify things. //TODO: Make this use ints referencing java.sql.Types instead of a boolean

	/**
	 * Table contructor
	 * @param connection Database connection to use.
	 * @param tableName Name of table in database to represent.
	 */
	Table(Connection connection, String tableName) throws SQLException {
		this.connection = connection;
		this.tableName = tableName;

		DatabaseMetaData dbmd = connection.getMetaData();
		ResultSet rs = dbmd.getPrimaryKeys(null, null, tableName);
		rs.next();
		this.primaryKey = rs.getString("COLUMN_NAME");

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
		Set<String> column_names = values.keySet(); // gets all columns

		StringBuilder stringBuilder = new StringBuilder("INSERT INTO " + tableName + "("); // first part of statement ie all column names
		StringBuilder stringBuilder2 = new StringBuilder("VALUES ("); // gets all values we are adding to each column name for the new node

		for (String column : column_names) { // builds values
			stringBuilder.append(column);
			stringBuilder.append(",");
			stringBuilder2.append("?,");
		}

		// remove extra comma
		stringBuilder.delete(stringBuilder.length()-1, stringBuilder.length());
		stringBuilder2.delete(stringBuilder2.length()-1, stringBuilder2.length());

		// adds needed parens
		stringBuilder.append(") ");
		stringBuilder2.append(")");

		// appends two halves together
		stringBuilder.append(stringBuilder2);

		// executes statement
		PreparedStatement smnt = connection.prepareStatement(stringBuilder.toString()); //TODO: Refactor out duplicate code?

		int i = 1;
		for (String column : column_names) {
			if (columns.get(column.toUpperCase())) {
				smnt.setString(i, values.get(column));
			} else {
				smnt.setInt(i, Integer.parseInt(values.get(column)));
			}
			i++;
		}

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
			if (columns.get(column.toUpperCase())) {
				smnt.setString(i, values.get(column));
			} else {
				smnt.setInt(i, Integer.parseInt(values.get(column)));
			}
			i++;
		}
		smnt.setString(i,key);

		return smnt.executeUpdate();
	}

	/**
	 * Deletes an in the database (assumes entry with provided primary key exists).
	 * @param entryID Primary key representing entry to delete.
	 * @return Rows in database updated.
	 */
	int deleteEntry(String entryID) throws SQLException{
		PreparedStatement smnt = connection.prepareStatement("DELETE FROM " + tableName + " WHERE " + primaryKey + " = ?");
		smnt.setString(1, tableName);
		smnt.setString(2, primaryKey);
		smnt.setString(3, entryID);

		return smnt.executeUpdate();
	}

	private List<Map<String,String>> resultSetToList(ResultSet rs) throws SQLException {
		List<Map<String,String>> entries = new ArrayList<Map<String,String>>();
		do {
			Map<String,String> entry = new HashMap<String,String>(); // new row to add
			for(String column : columns.keySet()) { // for every column in the table
				entry.put(column, rs.getString(column)); // put the value at that column into our new row vector
			}
			entries.add(entry); // add this row to the list
		} while(rs.next()); // increment the row
		return entries;
	}

	/**
	 * Get the full SQL table as a ResultSet.
	 * @return List of maps representing the full table.
	 */
	List<Map<String,String>> getEntries() throws SQLException {
		PreparedStatement smnt = connection.prepareStatement("SELECT * FROM " + tableName); // gets everything from table
		return resultSetToList(smnt.executeQuery()); // gets sql result and convert rs to List of maps
	}

	/**
	 * Query the SQL table for an entry with the provided primary key.
	 * @param entryID Primary key representing entry to look for.
	 * @return Map representing the entry to query for or null if entry not present.
	 */
	Map<String,String> getEntry(String entryID) throws SQLException {
		Map<String, String> entry = new HashMap<String, String>();

		PreparedStatement smnt = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE " + primaryKey + " = ?"); // gets row from table
		smnt.setString(1, entryID);

		ResultSet rs = smnt.executeQuery(); // gets sql result

		if (!rs.next()) return null;

		for (String column : columns.keySet()) { // starting from first row in table iterate thru until the end
			entry.put(column, rs.getString(column)); // put the value at that column into our new row vector
		}

		return entry;
	}

	/**
	 * Query the SQL table for entries with the provided value in the provided column.
	 * @param columnName Name of column to filter by.
	 * @param value Value to query for.
	 * @return List of maps containing the results of the query.
	 */
	List<Map<String,String>> getEntriesByValue(String columnName, String value) throws SQLException {

		PreparedStatement smnt = connection.prepareStatement("SELECT " + columnName + " FROM " + tableName + " WHERE " + columnName + " = ?"); // gets row/rows that have column name with value
		smnt.setString(1, value);

		return resultSetToList(smnt.executeQuery()); // gets sql result and convert RS to List of maps
	}

	/**
	 * Empties the table by deleting all entries.
	 * @return rows deleted.
	 */
	int emptyTable() throws SQLException {
		PreparedStatement smnt = connection.prepareStatement("DELETE FROM " + tableName);
		return smnt.executeUpdate();
	}
}
