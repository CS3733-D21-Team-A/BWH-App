package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class log_in {

@FXML
private JFXComboBox emp_or_pat;
ObservableList<String> emp_patList =  FXCollections
        .observableArrayList("Patient", "Employee");

    @FXML
    public void submit_button(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/Admin_Main_Page.fxml"));
            Aapp.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    @FXML
    public void initialize(){
        emp_or_pat.setItems(emp_patList);
    }


}
