package edu.wpi.cs3733.d21.teamA.db;

import org.apache.derby.iapi.services.io.FileUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseControllerTest2 {
	@AfterAll
	@BeforeAll
	static void cleanup() {
		DBTestUtil.resetDB();
	}

	@Test
	void dbCreation() {
		File dir = new File("./derby/EMBEDDED_BWH_DB");
		if (dir.exists()) {
			assertTrue(FileUtil.removeDirectory(dir));
		}
		try {
			DatabaseController db;
			assertNotNull(db = DatabaseController.getInstance());
			db.updateConnection();
			assertTrue(dir.exists());
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail();
		}

		assertTrue(DatabaseController.getInstance().shutdownDB());
		assertTrue(FileUtil.removeDirectory(dir));
	}

	/*@Test //This test is deprecated since we can no longer close DatabaseControllers
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
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail();
		}
	}*/
}
