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
    // new method for getting the submit information regarding the location id into submit
    @FXML
    void submitMedicine() throws SQLException, IOException {
        String errorVals = "";
        if(roomNumber.getSelectionModel().getSelectedItem().toString().isEmpty()) errorVals = "\n  -LOCATIONID";
        sharedValues.put("LOCATIONID", db.getNodesByValue("LONGNAME",roomNumber.getSelectionModel().getSelectedItem().toString()).get(0).get("NODEID"));
        submit(errorVals);
    }

}
