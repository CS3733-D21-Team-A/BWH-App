package edu.wpi.aquamarine_axolotls.views;

import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import com.jfoenix.controls.JFXButton;

import java.io.IOException;

public class Start_Page {
    @FXML
    private JFXButton sign_in_button;

    @FXML
    private JFXButton map_button;

    @FXML
    private JFXButton service_req;

    @FXML
    private JFXButton help_button;

    @FXML
    private JFXButton settings_button;
    @FXML
    public void Sign_In_Button(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/Log_In.fxml"));
            Aapp.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @FXML
    public void service_request_page(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/Default_Service_Page.fxml"));
            Aapp.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
