//package edu.wpi.aquamarine_axolotls.db;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class DatabaseControllerTest {
//	DatabaseController db = new DatabaseController();
//	CSVHandler csvHandler = new CSVHandler(db);
//
//	@BeforeEach
//	void resetDB() throws URISyntaxException, IOException, SQLException {
//		db.emptyEdgeTable();
//		db.emptyNodeTable();
//		csvHandler.importCSV(new File(getClass().getClassLoader().getResource("edu/wpi/aquamarine_axolotls/csv/L1Nodes.csv").toURI()), DatabaseInfo.TABLES.NODES);
//		csvHandler.importCSV(new File(getClass().getClassLoader().getResource("edu/wpi/aquamarine_axolotls/csv/L1Edges.csv").toURI()), DatabaseInfo.TABLES.EDGES);
//	}
//
//	@Test
//	void nodeDoesntExist() {
//		try {
//			assertFalse(db.nodeExists("foobar"));
//		} catch (SQLException e) {
//			e.printStackTrace();
//			fail();
//		}
//	}
//
//	@Test
//	void nodeDoesExist() {
//		try {
//			assertTrue(db.nodeExists("WELEV00ML1"));
//		} catch (SQLException e) {
//			e.printStackTrace();
//			fail();
//		}
//	}
//
//	@Test
//	void nodeColumnValidNames() {
//		Set<String> colName = db.getNodeColumns().keySet();
//		assertTrue(colName.contains("NODEID"));
//		assertTrue(colName.contains("XCOORD"));
//		assertTrue(colName.contains("YCOORD"));
//		assertTrue(colName.contains("FLOOR"));
//		assertTrue(colName.contains("BUILDING"));
//		assertTrue(colName.contains("NODETYPE"));
//		assertTrue(colName.contains("LONGNAME"));
//		assertTrue(colName.contains("SHORTNAME"));
//	}
//
//	@Test
//	void nodeColumnValidTypes() {
//		Map<String, Boolean> colName = db.getNodeColumns();
//		assertTrue(colName.get("NODEID")); // String
//		assertFalse(colName.get("XCOORD")); // not String
//		assertFalse(colName.get("YCOORD"));
//		assertTrue(colName.get("FLOOR"));
//		assertTrue(colName.get("BUILDING"));
//		assertTrue(colName.get("NODETYPE"));
//		assertTrue(colName.get("LONGNAME"));
//		assertTrue(colName.get("SHORTNAME"));
//	}
//
//	@Test
//	void getNode(){
//		try{
//			Map<String, String> node = db.getNode("CLABS003L1");
//			assertEquals(node.get("XCOORD"), "2290");
//			assertEquals(node.get("YCOORD"), "1284");
//			assertEquals(node.get("FLOOR"), "L1");
//			assertEquals(node.get("BUILDING"), "45 Francis");
//			assertEquals(node.get("NODETYPE"), "LABS");
//			assertEquals(node.get("LONGNAME"), "Nuclear Medicine Floor L1");
//			assertEquals(node.get("SHORTNAME"), "Lab C003L1");
//
//
//		}catch(SQLException e){
//			e.printStackTrace();
//			fail();
//		}
//
//
//	}
//
//	@Test
//	void addNodeAllValues() {
//		Map<String, String> newNode = new HashMap<String, String>();
//		newNode.put("NODEID", "Test1");
//		newNode.put("XCOORD", "12");
//		newNode.put("YCOORD", "300");
//		newNode.put("FLOOR", "G");
//		newNode.put("BUILDING", "Mars");
//		newNode.put("NODETYPE", "EXIT");
//		newNode.put("LONGNAME", "Its a made up place!");
//		newNode.put("SHORTNAME", "MRS");
//		try{
//			db.addNode(newNode);
//			assertTrue(db.nodeExists("Test1"));
//		}
//		catch(SQLException e){
//			e.printStackTrace();
//			fail();
//		}
//	}
//
//	@Test
//	void addNodeSomeValues() {
//		Map<String, String> newNode = new HashMap<String, String>();
//		newNode.put("NODEID", "Test2");
//		newNode.put("XCOORD", "20");
//		newNode.put("BUILDING", "FS");
//		newNode.put("NODETYPE", "BUILD");
//		newNode.put("SHORTNAME", "MRS");
//		try{
//			db.addNode(newNode);
//			assertTrue(db.nodeExists("Test2"));
//		}
//		catch(SQLException e){
//			e.printStackTrace();
//			fail();
//		}
//	}
//
//	@Test
//	void editNodeAllValues() {
//		Map<String, String> newNode = new HashMap<String, String>();
//		newNode.put("XCOORD", "1982");
//		newNode.put("YCOORD", "845");
//		newNode.put("FLOOR", "L2");
//		newNode.put("BUILDING", "Tower2");
//		newNode.put("NODETYPE", "DEPT2");
//		newNode.put("LONGNAME", "Its a made up place!");
//		newNode.put("SHORTNAME", "HR C002L1");
//
//
//		try{
//			db.editNode("CDEPT002L1", newNode);
//			Map<String, String> editted = db.getNode("CDEPT002L1");
//			assertEquals(editted.get("XCOORD"), "1982");
//			assertEquals(editted.get("YCOORD"), "845");
//			assertEquals(editted.get("FLOOR"), "L2");
//			assertEquals(editted.get("BUILDING"), "Tower2");
//			assertEquals(editted.get("NODETYPE"), "DEPT2");
//			assertEquals(editted.get("LONGNAME"), "Its a made up place!");
//			assertEquals(editted.get("SHORTNAME"), "HR C002L1");
//
//		}
//		catch(SQLException e){
//			e.printStackTrace();
//			fail();
//		}
//	}
//
//	@Test
//	void editNodeSomeValues() {
//		Map<String, String> newNode = new HashMap<String, String>();
//		newNode.put("XCOORD", "1700");
//		newNode.put("YCOORD", "900");
//
//
//		try{
//			db.editNode("CHALL001L1", newNode);
//			Map<String, String> editted = db.getNode("CHALL001L1");
//			assertEquals(editted.get("XCOORD"), "1700");
//			assertEquals(editted.get("YCOORD"), "900");
//		}
//		catch(SQLException e){
//			e.printStackTrace();
//			fail();
//		}
//	}
//
//	@Test
//	void deleteNode() {
//		try{
//			db.deleteNode("WELEV00JL1");
//			assertFalse(db.nodeExists("WELEV00JL1"));
//		}
//		catch(SQLException e){
//			e.printStackTrace();
//			fail();
//		}
//	}
///*
//	@Test
//	void getNodesALL(){
//		// TODO : how does one test this?
//	}
//*/
//	@Test
//	void emptyNodeTable(){
//		try{
//			db.emptyNodeTable();
//			assertTrue(db.getNodes().size() == 0);
//		}catch(SQLException e){
//			e.printStackTrace();
//			fail();
//		}
//		// TODO : what int will it return?
//	}
//	// Testing Edges
//	@Test
//	void edgeDoesntExist() {
//		try {
//			assertFalse(db.edgeExists("foobar"));
//		} catch (SQLException e) {
//			e.printStackTrace();
//			fail();
//		}
//	}
//
//	@Test
//	void edgeDoesExist() {
//		try {
//			assertTrue(db.edgeExists("CHALL009L1_CRETL001L1"));
//		} catch (SQLException e) {
//			e.printStackTrace();
//			fail();
//		}
//	}
//
//	@Test
//	void edgeColumnValidNames() {
//		Set<String> colName = db.getEdgeColumns().keySet();
//		assertTrue(colName.contains("EDGEID"));
//		assertTrue(colName.contains("STARTNODE"));
//		assertTrue(colName.contains("ENDNODE"));
//	}
//
//	@Test
//	void getEdge() {
//		try {
//			Map<String, String> edge = db.getEdge("CLABS002L1_CREST001L1");
//			assertEquals(edge.get("EDGEID"), "CLABS002L1_CREST001L1");
//			assertEquals(edge.get("STARTNODE"), "CLABS002L1");
//			assertEquals(edge.get("ENDNODE"), "CREST001L1");
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//			fail();
//		}
//	}
//
//
//
//	@Test
//	void addEdgeAllValues() { // must have all values since edgeID is a pkey and start/end are foreign keys
//		Map<String, String> newEdge = new HashMap<String, String>();
//		newEdge.put("EDGEID", "CCONF002L1_GHALL005L1");
//		newEdge.put("STARTNODE", "CCONF002L1");
//		newEdge.put("ENDNODE", "GHALL005L1");
//
//		try{
//			db.addEdge(newEdge);
//			assertTrue(db.edgeExists("CCONF002L1_GHALL005L1"));
//		}
//		catch(SQLException e){
//			e.printStackTrace();
//			fail();
//		}
//	}
//
//
//	@Test
//	void editEdgeChangeEnd() {
//		Map<String, String> newEdge = new HashMap<String, String>();
//		newEdge.put("EDGEID", "CCONF002L1_CHALL002L1"); // wouldnt make sense without this?
//		newEdge.put("STARTNODE", "CCONF002L1");
//		newEdge.put("ENDNODE", "CHALL002L1");
//
//		try{
//			db.editEdge("CCONF002L1_WELEV00HL1", newEdge);
//			assertTrue(db.edgeExists("CCONF002L1_CHALL002L1"));
//			assertFalse(db.edgeExists("CCONF002L1_WELEV00HL1"));
//
//			Map<String, String> edge = db.getEdge("CCONF002L1_CHALL002L1");
//
//			assertEquals(edge.get("STARTNODE"), "CCONF002L1");
//			assertEquals(edge.get("ENDNODE"), "CHALL002L1");
//		}
//		catch(SQLException e){
//			e.printStackTrace();
//			fail();
//		}
//	}
//
//	@Test
//	void editEdgeChangeStart() {
//		Map<String, String> newEdge = new HashMap<String, String>();
//		newEdge.put("EDGEID", "WELEV00HL1_CHALL002L1");
//		newEdge.put("STARTNODE", "WELEV00HL1");
//		newEdge.put("ENDNODE", "CHALL002L1");
//
//		try{
//			db.editEdge("CCONF002L1_WELEV00HL1", newEdge);
//			assertTrue(db.edgeExists("WELEV00HL1_CHALL002L1"));
//			assertFalse(db.edgeExists("CCONF002L1_WELEV00HL1"));
//
//			Map<String, String> edge = db.getEdge("WELEV00HL1_CHALL002L1");
//			assertEquals(edge.get("STARTNODE"), "WELEV00HL1");
//			assertEquals(edge.get("ENDNODE"), "CHALL002L1");
//		}
//		catch(SQLException e){
//			e.printStackTrace();
//			fail();
//		}
//	}
//
//	@Test
//	void deleteEdgeExists() {
//		try{
//			db.deleteEdge("CDEPT004L1_CHALL002L1");
//			assertFalse(db.edgeExists("CDEPT004L1_CHALL002L1"));
//		}
//		catch(SQLException e){
//			e.printStackTrace();
//			fail();
//		}
//	}
//
///*
//	@Test
//	void getEdgesALL(){
//		// TODO : how does one test this?
//
//	}
//*/
//	@Test
//	void emptyEdgeTable(){
//		try{
//			db.emptyEdgeTable();
//			assertTrue(db.getEdges().size() == 0);
//		}catch(SQLException e){
//			e.printStackTrace();
//			fail();
//		}
//
//	}
//
//
//	@Test
//	void getEdgesConnectedToNode() {
//		try{
//			List<Map<String,String>> connected = db.getEdgesConnectedToNode("CREST003L1");
//			for(Map<String, String> node : connected){
//				String id = node.get("NODEID");
//				assertTrue(id.equals("CHALL015L1")
//						|| id.equals("CHALL010L1"));
//			}
//		}
//		catch(SQLException e){
//			e.printStackTrace();
//			fail();
//		}
//	}
//
//
//
//
//}