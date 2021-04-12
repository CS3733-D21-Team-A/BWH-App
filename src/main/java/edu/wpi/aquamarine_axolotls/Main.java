package edu.wpi.aquamarine_axolotls;

import edu.wpi.aquamarine_axolotls.db.DatabaseController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Main {

  public static void main(String[] args) throws IOException {
    //Aapp.launch(Aapp.class, args);
    //Adb.main(args);
    DatabaseController db = new DatabaseController();
    List<Map<String,String>> d;
    List<Map<String,String>> g;
    try {
      d = db.getNodes();
      g = db.getEdges();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
