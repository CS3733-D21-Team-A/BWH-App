package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseUtil;
import edu.wpi.aquamarine_axolotls.db.enums.USERTYPE;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static edu.wpi.aquamarine_axolotls.Settings.*;

public class UserSettings extends GenericPage {

    @FXML private JFXButton reloadDbButton;
    @FXML private JFXCheckBox databasePicker;

    @FXML private JFXTextField firstName;
    @FXML private JFXTextField lastName;
    @FXML private JFXTextField pronouns;
    @FXML private JFXTextField gender;
    @FXML private JFXTextField userName;
    @FXML private JFXTextField email;

    private final DatabaseController db = DatabaseController.getInstance();

    private String username;

    public void initialize(){
        //databasePicker.setVisible(Aapp.userType.equals("Admin")); //TODO: RE-ADD THESE ELEMENTS
        //reloadDbButton.setVisible(Aapp.userType.equals("Admin"));
        //databasePicker.setSelected(PREFERENCES.get(USE_CLIENT_SERVER_DATABASE,null) != null);

        try{
            userName.setText(username);
            firstName.setText(db.getUserByUsername(username).get("FIRSTNAME"));
            lastName.setText(db.getUserByUsername(username).get("LASTNAME"));
            email.setText(db.getUserByUsername(username).get("EMAIL"));
            pronouns.setText(db.getUserByUsername(username).get("PRONOUNS"));
            gender.setText(db.getUserByUsername(username).get("GENDER"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        userName.setEditable(false);
        firstName.setEditable(false);
        lastName.setEditable(false);
        email.setEditable(false);
        pronouns.setEditable(false);
        gender.setEditable(false);
    }

    public void editAccount() {

        firstName.setEditable(true);
        lastName.setEditable(true);
        email.setEditable(true);
        pronouns.setEditable(true);
        gender.setEditable(true);
    }

    public void saveEdits() {

        firstName.setEditable(false);
        lastName.setEditable(false);
        email.setEditable(false);
        pronouns.setEditable(false);
        gender.setEditable(false);

        try{
            Map<String, String> user = new HashMap<String, String>();

            user.put("FIRSTNAME", firstName.getText());
            user.put("LASTNAME", lastName.getText());
            user.put("EMAIL", email.getText());
            user.put("PRONOUNS", pronouns.getText());
            user.put("GENDER", gender.getText());

            db.editUser(username, user);
            PREFERENCES.put(USER_FIRST_NAME, firstName.getText());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void aboutP() {
        sceneSwitch ( "AboutPage" );
    }
    public void creditsP(){
        sceneSwitch ( "CreditsPage" );
    }

    public void updateDB() {
        if (databasePicker.isSelected()) PREFERENCES.put(USE_CLIENT_SERVER_DATABASE,"true");
        else PREFERENCES.remove(USE_CLIENT_SERVER_DATABASE);

        reloadDB();
    }

    public void reloadDB() {
        boolean usingEmbedded;

        try {
            usingEmbedded = db.updateConnection();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            popUp("Database Update","An error occurred when updating the database connection. THIS SHOULD NEVER HAPPEN FIGURE OUT WHAT HAPPENED AAAAAAAAAAAAAAAAAAA");
            return;
        }

        String instanceID = PREFERENCES.get(INSTANCE_ID,null);
        try {
            if (!db.checkUserExists(instanceID)) {
                Map<String,String> instanceUser = new HashMap<>();
                instanceUser.put("USERTYPE",DatabaseUtil.USER_TYPE_NAMES.get(USERTYPE.GUEST));
                instanceUser.put("USERNAME",instanceID);
                instanceUser.put("EMAIL",instanceID); //This is because email must be unique and not null
                instanceUser.put("PASSWORD",instanceID); //this should never be used, but it's a thing

                db.addUser(instanceUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (PREFERENCES.get(USE_CLIENT_SERVER_DATABASE,null) != null) {
            popUp("Database Update", usingEmbedded ? "Unable to connect to client-server database. Falling back to embedded database." : "Connected to client-server database!");
        } else {
            popUp("Database Update", "Connected to embdedded database!");
        }
    }
}
