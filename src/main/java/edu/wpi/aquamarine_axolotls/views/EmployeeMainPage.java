package edu.wpi.aquamarine_axolotls.views;

import javafx.event.ActionEvent;

public class EmployeeMainPage extends PatientMainPage {

    public void requestP(ActionEvent actionEvent) {
        sceneSwitch("EmployeeRequests");
    }

    public void serviceReqEP(ActionEvent actionEvent) {
        sceneSwitch("EmployeeServiceRequestPage");
    }


}
