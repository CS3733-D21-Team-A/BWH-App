package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

public class ReligiousRequest extends SServiceRequest {
    //ObservableList<String> terminalList = FXCollections
     //       .observableArrayList("Yes", "No");

    //@FXML
    //private ComboBox terminal_illness;

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


    public void initialize(){
        //terminal_illness.setItems(terminalList);
    }

    }
