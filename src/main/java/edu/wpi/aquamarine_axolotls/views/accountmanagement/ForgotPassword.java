package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.extras.Security;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.fxml.FXML;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends GenericPage {
        @FXML private JFXTextField username;
        @FXML private JFXTextField email;
        @FXML private JFXPasswordField password;
        @FXML private JFXTextField tfa;
        @FXML private JFXButton next;

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
                    Map<String,String> updated = new HashMap<>();
                    Security.addHashedPassword(updated, pass);
                    db.editUser(usrname,updated);
                    popUp ( "New Password" ,"\n\n\n\n\nYour password has been successfully created. " +
                                            "Please check your email for a confirmation message. Log in using your new credentials." );
                    sceneSwitch ( "Login" );
                }
                else popUp ( "This account does not exist." ,"\n\n\n\n\nVerify that you have input the right username and email. ");
            } catch (SQLException throwables) {
                throwables.printStackTrace ( );
            }
        }

        public void next_button(){
            tfa.setVisible (true);
            tfa.setVisible ( true );
            next.setVisible ( false );


        }

    }

