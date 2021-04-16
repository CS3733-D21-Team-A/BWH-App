package edu.wpi.aquamarine_axolotls.db;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Class containing static info for reference when working with the database.
 */
public class DatabaseInfo {
	/**
	 * Enum for available tables.
	 */
	public enum TABLES {
		/**
		 * Node table.
		 */
		NODES,
		/**
		 * Edge table.
		 */
		EDGES,
		/**
		 * Service Requests table.
		 */
		SERVICEREQUESTS
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
			"CONSTRAINT FK_STARTNODE FOREIGN KEY (STARTNODE) REFERENCES " + TABLES.NODES.name() + "(NODEID) ON DELETE CASCADE ON UPDATE RESTRICT," +
			"CONSTRAINT FK_ENDNODE FOREIGN KEY (ENDNODE) REFERENCES " + TABLES.NODES.name() + "(NODEID) ON DELETE CASCADE ON UPDATE RESTRICT" +
		")"; //TODO: FIGURE OUT TAGS

	/**
	 * SQL for building the SERVICEREQUESTS table.
	 */
	static final String SERVICE_REQUESTS_TABLE_SQL =
		"CREATE TABLE " + TABLES.SERVICEREQUESTS.name() + " (" +
			"REQUESTID VARCHAR(25) PRIMARY KEY," +
			"STATUS ENUM('Unassigned','Assigned','In Progress','Done','Canceled') DEFAULT 'Unassigned'," +
			"EMPLOYEEID VARCHAR(30)," + //TODO: THIS IS A FOREIGN KEY TO THE USER TABLE
			"FIRSTNAME VARCHAR(30)," +
			"LASTNAME VARCHAR(50)," +
			"LOCATIONID VARCHAR(25)," +
			"REQUESTTYPE VARCHAR(50))," +
			"NOTE VARCHAR(300))," +
			"DIETARYRESTRICTIONS VARCHAR(100)," + // Food Delivery //TODO: FIGURE OUT WHAT TO USE HERE
			"DELIVERYTIME TIME(0)," + // Food Delivery, Floral Delivery
			"CONSTRAINT FK_LOCATIONID FOREIGN KEY (LOCATIONID) REFERENCES " + TABLES.NODES.name() + "(NODEID)," +
		")";

	/**
	 * Convert resource path string to File.
	 * @param resourcePath path to resource in app structure.
	 * @return File for corresponding resource.
	 * @throws URISyntaxException Something went wrong.
	 */
	static InputStream resourceAsStream(String resourcePath) throws URISyntaxException {
		return DatabaseInfo.class.getClassLoader().getResourceAsStream(resourcePath);
	}
}
