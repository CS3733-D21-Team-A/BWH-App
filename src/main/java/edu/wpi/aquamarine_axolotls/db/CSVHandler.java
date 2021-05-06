package edu.wpi.aquamarine_axolotls.db;

import edu.wpi.aquamarine_axolotls.db.enums.TABLES;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * Class for handling importing and exporting CSVs to and from the database.
 */
public class CSVHandler {

	/**
	 * Strategy interface for use in CSV importing
	 */
	private interface ValueInsertionTemplate {
		/**
		 * Get the primary key corresponding to use in insertion.
		 * @return the primary key column name corresponding to use in insertion.
		 */
		String getPrimaryKey();

		/**
		 * Add provided values into the corresponding table.
		 * @param values Values of entry to add
		 * @throws SQLException Something went wrong.
		 */
		void add(Map<String,String> values) throws SQLException;

		/**
		 * Checks if the entry with the provided ID exists.
		 * @param id ID to check for the existence of.
		 * @return Whether or not an entry with the provided ID exists.
		 * @throws SQLException Something went wrong.
		 */
		boolean exists(String id) throws SQLException;

		/**
		 * Delete an entry from the database.
		 * @param id ID of entry to delete.
		 * @throws SQLException Something went wrong.
		 */
		void delete(String id) throws SQLException;

		/**
		 * Clears the table.
		 * @throws SQLException Something went wrong.
		 */
		void empty() throws SQLException;
	}

	private final ValueInsertionTemplate nodeInserter = new ValueInsertionTemplate() {
		@Override
		public String getPrimaryKey() {
			return "NODEID"; //TODO: MAKE THIS REFERENCE DATABASEINFO
		}

		@Override
		public void add(Map<String, String> values) throws SQLException {
			databaseController.addNode(values);
		}

		@Override
		public boolean exists(String id) throws SQLException {
			return databaseController.nodeExists(id);
		}

		@Override
		public void delete(String id) throws SQLException {
			databaseController.deleteNode(id);
		}

		@Override
		public void empty() throws SQLException {
			databaseController.emptyNodeTable();
		}
	};

	private final ValueInsertionTemplate edgeInserter = new ValueInsertionTemplate() {
		@Override
		public String getPrimaryKey() {
			return "EDGEID"; //TODO: MAKE THIS REFERENCE DATABASEINFO
		}

		@Override
		public void add(Map<String, String> values) throws SQLException {
			databaseController.addEdge(values);
		}

		@Override
		public boolean exists(String id) throws SQLException {
			return databaseController.edgeExists(id);
		}

		@Override
		public void delete(String id) throws SQLException {
			databaseController.deleteEdge(id);
		}

		@Override
		public void empty() throws SQLException {
			databaseController.emptyEdgeTable();
		}
	};


	final DatabaseController databaseController;

	/**
	 * CSVHandler constructer.
	 * @param databaseController DatabaseController to use in operations.
	 */
	public CSVHandler(DatabaseController databaseController) {
		this.databaseController = databaseController;
	}

	/**
	 * Inserts values from the provided reader based on the provided methods.
	 * @param br BufferedReader for input.
	 * @param insertionStrategy Strategy used for checking the existence of, deleting and inserting into a table.
	 * @throws IOException If the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other reason
	 * @throws SQLException Something went wrong.
	 */
	private void insertValues(BufferedReader br, boolean deleteAllOld, ValueInsertionTemplate insertionStrategy) throws IOException, SQLException {
		if (deleteAllOld) {
			insertionStrategy.empty();
		}

		Map<String,String> values = new HashMap<>();

		String line = br.readLine();
		String[] columns = line.split(","); //record column names
		String[] inp;

		while ((line = br.readLine()) != null) {
			inp = line.split(","); //split entries by column
			for (int i = 0; i < inp.length; i++) {
				values.put(columns[i].toUpperCase(),inp[i]); //add value to map indexed by column
			}
			if (!deleteAllOld && insertionStrategy.exists(values.get(insertionStrategy.getPrimaryKey()))) {
				insertionStrategy.delete(values.get(insertionStrategy.getPrimaryKey())); //TODO: Change this to use edit so we only need to call one method AND SO EDGES DON'T GET DELETED
			}
			insertionStrategy.add(values);
		}
	}


	/**
	 * Import a CSV into the database (assumes no conflicts will occur).
	 * @param file CSV to import.
	 * @param table identifer for table import CSV into.
	 * @throws IOException If the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other reason.
	 * @throws SQLException Something went wrong.
	 */
	public void importCSV(File file, TABLES table, boolean deleteAllOld) throws IOException, SQLException {
		importCSV(new FileInputStream(file), table, deleteAllOld);
	}

	/**
	 * Import a CSV into the database (assumes no conflicts will occur).
	 * Note: This will also close the provided InputStream.
	 * @param inputStream InputStream to import with.
	 * @param table identifer for table import CSV into.
	 * @throws IOException If the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other reason.
	 * @throws SQLException Something went wrong.
	 */
	void importCSV(InputStream inputStream, TABLES table, boolean deleteAllOld) throws IOException, SQLException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			switch (table) {
				case NODES:
					insertValues(br, deleteAllOld, nodeInserter);
					break;
				case EDGES:
					insertValues(br, deleteAllOld, edgeInserter);
					break;
				default:
					System.out.println("Import failed: Reference to table not implemented!"); //this should never happen since we're using enums. This will just catch if we add a new table and forget to add it here.
			}
		}
	}

	/**
	 * Export the contents of the database to a CSV.
	 * @param file File to export to.
	 * @param table identifer for table to export.
	 * @throws IOException If the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other reason.
	 * @throws SQLException Something went wrong.
	 */
	public void exportCSV(File file, TABLES table) throws IOException, SQLException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file,false))) {
			List<Map<String, String>> values;

			switch (table) {
				case NODES:
					values = databaseController.getNodes();
					break;
				case EDGES:
					values = databaseController.getEdges();
					break;
				default:
					System.out.println("Import failed: Reference to table not implemented!"); //this should never happen since we're using enums. This will just catch if we add a new table and forget to add it here.
					return;
			}

			Object[] objArr = values.get(0).keySet().toArray();
			String[] columns = Arrays.copyOf(objArr, objArr.length, String[].class);

			StringBuilder sb = new StringBuilder();
			for (String column : columns) {
				sb.append(column);
				sb.append(',');
			}
			sb.delete(sb.length() - 1, sb.length());
			sb.append('\n');

			for (Map<String, String> value : values) {
				for (String column : columns) {
					sb.append(value.get(column.toUpperCase()));
					sb.append(',');
				}
				sb.delete(sb.length() - 1, sb.length());
				sb.append('\n');
			}

			bw.write(sb.toString());
		}
	}
}
