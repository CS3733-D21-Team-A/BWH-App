package edu.wpi.aquamarine_axolotls.views;

import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javax.xml.soap.Text;
import java.awt.*;
import java.io.IOException;

public class Laundry extends Service_Request{

    @FXML
    private Button back_button;

    @FXML
    private TextField firstname;

    @FXML
    private TextField lastName;

    @FXML
    private TextField roomNumber;

    @FXML
    public void return_home(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/Default_Service_Page.fxml"));
            Aapp.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /*


     */


}
