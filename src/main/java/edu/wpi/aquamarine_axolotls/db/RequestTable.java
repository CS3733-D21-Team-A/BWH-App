package edu.wpi.aquamarine_axolotls.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class RequestTable extends Table {
	RequestTable(Connection connection, String tableName) throws SQLException {
		super(connection, tableName);
	}

	public List<Map<String,String>> getRequests() throws SQLException {
		try (PreparedStatement smnt = connection.prepareStatement("SELECT * FROM " + tableName + " JOIN " + DatabaseInfo.TABLES.SERVICE_REQUESTS.name() + " ON " + tableName + ".REQUESTID = " + DatabaseInfo.TABLES.SERVICE_REQUESTS.name() + ".REQUESTID")) {
			try (ResultSet rs = smnt.executeQuery()) {
				return resultSetToList(rs);
			}
		}
	}
}
