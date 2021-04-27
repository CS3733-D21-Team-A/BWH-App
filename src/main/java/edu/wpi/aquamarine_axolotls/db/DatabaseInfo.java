package edu.wpi.aquamarine_axolotls.db;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

/**
 * Class containing static info for reference when working with the database. Functionally static.
 */
final class DatabaseInfo {

	private DatabaseInfo() {} //NO CONSTRUCTION ALLOWED >:(

	// ========== PRIMARY TABLE SQL ==========


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
			"LONGNAME VARCHAR(100)," +
			"SHORTNAME VARCHAR(50)" +
		")";

	/**
	 * SQL for building the EDGES table.
	 */
	static final String EDGE_TABLE_SQL =
		"CREATE TABLE " + TABLES.EDGES.name() + " (" +
			"EDGEID VARCHAR(51) PRIMARY KEY," +
			"STARTNODE VARCHAR(25) CONSTRAINT FK_STARTNODE REFERENCES " + TABLES.NODES.name() + " ON DELETE CASCADE ON UPDATE RESTRICT," +
			"ENDNODE VARCHAR(25) CONSTRAINT FK_ENDNODE REFERENCES " + TABLES.NODES.name() + " ON DELETE CASCADE ON UPDATE RESTRICT" +
		")";

	/**
	 * SQL for building the SERVICE_REQUESTS table.
	 */
	static final String SERVICE_REQUESTS_TABLE_SQL =
		"CREATE TABLE " + TABLES.SERVICE_REQUESTS.name() + " (" +
			"REQUESTID VARCHAR(25) PRIMARY KEY," +
			"STATUS VARCHAR(11) DEFAULT 'Unassigned'," +
			"EMPLOYEEID VARCHAR(30)," + //TODO: THIS IS A FOREIGN KEY TO THE USER TABLE (NOT YET IMPLEMENTED)
			"LOCATIONID VARCHAR(25)," +
			"FIRSTNAME VARCHAR(30)," +
			"LASTNAME VARCHAR(50)," +
			"REQUESTTYPE VARCHAR(20) NOT NULL," + //TODO: MAKE THIS USE ENUM
			"CONSTRAINT FK_LOCATIONID FOREIGN KEY (LOCATIONID) REFERENCES " + TABLES.NODES.name() + "(NODEID) ON DELETE SET NULL ON UPDATE RESTRICT" +
			//TODO: Constraint to replace ENUM('Unassigned','Assigned','In Progress','Done','Canceled') for STATUS
			//TODO: Constraint to replace ENUM('Floral Delivery', 'External Transport', 'Gift Delivery', 'Food Delivery', 'Language Interpreter', 'Internal Transport', 'Medicine Delivery', 'Laundry', 'Sanitation', 'Religious Requests', 'Security') for REQUESTTYPE
		")";

	/**
	 * SQL for building the ATTRIBUTES table.
	 */
	static final String ATTRIBUTES_TABLE_SQL =
		"CREATE TABLE " + TABLES.ATTRIBUTES.name() + " (" +
			"ATTRID INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
			"NODEID VARCHAR(25) CONSTRAINT FK_NODEID REFERENCES " + TABLES.NODES.name() + "(NODEID) ON DELETE CASCADE ON UPDATE RESTRICT," +
			"EDGEID VARCHAR(51) CONSTRAINT FK_EDGEID REFERENCES " + TABLES.EDGES.name() + "(EDGEID) ON DELETE CASCADE ON UPDATE RESTRICT," +
			"ATTRIBUTE VARCHAR(30)" +
		")"; //TODO: ENUM CONSTRAINT FOR ATTRIBUTE?

	// ========== USER TABLE ================= //
	/**
	 * SQL table for the USER
	 */

	static final String USER_TABLE_SQL =
		"CREATE TABLE " + TABLES.USERS.name() + " (" +
			"USERNAME VARCHAR(25) PRIMARY KEY, " +
			"FIRSTNAME VARCHAR(25)," +
			"LASTNAME VARCHAR(25)," +
			"EMAIL VARCHAR(25) NOT NULL," +
			"CONSTRAINT emailConst UNIQUE(EMAIL)," +
			"USERTYPE VARCHAR(25)," +
			"PASSWORD VARCHAR(25) NOT NULL" +
			")"; //TODO CONSTRAINT FOR USERTYPE OR POSSIBLE TYPE DEFAULT TO PATIENT

	// ========== ATTRIBUTES STRINGS ==========


