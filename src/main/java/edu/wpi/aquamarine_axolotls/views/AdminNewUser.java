package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AdminNewUser extends GenericPage {


        @FXML private JFXTextField firstName;

        @FXML private JFXTextField lastName;

        @FXML private JFXTextField emailAddress;

        @FXML private JFXTextField userName;

        @FXML private JFXPasswordField password;

        @FXML private JFXPasswordField confirmPassword;

        @FXML private JFXComboBox userType;

        DatabaseController db;

    @FXML
    public void initialize() throws SQLException, IOException, URISyntaxException {
        db = new DatabaseController();
        userType.setItems( FXCollections
                .observableArrayList("Employee", "Admin")
        );
    }
    @FXML
    public void submit_button(ActionEvent actionEvent) throws SQLException {
        String email = emailAddress.getText();
        // maybe we should wait to check emails until they work? Not entirely sure how this regex works the $ and ^
/*        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        Pattern pat = Pattern.compile(emailRegex);
        if (!email.matches(pat.pattern())){
            popUp("Failed Submission.", "\n\n\n\n\n\nInvalid email.");

        }*/
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty() ||
            emailAddress.getText().isEmpty() || userName.getText().isEmpty() ||
            userType.getSelectionModel().getSelectedItem() == null) {
            popUp("Failed Submission.", "\n\n\n\n\nPlease fill out all fields listed.");
            return;
        }

        if (!password.getText().equals(confirmPassword.getText())) {
            popUp("Account Creation Failed", "\n\n\n\n\n\nThe two passwords do not match, Try again.");
            return;
        }

        if (db.checkUserExists(userName.getText())) {
            popUp("Account Creation Failed", "\n\n\n\n\n\nUsername already exists.");
            return;
        }

        for (Map<String, String> usr : db.getUsers()) {
            if (usr.get("EMAIL").equals(email)) {
                popUp("Account Creation Failed", "\n\n\n\n\n\nUser with this email already exists.");
                return;
            }
        }

        Map<String, String> user = new HashMap<String, String>();
        user.put("USERNAME", userName.getText());
        user.put("FIRSTNAME", firstName.getText());
        user.put("LASTNAME", lastName.getText());
        user.put("EMAIL", emailAddress.getText());
        user.put("USERTYPE", userType.getSelectionModel().getSelectedItem().toString());
        user.put("PASSWORD", password.getText());
        db.addUser(user);

        popUp ( "Account Success", "\n\n\n\n\nAdmin, the account you submitted was successfully created." );
        sceneSwitch("AdminMainPage");
    }
}

