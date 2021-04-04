package edu.wpi.aquamarine_axolotls;

import com.opencsv.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Main {

  public static void main(String[] args) throws IOException {
    //Aapp.launch(Aapp.class, args);
    //DatabaseService DB = new DatabaseService();
    //System.out.println(DB.getEmployeeName());

    List<List<String>> records = new ArrayList<List<String>>();
    CSVReader csvReader = new CSVReader(new FileReader("src/main/resources/edu/wpi/aquamarine_axolotls/csv/L1Edges.csv"));
    String[] values = null;
    while ((values = csvReader.readNext()) != null) {
      records.add(Arrays.asList(values));
    }

    for (List<String> row : records) {
      System.out.println(row.toString());
    }
  }
}
