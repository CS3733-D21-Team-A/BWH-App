package edu.wpi.aquamarine_axolotls.views.homepages;

import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;
//import javafx.application.Platform.exit;
//import javafx.application.Platform.exit;


public class AdminMainPage extends EmployeeMainPage {

    public void initialize() throws IOException, SQLException {
        startUp();
        userNameText.setText ( "ADMIN: " + Aapp.username );
    }
    @FXML
    public void nodeP(ActionEvent actionEvent) {
        sceneSwitch("NodeEditing");
    }

    @FXML
    public void addUser(ActionEvent actionEvent) {
        sceneSwitch("AdminNewUser");
    }

}
