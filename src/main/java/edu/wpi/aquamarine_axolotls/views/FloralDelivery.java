package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import edu.wpi.aquamarine_axolotls.Aapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    // TODO: Filter out bad input
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

    @FXML
    public void handleButtonAction(javafx.event.ActionEvent actionEvent) throws IOException {
        String fn = firstName.getText();
        String ln = lastName.getText();
        String dt = deliveryTime.getText();
        String room = roomNumber.getText();
        String pmsg = persMessage.getText();

        //TODO: make pop up here
        if(!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+") || dt.isEmpty()) return;

        Map<String, String> service = new HashMap<String, String>();
        service.put("SERVICENAME", "Floral Delivery");
        service.put("FIRSTNAME", fn);
        service.put("LASTNAME", ln);
        service.put("DELIVERYTIME", dt);
        service.put("ROOMID", room);
        service.put("NOTE", pmsg);
        Aapp.serviceRequests.add(service);
        submit(actionEvent);
    }



}


