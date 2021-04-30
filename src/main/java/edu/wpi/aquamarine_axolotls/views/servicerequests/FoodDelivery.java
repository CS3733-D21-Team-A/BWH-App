package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class FoodDelivery extends GenericServiceRequest {

    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private JFXTimePicker deliveryTime;
    @FXML
    private JFXComboBox roomNumber;
    @FXML
    private JFXComboBox foodOptions;
    @FXML
    private JFXComboBox drinkOptions;
    @FXML
    private JFXComboBox numberServings;
    @FXML
    private JFXTextArea dietaryRestA;
    @FXML
    private ArrayList<String> nodeIDS;
    @FXML
    private JFXTextField contactNumber;


    @FXML
    public void initialize() {
        startUp();
        foodOptions.setItems(FXCollections
                .observableArrayList("Mac and Cheese", "Salad", "Pizza"));
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");
        roomNumber.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk", "Connors Center Security Desk Floor 1")
        );
        drinkOptions.setItems(FXCollections
                .observableArrayList("Water", "Coca-Cola", "Sprite", "Milk", "Orange Juice")
        );
        numberServings.setItems(FXCollections
                .observableArrayList("1", "2", "3", "4", "5")
        );
    }


    @FXML
    public void handleButtonAction(ActionEvent actionEvent) throws IOException {

        if (foodOptions.getSelectionModel().getSelectedItem() == null
                || roomNumber.getSelectionModel().getSelectedItem() == null) {
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }
        String fn = firstName.getText();
        String ln = lastName.getText();
        String dt = deliveryTime.getValue().format(DateTimeFormatter.ofPattern("HH.mm"));
        int room = roomNumber.getSelectionModel().getSelectedIndex();
        String food = foodOptions.getSelectionModel().getSelectedItem().toString();
        String rest = dietaryRestA.getText();
        String servings = numberServings.getSelectionModel().getSelectedItem().toString();
        String cn = contactNumber.getText();
        String dop = drinkOptions.getSelectionModel().getSelectedItem().toString();
        if (!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+")
                || dt.isEmpty()) {
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }

        ArrayList<String> fields = new ArrayList<String>();
        fields.add(cn); // Contact number
        fields.add(dt); // Deiverytime
        fields.add(rest); // dietary restriction
        fields.add(dop); // drink option
        fields.add(food); // food option
        fields.add("NOTE"); // Note // TODO : see if we should remove
        // TODO add location
        fields.add(servings); // number of servicngs

        try {
            createServiceRequest(SERVICEREQUEST.FOOD_DELIVERY, fields);
            submit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

