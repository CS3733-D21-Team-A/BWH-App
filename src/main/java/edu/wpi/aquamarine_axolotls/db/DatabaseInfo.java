package edu.wpi.aquamarine_axolotls.db;

import java.io.InputStream;

/**
 * Class containing static info for reference when working with the database.
 */
public class DatabaseInfo {
	/**
	 * Enum for database tables.
	 */
	public enum TABLES {
		NODES,
		EDGES,
		SERVICE_REQUESTS;

		/**
		 * Enum for service request tables
		 */
		public enum SERVICEREQUESTS {
			EXTERNAL_TRANSPORT,
			FLORAL_DELIVERY,
			FOOD_DELIVERY,
			GIFT_DELIVERY,
			INTERNAL_TRANSPORT,
			LANGUAGE_INTERPRETER,
			LAUNDRY,
			MEDICINE_DELIVERY,
			RELIGIOUS_REQUEST,
			SANITATION,
			SECURITY
		}
	}

	/**
	 * Path to default node CSV resource.
	 */
	static final String nodeResourcePath = "edu/wpi/aquamarine_axolotls/csv/MapAnodes.csv";

	/**
	 * Path to default edge CSV resource.
	 */
	static final String edgeResourcePath = "edu/wpi/aquamarine_axolotls/csv/MapAedges.csv";

	/**
	 * SQL for building the NODES table.
	 */
	static final String NODE_TABLE_SQL =
		"CREATE TABLE " + TABLES.NODES.name() + " (" +
			"NODEID VARCHAR(25) PRIMARY KEY," +
			"XCOORD NUMERIC(5)," +
			"YCOORD NUMERIC(5)," +
			"FLOOR VARCHAR(3)," +
			"BUILDING VARCHAR(30)," +
			"NODETYPE VARCHAR(5)," +
			"LONGNAME VARCHAR(50)," +
			"SHORTNAME VARCHAR(30)" +
		")"; //TODO: FIGURE OUT TAGS

	/**
	 * SQL for building the EDGES table.
	 */
	static final String EDGE_TABLE_SQL =
	   "CREATE TABLE " + TABLES.EDGES.name() + " (" +
		   "EDGEID VARCHAR(51) PRIMARY KEY," +
			"STARTNODE VARCHAR(25)," +
			"ENDNODE VARCHAR(25)," +
			"CONSTRAINT FK_STARTNODE FOREIGN KEY (STARTNODE) REFERENCES " + TABLES.NODES.name() + "(NODEID) ON DELETE CASCADE ON UPDATE RESTRICT," + //TODO: wanna keep on update restrict?
			"CONSTRAINT FK_ENDNODE FOREIGN KEY (ENDNODE) REFERENCES " + TABLES.NODES.name() + "(NODEID) ON DELETE CASCADE ON UPDATE RESTRICT" + //TODO: wanna keep on update restrict?
		")"; //TODO: FIGURE OUT TAGS

	/**
	 * SQL for building the SERVICE_REQUESTS table.
	 */
	static final String SERVICE_REQUESTS_TABLE_SQL =
		"CREATE TABLE " + TABLES.SERVICE_REQUESTS.name() + " (" +
			"REQUESTID VARCHAR(25) PRIMARY KEY," +
			"STATUS ENUM('Unassigned','Assigned','In Progress','Done','Canceled') DEFAULT 'Unassigned'," +
			"EMPLOYEEID VARCHAR(30)," + //TODO: THIS IS A FOREIGN KEY TO THE USER TABLE (NOT YET IMPLEMENTED)
			"LOCATIONID VARCHAR(25)," +
			"FIRSTNAME VARCHAR(30)," +
			"LASTNAME VARCHAR(50)," +
			"REQUESTTYPE ENUM('Floral Delivery', 'External Transport', 'Gift Delivery', 'Food Delivery', 'Language Interpreter', 'Internal Transport', 'Medicine Delivery', 'Laundry', 'Sanitation', 'Religious Requests', 'Security') NOT NULL," +
			"CONSTRAINT FK_LOCATIONID FOREIGN KEY (LOCATIONID) REFERENCES " + TABLES.NODES.name() + "(NODEID)," + //TODO: what to do on delete or update?
		")";

	/**
	 * SQL for building the FOOD_DELIVERY table.
	 */
	static final String FOOD_DELIVERY_TABLE_SQL =
		"CREATE TABLE " + TABLES.SERVICEREQUESTS.FOOD_DELIVERY.name() + " (" +
			"REQUESTID VARCHAR(25) PRIMARY KEY," +
			"DELIVERYTIME TIME(0)," +
			"DIETARYRESTRICTIONS VARCHAR(150)," +
			"NOTE VARCHAR(300)," +
			"CONSTRAINT FK_REQUESTID FOREIGN KEY (REQUESTID) REFERENCES " + TABLES.SERVICE_REQUESTS.name() + "(REQUESTID)," + //TODO: what to do on delete or update?
		")";

	/**
	 * SQL for building the FLORAL_DELIVERY table.
	 */
	static final String FLORAL_DELIVERY_TABLE_SQL =
		"CREATE TABLE " + TABLES.SERVICEREQUESTS.FLORAL_DELIVERY.name() + " (" +
			"REQUESTID VARCHAR(25) PRIMARY KEY," +
			"DELIVERYTIME TIME(0)," +
			"NOTE VARCHAR(300)," +
			"CONSTRAINT FK_REQUESTID FOREIGN KEY (REQUESTID) REFERENCES " + TABLES.SERVICE_REQUESTS.name() + "(REQUESTID)," + //TODO: what to do on delete or update?
		")";

	/**
	 * Convert resource path string to File.
	 * @param resourcePath path to resource in app structure.
	 * @return File for corresponding resource.
	 */
	static InputStream resourceAsStream(String resourcePath) {
		return DatabaseInfo.class.getClassLoader().getResourceAsStream(resourcePath);
	}
}
