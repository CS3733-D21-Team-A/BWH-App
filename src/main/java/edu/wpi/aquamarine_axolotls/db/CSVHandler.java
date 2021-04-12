package edu.wpi.aquamarine_axolotls.db;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class CSVHandler {
	@FunctionalInterface
	private interface SQLRunnable { //need this because normal Runnables can't throw exceptions
		void run() throws SQLException;
	}

	@FunctionalInterface
	private interface SQLConsumer<T> { //need this because normal Consumers can't throw exceptions
		void accept(T input) throws SQLException;
	}

	final DatabaseController databaseController;

	public CSVHandler(DatabaseController databaseController) {
		this.databaseController = databaseController;
	}

	/**
	 * Inserts values from the provided reader based on the provided methods.
	 * @param br BufferedReader for input.
	 * @param tableEmptier method for emptying the table to input to.
	 * @param tableAdder method for adding new entries to the table.
	 */
	private void insertValues(BufferedReader br, SQLRunnable tableEmptier, SQLConsumer<Map<String,String>> tableAdder) throws IOException, SQLException {
		Map<String,String> values = new HashMap<String,String>();

		String line = br.readLine();
		String[] columns = line.split(","); //record column names
		String[] inp;

		tableEmptier.run();

		while ((line = br.readLine()) != null) {
			inp = line.split(","); //split entries by column
			for (int i = 0; i < inp.length; i++) {
				values.put(columns[i],inp[i]); //add value to map indexed by column
			}
			tableAdder.accept(values);
		}
	}


	/**
	 * Import a CSV into the database (assumes no conflicts will occur).
	 * @param file CSV to import.
	 * @param table identifer for table import CSV into.
	 */
	public void importCSV(File file, DatabaseInfo.TABLES table) throws IOException, SQLException {
		BufferedReader br = new BufferedReader(new FileReader(file));

		switch (table) { //Clear the database on import
			case NODES:
				insertValues(br, databaseController::emptyNodeTable, databaseController::addNode);
				break;
			case EDGES:
				insertValues(br, databaseController::emptyEdgeTable, databaseController::addEdge);
				break;
			default:
				System.out.println("Import failed: Reference to table not implemented!"); //this should never happen since we're using enums. This will just catch if we add a new table and forget to add it here.
		}
	}

	/**
	 * Export the contents of the database to a CSV.
	 * @param file File to export to.
	 * @param table identifer for table to export.
	 */
	public void exportCSV(File file, DatabaseInfo.TABLES table) throws IOException, SQLException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(file,false));

		List<Map<String,String>> values;

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
		sb.delete(sb.length()-1,sb.length());
		sb.append('\n');

		for (Map<String,String> value : values) {
			for (String column : columns) {
				sb.append(value.get(column.toUpperCase()));
				sb.append(',');
			}
			sb.delete(sb.length()-1,sb.length());
			sb.append('\n');
		}

		bw.write(sb.toString());
	}
}
