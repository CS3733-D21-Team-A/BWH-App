package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static edu.wpi.aquamarine_axolotls.Settings.USE_CLIENT_SERVER_DATABASE;
import static edu.wpi.aquamarine_axolotls.Settings.prefs;

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

    public void initialize(){
        //databasePicker.setVisible(Aapp.userType.equals("Admin")); //TODO: RE-ADD THESE ELEMENTS
        //reloadDbButton.setVisible(Aapp.userType.equals("Admin"));
        //databasePicker.setSelected(prefs.get(USE_CLIENT_SERVER_DATABASE,null) != null);

        try{
            userName.setText(Aapp.username);
            firstName.setText(db.getUserByUsername(Aapp.username).get("FIRSTNAME"));
            lastName.setText(db.getUserByUsername(Aapp.username).get("LASTNAME"));
            email.setText(db.getUserByUsername(Aapp.username).get("EMAIL"));
            pronouns.setText(db.getUserByUsername(Aapp.username).get("PRONOUNS"));
            gender.setText(db.getUserByUsername(Aapp.username).get("GENDER"));
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

            db.editUser(Aapp.username, user);
            Aapp.userFirstName = firstName.getText();
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
        if (databasePicker.isSelected()) prefs.put(USE_CLIENT_SERVER_DATABASE,"true");
        else prefs.remove(USE_CLIENT_SERVER_DATABASE);

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

        if (prefs.get(USE_CLIENT_SERVER_DATABASE,null) != null) {
            popUp("Database Update", usingEmbedded ? "Unable to connect to client-server database. Falling back to embedded database." : "Connected to client-server database!");
        } else {
            popUp("Database Update", "Connected to embdedded database!");
        }
    }
}
