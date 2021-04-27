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
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class CreateNewAccount extends SPage {

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXButton backB;

    @FXML private JFXTextField firstName;

    @FXML private JFXTextField lastName;

    @FXML private JFXTextField emailAddress;

    @FXML private JFXTextField userName;

    @FXML private JFXComboBox userType;

    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXPasswordField confirmPassword;


    DatabaseController db;

    @FXML
    public void initialize() throws SQLException, IOException, URISyntaxException {
        db = new DatabaseController ();
        userType.setItems( FXCollections
                .observableArrayList("Patient","Employee")
        );
    }


    @FXML
    public void submit_button(ActionEvent actionEvent) throws SQLException {
        String email = emailAddress.getText();
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email.matches(pat.pattern())){
            popUp("Failed Submission.", "\n\n\n\n\n\nInvalid email.");

        }
        else if(firstName.getText().isEmpty() && lastName.getText().isEmpty() &&emailAddress.getText().isEmpty() && userName.getText().isEmpty()){
            popUp("Failed Submission.", "\n\n\n\n\nPlease fill out all required fields.");
        }
        else if (!password.getText().equals(confirmPassword.getText())) {
            popUp("Account Creation Failed", "\n\n\n\n\n\nPlease type in the same password for both fields");
        }

        //if(!password.equals(confirmPassword)) Account is already in DB
        else if(db.checkUserExists ( userName.getText ())) {
            popUp ( "Account Creation Failed" ,"\n\n\n\n\n\nUsername already exists." );
        }
        else{
            Map<String, String> user = new HashMap<String, String> ();
            user.put("USERNAME", userName.getText ());
            user.put("FIRSTNAME", firstName.getText ());
            user.put("LASTNAME", lastName.getText ());
            user.put("EMAIL", emailAddress.getText ());
            user.put("USERTYPE", userType.getSelectionModel().getSelectedItem().toString());
            user.put("PASSWORD", password.getText ());

            db.addUser(user);

            sceneSwitch("LogIn");

            }
    }

    public void goBack(){
        sceneSwitch ( "LogIn" );
    }



    public void goHome(javafx.event.ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/GuestMainPage.fxml"));
            Aapp.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }



}
