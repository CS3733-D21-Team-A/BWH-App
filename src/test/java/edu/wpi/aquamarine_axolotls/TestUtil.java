package edu.wpi.aquamarine_axolotls;

import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import org.apache.derby.iapi.services.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static edu.wpi.aquamarine_axolotls.Settings.USE_CLIENT_SERVER_DATABASE;
import static edu.wpi.aquamarine_axolotls.Settings.prefs;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtil {
	public static void resetDB() {
		prefs.remove(USE_CLIENT_SERVER_DATABASE);

		File dir = new File("./EMBEDDED_BWH_DB");
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
