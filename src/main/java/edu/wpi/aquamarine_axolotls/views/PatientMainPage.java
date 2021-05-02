package edu.wpi.aquamarine_axolotls.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class PatientMainPage extends GuestMainPage{

    @FXML
    public void serviceReqP(ActionEvent actionEvent) {
        sceneSwitch("DefaultServicePage");
    }

}
