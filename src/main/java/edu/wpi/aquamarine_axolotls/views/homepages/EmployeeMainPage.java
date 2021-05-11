package edu.wpi.aquamarine_axolotls.views.homepages;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;


public class EmployeeMainPage extends PatientMainPage {
    @FXML StackPane managePane;

    public void initialize() {
        super.initialize();
        managePane.setOnMouseExited(mouseEvent -> {
            if (managePane.isVisible()) {
                managePane.setVisible(false);
                managePane.toBack();
            }
        });
    }

    @FXML
    public void requestP() {
        sceneSwitch("EmployeeRequests");
    }

//    @FXML
//    public void serviceReqEP() {
//        sceneSwitch("EmployeeServiceRequestPage");
//    }

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
