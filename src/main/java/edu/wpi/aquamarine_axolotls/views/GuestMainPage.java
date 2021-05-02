package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GuestMainPage extends GenericPage {
    @FXML
    public StackPane stackPane;

    @FXML
    public void signInP(ActionEvent actionEvent) {
        sceneSwitch("LogIn");
    }

    @FXML
    public void mapP(ActionEvent actionEvent) {
        sceneSwitch("Navigation");
    }

    public void stop(javafx.event.ActionEvent event){
        JFXDialogLayout content = new JFXDialogLayout();
        JFXDialog help = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.BOTTOM);
        content.setHeading(new Text("Shut Down Application"));
        content.setBody(new Text("Clicking the Shut Down button will close the application."));
        JFXButton shutDownB = new JFXButton("Shut Down");
        JFXButton cancel_button = new JFXButton( "Cancel");
        shutDownB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                help.close();
                Platform.exit();
            }
        });
        cancel_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                help.close();
            }
        });
        content.setActions(cancel_button, shutDownB);
        help.show();
    }

    public void covidSurveyPage(ActionEvent actionEvent) {
        sceneSwitch("CovidSurvey");
    }
}
