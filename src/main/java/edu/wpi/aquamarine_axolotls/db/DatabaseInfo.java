package edu.wpi.aquamarine_axolotls.db;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

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

		static final Map<TABLES,String> TABLE_SQL;
		static {
			TABLE_SQL = new EnumMap<>(TABLES.class);
			TABLE_SQL.put(NODES, NODE_TABLE_SQL);
			TABLE_SQL.put(EDGES, EDGE_TABLE_SQL);
			TABLE_SQL.put(SERVICE_REQUESTS, SERVICE_REQUESTS_TABLE_SQL);
		}

		/**
		 * Enum for service request tables
		 */
		public enum SERVICEREQUESTS {
			EXTERNAL_TRANSPORT("External Transport"),
			FLORAL_DELIVERY("Floral Delivery"),
			FOOD_DELIVERY("Food Delivery"),
			GIFT_DELIVERY("Gift Delivery"),
			INTERNAL_TRANSPORT("Internal Transport"),
			LANGUAGE_INTERPRETER("Language Interpreter"),
			LAUNDRY("Laundry"),
			MEDICINE_DELIVERY("Medicine Delivery"),
			RELIGIOUS_REQUEST("Religious Request"),
			SANITATION("Sanitation"),
			SECURITY("Security");

			static final Map<SERVICEREQUESTS,String> SERVICEREQUESTS_SQL;
			static {
				SERVICEREQUESTS_SQL = new EnumMap<>(SERVICEREQUESTS.class);
				SERVICEREQUESTS_SQL.put(FLORAL_DELIVERY, FLORAL_DELIVERY_TABLE_SQL);
				SERVICEREQUESTS_SQL.put(FOOD_DELIVERY, FOOD_DELIVERY_TABLE_SQL);
			}

			public String text;

			SERVICEREQUESTS(String text) {
				this.text = text;
			}

			public enum STATUSES {
				UNASSIGNED("Unassigned"),
				ASSIGNED("Assigned"),
				IN_PROGRESS("In Progress"),
				DONE("Done"),
				CANCELED("Canceled");

				public String text;

				STATUSES(String text) {
					this.text = text;
				}
			}
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
	 * Convert resource path string to File.
	 * @param resourcePath path to resource in app structure.
	 * @return File for corresponding resource.
	 */
	static InputStream resourceAsStream(String resourcePath) {
		return DatabaseInfo.class.getClassLoader().getResourceAsStream(resourcePath);
	}

	/**
	 * SQL for building the NODES table.
	 */
	private static final String NODE_TABLE_SQL =
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
	private static final String EDGE_TABLE_SQL =
	   "CREATE TABLE " + TABLES.EDGES.name() + " (" +
		   "EDGEID VARCHAR(51) PRIMARY KEY," +
			"STARTNODE VARCHAR(25) CONSTRAINT FK_STARTNODE REFERENCES " + TABLES.NODES.name() + " ON DELETE CASCADE ON UPDATE RESTRICT," + //TODO: wanna keep on update restrict?
			"ENDNODE VARCHAR(25) CONSTRAINT FK_ENDNODE REFERENCES " + TABLES.NODES.name() + " ON DELETE CASCADE ON UPDATE RESTRICT" + //TODO: wanna keep on update restrict?
		")"; //TODO: FIGURE OUT TAGS

	/**
	 * SQL for building the SERVICE_REQUESTS table.
	 */
	private static final String SERVICE_REQUESTS_TABLE_SQL =
		"CREATE TABLE " + TABLES.SERVICE_REQUESTS.name() + " (" +
			"REQUESTID VARCHAR(25) PRIMARY KEY," +
			"STATUS VARCHAR(11) DEFAULT 'Unassigned'," +
			"EMPLOYEEID VARCHAR(30)," + //TODO: THIS IS A FOREIGN KEY TO THE USER TABLE (NOT YET IMPLEMENTED)
			"LOCATIONID VARCHAR(25) CONSTRAINT FK_LOCATIONID REFERENCES " + TABLES.NODES.name() + "," + //TODO: what to do on delete or update?
			"FIRSTNAME VARCHAR(30)," +
			"LASTNAME VARCHAR(50)," +
			"REQUESTTYPE VARCHAR(20) NOT NULL" + //TODO: MAKE THIS USE ENUM
				  //TODO: Constraint to replace ENUM('Unassigned','Assigned','In Progress','Done','Canceled') for STATUS
				  //TODO: Constraint to replace ENUM('Floral Delivery', 'External Transport', 'Gift Delivery', 'Food Delivery', 'Language Interpreter', 'Internal Transport', 'Medicine Delivery', 'Laundry', 'Sanitation', 'Religious Requests', 'Security') for REQUESTTYPE
		")";

	/**
	 * SQL for building the FOOD_DELIVERY table.
	 */
	private static final String FOOD_DELIVERY_TABLE_SQL =
		"CREATE TABLE " + TABLES.SERVICEREQUESTS.FOOD_DELIVERY.name() + " (" +
			"REQUESTID VARCHAR(25) PRIMARY KEY CONSTRAINT FK_REQUESTID REFERENCES " + TABLES.SERVICE_REQUESTS.name() + "," + //TODO: what to do on delete or update? +
			"DELIVERYTIME TIME(0)," +
			"DIETARYRESTRICTIONS VARCHAR(150)," +
			"NOTE VARCHAR(300)," +
		")";

	/**
	 * SQL for building the FLORAL_DELIVERY table.
	 */
	private static final String FLORAL_DELIVERY_TABLE_SQL =
		"CREATE TABLE " + TABLES.SERVICEREQUESTS.FLORAL_DELIVERY.name() + " (" +
			"REQUESTID VARCHAR(25) PRIMARY KEY CONSTRAINT FK_REQUESTID REFERENCES " + TABLES.SERVICE_REQUESTS.name() + "," + //TODO: what to do on delete or update? +
			"DELIVERYTIME TIME(0)," +
			"NOTE VARCHAR(300)," +
		")";
}
