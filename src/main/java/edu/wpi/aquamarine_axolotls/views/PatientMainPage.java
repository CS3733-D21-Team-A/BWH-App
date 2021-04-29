package edu.wpi.aquamarine_axolotls.views;

import javafx.event.ActionEvent;

public class PatientMainPage extends SMainPage{

    public void goHome(){
        sceneSwitch("PatientMainPage");
    }
    public void covidSurveyPage(ActionEvent actionEvent) {
        sceneSwitch("CovidSurvey");
    }
}
