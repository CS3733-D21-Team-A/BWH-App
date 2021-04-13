package edu.wpi.aquamarine_axolotls.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class CSVHandlerTest {
	private final DatabaseController db = new DatabaseController();
	private final CSVHandler csvHandler = new CSVHandler(db);
	private final File nodeFile = DatabaseInfo.resourcePathToFile(DatabaseInfo.nodeResourcePath);
	private final File edgeFile = DatabaseInfo.resourcePathToFile(DatabaseInfo.edgeResourcePath);

	public CSVHandlerTest() throws URISyntaxException, SQLException, IOException {}

	@BeforeEach
	void resetDB() throws IOException, SQLException {
		db.emptyEdgeTable();
		db.emptyNodeTable();
		csvHandler.importCSV(nodeFile, DatabaseInfo.TABLES.NODES);
		csvHandler.importCSV(edgeFile, DatabaseInfo.TABLES.EDGES);
	}

	@Test
	void importEdgesTest() throws SQLException, IOException {
		assertNotNull(db.getEdges());
		db.emptyEdgeTable();
		assertNull(db.getEdges());
		csvHandler.importCSV(edgeFile, DatabaseInfo.TABLES.EDGES);
		assertNotNull(db.getEdges());
	}

	@Test
	void exportEdgesTest() {
		try {
			File file = new File("edgeTest.csv");
			file.delete();
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
			file.delete();
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
