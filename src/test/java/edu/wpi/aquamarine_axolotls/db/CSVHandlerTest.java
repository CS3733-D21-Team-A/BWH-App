package edu.wpi.aquamarine_axolotls.db;

import edu.wpi.aquamarine_axolotls.TestUtil;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class CSVHandlerTest {
	private final DatabaseController db = new DatabaseController();
	private CSVHandler csvHandler;
	private InputStream nodeStream;
	private InputStream edgeStream;

	public CSVHandlerTest() throws URISyntaxException, SQLException, IOException {}

	@BeforeEach
	void resetDB() throws IOException, SQLException {
		db.emptyEdgeTable();
		db.emptyNodeTable();

		nodeStream = DatabaseUtil.resourceAsStream(DatabaseInfo.DEFAULT_NODE_RESOURCE_PATH);
		edgeStream = DatabaseUtil.resourceAsStream(DatabaseInfo.DEFAULT_EDGE_RESOURCE_PATH);

		csvHandler = new CSVHandler(db);
		csvHandler.importCSV(nodeStream, TABLES.NODES, true);
		csvHandler.importCSV(edgeStream, TABLES.EDGES, true);

		nodeStream = DatabaseUtil.resourceAsStream(DatabaseInfo.DEFAULT_NODE_RESOURCE_PATH);
		edgeStream = DatabaseUtil.resourceAsStream(DatabaseInfo.DEFAULT_EDGE_RESOURCE_PATH);
	}

	@AfterAll
	@BeforeAll
	static void cleanup() {
		TestUtil.resetDB();
	}


	@Test
	void importEdgesTest() throws SQLException, IOException {
		db.emptyEdgeTable();
		assertEquals(0, db.getEdges().size());
		csvHandler.importCSV(edgeStream, TABLES.EDGES, true);
		assertNotNull(db.getEdges());
	}

	@Test
	void importNodesTest() throws SQLException, IOException {
		db.emptyNodeTable();
		assertEquals(0, db.getNodes().size());
		csvHandler.importCSV(nodeStream, TABLES.NODES, true);
		assertNotNull(db.getNodes());
	}

	@Test
	void exportEdgesTest() {
		try {
			File file = new File("edgeTest.csv");
			if (file.exists()) {
				assertTrue(file.delete());
			}
			assertTrue(file.createNewFile());
			assertEquals(file.length(), 0);

			csvHandler.exportCSV(file, TABLES.EDGES);

			assertNotEquals(file.length(), 0);
			assertTrue(file.delete());
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void exportNodesTest() {
		try {
			File file = new File("nodeTest.csv");
			if (file.exists()) {
				assertTrue(file.delete());
			}
			assertTrue(file.createNewFile());
			assertEquals(file.length(), 0);

			csvHandler.exportCSV(file, TABLES.NODES);

			assertNotEquals(file.length(), 0);
			assertTrue(file.delete());
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

}
