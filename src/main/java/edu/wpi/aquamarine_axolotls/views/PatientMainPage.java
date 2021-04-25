package edu.wpi.aquamarine_axolotls.views;

import javafx.event.ActionEvent;

public class PatientMainPage extends SMainPage{

    public void goHome(){
        sceneSwitch("PatientMainPage");
    }

    public void signOutPage(){
        popUp("Sign Out", "\n\n\n\n\nYou have been signed out of your account.");
        sceneSwitch("GuestMainPage");
    }
    public void covidSurveyPage(ActionEvent actionEvent) {
        sceneSwitch("CovidSurvey");
    }


}
