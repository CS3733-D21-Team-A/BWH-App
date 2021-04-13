package edu.wpi.aquamarine_axolotls.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
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
