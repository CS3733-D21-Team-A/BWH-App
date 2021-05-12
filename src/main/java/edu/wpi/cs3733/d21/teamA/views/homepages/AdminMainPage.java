package edu.wpi.cs3733.d21.teamA.views.homepages;

import javafx.fxml.FXML;

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
