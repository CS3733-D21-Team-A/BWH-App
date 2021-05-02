package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.*;
import edu.wpi.aquamarine_axolotls.views.servicerequests.GenericServiceRequest;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class LaundryService extends GenericServiceRequest {

    @FXML
    private JFXTimePicker deliveryTime;

    @FXML
    private JFXComboBox roomNumber;

    @FXML
    private JFXComboBox loadOptions;

    @FXML
    private JFXComboBox detergentType;

    @FXML
    private JFXComboBox articlesOfClothing;


    @FXML
    private JFXTextArea specialRequest;

    @FXML
    private ArrayList<String> nodeIDS;



    @FXML
    public void initialize() { //TODO: fill these out
        requestFieldList.add(new FieldTemplate<JFXTimePicker>(
                "DELIVERYTIME",
                deliveryTime,
                (a) -> a.getValue().format(DateTimeFormatter.ofPattern("HH.mm")),
                (a) -> a.getValue() != null));
        requestFieldList.add(new FieldTemplate<JFXComboBox<String>>(
                "ARTICLESOFCLOTHING",
                articlesOfClothing,
                (a) -> a.getSelectionModel().getSelectedItem(),
                (a) -> a.getSelectionModel().getSelectedItem() != null
        ));
        requestFieldList.add(new FieldTemplate<JFXTextArea>(
                "NOTE",
                specialRequest,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));
        serviceRequestType = SERVICEREQUEST.LAUNDRY;
        startUp();
        loadOptions.setItems(FXCollections
                .observableArrayList("Delicates", "Light", "Heavy"));
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");
        roomNumber.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        );
        detergentType.setItems(FXCollections
                .observableArrayList("Tide","All", "Gain")
        );
        articlesOfClothing.setItems(FXCollections
                .observableArrayList("Work Clothes","T-Shirts", "Under Garments", "Clerical Garb")
        );
    }

}

