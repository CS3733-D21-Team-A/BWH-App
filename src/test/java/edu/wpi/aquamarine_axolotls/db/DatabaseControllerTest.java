package edu.wpi.aquamarine_axolotls.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseControllerTest {
	private final DatabaseController db = new DatabaseController();
	private final CSVHandler csvHandler = new CSVHandler(db);
	private final File nodeFile = DatabaseInfo.resourcePathToFile(DatabaseInfo.nodeResourcePath);
	private final File edgeFile = DatabaseInfo.resourcePathToFile(DatabaseInfo.edgeResourcePath);

	DatabaseControllerTest() throws SQLException, IOException, URISyntaxException {}

	@BeforeEach
	void resetDB() throws IOException, SQLException {
		db.emptyEdgeTable();
		db.emptyNodeTable();
		csvHandler.importCSV(nodeFile, DatabaseInfo.TABLES.NODES);
		csvHandler.importCSV(edgeFile, DatabaseInfo.TABLES.EDGES);
	}

	@Test
	void nodeDoesntExist() {
		try {
			assertFalse(db.nodeExists("foobar"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void nodeDoesExist() {
		try {
			assertTrue(db.nodeExists("WELEV00ML1"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void nodeColumnValidNames() {
		Set<String> colName = db.getNodeColumns().keySet();
		assertTrue(colName.contains("NODEID"));
		assertTrue(colName.contains("XCOORD"));
		assertTrue(colName.contains("YCOORD"));
		assertTrue(colName.contains("FLOOR"));
		assertTrue(colName.contains("BUILDING"));
		assertTrue(colName.contains("NODETYPE"));
		assertTrue(colName.contains("LONGNAME"));
		assertTrue(colName.contains("SHORTNAME"));
	}

	@Test
	void nodeColumnValidTypes() {
		Map<String, Boolean> colName = db.getNodeColumns();
		assertTrue(colName.get("NODEID")); // String
		assertFalse(colName.get("XCOORD")); // not String
		assertFalse(colName.get("YCOORD"));
		assertTrue(colName.get("FLOOR"));
		assertTrue(colName.get("BUILDING"));
		assertTrue(colName.get("NODETYPE"));
		assertTrue(colName.get("LONGNAME"));
		assertTrue(colName.get("SHORTNAME"));
	}

	@Test
	void addNodeAllValues() {
		Map<String, String> newNode = new HashMap<String, String>();
		newNode.put("NODEID", "Test1");
		newNode.put("XCOORD", "12");
		newNode.put("YCOORD", "300");
		newNode.put("FLOOR", "G");
		newNode.put("BUILDING", "Mars");
		newNode.put("NODETYPE", "EXIT");
		newNode.put("LONGNAME", "Its a made up place!");
		newNode.put("SHORTNAME", "MRS");
		try{
			db.addNode(newNode);
			assertTrue(db.nodeExists("Test1"));
		}
		catch(SQLException e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void addNodeSomeValues() {
		Map<String, String> newNode = new HashMap<String, String>();
		newNode.put("NODEID", "Test2");
		newNode.put("XCOORD", "20");
		newNode.put("BUILDING", "FS");
		newNode.put("NODETYPE", "BUILD");
		newNode.put("SHORTNAME", "MRS");
		try{
			db.addNode(newNode);
			assertTrue(db.nodeExists("Test2"));
		}
		catch(SQLException e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void addNodeNoPKEY() {
		Map<String, String> newNode = new HashMap<String, String>();
		newNode.put("XCOORD", "20");
		newNode.put("BUILDING", "Empire State");
		newNode.put("NODETYPE", "BUILDING");
		newNode.put("SHORTNAME", "MRS");
		assertThrows(SQLException.class, () -> db.addNode(newNode));
	}

	@Test
	void addNodeDupKeys() {
		Map<String, String> newNode = new HashMap<String, String>();
		newNode.put("NODEID", "CCONF001L1");

		assertThrows(SQLException.class, () -> db.addNode(newNode));
	}

	@Test
	void editNodeAllValues() {
		Map<String, String> newNode = new HashMap<String, String>();
		newNode.put("XCOORD", "100");
		newNode.put("YCOORD", "5");
		newNode.put("FLOOR", "G");
		newNode.put("BUILDING", "KEN");
		newNode.put("NODETYPE", "EXIT");
		newNode.put("LONGNAME", "Its a made up place!");
		newNode.put("SHORTNAME", "KHALL");

		try{
			db.editNode("CCONF003L1", newNode);
			Map<String, String> editted = db.getNode("CCONF003L1");
			assertEquals(editted.get("XCOORD"), "100");
			assertEquals(editted.get("YCOORD"), "5");
			assertEquals(editted.get("FLOOR"), "G");
			assertEquals(editted.get("BUILDING"), "KEN");
			assertEquals(editted.get("NODETYPE"), "EXIT");
			assertEquals(editted.get("LONGNAME"), "Its a made up place!");
			assertEquals(editted.get("SHORTNAME"), "KHALL");

		}
		catch(SQLException e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void editNodeSomeValues() {
		Map<String, String> newNode = new HashMap<String, String>();
		newNode.put("XCOORD", "13");
		newNode.put("FLOOR", "2");

		try{
			db.editNode("CDEPT002L1", newNode);
			Map<String, String> editted = db.getNode("CDEPT002L1");
			assertEquals(editted.get("XCOORD"), "13"); // changed value
			assertEquals(editted.get("FLOOR"), "2"); // changed value

		}
		catch(SQLException e){
			e.printStackTrace();
			fail();
		}
	}


}