package edu.wpi.aquamarine_axolotls.views.homepages;

import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class PatientMainPage extends GuestMainPage{

    @FXML
    public void serviceReqP(ActionEvent actionEvent) {
        sceneSwitch("DefaultServicePage");
    }

    public void signOutPage(){
        popUp("Sign Out", "\n\n\n\n\nYou have been signed out of your account.");
        Aapp.username = null;
        Aapp.userType = "Guest";
        sceneSwitch("GuestMainPage");
    }

}
