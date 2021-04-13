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
		System.out.println(DatabaseController.liveInstances);
		File dir = new File("./BWH");
		if (dir.exists()) {
			FileUtil.removeDirectory(dir);
		}
		try (DatabaseController db = new DatabaseController()) {
			System.out.println(db.getNodes());
			System.out.println(db.getEdges());
		} catch (SQLException | IOException | URISyntaxException e) {
			e.printStackTrace();
			fail();
		}

		assertTrue(dir.exists());
		assertTrue(FileUtil.removeDirectory(dir));
	}
}
