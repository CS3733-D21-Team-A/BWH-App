package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;


public class LanguageInterpreter extends GenericServiceRequest {

    @FXML
    private JFXComboBox languageSelect;
    @FXML
    private JFXTextArea preferences;
    @FXML
    private JFXComboBox roomNumber;
    @FXML
    private JFXTextField contactNumber;



    @FXML
    public void initialize() {
        requestFieldList.add(new FieldTemplate<JFXComboBox<String>>(
                "LANGUAGE",
                languageSelect,
                (a) -> a.getSelectionModel().getSelectedItem(),
                (a) -> a.getSelectionModel().getSelectedItem() != null
        ));
        requestFieldList.add(new FieldTemplate<JFXTextArea>(
                "NOTE",
                preferences,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));
        requestFieldList.add(new FieldTemplate<JFXTextField>(
                "CONTACTNUMBER",
                contactNumber,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));

        serviceRequestType = SERVICEREQUEST.LANGUAGE_INTERPRETER;

        languageSelect.setItems(FXCollections
                .observableArrayList("Espanol", "Portugues", "Francais", "Polskie"));

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
    void submitLanguage() throws SQLException, IOException {
        String errorVals = "";
        if(roomNumber.getSelectionModel().getSelectedItem().toString().isEmpty()) errorVals = "\n  -LOCATIONID";
        sharedValues.put("LOCATIONID", db.getNodesByValue("LONGNAME",roomNumber.getSelectionModel().getSelectedItem().toString()).get(0).get("NODEID"));
        submit(errorVals);
    }

}