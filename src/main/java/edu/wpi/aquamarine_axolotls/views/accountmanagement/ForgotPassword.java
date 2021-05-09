package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class ForgotPassword extends GenericPage {
        @FXML private JFXTextField username;
        @FXML private JFXTextField email;
        @FXML private JFXPasswordField password;

        private final DatabaseController db = DatabaseController.getInstance();

        public void submit_button()  {
            String usrname = username.getText();
            String eml = email.getText ();
            String pass = password.getText ();
            if(usrname.isEmpty() || eml.isEmpty ()|| pass.isEmpty ()) {
                popUp ( "Error","\n\n\n\nYour password was not updated" );
                return;
            }

            try {
                if(db.checkUserExists(usrname) && db.getUserByUsername(usrname).get("EMAIL").equals(eml)){
                    db.updatePassword (usrname,eml ,pass);
                    popUp ( "New Password" ,"\n\n\n\n\nYour password has been successfully created. " +
                                            "Please check your email for a confirmation message. Log in using your new credentials." );
                    sceneSwitch ( "Login" );
                }
                else popUp ( "This account does not exist." ,"\n\n\n\n\nVerify that you have input the right username and email. ");
            } catch (SQLException throwables) {
                throwables.printStackTrace ( );
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

    }

