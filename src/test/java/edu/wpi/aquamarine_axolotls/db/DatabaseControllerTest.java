package edu.wpi.aquamarine_axolotls.db;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseControllerTest {
	private DatabaseController db;
	private final File nodeFile = DatabaseInfo.resourcePathToFile(DatabaseInfo.nodeResourcePath);
	private final File edgeFile = DatabaseInfo.resourcePathToFile(DatabaseInfo.edgeResourcePath);

	DatabaseControllerTest() throws URISyntaxException {}

	@BeforeEach
	void resetDB() throws IOException, SQLException, URISyntaxException {
		db = new DatabaseController();
		db.emptyEdgeTable();
		db.emptyNodeTable();

		CSVHandler csvHandler = new CSVHandler(db);
		csvHandler.importCSV(nodeFile, DatabaseInfo.TABLES.NODES);
		csvHandler.importCSV(edgeFile, DatabaseInfo.TABLES.EDGES);
	}

	@AfterEach
	void closeDB() {
		try {
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@AfterAll
	static void shutdownDB() {
		assertTrue(DatabaseController.shutdownDB());
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
		Map<String,Boolean> colName = db.getNodeColumns();
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
		Map<String,String> newNode = new HashMap<>();
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
	void addNodeNotAllValues() {
		Map<String,String> newNode = new HashMap<>();
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
		Map<String,String> newNode = new HashMap<>();
		newNode.put("XCOORD", "20");
		newNode.put("BUILDING", "Empire State");
		newNode.put("NODETYPE", "BUILDING");
		newNode.put("SHORTNAME", "MRS");
		assertThrows(SQLException.class, () -> db.addNode(newNode));
	}

	@Test
	void addNodeDupKeys() {
		Map<String,String> newNode = new HashMap<>();
		newNode.put("NODEID", "aPARK023GG");

		assertThrows(SQLException.class, () -> db.addNode(newNode));
	}

	@Test
	void editNodeIDFails() {
		Map<String,String> newNode = new HashMap<>();
		newNode.put("NODEID", "Test");

		assertThrows(SQLException.class, () -> db.editNode("aWALK011GG", newNode));
	}

	@Test
	void editNode() {
		try {
			Map<String,String> before = db.getNode("aPARK019GG");
			assertEquals("195", before.get("XCOORD"));
			assertEquals("G", before.get("FLOOR"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

		Map<String,String> newNode = new HashMap<>();
		newNode.put("XCOORD", "13");
		newNode.put("FLOOR", "2");

		try{
			db.editNode("aPARK019GG", newNode);
			Map<String,String> editted = db.getNode("aPARK019GG");
			assertEquals("13", editted.get("XCOORD")); // changed value
			assertEquals("2", editted.get("FLOOR")); // changed value
		}
		catch(SQLException e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void deleteNode() {
		try {
			assertTrue(db.nodeExists("aEXIT00101"));
			db.deleteNode("aEXIT00101");
			assertFalse(db.nodeExists("aEXIT00101"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
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
			fail();
		}
	}

	@Test
	void getNode() {
		try {
			Map<String,String> node = db.getNode("aPARK009GG");
			assertEquals("3390", node.get("XCOORD"));
			assertEquals("1207", node.get("YCOORD"));
			assertEquals("G", node.get("FLOOR"));
			assertEquals("Parking", node.get("BUILDING"));
			assertEquals("PARK", node.get("NODETYPE"));
			assertEquals("Parking Spot 9 45 Francis Street Lobby", node.get("LONGNAME"));
			assertEquals("45FSL09", node.get("SHORTNAME"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void getNodeDNE() {
		try {
			assertNull(db.getNode("Test1"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	void emptyNodeTable() {
		try {
			assertTrue(db.getNodes().size() != 0);
			db.emptyNodeTable();
			assertNull(db.getNodes());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
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
			fail();
		}
	}

	@Test
	void edgeExistsDNE() {
		try {
			assertFalse(db.edgeExists("aWALK002GG_aWALK043GG"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void addEdge() {
		try {
			assertFalse(db.edgeExists("helloworld"));

			Map<String,String> newEdge = new HashMap<>();
			newEdge.put("EDGEID","helloworld");
			newEdge.put("STARTNODE", "aPARK024GG");
			newEdge.put("ENDNODE", "aWALK012GG");

			db.addEdge(newEdge);
			assertTrue(db.edgeExists("helloworld"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	void addEdgeDupEdge() {
		try {
			assertTrue(db.edgeExists("aPARK001GG_aWALK001GG"));

			Map<String,String> newEdge = new HashMap<>();
			newEdge.put("EDGEID","aPARK001GG_aWALK001GG");
			newEdge.put("STARTNODE", "aPARK001GG");
			newEdge.put("ENDNODE", "aWALK001GG");

			assertThrows(SQLException.class, () -> db.addEdge(newEdge));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	void deleteEdge() {
		try {
			assertTrue(db.edgeExists("aPARK022GG_aWALK009GG"));
			db.deleteEdge("aPARK022GG_aWALK009GG");
			assertFalse(db.edgeExists("aPARK022GG_aWALK009GG"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
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
			fail();
		}
	}

	@Test
	void editEdge() {
		try {
			Map<String,String> before = db.getEdge("aWALK004GG_aEXIT00201");
			assertEquals("aWALK004GG", before.get("STARTNODE"));
			assertEquals("aEXIT00201", before.get("ENDNODE"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

		Map<String,String> newEdge = new HashMap<>();
		newEdge.put("EDGEID","title");
		newEdge.put("STARTNODE", "aPARK009GG");
		newEdge.put("ENDNODE", "aEXIT00201");

		try{
			db.editEdge("aWALK004GG_aEXIT00201", newEdge);
			assertTrue(db.edgeExists("title"));
			assertFalse(db.edgeExists("aWALK004GG_aEXIT00201"));

			Map<String,String> edited = db.getEdge("title");
			assertEquals("aPARK009GG", edited.get("STARTNODE")); // changed value
			assertEquals("aEXIT00201", edited.get("ENDNODE")); // changed value
		}
		catch(SQLException e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void addEditEdge() {
		try {
			Map<String,String> newEdge = new HashMap<>();
			newEdge.put("EDGEID","title");
			newEdge.put("STARTNODE", "aPARK009GG");
			newEdge.put("ENDNODE", "aEXIT00201");
			db.addEdge(newEdge);
			assertTrue(db.edgeExists("title"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

		Map<String,String> newEdit = new HashMap<>();
		newEdit.put("EDGEID","title2");
		newEdit.put("ENDNODE", "aEXIT00101");

		try{
			db.editEdge("title", newEdit);
			assertTrue(db.edgeExists("title2"));
			assertFalse(db.edgeExists("title"));

			Map<String,String> edited = db.getEdge("title2");
			assertEquals("aPARK009GG", edited.get("STARTNODE")); // changed value
			assertEquals("aEXIT00101", edited.get("ENDNODE")); // changed value
		}
		catch(SQLException e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void getEdge() {
		try {
			Map<String,String> edge = db.getEdge("aPARK010GG_aWALK012GG");
			assertEquals("aPARK010GG", edge.get("STARTNODE"));
			assertEquals("aWALK012GG", edge.get("ENDNODE"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void getEdgeDNE() {
		try {
			assertNull(db.getEdge("Test1_Test5"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	void emptyEdgeTable() {
		try {
			List<Map<String,String>> edges = db.getEdges();
			assertTrue(edges.size() != 0);
			db.emptyNodeTable();
			assertNull(db.getEdges());
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}


	}

	@Test
	void connectedEdges() {
		Map<String,String> newNode1 = new HashMap<>();
		newNode1.put("NODEID","TEST1");
		newNode1.put("XCOORD","420");
		newNode1.put("YCOORD","69");

		Map<String,String> newNode2 = new HashMap<>();
		newNode2.put("NODEID","TEST2");
		newNode2.put("XCOORD","69");
		newNode2.put("YCOORD","420");

		Map<String,String> newNode3 = new HashMap<>();
		newNode3.put("NODEID","TEST3");
		newNode3.put("XCOORD","100");
		newNode3.put("YCOORD","100");

		Map<String,String> newEdge1 = new HashMap<>();
		newEdge1.put("EDGEID","TEST1_TEST2");
		newEdge1.put("STARTNODE","TEST1");
		newEdge1.put("ENDNODE","TEST2");

		Map<String,String> newEdge2 = new HashMap<>();
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
			assertEquals(2, edges.size());
			assertEquals("TEST1_TEST2", edges.get(0).get("EDGEID"));
			assertEquals("TEST1_TEST3", edges.get(1).get("EDGEID"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void editEdgeChangeStart() {
		Map<String,String> newEdge = new HashMap<>();
		newEdge.put("STARTNODE", "aWALK001GG");
		newEdge.put("ENDNODE", "aPARK001GG");

		try {
			Map<String,String> edge = db.getEdge("aPARK001GG_aWALK001GG");
			assertEquals("aPARK001GG", edge.get("STARTNODE"));
			assertEquals("aWALK001GG", edge.get("ENDNODE"));

			db.editEdge("aPARK001GG_aWALK001GG", newEdge);
			edge = db.getEdge("aPARK001GG_aWALK001GG");
			assertEquals("aWALK001GG", edge.get("STARTNODE"));
			assertEquals("aPARK001GG", edge.get("ENDNODE"));
		} catch(SQLException e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void deleteEdgeExists() {
		try {
			assertTrue(db.edgeExists("aPARK001GG_aWALK001GG"));
			db.deleteEdge("aPARK001GG_aWALK001GG");
			assertFalse(db.edgeExists("aPARK001GG_aWALK001GG"));
		} catch (SQLException e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void cascadeDownTest() {
		try {
			assertTrue(db.edgeExists("aPARK001GG_aWALK001GG"));
			db.deleteNode("aPARK001GG");
			assertFalse(db.edgeExists("aPARK001GG_aWALK001GG"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void noCascadeUp() {
		try {
			assertTrue(db.nodeExists("aPARK001GG"));
			db.deleteEdge("aPARK001GG_aWALK001GG");
			assertTrue(db.nodeExists("aPARK001GG"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	//TODO: TEST EDITING PRIMARY AND FOREIGN KEYS MORE
}