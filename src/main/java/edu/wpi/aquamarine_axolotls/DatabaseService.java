package edu.wpi.aquamarine_axolotls;

import java.sql.*;

public class DatabaseService {
	private Connection connection;

	public DatabaseService(int arg) {
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}

		try {
			connection = DriverManager.getConnection("jdbc:derby:test3");
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		if (arg == -1) System.out.println(
			"1 - Node Information\n"
			+ "2 - Update Node Coordinates\n"
			+ "3 - Update Node Location Long Name\n"
			+ "4 - Edge Information\n"
			+ "5 - Exit Program"
		);

		try {
			switch(arg) {
				case 1:
					nodeInformation();
					break;
				case 2:
					updateNodeCoords();
					break;
				case 3:
					updateNodeLocationName();
					break;
				case 4:
					edgeInformation();
					break;
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}

	private void nodeInformation() {
	}

	private void updateNodeCoords() {
	}

	private void updateNodeLocationName() {
	}

	private void edgeInformation() {
	}
}
