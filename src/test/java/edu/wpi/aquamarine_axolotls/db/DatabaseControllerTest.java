package edu.wpi.aquamarine_axolotls.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

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
	void editNodeIDFails() {
		Map<String, String> newNode = new HashMap<String, String>();
		newNode.put("NODEID", "Test");

		assertThrows(SQLException.class, () -> db.editNode("CCONF001L1", newNode));
	}

	@Test
	void editNodeSomeValues() {
		try {
			Map<String, String> before = db.getNode("CCONF001L1");
			assertEquals(before.get("XCOORD"),"2255");
			assertEquals(before.get("FLOOR"),"L1");
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

		Map<String, String> newNode = new HashMap<String, String>();
		newNode.put("XCOORD", "13");
		newNode.put("FLOOR", "2");

		try{
			db.editNode("CCONF001L1", newNode);
			Map<String, String> editted = db.getNode("CCONF001L1");
			assertEquals(editted.get("XCOORD"), "13"); // changed value
			assertEquals(editted.get("FLOOR"), "2"); // changed value
		}
		catch(SQLException e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void connectedEdges() {
		Map<String,String> newNode1 = new HashMap<String,String>();
		newNode1.put("NODEID","TEST1");
		newNode1.put("XCOORD","420");
		newNode1.put("YCOORD","69");

		Map<String,String> newNode2 = new HashMap<String,String>();
		newNode2.put("NODEID","TEST2");
		newNode2.put("XCOORD","69");
		newNode2.put("YCOORD","420");

		Map<String,String> newNode3 = new HashMap<String,String>();
		newNode3.put("NODEID","TEST3");
		newNode3.put("XCOORD","100");
		newNode3.put("YCOORD","100");

		Map<String,String> newEdge1 = new HashMap<String,String>();
		newEdge1.put("EDGEID","TEST1_TEST2");
		newEdge1.put("STARTNODE","TEST1");
		newEdge1.put("ENDNODE","TEST2");

		Map<String,String> newEdge2 = new HashMap<String,String>();
		newEdge2.put("EDGEID","TEST1_TEST3");
		newEdge2.put("STARTNODE","TEST1");
		newEdge2.put("ENDNODE","TEST3");
		try {
			db.addNode(newNode1);
			db.addNode(newNode2);
			db.addNode(newNode3);
			db.addEdge(newEdge1);
			db.addEdge(newEdge2);

			List<Map<String,String>> edges = db.getEdgesConnectedToNode("TEST1");
			assertEquals(edges.get(0), newEdge1);
			assertEquals(edges.get(1), newEdge2);
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void editEdgeChangeStart() {
		Map<String, String> newEdge = new HashMap<String, String>();
		newEdge.put("EDGEID", "WELEV00HL1_CHALL002L1");
		newEdge.put("STARTNODE", "WELEV00HL1");
		newEdge.put("ENDNODE", "CHALL002L1");

		try{
			db.editEdge("CCONF002L1_WELEV00HL1", newEdge);
			assertTrue(db.edgeExists("WELEV00HL1_CHALL002L1"));
			assertFalse(db.edgeExists("CCONF002L1_WELEV00HL1"));

			Map<String, String> edge = db.getEdge("WELEV00HL1_CHALL002L1");
			assertEquals(edge.get("STARTNODE"), "WELEV00HL1");
			assertEquals(edge.get("ENDNODE"), "CHALL002L1");
		}
		catch(SQLException e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void deleteEdgeExists() {
		try{
			db.deleteEdge("CDEPT004L1_CHALL002L1");
			assertFalse(db.edgeExists("CDEPT004L1_CHALL002L1"));
		}
		catch(SQLException e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void cascadeDownTest() throws SQLException {
		assertTrue(db.edgeExists("CCONF002L1_WELEV00HL1"));
		db.deleteNode("CCONF002L1");
		assertFalse(db.edgeExists("CCONF002L1_WELEV00HL1"));
	}

	@Test
	void cascadeUpTest() throws SQLException {
		assertTrue(db.nodeExists("CCONF002L1"));
		db.deleteEdge("CCONF002L1_WELEV00HL1");
		assertTrue(db.nodeExists("CCONF002L1"));
	}
}