	static final String NOT_NAVIGABLE_TEXT = "Not Navigable";
	static final String HANDICAPPED_ACCESSIBLE_TEXT = "Handicapped Accessible";
	static final String COVID_SAFE_TEXT = "COVID Safe";


	// ========== SERVICE REQUESTS ==========


	// delivery time, dietary restrictions, note, food requested mess
	// 4/26 added Number of Servings, Contact Number and Drink Options
	// TODO: Check newly added attributes to make sure they fit the needed value holders
	/**
	 * SQL for building the FOOD_DELIVERY table.
	 */
	static final String FOOD_DELIVERY_TABLE_SQL =
		"CREATE TABLE " + SERVICEREQUEST.FOOD_DELIVERY.name() + " (" +
			"REQUESTID VARCHAR(25) PRIMARY KEY," +
			"DELIVERYTIME VARCHAR(10)," + //TODO: MAKE THIS TAKE TIME TYPE
			"DIETARYRESTRICTIONS VARCHAR(150)," +
			"NOTE VARCHAR(300)," +
			"NUMBEROFSERVINGS VARCHAR(3)," +
			"CONTACTNUMBER VARCHAR(15)," +
			"DRINKOPTIONS VARCHAR(25)" + // TODO: do the same thing as what happened with food options thing
			"CONSTRAINT FK_FOOD_REQUESTID FOREIGN KEY (REQUESTID) REFERENCES " +
					TABLES.SERVICE_REQUESTS.name() +
					"(REQUESTID) ON DELETE CASCADE ON UPDATE RESTRICT" +
		")";

	// delivery time, note, flower requested mess
	// 4/26 added Delivery Date, Flower Options, Vase Options and Contact Number
	// TODO: Check newly added attributes to make sure they fit the needed value holders
	/**
	 * SQL for building the FLORAL_DELIVERY table.
	 */
	static final String FLORAL_DELIVERY_TABLE_SQL =
		"CREATE TABLE " + SERVICEREQUEST.FLORAL_DELIVERY.name() + " (" +
			"REQUESTID VARCHAR(25) PRIMARY KEY," +
			"DELIVERYTIME VARCHAR(10)," + //TODO: MAKE THIS TAKE TIME TYPE
			"DELIVERYDATE VARCHAR(50)," + // TODO: Make this take date type
			"FLOWEROPTION VARCHAR(50)," +
			"VASEOPTION VARCHAR(50)," +
			"CONTACTNUMBER VARCHAR(15)," +
			"NOTE VARCHAR(300)," +
			"CONSTRAINT FK_FLORAL_REQUESTID FOREIGN KEY (REQUESTID) REFERENCES " + TABLES.SERVICE_REQUESTS.name() + "(REQUESTID) ON DELETE CASCADE ON UPDATE RESTRICT" +
		")";

	/**
	 * SQL for building the EXTERNAL_TRANSPORTATION table.
	 */
	static final String EXTERNALTRANSPORTATION_TABLE_SQL =
		"CREATE TABLE " + SERVICEREQUEST.EXTERNAL_TRANSPORT.name() + " (" +
			"REQUESTID VARCHAR(25) PRIMARY KEY," +
			"NEWLOCATION VARCHAR(25)," + //TODO THIS TABLE FEELS LOGICALLY WRONG FOR THE SR
			"CONTRAINT FK_EXTERNALTRANSPORTION_REQUESTID FOREIGN KEY (REQUESTID) REFERENCES " + TABLES.SERVICE_REQUESTS.name() + "(REQUESTID) ON DELETE CASCADE ON UPDATE RESTRICT" +
		")";

	/**
	 * SQL for building the INTERNAL_TRANSPORTATION table.
	 */
	static final String INTERNALTRANSPORTATION_TABLE_SQL =
			"CREATE TABLE " + SERVICEREQUEST.INTERNAL_TRANSPORT.name() + " (" +
					"REQUESTID VARCHAR(25) PRIMARY KEY," +
					"NEWLOCATION VARCHAR(25)," + //TODO THIS TABLE FEELS LOGICALLY WRONG FOR THE SR
					"CONTRAINT FK_INTERNALTRANSPORTATION_REQUESTID FOREIGN KEY (REQUESTID) REFERENCES " + TABLES.SERVICE_REQUESTS.name() + "(REQUESTID) ON DELETE CASCADE ON UPDATE RESTRICT" +
					")";

