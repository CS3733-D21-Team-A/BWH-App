package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
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


    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXPasswordField confirmPassword;


    DatabaseController db;
    @FXML
    public void submit_button(ActionEvent actionEvent) {
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
        else if(!db.checkUserExists ( userName.getText ())) {
            popUp ( "Account Creation Failed" ,"\n\n\n\n\n\nUsername already exists." );
        }
        else{
            //Map<String,String> user = new Map<String, String>();
            //db.addUser (  );
            sceneSwitch("LogIn");

            }
    }

    @FXML
    public void initialize() {
    }
/*

    public void confirmUserDoesNotExist(){

        String CUusername = userName.getText();
        if(db.checkNotPreexistingUser(CUusername)
        //user does not already exist, so the username is Valid.


    }
*/

    public void goHome(javafx.event.ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/GuestMainPage.fxml"));
            Aapp.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }



}
