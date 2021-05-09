package edu.wpi.aquamarine_axolotls.views.homepages;

import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;


public class AdminMainPage extends EmployeeMainPage {

    public void initialize() throws IOException, SQLException {
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
