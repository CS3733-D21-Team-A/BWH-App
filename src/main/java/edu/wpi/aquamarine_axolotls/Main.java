package edu.wpi.aquamarine_axolotls;

import edu.wpi.aquamarine_axolotls.db.DatabaseController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) throws SQLException, IOException, URISyntaxException {
    //Aapp.launch(Aapp.class, args);
    System.out.println(new DatabaseController().getNodes());
  }
}
