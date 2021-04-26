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
import java.sql.SQLException;

public class ForgotPassword extends SPage{
        @FXML
        private JFXButton backB;

        @FXML private JFXTextField username;

        @FXML private JFXTextField email;

        @FXML private JFXPasswordField password;

        DatabaseController db;

        @FXML
        public void initialize() {
        }

        public void submit_button() throws SQLException {
    if(username.getText ().isEmpty() || email.getText ().isEmpty ()|| password.getText().isEmpty ()) {
        popUp ( "Error","\n\n\n\nYour password was not updated" );
    }
    else{
        //db.updatePassword ( username.getText ( ) ,email.getText ( ) ,password.getText ( ) );
        popUp ( "New Password" ,"\n\n\n\n\n\n\nYour password has been successfully created. " +
                                "Please check your email for a confirmation message. Log in using your new credentials." );
        sceneSwitch ( "Login" );
    }
    }


        public void goHome(javafx.event.ActionEvent event){
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/GuestMainPage.fxml"));
                Aapp.getPrimaryStage().getScene().setRoot(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        public void createNewAccount(){
            sceneSwitch("CreateNewAccount");
        }



    }

