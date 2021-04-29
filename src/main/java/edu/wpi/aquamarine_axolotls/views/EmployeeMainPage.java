package edu.wpi.aquamarine_axolotls.views;

import edu.wpi.aquamarine_axolotls.views.SMainPage;
import javafx.event.ActionEvent;

public class EmployeeMainPage extends SMainPage {

    public void goHome(){
        sceneSwitch("PatientMainPage");
    }

    public void covidSurveyPage(ActionEvent actionEvent) {
        sceneSwitch("CovidSurvey");
    }

    public void employeeP(ActionEvent actionEvent) {
        sceneSwitch("EmployeeRequests");
    }

    public void serviceReqEP(ActionEvent actionEvent) {
        sceneSwitch("EmployeeServiceRequestPage");
    }


}
