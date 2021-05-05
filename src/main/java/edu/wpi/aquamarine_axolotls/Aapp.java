package edu.wpi.aquamarine_axolotls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Aapp extends Application {

  private static Stage primaryStage;

  public static List<Map<String,String>> serviceRequests = new ArrayList<>();

  //TODO: STORE THESE IN PREFERENCES
  public static String userType = "Guest"; //TODO: REPLACE THIS WITH ENUM
  public static String username;


  @Override
  public void init() {
    System.out.println("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) {
    Aapp.primaryStage = primaryStage;
    try {
      Parent root = FXMLLoader.load(getClass().getResource("fxml/GuestMainPage.fxml"));
      Scene scene = new Scene(root);
      primaryStage.setScene(scene);
      primaryStage.setResizable(false);
      primaryStage.show();
    } catch (IOException e) {
      e.printStackTrace();
      Platform.exit();
    }
  }

  public static void setPrimaryStage(Stage primaryStage) {
    Aapp.primaryStage = primaryStage;
  }

  public static Stage getPrimaryStage(){
    return primaryStage;
  }

  @Override
  public void stop() {
    System.out.println("Shutting Down");
  }
}
