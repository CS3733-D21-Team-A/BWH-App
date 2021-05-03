package edu.wpi.aquamarine_axolotls.views.homepages;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class EmployeeMainPage extends PatientMainPage {
    @FXML
    public void requestP(ActionEvent actionEvent) {
        sceneSwitch("EmployeeRequests");
    }

    @FXML
    public void serviceReqEP(ActionEvent actionEvent) {
        sceneSwitch("EmployeeServiceRequestPage");
    }
}
