package edu.wpi.aquamarine_axolotls.db;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseControllerTest2 {
	private boolean deleteDirectory(File directoryToBeDeleted) {
		File[] allContents = directoryToBeDeleted.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}
		try {
			Files.delete(directoryToBeDeleted.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return !directoryToBeDeleted.exists();
	}

	@Test
	void dbCreation() {
		System.out.println(DatabaseController.liveInstances);
		File dir = new File("./BWH");
		if (dir.exists()) {
			assertTrue(deleteDirectory(dir));
		}
		try (DatabaseController db = new DatabaseController()) {
			System.out.println(db.getNodes());
			System.out.println(db.getEdges());
		} catch (SQLException | IOException | URISyntaxException e) {
			e.printStackTrace();
			fail();
		}

		assertTrue(dir.exists());
		assertTrue(deleteDirectory(dir));
	}
}
