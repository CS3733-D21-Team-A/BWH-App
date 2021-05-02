package edu.wpi.aquamarine_axolotls.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
//import javafx.application.Platform.exit;
//import javafx.application.Platform.exit;


public class AdminMainPage extends EmployeeMainPage {

    @FXML
    public void nodeP(ActionEvent actionEvent) {
        sceneSwitch("NodeEditing");
    }

    public void addUser(ActionEvent actionEvent) {
        sceneSwitch("AdminNewUser");
    }

}
