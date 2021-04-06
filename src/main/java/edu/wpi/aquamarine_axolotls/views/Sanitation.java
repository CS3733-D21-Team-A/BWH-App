package edu.wpi.aquamarine_axolotls.views;

import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;

import java.io.IOException;

public class Sanitation extends Service_Request {

    ObservableList<String> service_request_list = FXCollections.observableArrayList("Patient Vacancy", "Trash", "Spill");

    @FXML
    private ComboBox service_requests;


    @FXML
    public void initialize(){
        service_requests.setItems(service_request_list);
    }

}
