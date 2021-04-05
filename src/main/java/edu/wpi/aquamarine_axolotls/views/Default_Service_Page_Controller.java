package edu.wpi.aquamarine_axolotls.views;

import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class Default_Service_Page_Controller {
    public void food_delivery_b(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/Food_Delivery.fxml"));
            Aapp.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void language_interpreter_b(ActionEvent actionEvent) {
    }

    public void sanitation_b(ActionEvent actionEvent) {
    }

    public void laundry_b(ActionEvent actionEvent) {
    }
}
