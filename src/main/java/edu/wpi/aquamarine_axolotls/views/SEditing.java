package edu.wpi.aquamarine_axolotls.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public abstract class SEditing extends SPage{
    @FXML
    public void adminHome(ActionEvent actionEvent) {
        sceneSwitch("AdminMainPage");

    }

}