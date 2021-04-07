package edu.wpi.aquamarine_axolotls.views;

import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;

public class Sanitation extends Service_Request {

    ObservableList<String> service_request_list = FXCollections.observableArrayList("Patient Vacancy", "Trash", "Spill");



    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField locationInput;

    @FXML
    private RadioButton isCovidRelated;

    @FXML
    private RadioButton isBiohazard;

    @FXML
    private ComboBox serviceRequests;

    @FXML
    private TextField descriptionInput;

    @FXML
    private Button uploadImage;

    @FXML
    private Button submitButton;


    @FXML
    public void initialize(){
        serviceRequests.setItems(service_request_list);
    }

}
