package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ExternalTransport extends GenericServiceRequest {

    @FXML
    private JFXTextField patientFirstName;
    @FXML
    private JFXTextField patientLastName;
    @FXML
    private JFXTextField destination;
    @FXML
    private JFXComboBox<String> modeOfTrans;
    @FXML
    private JFXTimePicker transpTime;
    @FXML
    private JFXComboBox<String> levelOfEmergency;
    @FXML
    private JFXComboBox<String> roomNumber;

    @FXML
    private ArrayList<String> nodeIDS;


    @FXML
    public void initialize() {
        requestFieldList.add(new FieldTemplate<JFXTimePicker>(
                "TRANSPORTTIME",
                transpTime,
                (a) -> a.getValue().format(DateTimeFormatter.ofPattern("HH.mm")),
                (a) -> a.getValue() != null));
        requestFieldList.add(new FieldTemplate<JFXTextField>(
                "DESTINATION",
                destination,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));
        requestFieldList.add(new FieldTemplate<JFXTextField>(
                "PATIENTFIRSTNAME",
                patientFirstName,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));
        requestFieldList.add(new FieldTemplate<JFXTextField>(
                "PATIENTLASTNAME",
                patientLastName,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));
        requestFieldList.add(new FieldTemplate<JFXComboBox<String>>(
                "MODEOFTRANSPORT",
                modeOfTrans,
                (a) -> a.getSelectionModel().getSelectedItem(),
                (a) -> a.getSelectionModel().getSelectedItem() != null
        ));
        requestFieldList.add(new FieldTemplate<JFXComboBox<String>>(
                "EMERGENCYLEVEL",
                levelOfEmergency,
                (a) -> a.getSelectionModel().getSelectedItem(),
                (a) -> a.getSelectionModel().getSelectedItem() != null
        ));

        serviceRequestType = SERVICEREQUEST.EXTERNAL_TRANSPORT;
        startUp();


        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");
        levelOfEmergency.setItems(FXCollections
                .observableArrayList("Very Critical","Critical","Moderately Critical","Non-Emergent")
        );
        modeOfTrans.setItems(FXCollections
                .observableArrayList("Helicopter","Ambulance","Non-Emergent Vehicle Transport")
        );
        roomNumber.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        );
    }

}
