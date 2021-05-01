package edu.wpi.aquamarine_axolotls.db;

import edu.wpi.aquamarine_axolotls.TestUtil;
import org.apache.derby.iapi.services.io.FileUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseControllerTest2 {
	@AfterAll
	@BeforeAll
	static void cleanup() {
		TestUtil.resetDB();
	}

	@Test
	void dbCreation() {
		File dir = new File("./BWH");
		if (dir.exists()) {
			assertTrue(FileUtil.removeDirectory(dir));
		}
		try (DatabaseController db = DatabaseController.getInstance()) {
			assertTrue(dir.exists());
		} catch (SQLException | IOException | URISyntaxException e) {
			e.printStackTrace();
			fail();
		}

		assertTrue(DatabaseController.shutdownDB());
		assertTrue(FileUtil.removeDirectory(dir));
	}

	@Test
	void closingCreatesNewController() {
		try {
			DatabaseController db1 = DatabaseController.getInstance();
			assertNotNull(db1.getNodes());
			DatabaseController db2 = DatabaseController.getInstance();
			assertNotNull(db2.getNodes());
			assertSame(db1, db2);
			db1.close();
			assertThrows(SQLNonTransientConnectionException.class, db2::getNodes);

			DatabaseController db3 = DatabaseController.getInstance();
			assertNotNull(db3.getNodes());
			assertNotSame(db3, db2);
			assertNotSame(db3, db1);
			assertSame(db1, db2);
		} catch (SQLException | IOException | URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
	}
}
