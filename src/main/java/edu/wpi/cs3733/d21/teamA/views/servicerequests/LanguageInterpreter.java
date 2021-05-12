package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.util.ArrayList;


public class LanguageInterpreter extends GenericServiceRequest {

    @FXML
    private JFXComboBox languageSelect;
    @FXML
    private JFXTextArea preferences;
    @FXML
    private JFXComboBox roomNumber;
    @FXML
    private ArrayList<String> nodeIDS;
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
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");
        roomNumber.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        );
    }

}