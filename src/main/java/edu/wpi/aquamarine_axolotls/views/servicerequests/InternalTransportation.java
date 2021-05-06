package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.db.enums.SERVICEREQUEST;
import javafx.fxml.FXML;

import java.util.ArrayList;


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
        startUp(); // TODO : whos name should eb on the request?
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");

    }

}
