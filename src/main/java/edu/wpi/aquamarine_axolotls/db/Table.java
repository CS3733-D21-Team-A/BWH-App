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
	/*
					List<String[]> table = new ArrayList<>(); //create table to display
				int colCount = rsmd.getColumnCount();

				table.add(new String[colCount]); //add column label row
				for (int i = 0; i < colCount; i++) {
					table.get(0)[i] = rsmd.getColumnName(i+1); //add column labels to first row
				}

				for (int row = 1; rset.next(); row++) {
					table.add(new String[colCount]);
					for (int i = 0; i < colCount; i++) {
						table.get(row)[i] = rset.getString(rsmd.getColumnName(i+1)); //add row values
					}
				}

				for (final Object[] row : table) {
					System.out.format("%10s%10s%10s%10s%15s%10s%45s%30s%n", row); //printout
				}

				rset.close();
				smnt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	 */

	/**
	 * Get the full SQL table as a ResultSet.
	 * @return List of maps representing the full table.
	 */
	List<Map<String,String>> getEntries() {
		// TODO: implement
		return null;

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
