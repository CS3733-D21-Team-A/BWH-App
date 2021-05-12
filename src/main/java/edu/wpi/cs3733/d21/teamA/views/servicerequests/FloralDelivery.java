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
    void submitFloral() throws SQLException, IOException {
        String errorVals = "";
        if(roomNumber.getSelectionModel().getSelectedItem().toString().isEmpty()) errorVals = "\n  -LOCATIONID";
        sharedValues.put("LOCATIONID", db.getNodesByValue("LONGNAME",roomNumber.getSelectionModel().getSelectedItem().toString()).get(0).get("NODEID"));
        submit(errorVals);
    }

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
        flowerOptions.setItems(FXCollections
                .observableArrayList("Roses", "Sunflowers", "Peruvian Lilies", "Hydrangeas", "Orchids")
        );

        vaseOptions.setItems(FXCollections
                .observableArrayList("Bouquet Vase", "Square Vase", "Cylinder Vase", "Milk Bottle", "Pedestal Vase")
        );
    }

}


