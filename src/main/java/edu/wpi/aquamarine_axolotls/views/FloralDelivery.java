package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import edu.wpi.aquamarine_axolotls.Aapp;


public class FloralDelivery extends SServiceRequest {

    @FXML
    private JFXTextField firstName;

    @FXML
    private JFXTextField lastName;

    @FXML
    private JFXTextField deliveryTime;

    @FXML
    private JFXTextField roomNumber;

    @FXML
    private JFXTextField persMessage;

    @FXML
    private AnchorPane myAnchorPane;

    @FXML
    public void loadHelp(javafx.event.ActionEvent event) {
        JFXDialogLayout content = new JFXDialogLayout();

    JFXDialog help = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.BOTTOM);
    content.setHeading(new Text("Help Page"));
    content.setBody(new Text("Help Page Information:"));

        JFXButton exit_button = new JFXButton("Close");
        exit_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                help.close();
            }
        });

    content.setActions(exit_button);
    help.show();
    }



}


