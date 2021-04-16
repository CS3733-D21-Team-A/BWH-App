package edu.wpi.aquamarine_axolotls;

import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import org.apache.derby.iapi.services.io.FileUtil;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtil {
	public static void resetDB() {
		File dir = new File("./BWH");
		if (dir.exists()) {
			assertTrue(DatabaseController.shutdownDB());
			assertTrue(FileUtil.removeDirectory(dir));
		}
	}
}
