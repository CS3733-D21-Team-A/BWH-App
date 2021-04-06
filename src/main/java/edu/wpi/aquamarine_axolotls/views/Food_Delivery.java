package edu.wpi.aquamarine_axolotls.views;

import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;

public class Food_Delivery extends Service_Request {

    ObservableList<String> foodOptionList = FXCollections
            .observableArrayList("Mac and Cheese", "Salad", "Pizza");

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField deliveryTime;

    @FXML
    private TextField roomNumber;

    @FXML
    private ComboBox foodOptions;

    /*
    @FXML
    public void initialize(){
        foodOptions.setItems(foodOptionList);
    }
    */

}
