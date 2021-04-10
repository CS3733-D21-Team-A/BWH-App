package edu.wpi.aquamarine_axolotls.db;

import java.sql.Connection;

class TableFactory {
	private Connection connection;

	/**
	 * TableFactory constructor.
	 */
	TableFactory() { //Note: may want to pass the connection into this?
		//TODO: Implement this
	}

	/**
	 * Create and return an object representing the table with the corresponding name.
	 * @param tableName Name of table to link to.
	 * @return Table object representing the corresponding table in the database.
	 */
	Table getTable(String tableName) {
		return new Table(connection, tableName);
	}
}
