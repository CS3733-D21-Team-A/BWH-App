package edu.wpi.cs3733.d21.teamA.db;

import java.sql.*;
import java.util.*;

/**
 * Class acting as an entity representing/working on a SQL table.
 */
class Table {
	Connection connection;
	final String tableName;
	final private String primaryKey; //this isn't absolutely necessary, but may simplify things.
	final Map<String,Boolean> columns; //this isn't absolutely necessary, but may simplify things. //TODO: Make this use ints referencing java.sql.Types instead of a boolean

	/**
	 * Table contructor
	 * @param connection Database connection to use.
	 * @param tableName Name of table in database to represent.
	 * @throws SQLException Something went wrong.
	 */
	Table(Connection connection, String tableName) throws SQLException {
		this.connection = connection;
		this.tableName = tableName;

		DatabaseMetaData dbmd = connection.getMetaData();
		try (ResultSet rs = dbmd.getPrimaryKeys(null, null, tableName)) {
			rs.next();
			this.primaryKey = rs.getString("COLUMN_NAME");
		}

		this.columns = new HashMap<>();
		try (ResultSet rs = dbmd.getColumns(null, null, tableName, null)){
			while (rs.next()) {
				columns.put(rs.getString("COLUMN_NAME"), rs.getInt("DATA_TYPE") == Types.VARCHAR); //TODO: Make this use ints referencing java.sql.Types instead of a boolean
			}
		}
	}

