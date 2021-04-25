package edu.wpi.aquamarine_axolotls.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GuestMainPage extends SMainPage {
    public void goHome() {
        sceneSwitch("GuestMainPage");
    }


    @FXML
    public void initialize() {
        popUp("Fill out Covid", "fill it out");
    }

    public void covidSurveyPage(ActionEvent actionEvent) {
        sceneSwitch("CovidSurvey");
    }


}
