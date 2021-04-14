package edu.wpi.aquamarine_axolotls.db;

import java.io.File;
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
		EDGES
	}

	/**
	 * Path to default node CSV resource.
	 */
	static final String nodeResourcePath = "edu/wpi/aquamarine_axolotls/csv/L1Nodes.csv";

	/**
	 * Path to default edge CSV resource.
	 */
	static final String edgeResourcePath = "edu/wpi/aquamarine_axolotls/csv/L1Edges.csv";

	/**
	 * Map associating TABLES enum to usable table names.
	 */
	public static final Map<TABLES,String> TABLE_NAMES;
	static {
		Map<TABLES,String> aMap = new EnumMap<>(TABLES.class);
		aMap.put(TABLES.NODES, "NODES");
		aMap.put(TABLES.EDGES, "EDGES");
		TABLE_NAMES = Collections.unmodifiableMap(aMap);
	}

	/**
	 * SQL for building the NODES table.
	 */
	static final String NODE_TABLE_SQL =
		"CREATE TABLE NODES (" +
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
	static final String EDGE_TABLE_SQL =
	   "CREATE TABLE EDGES (" +
		   "EDGEID VARCHAR(51) PRIMARY KEY," +
			"STARTNODE VARCHAR(25)," +
			"ENDNODE VARCHAR(25)," +
			"CONSTRAINT FK_STARTNODE FOREIGN KEY (STARTNODE) REFERENCES Nodes(NODEID) ON DELETE CASCADE ON UPDATE RESTRICT," +
			"CONSTRAINT FK_ENDNODE FOREIGN KEY (ENDNODE) REFERENCES Nodes(NODEID) ON DELETE CASCADE ON UPDATE RESTRICT" +
		")";

	/**
	 * Convert resource path string to File.
	 * @param resourcePath path to resource in app structure.
	 * @return File for corresponding resource.
	 * @throws URISyntaxException Something went wrong.
	 */
	static File resourcePathToFile(String resourcePath) throws URISyntaxException {
		return new File(DatabaseInfo.class.getClassLoader().getResource(resourcePath).toURI());
	}
}
