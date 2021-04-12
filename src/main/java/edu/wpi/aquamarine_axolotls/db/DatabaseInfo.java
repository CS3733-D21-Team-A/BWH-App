package edu.wpi.aquamarine_axolotls.db;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class DatabaseInfo {
	public enum TABLES {
		NODES,
		EDGES
	}
	public static final Map<TABLES,String> TABLE_NAMES;

	static {
		Map<TABLES,String> aMap = new EnumMap<TABLES,String>(TABLES.class);
		aMap.put(TABLES.NODES, "NODES");
		aMap.put(TABLES.EDGES, "EDGES");
		TABLE_NAMES = Collections.unmodifiableMap(aMap);
	}
}
