package edu.wpi.aquamarine_axolotls;

import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.pathplanning.SearchAlgorithm;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

  public static void main(String[] args) throws IOException {

    String arguments[] = {"1"};
    Aapp.launch(Aapp.class, args);
    Adb.main(arguments);
    SearchAlgorithm sa = new SearchAlgorithm();
    List<Node> path = sa.getPath("CCONF002L1", "WELEV00HL1");
    System.out.println(path);
  }
}
