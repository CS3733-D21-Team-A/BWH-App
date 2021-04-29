package edu.wpi.aquamarine_axolotls.views;

import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class GenericPage {

    String previousPage; // TODO: set previous page

    @FXML
    public void sceneSwitch(String target){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/" + target + ".fxml"));
            Aapp.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void goBack(){
        // TODO: implement this
    }
    @FXML  //TODO: make this look better
    public void popUp(String title, String disp){
        final Stage myDialog = new Stage();
        myDialog.initModality(Modality.APPLICATION_MODAL);
        myDialog.centerOnScreen();
        myDialog.setTitle(title);
        Text text1 = new Text(disp);
        text1.setStyle("-fx-font-size: 20; -fx-fill: blue; -fx-font-family: Comic Sans; -fx-alignment: center");
        TextFlow textFlow = new TextFlow(text1);
        textFlow.setTextAlignment(TextAlignment.CENTER);
        myDialog.setScene(new Scene(textFlow, 400, 300));
        myDialog.show();
    }

    // TODO : Logout/closedown application on every page
    public void signOutPage(){
        popUp("Sign Out", "\n\n\n\n\nYou have been signed out of your account.");
        Aapp.username = null;
        Aapp.userType = "Guest";
        sceneSwitch("GuestMainPage");
    }

    /*    @FXML
    public void goHome() {
        if(Aapp.userType.equals("Employee")) sceneSwitch("EmployeeMainPage");
        else if(Aapp.userType.equals("Patient")) sceneSwitch("PatientMainPage");
        else if(Aapp.userType.equals("Admin")) sceneSwitch("AdminMainPage");
        else{ sceneSwitch ( "GuestMainPage" ); }
    }*/

/*    @FXML
    public void defaultServicePageP(ActionEvent actionEvent) {
        sceneSwitch("DefaultServicePage");

    }
    @FXML
    public void mapEditP(ActionEvent actionEvent) {
        sceneSwitch("NodeEditing");
    }

    @FXML
    public void mapP(ActionEvent actionEvent) {
        sceneSwitch("Navigation");

    }*/
}
