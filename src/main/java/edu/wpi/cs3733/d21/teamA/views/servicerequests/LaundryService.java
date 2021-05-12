package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class LaundryService extends GenericServiceRequest {

    @FXML
    private JFXTimePicker deliveryTime;
    @FXML
    private JFXComboBox roomNumber;
    @FXML
    private JFXComboBox loadOptions;
    @FXML
    private JFXComboBox detergentType;
    @FXML
    private JFXComboBox articlesOfClothing;
    @FXML
    private JFXTextArea specialRequest;


    @FXML
    public void initialize() { //TODO: fill these out
        requestFieldList.add(new FieldTemplate<JFXTimePicker>(
                "DELIVERYTIME",
                deliveryTime,
                (a) -> a.getValue().format(DateTimeFormatter.ofPattern("HH.mm")),
                (a) -> a.getValue() != null));
        requestFieldList.add(new FieldTemplate<JFXComboBox<String>>(
                "ARTICLESOFCLOTHING",
                articlesOfClothing,
                (a) -> a.getSelectionModel().getSelectedItem(),
                (a) -> a.getSelectionModel().getSelectedItem() != null
        ));
        requestFieldList.add(new FieldTemplate<JFXComboBox<String>>(
                "DETERGENTTYPE",
                detergentType,
                (a) -> a.getSelectionModel().getSelectedItem(),
                (a) -> a.getSelectionModel().getSelectedItem() != null
        ));
        requestFieldList.add(new FieldTemplate<JFXComboBox<String>>(
                "LOADOPTION",
                loadOptions,
                (a) -> a.getSelectionModel().getSelectedItem(),
                (a) -> a.getSelectionModel().getSelectedItem() != null
        ));
        requestFieldList.add(new FieldTemplate<JFXTextArea>(
                "NOTE",
                specialRequest,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));
        serviceRequestType = SERVICEREQUEST.LAUNDRY;

        loadOptions.setItems(FXCollections
                .observableArrayList("Delicates", "Light", "Heavy"));
        roomNumber.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        );
        detergentType.setItems(FXCollections
                .observableArrayList("Tide","All", "Gain")
        );
        articlesOfClothing.setItems(FXCollections
                .observableArrayList("Work Clothes","T-Shirts", "Under Garments", "Clerical Garb")
        );
    }

    // new method for getting the submit information regarding the location id into submit
    @FXML
    void submitLaundry() throws SQLException, IOException{
        String errorVals = "";
        if(roomNumber.getSelectionModel().getSelectedItem().toString().isEmpty()) errorVals = "\n  -LOCATIONID";
        sharedValues.put("LOCATIONID", db.getNodesByValue("LONGNAME",roomNumber.getSelectionModel().getSelectedItem().toString()).get(0).get("NODEID"));
        submit(errorVals);
    }

}

