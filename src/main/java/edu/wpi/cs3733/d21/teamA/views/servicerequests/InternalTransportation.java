package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;


public class InternalTransportation extends GenericServiceRequest {

    @FXML
    private JFXTextField patientFirstName;

    @FXML
    private JFXTextField patientLastName;

    @FXML
    private JFXComboBox<String> newLocation;


    @FXML
    public void initialize() {
        super.initialize();
        try {
            newLocation.setItems(FXCollections.observableArrayList(db.getNodes().stream().map(node -> node.get("LONGNAME")).collect(Collectors.toList())));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        requestFieldList.add(new FieldTemplate<>(
            "NEWLOCATION",
            newLocation,
            (a) -> a.getSelectionModel().getSelectedItem(),
            (a) -> a.getSelectionModel().getSelectedItem() != null
        ));

        requestFieldList.add(new FieldTemplate<>(
            "PATIENTFIRSTNAME",
            patientFirstName,
            (a) -> a.getText(),
            (a) -> !a.getText().isEmpty()
        ));

        requestFieldList.add(new FieldTemplate<>(
            "PATIENTLASTNAME",
            patientLastName,
            (a) -> a.getText(),
            (a) -> !a.getText().isEmpty()
        ));

        serviceRequestType = SERVICEREQUEST.INTERNAL_TRANSPORT;
    }
}
