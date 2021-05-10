package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.extras.EmailService;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.sql.SQLException;

public class ForgotPassword extends GenericPage {
        @FXML private JFXTextField username;
        @FXML private JFXTextField email;
        @FXML private JFXPasswordField password;
        @FXML private JFXPasswordField confirmPassword;
        @FXML private JFXTextField tfa;
        @FXML private JFXButton next;
        @FXML private GridPane gridPane;
        @FXML private Pane newPassPane;
        @FXML private Pane verfPane;
        @FXML private JFXButton first;
        @FXML private JFXButton second;
        @FXML private JFXButton finalSubmit;
        @FXML private JFXTextField verifyEmail;
        @FXML private Label label;



    private final DatabaseController db = DatabaseController.getInstance();
    private EmailService emailService = new EmailService();

        public void userCheck()  {
            String usrname = username.getText();
            String eml = email.getText ();
            if(usrname.isEmpty() || eml.isEmpty ()) {
                popUp ( "Error","\n\n\n\nYour password was not updated" );
                return;
            }

            try {
                if(db.checkUserExists(usrname) && db.getUserByUsername(usrname).get("EMAIL").equals(eml)){
                    //send email here!
                    verfPane.setVisible ( true );
                    gridPane.setVisible ( false );
                    first.setVisible ( false );
                    second.setVisible ( true );
                    label.setText ("Please enter the verification code sent to your email"  );

                }
                else popUp ( "This account does not exist." ,"\n\n\n\n\nVerify that you have input the right username and email. ");
            } catch (SQLException throwables) {
                throwables.printStackTrace ( );
            }
        }

        public void verifyEmailConf(){
            if(!verifyEmail.getText ().isEmpty ()){

                String emailConf = "";
                if(verifyEmail.getText ().equals(emailConf)){
                    second.setVisible ( false );
                    finalSubmit.setVisible ( true );
                    newPassPane.setVisible ( true );
                    label.setText ("Please enter your new password"  );
                }
                else {
                    popUp ( "Invalid Request" ,"The email verification code that you entered was not correct. Please try again" );
                }
            }
            else{
                popUp ( "Invalid Request", "Please try again" );
            }
        }

        public void finalSubmit(){
            if(!password.getText ().isEmpty ()&& confirmPassword.getText ().isEmpty ()) {
                if ( password.equals ( confirmPassword ) ) {
                    try {
                        db.updatePassword ( username.getText ( ) ,email.getText ( ) ,password.getText ( ) );
                    } catch (SQLException throwables) {
                        throwables.printStackTrace ( );
                    }
                }
                else {
                    popUp ( "Invalid Request" ,"The two passwords did not match. Please try again" );
                }
            }
                else{
                        popUp ( "Invalid Request" ,"Please try again" );
                    }
        }

}
