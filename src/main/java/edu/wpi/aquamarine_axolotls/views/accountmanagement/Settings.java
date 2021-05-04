package edu.wpi.aquamarine_axolotls.views.accountmanagement;

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

public class Settings extends GenericPage {
    @FXML HBox saveHbox;
    @FXML HBox editHbox;

    @FXML JFXTextField firstName;
    @FXML JFXTextField lastName;
    @FXML JFXTextField pronouns;
    @FXML JFXTextField gender;
    @FXML JFXTextField userName;
    @FXML JFXTextField email;

    DatabaseController db;

    public void initialize(){
        editHbox.setVisible(true);
        saveHbox.setVisible(false);
        editHbox.toFront();

        try{
            db = DatabaseController.getInstance();

            userName.setText(Aapp.username);
            firstName.setText(db.getUserByUsername(Aapp.username).get("FIRSTNAME"));      // TODO: connect with DB user account
            lastName.setText(db.getUserByUsername(Aapp.username).get("LASTNAME"));
            email.setText(db.getUserByUsername(Aapp.username).get("EMAIL"));
            pronouns.setText(db.getUserByUsername(Aapp.username).get("PRONOUNS"));
            gender.setText(db.getUserByUsername(Aapp.username).get("GENDER"));
        } catch (SQLException | IOException throwables) {
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

    public void saveEdits(ActionEvent actionEvent) {
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

            db.editUser(Aapp.username, user);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
