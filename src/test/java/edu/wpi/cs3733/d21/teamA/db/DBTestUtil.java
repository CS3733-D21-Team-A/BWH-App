package edu.wpi.cs3733.d21.teamA.db;

import org.apache.derby.iapi.services.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static edu.wpi.cs3733.d21.teamA.Settings.DATABASE_HOSTNAME;
import static edu.wpi.cs3733.d21.teamA.Settings.PREFERENCES;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class DBTestUtil {
	public static void resetDB() {
		PREFERENCES.remove(DATABASE_HOSTNAME);

		File dir = new File("./derby/EMBEDDED_BWH_DB");
		if (dir.exists()) {
			try {
				DatabaseController db = DatabaseController.getInstance();
				db.updateConnection();
				assertTrue(db.shutdownDB());
			} catch (SQLException | IOException throwables) {
				throwables.printStackTrace();
				fail();
			}
			assertTrue(FileUtil.removeDirectory(dir));
		}
	}
}