	/**
	 * Add an entry to the database (assumes entry with provided primary key doesn't already exist).
	 * @param values Values to enter into the database. Key is column name, value is value to enter.
	 * @throws SQLException Something went wrong.
	 */
	void addEntry(Map<String,String> values) throws SQLException {
		Set<String> addColumns = values.keySet(); // gets all columns

		StringBuilder stringBuilder = new StringBuilder("INSERT INTO " + tableName + "("); // first part of statement ie all column names
		StringBuilder stringBuilder2 = new StringBuilder("VALUES ("); // gets all values we are adding to each column name for the new node

		for (String column : addColumns) { // builds values
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
		try (PreparedStatement smnt = connection.prepareStatement(stringBuilder.toString())) { //TODO: Refactor out duplicate code?
			int i = 1;
			for (String column : addColumns) {
				if (columns.get(column.toUpperCase())) {
					smnt.setString(i, values.get(column));
				} else {
					smnt.setInt(i, Integer.parseInt(values.get(column)));
				}
				i++;
			}

			smnt.executeUpdate();
		}
	}

	/**
	 * Edit an existing entry in the database (assumines entry with provided primary key exists).
	 * @param key Primary key representing entry to edit.
	 * @param values Values to edit for the entry. Key is attribute to change, value is new value.
	 * @throws SQLException Something went wrong.
	 */
	void editEntry(String key, Map<String,String> values) throws SQLException {

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

		try (PreparedStatement smnt = connection.prepareStatement(stringBuilder.toString())) {
			int i = 1;
			for (String column : editColumns) {
				if (columns.get(column.toUpperCase())) {
					smnt.setString(i, values.get(column));
				} else {
					smnt.setInt(i, Integer.parseInt(values.get(column)));
				}
				i++;
			}
			smnt.setString(i, key);

			smnt.executeUpdate();
		}
	}

	/**
	 * Deletes an in the database (assumes entry with provided primary key exists).
	 * @param entryID Primary key representing entry to delete.
	 * @throws SQLException Something went wrong.
	 */
	void deleteEntry(String entryID) throws SQLException {
		try (PreparedStatement smnt = connection.prepareStatement("DELETE FROM " + tableName + " WHERE " + primaryKey + " = ?")) {
			smnt.setString(1, entryID);
			smnt.executeUpdate();
		}
	}

	/**
	 * Converts a ResultSet to a List of Maps.
	 * @param rs ResultSet to convert.
	 * @return List of Maps representing ResultSet.
	 * @throws SQLException Something went wrong.
	 */
	protected List<Map<String,String>> resultSetToList(ResultSet rs) throws SQLException {
		List<Map<String,String>> entries = new ArrayList<>();
		while (rs.next()) {
			Map<String,String> entry = new HashMap<>(); // new row to add
			for(String column : columns.keySet()) { // for every column in the table
				entry.put(column, rs.getString(column)); // put the value at that column into our new row vector
			}
			entries.add(entry); // add this row to the list
		} // increment the row
		return entries;
	}

	/**
	 * Get the full SQL table as a ResultSet.
	 * @return List of maps representing the full table.
	 * @throws SQLException Something went wrong.
	 */
	List<Map<String,String>> getEntries() throws SQLException {
		try (PreparedStatement smnt = connection.prepareStatement("SELECT * FROM " + tableName)) {
			try (ResultSet rs = smnt.executeQuery()) {
				return resultSetToList(rs); // gets sql result and convert rs to List of maps
			}
		}
	}

	/**
	 * Query the SQL table for an entry with the provided primary key.
	 * @param entryID Primary key representing entry to look for.
	 * @return Map representing the entry to query for or null if entry not present.
	 * @throws SQLException Something went wrong.
	 */
	Map<String,String> getEntry(String entryID) throws SQLException {
		try (PreparedStatement smnt = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE " + primaryKey + " = ?")) { // gets row from table
			smnt.setString(1, entryID);

			try (ResultSet rs = smnt.executeQuery()) { // gets sql result
				if (!rs.next()) {
					return null;
				}
				Map<String, String> entry = new HashMap<>();
				for (String column : columns.keySet()) { // starting from first row in table iterate thru until the end
					entry.put(column, rs.getString(column)); // put the value at that column into our new row vector
				}
				return entry;
			}
		}
	}

	/**
	 * Query the SQL table for entries with the provided value in the provided column.
	 * @param columnName Name of column to filter by.
	 * @param value Value to query for.
	 * @return List of maps containing the results of the query.
	 * @throws SQLException Something went wrong.
	 */
	List<Map<String,String>> getEntriesByValue(String columnName, String value) throws SQLException { //TODO: MAKE THIS HANDLE NULLS CORRECTLY
		try (PreparedStatement smnt = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE " + columnName + " = ?")) { // gets row/rows that have column name with value
			smnt.setString(1, value);
			try (ResultSet rs = smnt.executeQuery()) {
				return resultSetToList(rs); // gets sql result and convert RS to List of maps
			}
		}
	}

	/**
	 * Query the SQL table for entries with the provided value in the provided column.
	 * @param filters Map of column names and values to filter by.
	 * @return List of maps containing the results of the query.
	 * @throws SQLException Filter with no values provided or something else went wrong.
	 */
	List<Map<String,String>> getEntriesByValues(Map<String,List<String>> filters) throws SQLException {
		StringBuilder sb = new StringBuilder("SELECT * FROM " + tableName + " WHERE ");
		String[] filterColumns = Arrays.copyOf(filters.keySet().toArray(), filters.keySet().size(), String[].class);

		for (String columnName : filterColumns) { //TODO: Refactor this somehow / use better iteration
			if (filters.get(columnName).size() == 0) {
				throw new SQLException("Invalid filter: Cannot filter by no values.");
			}

			sb.append("(");

			filters.get(columnName).forEach((i) -> {
				sb.append(columnName);
				if (i == null) {
					sb.append(" IS NULL");
				} else {
					sb.append(" = ?");
				}
				sb.append(" OR ");
			});

			sb.delete(sb.length() - " OR ".length(), sb.length()); //get rid of hanging OR

			sb.append(") AND ");
			// (ID = ? OR ID = ? OR ID = ?) AND (STATUS = ? OR STATUS = ? OR STATUS = ?)
		}
		sb.delete(sb.length() - " AND ".length(), sb.length()); //get rid of hanging AND

		try (PreparedStatement smnt = connection.prepareStatement(sb.toString())) {
			int i = 1;
			for (String columnName : filterColumns) {
				for (String value : filters.get(columnName)) {
					if (value != null) {
						smnt.setString(i, value);
					}
					i++;
				}
			}
			try (ResultSet rs = smnt.executeQuery()) {
				return resultSetToList(rs);
			}
		}
	}

	/**
	 * Empties the table by deleting all entries.
	 * @throws SQLException Something went wrong.
	 */
	void emptyTable() throws SQLException {
		try (PreparedStatement smnt = connection.prepareStatement("DELETE FROM " + tableName)) {
			smnt.executeUpdate();
		}
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}