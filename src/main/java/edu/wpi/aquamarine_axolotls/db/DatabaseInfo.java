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
		ATTRIBUTES,
		SERVICE_REQUESTS;

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
			")";

		/**
		 * SQL for building the EDGES table.
		 */
		private static final String EDGE_TABLE_SQL =
		   "CREATE TABLE " + TABLES.EDGES.name() + " (" +
			   "EDGEID VARCHAR(51) PRIMARY KEY," +
				"STARTNODE VARCHAR(25) CONSTRAINT FK_STARTNODE REFERENCES " + TABLES.NODES.name() + " ON DELETE CASCADE ON UPDATE RESTRICT," +
				"ENDNODE VARCHAR(25) CONSTRAINT FK_ENDNODE REFERENCES " + TABLES.NODES.name() + " ON DELETE CASCADE ON UPDATE RESTRICT" +
			")";

		/**
		 * SQL for building the SERVICE_REQUESTS table.
		 */
		private static final String SERVICE_REQUESTS_TABLE_SQL =
			"CREATE TABLE " + TABLES.SERVICE_REQUESTS.name() + " (" +
				"REQUESTID VARCHAR(25) PRIMARY KEY," +
				"STATUS VARCHAR(11) DEFAULT 'Unassigned'," +
				"EMPLOYEEID VARCHAR(30)," + //TODO: THIS IS A FOREIGN KEY TO THE USER TABLE (NOT YET IMPLEMENTED)
				"LOCATIONID VARCHAR(25) CONSTRAINT FK_LOCATIONID REFERENCES " + TABLES.NODES.name() + " ON DELETE SET NULL ON UPDATE RESTRICT," +
				"FIRSTNAME VARCHAR(30)," +
				"LASTNAME VARCHAR(50)," +
				"REQUESTTYPE VARCHAR(20) NOT NULL" + //TODO: MAKE THIS USE ENUM
					  //TODO: Constraint to replace ENUM('Unassigned','Assigned','In Progress','Done','Canceled') for STATUS
					  //TODO: Constraint to replace ENUM('Floral Delivery', 'External Transport', 'Gift Delivery', 'Food Delivery', 'Language Interpreter', 'Internal Transport', 'Medicine Delivery', 'Laundry', 'Sanitation', 'Religious Requests', 'Security') for REQUESTTYPE
			")";

		/**
		 * SQL for building the ATTRIBUTES table.
		 */
		private static final String ATTRIBUTES_TABLE_SQL =
			"CREATE TABLE " + TABLES.ATTRIBUTES.name() + " (" +
				"ATTRID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
				"NODEID VARCHAR(25) CONSTRAINT FK_NODEID REFERENCES " + TABLES.NODES.name() + " ON DELETE CASCADE ON UPDATE RESTRICT," +
				"EDGEID VARCHAR(51) CONSTRAINT FK_EDGEID REFERENCES " + TABLES.EDGES.name() + " ON DELETE CASCADE ON UPDATE RESTRICT," +
				"ATTRIBUTE VARCHAR(30)" +
			")"; //TODO: ENUM CONSTRAINT FOR ATTRIBUTE?

		/**
		 * Map linking TABLES enum to the SQL code that builds the corresponding table.
		 */
		static final Map<TABLES,String> TABLE_SQL;
		static {
			TABLE_SQL = new EnumMap<>(TABLES.class);
			TABLE_SQL.put(NODES, NODE_TABLE_SQL);
			TABLE_SQL.put(EDGES, EDGE_TABLE_SQL);
			TABLE_SQL.put(ATTRIBUTES, ATTRIBUTES_TABLE_SQL);
			TABLE_SQL.put(SERVICE_REQUESTS, SERVICE_REQUESTS_TABLE_SQL);
		}



		// ========== ATTRIBUTES ==========



		private static final String NOT_NAVIGABLE_TEXT = "Not Navigable";
		private static final String HANDICAPPED_ACCESSIBLE_TEXT = "Handicapped Accessible";
		private static final String COVID_SAFE_TEXT = "COVID Safe";

		/**
		 * Enum for Attributes table
		 */
		public enum ATTRIBUTE {
			NOT_NAVIGABLE(NOT_NAVIGABLE_TEXT),
			HANDICAPPED_ACCESSIBLE(HANDICAPPED_ACCESSIBLE_TEXT),
			COVID_SAFE(COVID_SAFE_TEXT);

			public final String text;

			ATTRIBUTE(String text) {
				this.text = text;
			}

			/**
			 * Get the ATTRIBUTE enum corresponding to the provided string.
			 * @param attribute String to get enum equivalent for.
			 * @return ATTRIBUTE enum equivalent of provided string.
			 */
			public static ATTRIBUTE stringToAttribute(String attribute) {
				switch (attribute) {
					case NOT_NAVIGABLE_TEXT:
						return ATTRIBUTE.NOT_NAVIGABLE;
					case COVID_SAFE_TEXT:
						return ATTRIBUTE.COVID_SAFE;
					case HANDICAPPED_ACCESSIBLE_TEXT:
						return ATTRIBUTE.HANDICAPPED_ACCESSIBLE;
					default:
						System.out.println("Not Implemented");
						return null;
				}
			}
		}



		// ========== SERVICE REQUESTS ==========



		private static final String EXTERNAL_TRANSPORT_TEXT = "External Transport";
		private static final String FLORAL_DELIVERY_TEXT = "Floral Delivery";
		private static final String FOOD_DELIVERY_TEXT = "Food Delivery";
		private static final String GIFT_DELIVERY_TEXT = "Gift Delivery";
		private static final String INTERNAL_TRANSPORT_TEXT = "Internal Transport";
		private static final String LANGUAGE_INTERPRETER_TEXT = "Language Interpreter";
		private static final String LAUNDRY_TEXT = "Laundry";
		private static final String MEDICINE_DELIVERY_TEXT = "Medicine Delivery";
		private static final String RELIGIOUS_REQUEST_TEXT = "Religious Request";
		private static final String SANITATION_TEXT = "Sanitation";
		private static final String SECURITY_TEXT = "Security";

		/**
		 * Enum for service request tables.
		 */
		public enum SERVICEREQUESTS {
			EXTERNAL_TRANSPORT(EXTERNAL_TRANSPORT_TEXT),
			FLORAL_DELIVERY(FLORAL_DELIVERY_TEXT),
			FOOD_DELIVERY(FOOD_DELIVERY_TEXT),
			GIFT_DELIVERY(GIFT_DELIVERY_TEXT),
			INTERNAL_TRANSPORT(INTERNAL_TRANSPORT_TEXT),
			LANGUAGE_INTERPRETER(LANGUAGE_INTERPRETER_TEXT),
			LAUNDRY(LAUNDRY_TEXT),
			MEDICINE_DELIVERY(MEDICINE_DELIVERY_TEXT),
			RELIGIOUS_REQUEST(RELIGIOUS_REQUEST_TEXT),
			SANITATION(SANITATION_TEXT),
			SECURITY(SECURITY_TEXT);

			/**
			 * Get the SERVICEREQUESTS enum corresponding to the provided string.
			 * @param serviceRequest String to get enum equivalent for.
			 * @return SERVICEREQUESTS enum equivalent of provided string.
			 */
			public static SERVICEREQUESTS stringToServiceRequest(String serviceRequest) {
				switch (serviceRequest) {
					case EXTERNAL_TRANSPORT_TEXT:
						return SERVICEREQUESTS.EXTERNAL_TRANSPORT;
					case FLORAL_DELIVERY_TEXT:
						return SERVICEREQUESTS.FLORAL_DELIVERY;
					case FOOD_DELIVERY_TEXT:
						return SERVICEREQUESTS.FOOD_DELIVERY;
					case GIFT_DELIVERY_TEXT:
						return SERVICEREQUESTS.GIFT_DELIVERY;
					case INTERNAL_TRANSPORT_TEXT:
						return SERVICEREQUESTS.INTERNAL_TRANSPORT;
					case LANGUAGE_INTERPRETER_TEXT:
						return SERVICEREQUESTS.LANGUAGE_INTERPRETER;
					case LAUNDRY_TEXT:
						return SERVICEREQUESTS.LAUNDRY;
					case MEDICINE_DELIVERY_TEXT:
						return SERVICEREQUESTS.MEDICINE_DELIVERY;
					case RELIGIOUS_REQUEST_TEXT:
						return SERVICEREQUESTS.RELIGIOUS_REQUEST;
					case SANITATION_TEXT:
						return SERVICEREQUESTS.SANITATION;
					case SECURITY_TEXT:
						return SERVICEREQUESTS.SECURITY;
					default:
						System.out.println("Not Implemented");
						return null;
				}
			}

			public final String text;

			SERVICEREQUESTS(String text) {
				this.text = text;
			}

			/**
			 * SQL for building the FOOD_DELIVERY table.
			 */
			private static final String FOOD_DELIVERY_TABLE_SQL =
				"CREATE TABLE " + TABLES.SERVICEREQUESTS.FOOD_DELIVERY.name() + " (" +
					"REQUESTID VARCHAR(25) PRIMARY KEY CONSTRAINT FK_REQUESTID REFERENCES " + TABLES.SERVICE_REQUESTS.name() + " ON DELETE CASCADE ON UPDATE RESTRICT," +
					"DELIVERYTIME VARCHAR(10)," + //TODO: MAKE THIS TAKE TIME TYPE
					"DIETARYRESTRICTIONS VARCHAR(150)," +
					"NOTE VARCHAR(300)," +
				")";

			/**
			 * SQL for building the FLORAL_DELIVERY table.
			 */
			private static final String FLORAL_DELIVERY_TABLE_SQL =
				"CREATE TABLE " + TABLES.SERVICEREQUESTS.FLORAL_DELIVERY.name() + " (" +
					"REQUESTID VARCHAR(25) PRIMARY KEY CONSTRAINT FK_REQUESTID REFERENCES " + TABLES.SERVICE_REQUESTS.name() + " ON DELETE CASCADE ON UPDATE RESTRICT," +
					"DELIVERYTIME VARCHAR(10)," + //TODO: MAKE THIS TAKE TIME TYPE
					"NOTE VARCHAR(300)" +
				")";

			/**
			 * Map linking SERVICEREQUESTS enum to the SQL code that builds the corresponding table.
			 */
			static final Map<SERVICEREQUESTS,String> SERVICEREQUESTS_SQL;
			static {
				SERVICEREQUESTS_SQL = new EnumMap<>(SERVICEREQUESTS.class);
				SERVICEREQUESTS_SQL.put(FLORAL_DELIVERY, FLORAL_DELIVERY_TABLE_SQL);
				SERVICEREQUESTS_SQL.put(FOOD_DELIVERY, FOOD_DELIVERY_TABLE_SQL);
			}



			// ========== STATUSES ==========



			private static final String UNASSIGNED_TEXT = "Unassigned";
			private static final String ASSIGNED_TEXT = "Assigned";
			private static final String IN_PROGRESS_TEXT = "In Progress";
			private static final String DONE_TEXT = "Done";
			private static final String CANCELED_TEXT = "Canceled";

			/**
			 * Enum for statuses used with service requests.
			 */
			public enum STATUS {
				UNASSIGNED(UNASSIGNED_TEXT),
				ASSIGNED(ASSIGNED_TEXT),
				IN_PROGRESS(IN_PROGRESS_TEXT),
				DONE(DONE_TEXT),
				CANCELED(CANCELED_TEXT);

				public final String text;

				STATUS(String text) {
					this.text = text;
				}

				public static STATUS stringToStatus(String attribute) {
					switch (attribute) {
						case UNASSIGNED_TEXT:
							return STATUS.UNASSIGNED;
						case ASSIGNED_TEXT:
							return STATUS.ASSIGNED;
						case IN_PROGRESS_TEXT:
							return STATUS.IN_PROGRESS;
						case DONE_TEXT:
							return STATUS.DONE;
						case CANCELED_TEXT:
							return STATUS.CANCELED;
						default:
							System.out.println("Not implemented!");
							return null;
					}
				}
			}
		}
	}

	/**
	 * Path to default node CSV resource.
	 */
	static final String NODE_RESOURCE_PATH = "edu/wpi/aquamarine_axolotls/csv/MapAnodes.csv";

	/**
	 * Path to default edge CSV resource.
	 */
	static final String EDGE_RESOURCE_PATH = "edu/wpi/aquamarine_axolotls/csv/MapAedges.csv";

	/**
	 * Convert resource path string to File.
	 * @param resourcePath path to resource in app structure.
	 * @return File for corresponding resource.
	 */
	static InputStream resourceAsStream(String resourcePath) {
		return DatabaseInfo.class.getClassLoader().getResourceAsStream(resourcePath);
	}
}
