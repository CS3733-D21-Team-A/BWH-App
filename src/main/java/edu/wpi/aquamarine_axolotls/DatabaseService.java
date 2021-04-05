package edu.wpi.aquamarine_axolotls;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DatabaseService {
	private Connection connection;

	private class NodeTable {
		NodeTable() {}

		void nodeInformation() {
			try {
				Statement smnt = connection.createStatement();
				ResultSet rset = smnt.executeQuery("SELECT * FROM Nodes");
				ResultSetMetaData rsmd = rset.getMetaData();

				List<String[]> table = new ArrayList<>(); //create table to display
				int colCount = rsmd.getColumnCount();

				table.add(new String[colCount]); //add column label row
				for (int i = 0; i < colCount; i++) {
					table.get(0)[i] = rsmd.getColumnName(i+1); //add column labels to first row
				}

				for (int row = 1; rset.next(); row++) {
					table.add(new String[colCount]);
					for (int i = 0; i < colCount; i++) {
						table.get(row)[i] = rset.getString(rsmd.getColumnName(i+1)); //add row values
					}
				}

				for (final Object[] row : table) {
					System.out.format("%10s%10s%10s%10s%15s%10s%45s%20s%n", row); //printout
				}

				rset.close();
				smnt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		void updateNodeCoords() {
		}

		void updateNodeLocationName() {
			Scanner sc = new Scanner(System.in);

			System.out.print("Enter node ID: ");
			String nodeID = sc.next();
			System.out.print("Enter the new long name for this location: ");
			String newName = sc.next();

			try {
				Statement smnt = connection.createStatement();
				int updated = smnt.executeUpdate("UPDATE Nodes SET LongName = '" + newName + "' WHERE NodeID = '" + nodeID + "'");
				if (updated == 0) System.out.println("Error: Invalid node ID. Exiting...");

				smnt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private class EdgeTable {
		EdgeTable() {}

		void edgeInformation() {

		}
	}

	private NodeTable nodeTable;
	private EdgeTable edgeTable;

	public DatabaseService(int arg) {
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}

		try {
			connection = DriverManager.getConnection("jdbc:derby:BWH");
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		nodeTable = new NodeTable();
		edgeTable = new EdgeTable();

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
					nodeTable.nodeInformation();
					break;
				case 2:
					nodeTable.updateNodeCoords();
					break;
				case 3:
					nodeTable.updateNodeLocationName();
					break;
				case 4:
					edgeTable.edgeInformation();
					break;
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