	/**
	 * SQL for building the GIFTDELIVERY table.
	 */
	static final String GIFTDELIVERY_TABLE_SQL =
			"CREATE TABLE " + SERVICEREQUEST.GIFT_DELIVERY.name() + " (" +
					"REQUESTID VARCHAR(25) PRIMARY KEY," +
					"GIFTTYPE VARCHAR(25)," +
					"NOTE VARCHAR(300)" +
					"CONTRAINT FK_GIFTDELIVERY_REQUESTID FOREIGN KEY (REQUESTID) REFERENCES " + TABLES.SERVICE_REQUESTS.name() + "(REQUESTID) ON DELETE CASCADE ON UPDATE RESTRICT" +
					")";

	/**
	 * SQL for building the LANGUAGE_INTERPRETER table.
	 */
	static final String LANGUAGEINTERPRETER_TABLE_SQL =
			"CREATE TABLE " + SERVICEREQUEST.LANGUAGE_INTERPRETER.name() + " (" +
					"REQUESTID VARCHAR(25) PRIMARY KEY," +
					"LANGUAGES VARCHAR(50)," +
					"CONTRAINT FK_LANGUAGEINTERPRETER_REQUESTID FOREIGN KEY (REQUESTID) REFERENCES " + TABLES.SERVICE_REQUESTS.name() + "(REQUESTID) ON DELETE CASCADE ON UPDATE RESTRICT" +
					")";

	/**
	 * SQL for building the LAUNDRY table.
	 */
	static final String LAUNDRY_TABLE_SQL =
			"CREATE TABLE " + SERVICEREQUEST.LAUNDRY.name() + " (" +
					"REQUESTID VARCHAR(25) PRIMARY KEY," +
					"NOTE VARCHAR(300)," +
					"CONTRAINT FK_LAUNDRY_REQUESTID FOREIGN KEY (REQUESTID) REFERENCES " + TABLES.SERVICE_REQUESTS.name() + "(REQUESTID) ON DELETE CASCADE ON UPDATE RESTRICT" +
					")";

	/**
	 * SQL for building the MEDICINEDELIVERY table.
	 */
	static final String MEDICINEDELIVERY_TABLE_SQL =
			"CREATE TABLE " + SERVICEREQUEST.MEDICINE_DELIVERY.name() + " (" +
					"REQUESTID VARCHAR(25) PRIMARY KEY," +
					"TYPE VARCHAR(100)," +
					"DOSAGE VARCHAR(100)" +
					"CONTRAINT FK_MEDICINEDELIVERY_REQUESTID FOREIGN KEY (REQUESTID) REFERENCES " + TABLES.SERVICE_REQUESTS.name() + "(REQUESTID) ON DELETE CASCADE ON UPDATE RESTRICT" +
					")";

	/**
	 * SQL for building the RELIGOUSREQUEST table.
	 */
	static final String RELIGOUSREQUEST_TABLE_SQL =
			"CREATE TABLE " + SERVICEREQUEST.RELIGIOUS_REQUEST.name() + " (" +
					"REQUESTID VARCHAR(25) PRIMARY KEY," +
					"TYPEOFREQUEST VARCHAR(25)," + //TODO ANOTHER ENUM?
					"NOTE VARCHAR(300)" +
					"CONTRAINT FK_RELIGOUSREQUEST_REQUESTID FOREIGN KEY (REQUESTID) REFERENCES " + TABLES.SERVICE_REQUESTS.name() + "(REQUESTID) ON DELETE CASCADE ON UPDATE RESTRICT" +
					")";
	/**
	 * SQL for building the SANITATION table.
	 */
	static final String SANITATION_TABLE_SQL =
			"CREATE TABLE " + SERVICEREQUEST.SANITATION.name() + " (" +
					"REQUESTID VARCHAR(25) PRIMARY KEY," +
					"NOTE VARCHAR(300)," +
					"BIOHAZARD VARCHAR(10)" + //TODO MAKE BOOLEAN? OR DEFAULT TO NO? WANTED TO MAKE IT NOT NULL BUT WANT CLARIFICATION
					"CONTRAINT FK_SANITATION_REQUESTID FOREIGN KEY (REQUESTID) REFERENCES " + TABLES.SERVICE_REQUESTS.name() + "(REQUESTID) ON DELETE CASCADE ON UPDATE RESTRICT" +
					")";

