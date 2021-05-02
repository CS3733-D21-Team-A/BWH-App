package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.SERVICEREQUEST;
import edu.wpi.aquamarine_axolotls.views.servicerequests.GenericServiceRequest;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class GiftDelivery extends GenericServiceRequest {

    @FXML
    private JFXTimePicker deliveryTime;

    @FXML
    private JFXComboBox locationDropdown;

    @FXML
    private JFXComboBox giftOptions;

    @FXML
    private ArrayList<String> nodeIDS;

    @FXML
    public void initialize()  {
        requestFieldList.add(new FieldTemplate<JFXComboBox<String>>(
                "GIFTTYPE",
                giftOptions,
                (a) -> a.getSelectionModel().getSelectedItem(),
                (a) -> a.getSelectionModel().getSelectedItem() != null
        ));
        requestFieldList.add(new FieldTemplate<String>(
                "NOTE",
                "NOTE",
                (a) -> a,
                (a) -> true
        ));
        requestFieldList.add(new FieldTemplate<JFXTimePicker>(
                "DELIVERYTIME",
                deliveryTime,
                (a) -> a.getValue().format(DateTimeFormatter.ofPattern("HH.mm")),
                (a) -> a.getValue() != null));
        serviceRequestType = SERVICEREQUEST.GIFT_DELIVERY;

        startUp();
        giftOptions.setItems(FXCollections
                .observableArrayList("Hospital T-Shirt", "Teddy Bear", "Hospital Mug"));
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");
        locationDropdown.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        );
    }

}
