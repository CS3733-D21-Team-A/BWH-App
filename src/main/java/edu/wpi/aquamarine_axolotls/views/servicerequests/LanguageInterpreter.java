package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.*;
import edu.wpi.aquamarine_axolotls.views.servicerequests.GenericServiceRequest;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


public class LanguageInterpreter extends GenericServiceRequest {

    @FXML
    public JFXComboBox languageSelect;

    @FXML
    public JFXTextArea preferences;

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
        startUp();
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