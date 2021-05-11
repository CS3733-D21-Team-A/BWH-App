package edu.wpi.aquamarine_axolotls.views.homepages;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class AdminMainPage extends EmployeeMainPage {

    @FXML

    public void nodeP() {
        sceneSwitch("MapEditing");
    }

    @FXML
    public void addUser() {
        sceneSwitch("AdminNewUser");
    }

}
