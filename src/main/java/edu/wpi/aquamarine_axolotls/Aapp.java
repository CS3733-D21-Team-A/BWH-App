package edu.wpi.aquamarine_axolotls;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.prefs.BackingStoreException;

import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseUtil;
import edu.wpi.aquamarine_axolotls.db.enums.USERTYPE;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static edu.wpi.aquamarine_axolotls.Settings.*;

public class Aapp extends Application {

  private static Stage primaryStage;

  public static List<Map<String,String>> serviceRequests = new ArrayList<>();

  private final DatabaseController db = DatabaseController.getInstance();

  @Override
  public void init() {
    System.out.println("Starting Up");
  }

  private void createInstanceUser() throws SQLException {
    Map<String,String> instanceUser = new HashMap<>();

    instanceUser.put("USERTYPE",DatabaseUtil.USER_TYPE_NAMES.get(USERTYPE.GUEST));

    String instanceID;
    do {
      instanceID = UUID.randomUUID().toString();
    } while (db.checkUserExists(instanceID));

    instanceUser.put("USERNAME",instanceID);
    instanceUser.put("EMAIL",instanceID); //This is because email must be unique and not null
    instanceUser.put("PASSWORD",instanceID); //this should never be used, but it's a thing

    PREFERENCES.put(INSTANCE_ID,instanceID);
    db.addUser(instanceUser);
  }

  @Override
  public void start(Stage primaryStage) throws SQLException, BackingStoreException {
    Aapp.primaryStage = primaryStage;

    String usertype = PREFERENCES.get(USER_TYPE,null);
    if (usertype == null) { // First run
      usertype = DatabaseUtil.USER_TYPE_NAMES.get(USERTYPE.GUEST);
      PREFERENCES.put(USER_TYPE,usertype);
    }
    if (PREFERENCES.get(INSTANCE_ID,null) == null) {
      createInstanceUser();
      if (usertype.equals(DatabaseUtil.USER_TYPE_NAMES.get(USERTYPE.GUEST))) {
        PREFERENCES.put(USER_NAME, PREFERENCES.get(INSTANCE_ID,null));
      }
    }

    String instanceID = PREFERENCES.get(INSTANCE_ID,null);
    if (!db.checkUserExists(instanceID)) {
      Map<String,String> instanceUser = new HashMap<>();
      instanceUser.put("USERTYPE",DatabaseUtil.USER_TYPE_NAMES.get(USERTYPE.GUEST));
      instanceUser.put("USERNAME",instanceID);
      instanceUser.put("EMAIL",instanceID); //This is because email must be unique and not null
      instanceUser.put("PASSWORD",instanceID); //this should never be used, but it's a thing

      db.addUser(instanceUser);
    }


    try {
      Parent root = FXMLLoader.load(getClass().getResource("fxml/" + usertype + "MainPage.fxml"));
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
