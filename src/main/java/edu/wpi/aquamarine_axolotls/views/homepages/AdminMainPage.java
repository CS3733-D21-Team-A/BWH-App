package edu.wpi.aquamarine_axolotls.views.homepages;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
//import javafx.application.Platform.exit;
//import javafx.application.Platform.exit;


public class AdminMainPage extends EmployeeMainPage {
    @FXML
    public void nodeP(ActionEvent actionEvent) {
        sceneSwitch("NodeEditing");
    }

    @FXML
    public void addUser(ActionEvent actionEvent) {
        sceneSwitch("AdminNewUser");
    }

}
