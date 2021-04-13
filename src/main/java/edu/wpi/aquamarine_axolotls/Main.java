package edu.wpi.aquamarine_axolotls;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.pathplanning.SearchAlgorithm;

public class Main {

  public static void main(String[] args) throws IOException {
    Aapp.launch(Aapp.class, args);
  }
}
