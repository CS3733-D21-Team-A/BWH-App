package edu.wpi.cs3733.d21.teamA.db;

import edu.wpi.cs3733.d21.teamA.db.enums.TABLES;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Special table class used to handle Service Requests since they need to be joined with the SERVICE_REQUESTS table.
 */
class RequestTable extends Table {
	RequestTable(Connection connection, String tableName) throws SQLException {
		super(connection, tableName);
	}


	/**
	 * Converts a ResultSet to a List of Maps.
	 * @param rs ResultSet to convert.
	 * @return List of Maps representing ResultSet.
	 * @throws SQLException Something went wrong.
	 */
	@Override
	protected List<Map<String,String>> resultSetToList(ResultSet rs) throws SQLException {
		List<Map<String,String>> entries = new ArrayList<>();
		while (rs.next()) {
			Map<String,String> entry = new HashMap<>(); // new row to add
			for(String column : getColumns()) { // for every column in the table
				entry.put(column, rs.getString(column)); // put the value at that column into our new row vector
			}
			entries.add(entry); // add this row to the list
		} // increment the row
		return entries;
	}


	/**
	 * Gets all service requests in this table (joins with the SERVICE_REQUESTS table in order to get full information)
	 * @return List of Maps representing the entries.
	 * @throws SQLException Something went wrong.
	 */
	List<Map<String,String>> getRequests() throws SQLException {
		try (PreparedStatement smnt = connection.prepareStatement("SELECT * FROM " + tableName + " JOIN " + TABLES.SERVICE_REQUESTS.name() + " SR ON " + tableName + ".REQUESTID = SR.REQUESTID")) {
			try (ResultSet rs = smnt.executeQuery()) {
				return resultSetToList(rs);
			}
		}
	}

	@Override
	public List<String> getColumns() throws SQLException {
		List<String> cols = new ArrayList<>();
		DatabaseMetaData dbmd = connection.getMetaData();
		try (ResultSet rs = dbmd.getColumns(null, null, TABLES.SERVICE_REQUESTS.name(), null)){
			while (rs.next()) {
				cols.add(rs.getString("COLUMN_NAME"));
			}
		}
		cols.addAll(columns.keySet()); //do this second so shared values are first
		return cols;
	}
}
