package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public abstract class SMainPage extends SPage{
    @FXML
    private JFXButton signInB;

    @FXML
    private JFXButton mapB;

    @FXML
    private JFXButton serviceReqB;

    @FXML
    private JFXButton helpB;

    @FXML
    private JFXButton settingsB;

    @FXML
    public void signInP(ActionEvent actionEvent) {
        sceneSwitch("LogIn");
    }

    @FXML
    public void mapP(ActionEvent actionEvent) {
        sceneSwitch("Navigation");

    }

    @FXML
    public void serviceReqP(ActionEvent actionEvent) {
        sceneSwitch("DefaultServicePage");

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

}
