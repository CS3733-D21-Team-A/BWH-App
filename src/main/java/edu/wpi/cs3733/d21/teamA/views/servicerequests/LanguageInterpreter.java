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
    private JFXComboBox<String> languageSelect;
    @FXML
    private JFXTextArea preferences;
    @FXML
    private JFXTextField contactNumber;



    @FXML
    public void initialize() {
        super.initialize();
        requestFieldList.add(new FieldTemplate<>(
            "LANGUAGE",
            languageSelect,
            (a) -> a.getSelectionModel().getSelectedItem(),
            (a) -> a.getSelectionModel().getSelectedItem() != null
        ));
        requestFieldList.add(new FieldTemplate<>(
            "NOTE",
            preferences,
            (a) -> a.getText(),
            (a) -> !a.getText().isEmpty()
        ));
        requestFieldList.add(new FieldTemplate<>(
            "CONTACTNUMBER",
            contactNumber,
            (a) -> a.getText(),
            (a) -> !a.getText().isEmpty()
        ));

        serviceRequestType = SERVICEREQUEST.LANGUAGE_INTERPRETER;

        languageSelect.setItems(FXCollections.observableArrayList("Espanol", "Portugues", "Francais", "Polskie"));
    }
}