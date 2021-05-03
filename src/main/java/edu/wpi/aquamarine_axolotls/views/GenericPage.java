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

import javax.swing.*;
import java.io.IOException;

public class GenericPage {

    static String previousPage; // TODO: set previous page
    //TODO: change this to linked list for traversing backwards multiple pages

    @FXML
    public void sceneSwitch(String target){
        try {
            Aapp.prevPage = Aapp.currPage; //TODO: Store these in this class, not Aapp
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/" + target + ".fxml"));
            Aapp.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void goBack(String currentPage){
        //this should b done in initialize, is there a way to get the current pg we are on w/o passing it in
        //TODO: This shouldn't need the current page. Back goes to the previous page in the heirarchy, not alternate with the last page
        Aapp.prevPage = Aapp.currPage;
        Aapp.currPage = currentPage;
        sceneSwitch ( Aapp.prevPage);
        // TODO: implement this

    }
    @FXML  //TODO: make this look better
    public void popUp(String title, String disp){
        final Stage myDialog = new Stage();
        myDialog.initModality(Modality.APPLICATION_MODAL);
        myDialog.centerOnScreen();
        myDialog.setTitle(title);
        Text text1 = new Text(disp);
        text1.setStyle("-fx-font-size: 20; -fx-fill: blue; -fx-font-background: cream; -fx-font-family: Baskerville; -fx-alignment: center");
        TextFlow textFlow = new TextFlow(text1);
        textFlow.setTextAlignment(TextAlignment.CENTER);
        myDialog.setScene(new Scene(textFlow, 400, 300));
        myDialog.show();
    }

    @FXML
    public void goHome() {
        switch (Aapp.userType) {
            case "Employee":
                sceneSwitch("EmployeeMainPage");
                break;
            case "Patient":
                sceneSwitch("PatientMainPage");
                break;
            case "Admin":
                sceneSwitch("AdminMainPage");
                break;
            default:
                sceneSwitch("GuestMainPage");
                break;
        }
    }

    /*
    public void menu(){
        if(transition.getRate() == -1) menuDrawer.open();
        else menuDrawer.close();
        transition.setRate(transition.getRate() * -1);
        transition.play();
    }*/
}
