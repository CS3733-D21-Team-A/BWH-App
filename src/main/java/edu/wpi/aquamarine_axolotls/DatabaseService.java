package edu.wpi.aquamarine_axolotls;

import java.sql.*;

public class DatabaseService {
	private Connection connection;

	public DatabaseService() {
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}

		try {
			connection = DriverManager.getConnection("jdbc:derby://localhost:1527/test");
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}

	public String getEmployeeName() {
		try {
			Statement stmt = connection.createStatement();
			ResultSet res = stmt.executeQuery("SELECT Name from Employees");
			if (res.next()) return res.getString("Name");
			return "";
		} catch (SQLException throwables) {
			throwables.printStackTrace();
			return "";
		}
	}
}
