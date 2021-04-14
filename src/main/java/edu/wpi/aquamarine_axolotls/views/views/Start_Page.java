package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import com.jfoenix.controls.JFXButton;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
//import javafx.application.Platform.exit;
//import javafx.application.Platform.exit;
import java.io.IOException;

public class Start_Page {
    @FXML
    private JFXButton sign_in_button;

    @FXML
    private JFXButton map_button;

    @FXML
    private JFXButton service_req;

    @FXML
    private JFXButton help_button;

    @FXML
    private JFXButton settings_button;

    @FXML
    private StackPane stackPane;

    @FXML
    public void Sign_In_Button(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/log_in.fxml"));
            Aapp.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @FXML
    public void service_request_page(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/Default_Service_Page.fxml"));
            Aapp.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }


    public void stop(javafx.event.ActionEvent event){
        JFXDialogLayout content = new JFXDialogLayout();
        JFXDialog help = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.BOTTOM);
        content.setHeading(new Text("Shut Down Application"));
        content.setBody(new Text("Clicking the Shut Down button will close the application."));
        JFXButton shutDownButton = new JFXButton("Shut Down");
        JFXButton cancel_button = new JFXButton( "Cancel");
        shutDownButton.setOnAction(new EventHandler<ActionEvent>() {
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
        content.setActions(cancel_button, shutDownButton);
        help.show();
    }

}
