package edu.wpi.aquamarine_axolotls.views;

import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;

public class Language_Interpreter extends Service_Request {

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
    private Button submitButton;

    @FXML
    public void initialize(){
        language.setItems(languageList);
    }
}
