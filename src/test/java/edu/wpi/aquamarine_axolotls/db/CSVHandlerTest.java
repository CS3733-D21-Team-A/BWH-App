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
	private final File nodeFile;
	private final File edgeFile;

	public CSVHandlerTest() throws URISyntaxException {
		nodeFile = new File(getClass().getClassLoader().getResource("edu/wpi/aquamarine_axolotls/csv/L1Nodes.csv").toURI());
		edgeFile = new File(getClass().getClassLoader().getResource("edu/wpi/aquamarine_axolotls/csv/L1Edges.csv").toURI());
	}

	@BeforeEach
	void resetDB() throws URISyntaxException, IOException, SQLException {
		db.emptyEdgeTable();
		db.emptyNodeTable();
		csvHandler.importCSV(nodeFile, DatabaseInfo.TABLES.NODES);
		csvHandler.importCSV(edgeFile, DatabaseInfo.TABLES.EDGES);
	}

	@Test
	void importEdgesTest() throws SQLException, IOException, URISyntaxException {
		assertNotNull(db.getEdges());
		db.emptyEdgeTable();
		assertNull(db.getEdges());
		csvHandler.importCSV(edgeFile, DatabaseInfo.TABLES.EDGES);
		assertNotNull(db.getEdges());
	}
}
