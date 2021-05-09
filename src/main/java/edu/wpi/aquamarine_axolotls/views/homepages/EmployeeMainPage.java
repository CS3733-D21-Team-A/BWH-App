package edu.wpi.aquamarine_axolotls.views.homepages;

import javafx.fxml.FXML;


public class EmployeeMainPage extends PatientMainPage {

    public void initialize() {
        startUp();
    }

    @FXML
    public void requestP() {
        sceneSwitch("EmployeeRequests");
    }

    @FXML
    public void serviceReqEP() {
        sceneSwitch("EmployeeServiceRequestPage");
    }
}
