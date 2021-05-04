package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class MedicineDelivery extends GenericServiceRequest {
    @FXML
    private JFXTextField docFirstName;
    @FXML
    private JFXTextField docLastName;
    @FXML
    private JFXTextField medication;
    @FXML
    private JFXTextField doseSize;
    @FXML
    private JFXTimePicker deliveryTime;
    @FXML
    private JFXComboBox roomNumber;
    @FXML
    private ArrayList<String> nodeIDS;


    @FXML
    public void initialize() {
        requestFieldList.add(new FieldTemplate<JFXTextField>(
                "MEDICATION",
                medication,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));

        requestFieldList.add(new FieldTemplate<JFXTextField>(
                "DOSAGE",
                doseSize,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));

        requestFieldList.add(new FieldTemplate<JFXTimePicker>(
                "DELIVERYTIME",
                deliveryTime,
                (a) -> a.getValue().format(DateTimeFormatter.ofPattern("HH.mm")),
                (a) -> a.getValue() != null
        ));

        requestFieldList.add(new FieldTemplate<JFXTextField>(
                "DOCFIRSTNAME",
                docFirstName,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));

        requestFieldList.add(new FieldTemplate<JFXTextField>(
                "DOCLASTNAME",
                docLastName,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));

        serviceRequestType = SERVICEREQUEST.MEDICINE_DELIVERY;
        startUp();
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");
        roomNumber.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        );
    }

}
