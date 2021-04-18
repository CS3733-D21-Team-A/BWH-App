package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class FoodDelivery extends SServiceRequest {

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField deliveryTime;

    @FXML
    private TextField roomNumber;

    @FXML
    private ComboBox foodOptions;

    @FXML
    private AnchorPane myAnchorPane;


    @FXML
    public void initialize() {
        foodOptions.setItems(FXCollections
                .observableArrayList("Mac and Cheese", "Salad", "Pizza"));
    }


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
    public void handleButtonAction(ActionEvent actionEvent) throws IOException {

        if(foodOptions.getSelectionModel().getSelectedItem() == null) return;
        String fn = firstName.getText();
        String ln = lastName.getText();
        String dt = deliveryTime.getText();
        String room = roomNumber.getText();
        String food = foodOptions.getSelectionModel().getSelectedItem().toString();

        if(!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+")
                || dt.isEmpty() || room.isEmpty()){
            return;
        }

        Map<String, String> service = new HashMap<String, String>();
        service.put("SERVICENAME", "Food Delivery");
        service.put("FIRSTNAME", fn);
        service.put("LASTNAME", ln);
        service.put("DELIVERYTIME", dt);
        service.put("ROOMID", room);
        service.put("FOODOPTIONS", food);
        Aapp.serviceRequests.add(service);
        submit(actionEvent);
    }
}
