package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;


public class LogIn extends SPage{

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXButton backB;

    @FXML private JFXTextField username;

    @FXML private JFXPasswordField password;

    DatabaseController db;

    @FXML
    public void initialize() {
    }


    public void confirmUser ( ) throws SQLException {
        String CUusername = username.getText ( );
        String CUpassword = password.getText ( );

        if ( username.getText ( ).isEmpty ( ) || password.getText ( ).isEmpty ( ) ) {
            popUp ( "Submission Failed!" ,"\n\n\n\n\n\nYou have not filled in both the username and password fields" );
        }
        //   if ( ! db.checkUserMatchesPass ( CUusername ,CUpassword ) ) {
      //  popUp ( "Submission Failed!" ,"\n\n\n\n\n\nYou have entered either an incorrect username and password combination"
         //                             + "or the account does not exist" );
//WHY IS THIS NOT WORKING
        //update user to loggedin    db.getUserByUsername ( CUusername ).set
        //  }
else{
            popUp ( "Submission Success!" ,"\n\n\n\n\n\nYou have entered the correct credentials" );
            try {
                Parent root = FXMLLoader.load ( getClass ( ).getResource ( "/edu/wpi/aquamarine_axolotls/fxml/PatientMainPage.fxml" ) );
                Aapp.getPrimaryStage ( ).getScene ( ).setRoot ( root );
            } catch (IOException ex) {
                ex.printStackTrace ( );
            }
        }
    }




   public void  forgottenPassword(){
        sceneSwitch ( "ForgotPassword" );
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