	/**
	 *  SQL for building the FACILITIESMAINTENANCE //TODO THIS NEEDS TO BE THOUGHT OUT MORE BEFORE MAKING THE TABLE
	 */
//	static final String FACILITIESMAINTENANCE_TABLE_SQL =
//			"CREATE TABLE " + SERVICEREQUEST.


	static final String EXTERNAL_TRANSPORT_TEXT = "External Transport";
	static final String FLORAL_DELIVERY_TEXT = "Floral Delivery";
	static final String FOOD_DELIVERY_TEXT = "Food Delivery";
	static final String GIFT_DELIVERY_TEXT = "Gift Delivery";
	static final String INTERNAL_TRANSPORT_TEXT = "Internal Transport";
	static final String LANGUAGE_INTERPRETER_TEXT = "Language Interpreter";
	static final String LAUNDRY_TEXT = "Laundry";
	static final String MEDICINE_DELIVERY_TEXT = "Medicine Delivery";
	static final String RELIGIOUS_REQUEST_TEXT = "Religious Request";
	static final String SANITATION_TEXT = "Sanitation";
	static final String SECURITY_TEXT = "Security";






	// ========== STATUSES ==========


	static final String UNASSIGNED_TEXT = "Unassigned";
	static final String ASSIGNED_TEXT = "Assigned";
	static final String IN_PROGRESS_TEXT = "In Progress";
	static final String DONE_TEXT = "Done";
	static final String CANCELED_TEXT = "Canceled";

	// ========== USERS ================
	static final String USER_TEXT = "User";
	static final String EMPLOYEE_TEXT = "Employee";
	static final String ADMIN_TEXT = "Admin";
	static final String PATIENT_TEXT = "Patient";
	static final String GUEST_TEXT = "Guest";
	static final String PASSWORD_TEXT = "Password";
	static final String USERTYPE_TEXT = "Usertype";
	static final String LASTNAME_TEXT = "Last Name";
	static final String FIRSTNAME_TEXT = "First Name";

	// ========== RESOURCES ==========


	/**
	 * Path to default node CSV resource.
	 */
	static final String DEFAULT_NODE_RESOURCE_PATH = "edu/wpi/aquamarine_axolotls/csv/bwAnodes.csv";

	/**
	 * Path to default edge CSV resource.
	 */
	static final String DEFAULT_EDGE_RESOURCE_PATH = "edu/wpi/aquamarine_axolotls/csv/bwAedges.csv";

	/**
	 * Path to test node CSV resource.
	 */
	static final String TEST_NODE_RESOURCE_PATH = "edu/wpi/aquamarine_axolotls/csv/MapAnodes.csv";

	/**
	 * Path to test edge CSV resource.
	 */
	static final String TEST_EDGE_RESOURCE_PATH = "edu/wpi/aquamarine_axolotls/csv/MapAedges.csv";

	/**
	 * Map linking TABLES enum to the SQL code that builds the corresponding table.
	 */
	final static Map<TABLES,String> TABLE_SQL;

	/**
	 * Map linking SERVICEREQUEST enum to the SQL code that builds the corresponding table.
	 */
	final static Map<SERVICEREQUEST,String> SERVICEREQUEST_SQL;

	/**
	 * Convert resource path string to File.
	 * @param resourcePath path to resource in app structure.
	 * @return File for corresponding resource.
	 */
	static InputStream resourceAsStream(String resourcePath) {
		return DatabaseInfo.class.getClassLoader().getResourceAsStream(resourcePath);
	}

	static {
		TABLE_SQL = new EnumMap<>(TABLES.class);
		TABLE_SQL.put(TABLES.NODES, NODE_TABLE_SQL);
		TABLE_SQL.put(TABLES.EDGES, EDGE_TABLE_SQL);
		TABLE_SQL.put(TABLES.ATTRIBUTES, ATTRIBUTES_TABLE_SQL);
		TABLE_SQL.put(TABLES.SERVICE_REQUESTS, SERVICE_REQUESTS_TABLE_SQL);
		TABLE_SQL.put(TABLES.USERS, USER_TABLE_SQL);

		SERVICEREQUEST_SQL = new EnumMap<>(SERVICEREQUEST.class);
		SERVICEREQUEST_SQL.put(SERVICEREQUEST.FLORAL_DELIVERY, FLORAL_DELIVERY_TABLE_SQL);
		SERVICEREQUEST_SQL.put(SERVICEREQUEST.FOOD_DELIVERY, FOOD_DELIVERY_TABLE_SQL);
		//TODO: Add other service requests
	}
}
