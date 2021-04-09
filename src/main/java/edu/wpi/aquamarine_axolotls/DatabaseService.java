package edu.wpi.aquamarine_axolotls;

import java.io.*;
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
					System.out.format("%10s%10s%10s%10s%15s%10s%45s%30s%n", row); //printout
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
			String nodeID = sc.nextLine();
			System.out.print("Enter the new X coordinate: "); //TODO: check node ID before asking for values
			int newXC = Integer.parseInt(sc.nextLine());
			System.out.print("Enter the new Y coordinate: ");
			int newYC = Integer.parseInt(sc.nextLine());
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
			String nodeID = sc.nextLine();
			System.out.print("Enter the new long name for this location: "); //TODO: check node ID before asking for values
			String newName = sc.nextLine();

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

	private void createDB() {
		try {
			PreparedStatement smnt = connection.prepareStatement(
				"CREATE TABLE Nodes (" +
					"nodeID VARCHAR(25) PRIMARY KEY," +
					"xcoord NUMERIC(5)," +
					"ycoord NUMERIC(5)," +
					"floor VARCHAR(3)," +
					"building VARCHAR(30)," +
					"nodeType VARCHAR(5)," +
					"longName VARCHAR(50)," +
					"shortName VARCHAR(30)" +
				")"
			);
			smnt.execute();

			smnt = connection.prepareStatement(
				"CREATE TABLE Edges (" +
					"edgeID VARCHAR(51) PRIMARY KEY," +
					"startNode VARCHAR(25)," +
					"endNode VARCHAR(25)," +
					"CONSTRAINT FK_startNode FOREIGN KEY (startNode) REFERENCES Nodes(nodeID)," +
					"CONSTRAINT FK_endNode FOREIGN KEY (endNode) REFERENCES Nodes(nodeID)" +
				")"
			);
			smnt.execute();

			System.out.println("No database found. New one created.");
			populateDB();
		} catch (SQLException e) {
			System.out.println("Database Found!");
		}
	}

	private void populateDB() {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("edu/wpi/aquamarine_axolotls/csv/L1Nodes.csv");
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			PreparedStatement smnt = connection.prepareStatement("INSERT INTO Nodes (nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName) VALUES (?,?,?,?,?,?,?,?)");
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] inp = line.split(",");
				for (int i = 0; i < inp.length; i++) {
					if (inp[i].matches("[0-9]+")) {
						smnt.setInt(i+1, Integer.parseInt(inp[i]));
					} else {
						smnt.setString(i+1, inp[i]);
					}
				}
				smnt.execute();
			}
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return;
		}

		in = this.getClass().getClassLoader().getResourceAsStream("edu/wpi/aquamarine_axolotls/csv/L1Edges.csv");
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			PreparedStatement smnt = connection.prepareStatement("INSERT INTO Edges (edgeID, startNode, endNode) VALUES (?,?,?)");
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				String[] inp = line.split(",");
				for (int i = 0; i < inp.length; i++) {
					if (inp[i].matches("[0-9]+")) {
						smnt.setInt(i+1, Integer.parseInt(inp[i]));
					} else {
						smnt.setString(i+1, inp[i]);
					}
				}
				smnt.execute();
			}
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return;
		}
	}

	public DatabaseService() {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}

		try {
			this.connection = DriverManager.getConnection("jdbc:derby:BWH;create=true", "admin", "admin");
			createDB();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		this.nodeTable = new NodeTable();
		this.edgeTable = new EdgeTable();
	}

	
}
