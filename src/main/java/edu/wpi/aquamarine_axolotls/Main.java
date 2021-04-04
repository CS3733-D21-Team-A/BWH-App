package edu.wpi.aquamarine_axolotls;

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

    DatabaseService DB = new DatabaseService(args.length == 3 ? Integer.parseInt(args[2]) : -1);
  }
}
