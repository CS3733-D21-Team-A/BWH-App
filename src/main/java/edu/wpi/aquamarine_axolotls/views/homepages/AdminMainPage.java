package edu.wpi.aquamarine_axolotls.views.homepages;

import javafx.fxml.FXML;

public class AdminMainPage extends EmployeeMainPage {

    public void initialize() {
        startUp();
    }

    @FXML
    public void nodeP() {
        sceneSwitch("NodeEditing");
    }

    @FXML
    public void addUser() {
        sceneSwitch("AdminNewUser");
    }

}
