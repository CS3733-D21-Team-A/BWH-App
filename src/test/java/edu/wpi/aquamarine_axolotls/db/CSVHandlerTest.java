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
	void resetDB() throws IOException, SQLException, URISyntaxException {
		db.emptyEdgeTable();
		db.emptyNodeTable();

		nodeStream = DatabaseInfo.resourceAsStream(DatabaseInfo.nodeResourcePath);
		edgeStream = DatabaseInfo.resourceAsStream(DatabaseInfo.edgeResourcePath);

		csvHandler = new CSVHandler(db);
		csvHandler.importCSV(nodeStream, DatabaseInfo.TABLES.NODES);
		csvHandler.importCSV(edgeStream, DatabaseInfo.TABLES.EDGES);

		nodeStream = DatabaseInfo.resourceAsStream(DatabaseInfo.nodeResourcePath);
		edgeStream = DatabaseInfo.resourceAsStream(DatabaseInfo.edgeResourcePath);
	}

	@AfterAll
	@BeforeAll
	static void cleanup() {
		TestUtil.resetDB();
	}


	@Test
	void importEdgesTest() throws SQLException, IOException {
		assertNotNull(db.getEdges());
		db.emptyEdgeTable();
		assertNull(db.getEdges());
		csvHandler.importCSV(edgeStream, DatabaseInfo.TABLES.EDGES);
		assertNotNull(db.getEdges());
	}

	@Test
	void importNodesTest() throws SQLException, IOException {
		assertNotNull(db.getNodes());
		db.emptyNodeTable();
		assertNull(db.getNodes());
		csvHandler.importCSV(nodeStream, DatabaseInfo.TABLES.NODES);
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

			csvHandler.exportCSV(file, DatabaseInfo.TABLES.EDGES);

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

			csvHandler.exportCSV(file, DatabaseInfo.TABLES.NODES);

			assertNotEquals(file.length(), 0);
			assertTrue(file.delete());
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

}
