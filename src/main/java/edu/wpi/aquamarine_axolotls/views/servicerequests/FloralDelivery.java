package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.*;
import edu.wpi.aquamarine_axolotls.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class FloralDelivery extends GenericServiceRequest {

    @FXML
    private JFXTimePicker deliveryTime;
    @FXML private JFXComboBox roomNumber;
    @FXML
    private JFXTextArea persMessage;
    @FXML
    private JFXComboBox flowerOptions;
    @FXML
    private JFXDatePicker deliveryDate;
    @FXML
    private JFXTextField contactNumber;
    @FXML
    private JFXComboBox vaseOptions;


    @FXML
    public void initialize() {
        requestFieldList.add(new FieldTemplate<JFXTimePicker>(
                "DELIVERYTIME",
                deliveryTime,
                (a) -> a.getValue().format(DateTimeFormatter.ofPattern("HH.mm")),
                (a) -> a.getValue() != null));
        requestFieldList.add(new FieldTemplate<JFXDatePicker>(
                "DELIVERYDATE",
                deliveryDate,
                (a) -> a.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                (a) -> a.getValue() != null
        ));
        requestFieldList.add(new FieldTemplate<JFXComboBox<String>>(
                "FLOWEROPTION",
                flowerOptions,
                (a) -> a.getSelectionModel().getSelectedItem(),
                (a) -> a.getSelectionModel().getSelectedItem() != null
        ));
        requestFieldList.add(new FieldTemplate<JFXComboBox<String>>(
                "VASEOPTION",
                vaseOptions,
                (a) -> a.getSelectionModel().getSelectedItem(),
                (a) -> a.getSelectionModel().getSelectedItem() != null
        ));
        requestFieldList.add(new FieldTemplate<JFXTextField>(
                "CONTACTNUMBER",
                contactNumber,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));
        requestFieldList.add(new FieldTemplate<JFXTextArea>(
                "NOTE",
                persMessage,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));

        serviceRequestType = SERVICEREQUEST.FLORAL_DELIVERY;

        roomNumber.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        );
        flowerOptions.setItems(FXCollections
                .observableArrayList("Roses", "Sunflowers", "Peruvian Lilies", "Hydrangeas", "Orchids")
        );

        vaseOptions.setItems(FXCollections
                .observableArrayList("Bouquet Vase", "Square Vase", "Cylinder Vase", "Milk Bottle", "Pedestal Vase")
        );
    }

}


