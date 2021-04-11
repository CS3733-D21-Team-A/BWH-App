package edu.wpi.aquamarine_axolotls.db;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseControllerTest {
	DatabaseController db = new DatabaseController();

	@Test
	void nodeDoesntExist() {
		try {
			assertFalse(db.nodeExists("foobar"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void nodeDoesExist() {
		try {
			assertTrue(db.nodeExists("WELEV00ML1"));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
}