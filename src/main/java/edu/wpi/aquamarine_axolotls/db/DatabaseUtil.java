package edu.wpi.aquamarine_axolotls.db;

import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

import static edu.wpi.aquamarine_axolotls.db.DatabaseInfo.*;

public class DatabaseUtil {

	/**
	 * Convert resource path string to File.
	 * @param resourcePath path to resource in app structure.
	 * @return File for corresponding resource.
	 */
	static InputStream resourceAsStream(String resourcePath) {
		return DatabaseInfo.class.getClassLoader().getResourceAsStream(resourcePath);
	}

	/**
	 * Map linking TABLES enum to the SQL code that builds the corresponding table.
	 */
	final static Map<TABLES,String> TABLE_SQL;

	/**
	 * Map linking SERVICEREQUEST enum to the SQL code that builds the corresponding table.
	 */
	final static Map<SERVICEREQUEST,String> SERVICEREQUEST_SQL;

	/**
	 * BiMap for associating SERVICEREQUEST enum with corresponding string representations.
	 */
	public final static BiMap<SERVICEREQUEST,String> SERVICEREQUEST_NAMES;

	/**
	 * BiMap for associating STATUS enum with corresponding string representations.
	 */
	public final static BiMap<STATUS,String> STATUS_NAMES;

	/**
	 * BiMap for associating ATTRIBUTE enum with corresponding string representations.
	 */
	public final static BiMap<ATTRIBUTE,String> ATTRIBUTE_NAMES;

	static {
		TABLE_SQL = new EnumMap<>(TABLES.class);
		TABLE_SQL.put(TABLES.NODES, NODE_TABLE_SQL);
		TABLE_SQL.put(TABLES.EDGES, EDGE_TABLE_SQL);
		TABLE_SQL.put(TABLES.ATTRIBUTES, ATTRIBUTES_TABLE_SQL);
		TABLE_SQL.put(TABLES.SERVICE_REQUESTS, SERVICE_REQUESTS_TABLE_SQL);

		SERVICEREQUEST_SQL = new EnumMap<>(SERVICEREQUEST.class);
		SERVICEREQUEST_SQL.put(SERVICEREQUEST.FLORAL_DELIVERY, FLORAL_DELIVERY_TABLE_SQL);
		SERVICEREQUEST_SQL.put(SERVICEREQUEST.FOOD_DELIVERY, FOOD_DELIVERY_TABLE_SQL);

		SERVICEREQUEST_NAMES = EnumHashBiMap.create(SERVICEREQUEST.class);
		SERVICEREQUEST_NAMES.put(SERVICEREQUEST.EXTERNAL_TRANSPORT,EXTERNAL_TRANSPORT_TEXT);
		SERVICEREQUEST_NAMES.put(SERVICEREQUEST.FLORAL_DELIVERY,FLORAL_DELIVERY_TEXT);
		SERVICEREQUEST_NAMES.put(SERVICEREQUEST.FOOD_DELIVERY,FOOD_DELIVERY_TEXT);
		SERVICEREQUEST_NAMES.put(SERVICEREQUEST.GIFT_DELIVERY,GIFT_DELIVERY_TEXT);
		SERVICEREQUEST_NAMES.put(SERVICEREQUEST.INTERNAL_TRANSPORT,INTERNAL_TRANSPORT_TEXT);
		SERVICEREQUEST_NAMES.put(SERVICEREQUEST.LANGUAGE_INTERPRETER,LANGUAGE_INTERPRETER_TEXT);
		SERVICEREQUEST_NAMES.put(SERVICEREQUEST.LAUNDRY,LAUNDRY_TEXT);
		SERVICEREQUEST_NAMES.put(SERVICEREQUEST.MEDICINE_DELIVERY,MEDICINE_DELIVERY_TEXT);
		SERVICEREQUEST_NAMES.put(SERVICEREQUEST.RELIGIOUS_REQUEST,RELIGIOUS_REQUEST_TEXT);
		SERVICEREQUEST_NAMES.put(SERVICEREQUEST.SANITATION,SANITATION_TEXT);
		SERVICEREQUEST_NAMES.put(SERVICEREQUEST.SECURITY,SECURITY_TEXT);

		STATUS_NAMES = EnumHashBiMap.create(STATUS.class);
		STATUS_NAMES.put(STATUS.UNASSIGNED,UNASSIGNED_TEXT);
		STATUS_NAMES.put(STATUS.ASSIGNED,ASSIGNED_TEXT);
		STATUS_NAMES.put(STATUS.IN_PROGRESS,IN_PROGRESS_TEXT);
		STATUS_NAMES.put(STATUS.DONE,DONE_TEXT);
		STATUS_NAMES.put(STATUS.CANCELED,CANCELED_TEXT);

		ATTRIBUTE_NAMES = EnumHashBiMap.create(ATTRIBUTE.class);
		ATTRIBUTE_NAMES.put(ATTRIBUTE.NOT_NAVIGABLE,NOT_NAVIGABLE_TEXT);
		ATTRIBUTE_NAMES.put(ATTRIBUTE.HANDICAPPED_ACCESSIBLE,HANDICAPPED_ACCESSIBLE_TEXT);
		ATTRIBUTE_NAMES.put(ATTRIBUTE.COVID_SAFE,COVID_SAFE_TEXT);
	}
}
