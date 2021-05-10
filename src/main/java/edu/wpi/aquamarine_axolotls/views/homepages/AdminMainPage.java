package edu.wpi.aquamarine_axolotls.views.homepages;

import javafx.fxml.FXML;

import java.awt.event.ActionEvent;

public class AdminMainPage extends EmployeeMainPage {

    public void initialize() {
        startUp();
    }

    @FXML

    public void nodeP() {
        sceneSwitch("MapEditing");
    }

    @FXML
    public void addUser() {
        sceneSwitch("AdminNewUser");
    }

}
