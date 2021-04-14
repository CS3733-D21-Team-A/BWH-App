package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class Religious_Request extends Service_Request{
    //ObservableList<String> terminalList = FXCollections
     //       .observableArrayList("Yes", "No");

    //@FXML
    //private ComboBox terminal_illness;

    @FXML
    private JFXButton back_button;

    @FXML
    private JFXTextField firstName;

    @FXML
    private JFXTextField lastName;

    @FXML
    private JFXTextField deliveryTime;

    @FXML
    private JFXTextField roomNumber;


    @FXML
    private JFXTextField specInstruct;

    @FXML
    private JFXButton submitButton;

    public void initialize(){
        //terminal_illness.setItems(terminalList);
    }

    }
