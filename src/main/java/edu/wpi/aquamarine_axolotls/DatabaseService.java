package edu.wpi.aquamarine_axolotls;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class DatabaseService {
	private Connection connection;

	class NodeTable { //TODO: Make Table interface?
		NodeTable() {}

		/**
		 * Display list of nodes along with their attributes
		 */
		public void nodeInformation() {
			try {
				PreparedStatement smnt = connection.prepareStatement("SELECT * FROM Nodes");
				ResultSet rset = smnt.executeQuery();
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

		/**
		 * The user enters the ID of the node and is then prompted for the values of x and y coordinates
		 */
		public void updateNodeCoords() {
			Scanner sc = new Scanner(System.in);

			System.out.print("Enter nodeID: ");
			String nodeID = sc.next();
			System.out.print("Enter the new X coordinate: "); //TODO: check node ID before asking for values
			int newXC = Integer.parseInt(sc.next());
			System.out.print("Enter the new Y coordinate: ");
			int newYC = Integer.parseInt(sc.next());
			try {
				PreparedStatement smnt = connection.prepareStatement("UPDATE Nodes SET xcoord=?, ycoord=? WHERE NodeID=?");
				smnt.setInt(1,newXC);
				smnt.setInt(2,newYC);
				smnt.setString(3,nodeID);
				int updatedRows = smnt.executeUpdate();
				if (updatedRows == 0) System.out.println("Error: Invalid node ID. Exiting...");

				smnt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		/**
		 * Update node location long name
		 */
		public void updateNodeLocationName() {
			Scanner sc = new Scanner(System.in);

			System.out.print("Enter node ID: ");
			String nodeID = sc.next();
			System.out.print("Enter the new long name for this location: "); //TODO: check node ID before asking for values
			String newName = sc.next();

			try {
				PreparedStatement smnt = connection.prepareStatement("UPDATE Nodes SET LongName=? WHERE NodeID=?");
				smnt.setString(1,newName);
				smnt.setString(2,nodeID);
				int updated = smnt.executeUpdate();
				if (updated == 0) System.out.println("Error: Invalid node ID. Exiting...");

				smnt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	class EdgeTable {
		EdgeTable() {}

		/**
		 * Display list of edges along with their attributes
		 */
		public void edgeInformation() { //TODO: Refactor this into an interface?
			try {
				PreparedStatement smnt = connection.prepareStatement("SELECT * FROM Edges");
				ResultSet rset = smnt.executeQuery();
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

				//printing information
				for (final Object[] row : table) {
					System.out.format("%25s%15s%15s%n", row);
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	private NodeTable nodeTable;
	private EdgeTable edgeTable;

	NodeTable getNodeTable() { return this.nodeTable; }
	EdgeTable getEdgeTable() { return this.edgeTable; }

	public DatabaseService() {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			return;
		}

		try {
			this.connection = DriverManager.getConnection("jdbc:derby:BWH;user=admin;password=admin");
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		this.nodeTable = new NodeTable();
		this.edgeTable = new EdgeTable();
	}
}
