package edu.wpi.aquamarine_axolotls.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javax.xml.soap.Text;
import java.awt.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Medicine_Delivery extends Service_Request {

    @FXML
    private Button back_button;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField deliveryTime;

    @FXML
    private TextField roomNumber;

    @FXML
    private TextField specInstruct;

    @FXML
    private Button submitButton;

}
