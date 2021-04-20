package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import java.io.IOException;

public abstract class SServiceRequest extends SPage{
    @FXML
    public JFXButton backB;
    
    @FXML
    public JFXButton submitB;

    @FXML
    public JFXButton helpB;

    @FXML
    public JFXButton homeB;

    @FXML
    public StackPane stackPane;
    
    public void goHome(ActionEvent actionEvent) {
        sceneSwitch("DefaultServicePage");
    }

    @FXML
    public void submit() throws IOException {
        popUp("Submission Successful" ,"\nSubmission Success!\nYour information has successfully been submitted.\n");
        sceneSwitch("DefaultServicePage");
    }

    @FXML
    public void errorFields(String reqFields) throws IOException {
        popUp("ERROR" ,"\nThe submission has not been made...\nPlease fill in the following fields.\n" + reqFields);
    }


    @FXML
    public void loadHelp() throws IOException{
        popUp("Helpful information:","\n\n\n\nPlease provide your first name, last name, time you would like to receive the request patient's room number, and an optional message ");
    }
    @FXML
    public void popUp(String title, String disp){
        final Stage myDialog = new Stage();
        myDialog.initModality(Modality.APPLICATION_MODAL);
        myDialog.centerOnScreen();
        myDialog.setTitle(title);
        Text text1 = new Text(disp);
        text1.setStyle("-fx-font-size: 20; -fx-fill: black; -fx-font-family: Times; -fx-alignment: center");
        TextFlow textFlow = new TextFlow(text1);
        textFlow.setTextAlignment(TextAlignment.CENTER);
        myDialog.setScene(new Scene(textFlow, 400, 300));
        myDialog.show();
    }

}
