package edu.wpi.aquamarine_axolotls.views.homepages;

import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.sql.SQLException;

public class EmployeeMainPage extends PatientMainPage {
    @FXML StackPane managePane;

    public void initialize() throws IOException, SQLException {
        startUp();
        managePane.setOnMouseExited(mouseEvent -> {
            if (managePane.isVisible()) {
                managePane.setVisible(false);
                managePane.toBack();
            }
        });
    }
    @FXML
    public void requestP(ActionEvent actionEvent) {
        sceneSwitch("EmployeeRequests");
    }

    @FXML
    public void serviceReqEP(ActionEvent actionEvent) {
        sceneSwitch("EmployeeServiceRequestPage");
    }

    @FXML
    public void manageP(){
        managePane.setVisible(true);
        managePane.toFront();

        if (serviceRequestPane.isVisible()){
            serviceRequestPane.setVisible(false);
            serviceRequestPane.toBack();
        }

    }
}
