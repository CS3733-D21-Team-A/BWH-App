package edu.wpi.aquamarine_axolotls.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Factory class for Table object creation
 */
class TableFactory {
	private Connection connection;

	/**
	 * TableFactory constructor.
	 */
	TableFactory(Connection connection) { //Note: may want to pass the connection into this?
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			this.connection = connection;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create and return an object representing the table with the corresponding name.
	 * @param table identifier for table to link to.
	 * @return Table object representing the corresponding table in the database.
	 */
	Table getTable(DatabaseInfo.TABLES table) {
		try {
			return new Table(connection, table.name());
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Create and return an object representing the service request table with the corresponding name.
	 * @param requestType identifier for service request table to link to.
	 * @return RequestTable object representing the corresponding table in the database.
	 */
	RequestTable getRequestTable(DatabaseInfo.TABLES.SERVICEREQUESTS requestType) {
		try {
			return new RequestTable(connection, requestType.name());
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}