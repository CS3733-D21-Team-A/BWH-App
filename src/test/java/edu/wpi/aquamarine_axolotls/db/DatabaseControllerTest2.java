package edu.wpi.aquamarine_axolotls.db;

import org.apache.derby.iapi.services.io.FileUtil;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseControllerTest2 {
	@Test
	void dbCreation() {
		File dir = new File("./BWH");
		if (dir.exists()) {
			assertTrue(FileUtil.removeDirectory(dir));
		}
		try (DatabaseController db = new DatabaseController()) {
			assertTrue(dir.exists());
		} catch (SQLException | IOException | URISyntaxException e) {
			e.printStackTrace();
			fail();
		}

		assertTrue(DatabaseController.shutdownDB());
		assertTrue(FileUtil.removeDirectory(dir));
	}

	@Test
	void closingWithMultipleControllers() {
		try (DatabaseController db1 = new DatabaseController()) {
			try (DatabaseController db2 = new DatabaseController()) {
				assertNotNull(db2.getNodes());
			}
			assertNotNull(db1.getNodes());
		} catch (SQLException | IOException | URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(DatabaseController.shutdownDB());
	}
}
