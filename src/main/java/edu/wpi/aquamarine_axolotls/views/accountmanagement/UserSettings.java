package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static edu.wpi.aquamarine_axolotls.Settings.*;

public class UserSettings extends GenericPage { //TODO: RENAME THIS CLASS PLEASE
    @FXML HBox saveHbox;
    @FXML HBox editHbox;

    @FXML JFXTextField firstName;
    @FXML JFXTextField lastName;
    @FXML JFXTextField pronouns;
    @FXML JFXTextField gender;
    @FXML JFXTextField userName;
    @FXML JFXTextField email;

    DatabaseController db;

    private String username;

    public void initialize(){
        editHbox.setVisible(true);
        saveHbox.setVisible(false);
        editHbox.toFront();

        this.username = PREFERENCES.get(USER_NAME,null);

        try{
            db = DatabaseController.getInstance();

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
        editHbox.setVisible(false);
        saveHbox.setVisible(true);
        saveHbox.toFront();

        firstName.setEditable(true);
        lastName.setEditable(true);
        email.setEditable(true);
        pronouns.setEditable(true);
        gender.setEditable(true);
    }

    public void saveEdits() {
        editHbox.setVisible(true);
        saveHbox.setVisible(false);
        editHbox.toFront();

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
}
