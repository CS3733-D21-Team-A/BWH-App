package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class NewAccount extends GenericPage {

    @FXML
    private JFXTextField firstName;
    @FXML
    private JFXTextField lastName;
    @FXML
    private JFXTextField emailAddress;
    @FXML
    private JFXTextField userName;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXPasswordField confirmPassword;

    private DatabaseController db;

    @FXML
    public void initialize() throws SQLException, IOException, URISyntaxException {
        db = DatabaseController.getInstance();
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
                emailAddress.getText().isEmpty() || userName.getText().isEmpty()) {
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
        user.put("USERTYPE", "Patient" );
        user.put("PASSWORD", password.getText());
        db.addUser(user);
        popUp ( "Account Success", "\n\n\n\n\n\nThe account you submitted was successfully created." );


        sceneSwitch("LogIn");

    }
}
