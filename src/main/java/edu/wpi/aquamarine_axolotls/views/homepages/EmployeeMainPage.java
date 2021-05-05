package edu.wpi.aquamarine_axolotls.views.homepages;

import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class EmployeeMainPage extends PatientMainPage {

    public void initialize(){
        userNameText.setText ( "EMPLOYEE: " + Aapp.username );
    }
    @FXML
    public void requestP(ActionEvent actionEvent) {
        sceneSwitch("EmployeeRequests");
    }

    @FXML
    public void serviceReqEP(ActionEvent actionEvent) {
        sceneSwitch("EmployeeServiceRequestPage");
    }
}
