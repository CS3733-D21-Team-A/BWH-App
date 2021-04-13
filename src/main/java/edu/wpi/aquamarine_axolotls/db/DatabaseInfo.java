package edu.wpi.aquamarine_axolotls.db;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class DatabaseInfo {

	/**
	 * Enum for available tables.
	 */
	public enum TABLES {
		NODES,
		EDGES
	}

	public static final String nodeResourcePath = "edu/wpi/aquamarine_axolotls/csv/L1Nodes.csv";
	public static final String edgeResourcePath = "edu/wpi/aquamarine_axolotls/csv/L1Edges.csv";

	public static final Map<TABLES,String> TABLE_NAMES;
	static {
		Map<TABLES,String> aMap = new EnumMap<TABLES,String>(TABLES.class);
		aMap.put(TABLES.NODES, "NODES");
		aMap.put(TABLES.EDGES, "EDGES");
		TABLE_NAMES = Collections.unmodifiableMap(aMap);
	}

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
