package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;


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
        requestFieldList.add(new FieldTemplate<JFXTimePicker>(
                "DELIVERYTIME",
                deliveryTime,
                (a) -> a.getValue().format(DateTimeFormatter.ofPattern("HH.mm")),
                (a) -> a.getValue() != null));
        requestFieldList.add(new FieldTemplate<JFXComboBox<String>>(
                "FOODOPTION",
                foodOptions,
                (a) -> a.getSelectionModel().getSelectedItem(),
                (a) -> a.getSelectionModel().getSelectedItem() != null
        ));
        requestFieldList.add(new FieldTemplate<JFXTextArea>(
                "DIETARYRESTRICTIONS",
                dietaryRestA,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));
        requestFieldList.add(new FieldTemplate<JFXComboBox<String>>(
                "NUMBEROFSERVINGS",
                numberServings,
                (a) -> a.getSelectionModel().getSelectedItem(),
                (a) -> a.getSelectionModel().getSelectedItem() != null
        ));
        requestFieldList.add(new FieldTemplate<JFXTextField>(
                "CONTACTNUMBER",
                contactNumber,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));
        requestFieldList.add(new FieldTemplate<JFXComboBox<String>>(
                "DRINKOPTIONS",
                drinkOptions,
                (a) -> a.getSelectionModel().getSelectedItem(),
                (a) -> a.getSelectionModel().getSelectedItem() != null
        ));

        serviceRequestType = SERVICEREQUEST.FOOD_DELIVERY;

        foodOptions.setItems(FXCollections
                .observableArrayList("Mac and Cheese", "Salad", "Pizza"));

        ArrayList<String> locations = new ArrayList<>();
        try {
            for (Map<String, String> oneNode: db.getNodes()) {
                locations.add(oneNode.get("LONGNAME"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        roomNumber.setItems(FXCollections
                .observableArrayList(locations)
        );
        drinkOptions.setItems(FXCollections
                .observableArrayList("Water", "Coca-Cola", "Sprite", "Milk", "Orange Juice")
        );
        numberServings.setItems(FXCollections
                .observableArrayList("1", "2", "3", "4", "5")
        );
    }

    // new method for getting the submit information regarding the location id into submit
    @FXML
    void submitFood() throws SQLException, IOException {
        String errorVals = "";
        if(roomNumber.getSelectionModel().getSelectedItem().toString().isEmpty()) errorVals = "\n  -LOCATIONID";
        sharedValues.put("LOCATIONID", db.getNodesByValue("LONGNAME",roomNumber.getSelectionModel().getSelectedItem().toString()).get(0).get("NODEID"));
        submit(errorVals);
    }
}

