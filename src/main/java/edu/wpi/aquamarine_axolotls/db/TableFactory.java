package edu.wpi.aquamarine_axolotls.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class TableFactory {
	private Connection connection;

	/**
	 * TableFactory constructor.
	 */
	TableFactory() { //Note: may want to pass the connection into this?
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			this.connection = DriverManager.getConnection("jdbc:derby:BWH", "admin", "admin"); //TODO: login credentials
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create and return an object representing the table with the corresponding name.
	 * @param tableName Name of table to link to.
	 * @return Table object representing the corresponding table in the database.
	 */
	Table getTable(String tableName) {
		try {
			return new Table(connection, tableName);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
