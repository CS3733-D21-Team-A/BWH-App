package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class LanguageInterpreter extends SServiceRequest {

    ObservableList<String> languageList = FXCollections
            .observableArrayList("Espanol", "Francois", "Deustch");

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private ComboBox language;

    @FXML
    private TextField roomNumber;

    @FXML
    private TextField specInstruct;

    @FXML
    public JFXButton submitB;

    @FXML
    public void initialize(){
        language.setItems(languageList);
    }
}
