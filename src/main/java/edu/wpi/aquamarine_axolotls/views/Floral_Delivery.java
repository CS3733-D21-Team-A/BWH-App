package edu.wpi.aquamarine_axolotls.views;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.xml.soap.Text;

public class Floral_Delivery extends Service_Request{

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
    private TextField persMessage;

    @FXML
    private Button submitButton;
}
