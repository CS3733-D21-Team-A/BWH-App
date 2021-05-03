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
import java.util.Stack;

public class GenericPage {

    static String currentPage = "GuestMainPage"; //TODO: This will need to change once we get persistent logins set up
    static Stack<String> previousPages = new Stack<>();

    @FXML
    public void sceneSwitch(String target){
        try {
            previousPages.push(currentPage);
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/" + target + ".fxml"));
            Aapp.getPrimaryStage().getScene().setRoot(root);
            currentPage = target;

            System.out.println(previousPages.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void goBack(){
        currentPage = previousPages.pop();
        sceneSwitch(currentPage);
        previousPages.pop(); //switching back adds the page we were just returning from, so we need to remove it
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
        previousPages.clear(); //Need to clear the history when we go home
    }

    /*
    public void menu(){
        if(transition.getRate() == -1) menuDrawer.open();
        else menuDrawer.close();
        transition.setRate(transition.getRate() * -1);
        transition.play();
    }*/
}
