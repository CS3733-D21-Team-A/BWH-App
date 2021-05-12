package edu.wpi.cs3733.d21.teamA.db;

import edu.wpi.cs3733.d21.teamA.db.enums.TABLES;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	 * Gets all service requests in this table (joins with the SERVICE_REQUESTS table in order to get full information)
	 * @return List of Maps representing the entries.
	 * @throws SQLException Something went wrong.
	 */
	List<Map<String,String>> getRequests() throws SQLException {
		try (PreparedStatement smnt = connection.prepareStatement("SELECT * FROM " + tableName + " JOIN " + TABLES.SERVICE_REQUESTS.name() + " ON " + tableName + ".REQUESTID = " + TABLES.SERVICE_REQUESTS.name() + ".REQUESTID")) {
			try (ResultSet rs = smnt.executeQuery()) {
				return resultSetToList(rs);
			}
		}
	}
}
