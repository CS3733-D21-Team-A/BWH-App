package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Map;


public class LogIn extends GenericPage {

    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private Pane smsPane;
    @FXML
    private JFXTextField smsText;
    @FXML
    private JFXButton smsSubmit;

    private DatabaseController db;

    @FXML
    public void initialize() throws SQLException, IOException, URISyntaxException {
        db = DatabaseController.getInstance();
        smsPane.setVisible(false);
    }

    public void smsCancel(){
        smsPane.setVisible ( false );
    }

    public void smsSubmit(String CUusername) {
        smsPane.setVisible ( true );
        String r = "123";

        if (smsText.getText().equals(r)) {
            popUp ( "Submission Success!" ,"\n\n\n\n\n\nYou have entered the correct credentials" );
            try {
                Map<String, String> usr = db.getUserByUsername ( CUusername );
                Aapp.userType = usr.get ( "USERTYPE" );
                Aapp.username = usr.get ( "USERNAME" );
            } catch (SQLException throwables) {
                throwables.printStackTrace ( );
            }

        }
        else{
            popUp ( "Invalid! !" ,"\n\n\n\n\n\nYou have entered the incorrect" );
        }
    }

    public void confirmUser ( ) throws SQLException {
        String CUusername = username.getText ( );
        String CUpassword = password.getText ( );

        if ( username.getText ( ).isEmpty ( ) || password.getText ( ).isEmpty ( ) ) {
            popUp ( "Submission Failed!" ,"\n\n\n\n\n\nYou have not filled in both the username and password fields" );
            return;
        }
        if ( !db.checkUserMatchesPass ( CUusername ,CUpassword ) ) {
            popUp ( "Submission Failed!" ,"\n\n\n\n\n\nYou have entered either an incorrect username and password combination"
                                          + "or the account does not exist" );
            return;
        }
        try {
            Map<String, String> usr = db.getUserByUsername ( CUusername );
            Aapp.userType = usr.get ( "USERTYPE" );
            Aapp.username = usr.get ( "USERNAME" );
        } catch (SQLException throwables) {
            throwables.printStackTrace ( );
        }
        goHome();
      /*  smsPane.setVisible ( true );
        String r = "123";
        if ( smsSubmit.isPressed ( ) ) {
            smsSubmit(CUusername);
        }
        else {
            smsCancel( );
        }
    }

       */

    }





   public void  forgottenPassword(){
        sceneSwitch ( "ForgotPassword" );
   }

    public void createNewAccount(){
        sceneSwitch("CreateNewAccount");
    }
}

