package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;


public class InternalTransportation extends GenericServiceRequest {

    @FXML
    private JFXTextField patientFirstName;

    @FXML
    private JFXTextField patientLastName;

    @FXML
    private JFXTextField currentRoom;

    @FXML
    private JFXTextField newRoom;

    @FXML
    private ArrayList<String> nodeIDS;


    @FXML
    public void initialize() {
        requestFieldList.add(new FieldTemplate<JFXTextField>(
                "CURRENTLOCATION",
                currentRoom,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));
        requestFieldList.add(new FieldTemplate<JFXTextField>(
                "NEWLOCATION",
                newRoom,
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

        serviceRequestType = SERVICEREQUEST.INTERNAL_TRANSPORT;


    }

    @FXML
    void submitInternalTransport() throws SQLException, IOException {
        submit("");
    }
}
