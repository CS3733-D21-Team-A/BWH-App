package edu.wpi.aquamarine_axolotls;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.prefs.BackingStoreException;

import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseUtil;
import edu.wpi.aquamarine_axolotls.db.enums.USERTYPE;
import edu.wpi.aquamarine_axolotls.socketServer.SocketClient;
import edu.wpi.aquamarine_axolotls.extras.Security;
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

  public static boolean serverRunning;

  public static Thread clientThreadSender = new Thread();
  public static SocketClient clientSender = null;
  public static Thread clientThreadReceiver = new Thread();
  public static SocketClient clientReceiver = null;
  public static Thread clientInfoThreadReceiver = new Thread();
  public static SocketClient clientInfoReceiver = null;

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
    } while (db.checkUserExistsByUsername(instanceID));

    instanceUser.put("USERNAME",instanceID);
    instanceUser.put("EMAIL",instanceID); //This is because email must be unique and not null
    Security.addHashedPassword(instanceUser,instanceID); //account login should never be used, but it's a thing

    PREFERENCES.put(INSTANCE_ID,instanceID);
    db.addUser(instanceUser);
  }

  @Override
  public void start(Stage primaryStage) throws SQLException, BackingStoreException {
    Aapp.primaryStage = primaryStage;

    serverRunning = false;

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
    if (!db.checkUserExistsByUsername(instanceID)) {
      Map<String,String> instanceUser = new HashMap<>();
      instanceUser.put("USERTYPE",DatabaseUtil.USER_TYPE_NAMES.get(USERTYPE.GUEST));
      instanceUser.put("USERNAME",instanceID);
      instanceUser.put("EMAIL",instanceID); //This is because email must be unique and not null
      Security.addHashedPassword(instanceUser,instanceID); //account login should never be used, but it's a thing

      db.addUser(instanceUser);
    }

    if (!db.checkUserExistsByUsername(PREFERENCES.get(USER_NAME,null))) {
      PREFERENCES.put(USER_TYPE, DatabaseUtil.USER_TYPE_NAMES.get(USERTYPE.GUEST));
      PREFERENCES.put(USER_NAME, PREFERENCES.get(INSTANCE_ID,null));
      PREFERENCES.remove(USER_FIRST_NAME);
      usertype = DatabaseUtil.USER_TYPE_NAMES.get(USERTYPE.GUEST);
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
