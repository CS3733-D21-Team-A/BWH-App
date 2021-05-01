package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.Aapp;
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
    private JFXTimePicker deliveryTime;
    @FXML
    private JFXComboBox roomNumber;
    @FXML
    private JFXComboBox<String> foodOptions;
    @FXML
    private JFXComboBox<String> drinkOptions;
    @FXML
    private JFXComboBox<String> numberServings;
    @FXML
    private JFXTextArea dietaryRestA;
    @FXML
    private ArrayList<String> nodeIDS;
    @FXML
    private JFXTextField contactNumber;


    @FXML
    public void initialize() {
        requestFieldList.add(new FieldTemplate<JFXTimePicker>("DELIVERYTIME", deliveryTime,(a) -> a.getValue().format(DateTimeFormatter.ofPattern("HH.mm")), (a) -> a.getValue() != null));
        requestFieldList.add(new FieldTemplate<JFXComboBox<String>>("FOODOPTION", foodOptions, (a) -> a.getSelectionModel().getSelectedItem()));
        requestFieldList.add(new FieldTemplate<JFXTextArea>("DIETARYRESTRICTIONS", dietaryRestA, (a) -> a.getText()));
        requestFieldList.add(new FieldTemplate<JFXComboBox<String>>("NUMBEROFSERVINGS", numberServings, (a) -> a.getSelectionModel().getSelectedItem()));
        requestFieldList.add(new FieldTemplate<JFXTextField>("CONTACTNUMBER", contactNumber, (a) -> a.getText()));
        requestFieldList.add(new FieldTemplate<JFXComboBox<String>>("DRINKOPTIONS", drinkOptions, (a) -> a.getSelectionModel().getSelectedItem()));

        serviceRequestType = SERVICEREQUEST.FOOD_DELIVERY;
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




/*

    @FXML
    public void handleButtonAction(ActionEvent actionEvent) throws IOException, SQLException{
        submit();
    }
    @FXML
    public void handleButtonAction(ActionEvent actionEvent) throws IOException {
        StringBuilder errorMessage = new StringBuilder();
        for(FieldTemplate field : requestFieldList){
            if(!field.checkSyntax()) errorMessage.append("\n  -" + field.getColumn());
        }
        if(errorMessage.length() != 0) errorFields( "Please fix the following fields..."+ errorMessage.toString());

        if (foodOptions.getSelectionModel().getSelectedItem() == null
                || roomNumber.getSelectionModel().getSelectedItem() == null || deliveryTime.getValue() == null) {
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }
*//*        String fn = firstName.getText();
        String ln = lastName.getText();
        String dt = deliveryTime.getValue().format(DateTimeFormatter.ofPattern("HH.mm"));
        *//**//*int room = roomNumber.getSelectionModel().getSelectedIndex();
        String food = foodOptions.getSelectionModel().getSelectedItem().toString();
        String rest = dietaryRestA.getText();
        String servings = numberServings.getSelectionModel().getSelectedItem().toString();
        String cn = contactNumber.getText();
        String dop = drinkOptions.getSelectionModel().getSelectedItem().toString();*//**//* //TODO: REFACTOR SYNTAX CHECKING*//*
*//*        if (!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+")
                || dt.isEmpty()) {
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }*//*

        try {
            submit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

}

