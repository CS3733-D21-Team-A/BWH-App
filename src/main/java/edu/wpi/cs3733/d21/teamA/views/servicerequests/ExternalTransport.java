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

        levelOfEmergency.setItems(FXCollections
                .observableArrayList("Very Critical","Critical","Moderately Critical","Non-Emergent")
        );
        modeOfTrans.setItems(FXCollections
                .observableArrayList("Helicopter","Ambulance","Non-Emergent Vehicle Transport")
        );
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
    }

    @FXML
    void submitExternalTransport() throws SQLException, IOException {
        String errorVals = "";
        if(roomNumber.getSelectionModel().getSelectedItem().toString().isEmpty()) errorVals = "\n  -LOCATIONID";
        sharedValues.put("LOCATIONID", db.getNodesByValue("LONGNAME",roomNumber.getSelectionModel().getSelectedItem().toString()).get(0).get("NODEID"));
        submit(errorVals);
    }
}
