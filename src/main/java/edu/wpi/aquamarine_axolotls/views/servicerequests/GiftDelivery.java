package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;


public class GiftDelivery extends GenericServiceRequest {

    @FXML
    private JFXDatePicker deliveryDate;

    @FXML
    private JFXComboBox deliveryLocation;

    @FXML
    private JFXComboBox giftOptions;

    @FXML
    private JFXTextArea persMessage;

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
        requestFieldList.add(new FieldTemplate<JFXTextArea>(
                "NOTE",
                persMessage,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));
        requestFieldList.add(new FieldTemplate<JFXDatePicker>(
                "DELIVERYDATE",
                deliveryDate,
                (a) -> a.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                (a) -> a.getValue() != null));
        serviceRequestType = SERVICEREQUEST.GIFT_DELIVERY;


        giftOptions.setItems(FXCollections
                .observableArrayList("Hospital T-Shirt", "Teddy Bear", "Hospital Mug"));
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");
        ArrayList<String> locations = new ArrayList<>();
        try {
            for (Map<String, String> oneNode: db.getNodes()) {
                locations.add(oneNode.get("LONGNAME"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        deliveryLocation.setItems(FXCollections.observableArrayList(locations));
    }

    @FXML
    void submitGift() throws SQLException, IOException {
        String errorVals = "";
        if(deliveryLocation.getSelectionModel().getSelectedItem().toString().isEmpty()) errorVals = "\n  -LOCATIONID";
        sharedValues.put("LOCATIONID", db.getNodesByValue("LONGNAME",deliveryLocation.getSelectionModel().getSelectedItem().toString()).get(0).get("NODEID"));
        submit(errorVals);
    }

}
