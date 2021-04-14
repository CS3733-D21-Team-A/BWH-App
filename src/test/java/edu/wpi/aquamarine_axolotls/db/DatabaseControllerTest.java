package edu.wpi.aquamarine_axolotls.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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
			assertTrue(db.nodeExists("aPARK020GG"));
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
	void addNode() {
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
			assertFalse(db.nodeExists("Test1"));
			db.addNode(newNode);
			assertTrue(db.nodeExists("Test1"));
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
		newNode.put("NODEID", "aPARK023GG");

		assertThrows(SQLException.class, () -> db.addNode(newNode));
	}

	@Test
	void editNodeIDFails() {
		Map<String, String> newNode = new HashMap<String, String>();
		newNode.put("NODEID", "Test");

		assertThrows(SQLException.class, () -> db.editNode("aWALK011GG", newNode));
	}

	@Test
	void editNode() {
		try {
			Map<String, String> before = db.getNode("aPARK019GG");
			assertEquals(before.get("XCOORD"),"195");
			assertEquals(before.get("FLOOR"),"G");
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

		Map<String, String> newNode = new HashMap<String, String>();
		newNode.put("XCOORD", "13");
		newNode.put("YCOORD", "200");
		newNode.put("FLOOR", "2");
		newNode.put("BUILDING", "2");

		try{
			db.editNode("aPARK019GG", newNode);
			Map<String, String> editted = db.getNode("aPARK019GG");
			assertEquals(editted.get("XCOORD"), "13"); // changed value
			assertEquals(editted.get("FLOOR"), "2"); // changed value
		}
		catch(SQLException e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void deleteNode() {
		try {
			db.deleteNode("aEXIT00101");
			assertFalse(db.nodeExists("aEXIT00101"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void deleteNodeDNE() {
		try {
			assertFalse(db.nodeExists("Test1"));
			db.deleteNode("Test1");
			assertFalse(db.nodeExists("Test1"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void getNode() {
		try {
			Map<String, String> node = db.getNode("aPARK009GG");
			assertTrue(node.get("XCOORD").equals("3390"));
			assertTrue(node.get("YCOORD").equals("1207"));
			assertTrue(node.get("FLOOR").equals("G"));
			assertTrue(node.get("BUILDING").equals("Parking"));
			assertTrue(node.get("NODETYPE").equals("PARK"));
			assertTrue(node.get("LONGNAME").equals("Parking Spot 9 45 Francis Street Lobby"));
			assertTrue(node.get("SHORTNAME").equals("45FSL09"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void getNodeDNE() {
		try {
			assertEquals(db.getNode("Test1"), null);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Test
	void emptyNodeTable() {
		try {
			List<Map<String, String>> nodes = db.getNodes();
			assertTrue(nodes.size() != 0);
			db.emptyNodeTable();
			nodes = db.getNodes();
			assertEquals(nodes, null);

		} catch (SQLException e) {
			e.printStackTrace();
		}


	}


	@Test
	void edgeColumnValidNames() {
		Set<String> colName = db.getEdgeColumns().keySet();
		assertTrue(colName.contains("EDGEID"));
		assertTrue(colName.contains("STARTNODE"));
		assertTrue(colName.contains("ENDNODE"));
	}

	@Test
	void edgeColumnValidTypes() {
		Map<String, Boolean> colName = db.getEdgeColumns();
		assertTrue(colName.get("EDGEID")); // String
		assertTrue(colName.get("STARTNODE")); // not String
		assertTrue(colName.get("ENDNODE"));
	}

	@Test
	void edgeExists() {
		try {
			assertTrue(db.edgeExists("aWALK002GG_aWALK003GG"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void edgeExistsDNE() {
		try {
			assertFalse(db.edgeExists("aWALK002GG_aWALK043GG"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void addEdge() {

		try {
			assertFalse(db.edgeExists("helloworld"));

			Map<String, String> newEdge = new HashMap<String, String>();
			newEdge.put("EDGEID","helloworld");
			newEdge.put("STARTNODE", "aPARK024GG");
			newEdge.put("ENDNODE", "aWALK012GG");

			db.addEdge(newEdge);
			assertTrue(db.edgeExists("helloworld"));

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Test
	void addEdgeDupEdge() {

		try {
			assertTrue(db.edgeExists("aPARK001GG_aWALK001GG"));

			Map<String, String> newEdge = new HashMap<String, String>();
			newEdge.put("EDGEID","aPARK001GG_aWALK001GG");
			newEdge.put("STARTNODE", "aPARK001GG");
			newEdge.put("ENDNODE", "aWALK001GG");

			assertThrows(SQLException.class, () -> db.addEdge(newEdge));

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Test
	void deleteEdge() {
		try {
			db.deleteEdge("aPARK022GG_aWALK009GG");
			assertFalse(db.edgeExists("aPARK022GG_aWALK009GG"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void deleteEdgeDNE() {
		try {
			assertFalse(db.edgeExists("Test1_Test2"));
			db.deleteEdge("Test1_Test2");
			assertFalse(db.edgeExists("Test1_Test2"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void editEdge() {
		try {
			Map<String, String> before = db.getEdge("aWALK004GG_aEXIT00201");
			assertEquals(before.get("STARTNODE"),"aWALK004GG");
			assertEquals(before.get("ENDNODE"),"aEXIT00201");
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

		Map<String, String> newEdge = new HashMap<String, String>();
		newEdge.put("EDGEID","title");
		newEdge.put("STARTNODE", "aPARK009GG");
		newEdge.put("ENDNODE", "aEXIT00201");

		try{
			db.editEdge("aWALK004GG_aEXIT00201", newEdge);
			assertTrue(db.edgeExists("title"));
			assertFalse(db.edgeExists("aWALK004GG_aEXIT00201"));

			Map<String, String> edited = db.getEdge("title");
			assertEquals(edited.get("STARTNODE"), "aPARK009GG"); // changed value
			assertEquals(edited.get("ENDNODE"), "aEXIT00201"); // changed value
		}
		catch(SQLException e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void addEditEdge() {
		try {
			Map<String, String> newEdge = new HashMap<String, String>();
			newEdge.put("EDGEID","title");
			newEdge.put("STARTNODE", "aPARK009GG");
			newEdge.put("ENDNODE", "aEXIT00201");
			db.addEdge(newEdge);
			assertTrue(db.edgeExists("title"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

		Map<String, String> newEdit = new HashMap<String, String>();
		newEdit.put("EDGEID","title2");
		newEdit.put("ENDNODE", "aEXIT00101");

		try{
			db.editEdge("title", newEdit);
			assertTrue(db.edgeExists("title2"));
			assertFalse(db.edgeExists("title"));

			Map<String, String> edited = db.getEdge("title2");
			assertEquals(edited.get("STARTNODE"), "aPARK009GG"); // changed value
			assertEquals(edited.get("ENDNODE"), "aEXIT00101"); // changed value
		}
		catch(SQLException e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void getEdge() {
		try {
			Map<String, String> edge = db.getEdge("aPARK010GG_aWALK012GG");
			assertTrue(edge.get("STARTNODE").equals("aPARK010GG"));
			assertTrue(edge.get("ENDNODE").equals("aWALK012GG"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void getEdgeDNE() {
		try {
			assertEquals(db.getEdge("Test1_Test5"), null);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Test
	void emptyEdgeTable() {
		try {
			List<Map<String, String>> edges = db.getEdges();
			assertTrue(edges.size() != 0);
			db.emptyNodeTable();
			edges = db.getEdges();
			assertEquals(edges, null);

		} catch (SQLException e) {
			e.printStackTrace();
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
			assertTrue(edges.size() == 2);
			assertTrue(edges.get(0).get("EDGEID").equals("TEST1_TEST2"));
			assertTrue(edges.get(1).get("EDGEID").equals("TEST1_TEST3"));

		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
}