package edu.wpi.aquamarine_axolotls.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Sanitation extends SServiceRequest {

    ObservableList<String> sanitationReqL = FXCollections
            .observableArrayList("Patient Vacancy", "Trash", "Spill");

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
    private ComboBox sanitationL;

    @FXML
    private TextField descriptionInput;

    @FXML
    private Button uploadImage;

    @FXML
    public void initialize(){
        sanitationL.setItems(sanitationReqL);
    }

}
