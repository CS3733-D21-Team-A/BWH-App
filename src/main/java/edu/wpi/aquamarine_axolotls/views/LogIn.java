package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.Aapp;
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


public class LogIn extends SPage{

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXButton backB;

    @FXML private JFXTextField username;

    @FXML private JFXPasswordField password;

    @FXML
    public void submit_button(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/PatientMainPage.fxml"));
            Aapp.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @FXML
    public void initialize() {
    }


/*
    public void confirmUser(){
        String CUusername = username.getText();
        String CUpassword = password.getText();
        if(db.checkUserMatchesPass(CUusername,CUpassword)){
            popUp("Submission Success!", "\n\n\n\n\n\nYou have entered the correct credentials");
        }else
        {popUp("Submission Failed!", "\n\n\n\n\n\nYou have entered either an incorrect username and password combination
        , or the account does not exist");
        }
 */

    /*
   public void  forgottenPassword(){
       Stage s = new Stage();
       // set title for the stage
       s.setTitle("creating textInput dialog");
       // create a tile pane
       TilePane r = new TilePane();

       // create a label to show the input in text dialog
       Label l = new Label("no text input");

       // create a text input dialog
       JFXDialog td = new JFXDialog();

       // create a button
       JFXButton d = new JFXButton("click");

       // create a event handler
       EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
           public void handle(ActionEvent e)
           {
               // show the text input dialog
               td.showAndWait();

               // set the text of the label
               l.setText(td.getEditor().getText());
           }
       };

       // set on action of event
       d.setOnAction(event);

       // add button and label
       r.getChildren().add(d);
       r.getChildren().add(l);

       // create a scene
       Scene sc = new Scene(r, 500, 300);

       // set the scene
       s.setScene(sc);

       s.show();
   }

 */


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
