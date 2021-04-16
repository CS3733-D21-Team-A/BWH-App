package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import com.jfoenix.controls.JFXButton;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
//import javafx.application.Platform.exit;
//import javafx.application.Platform.exit;
import java.io.IOException;

public class AdminMainPage extends SMainPage{
    @FXML
    private JFXButton nodeEditing;

    @FXML
    private JFXButton edgeEditing;

    @FXML
    public void nodeP(ActionEvent actionEvent) {
        sceneSwitch("NodeEditing");
    }
    @FXML
    public void edgeP(ActionEvent actionEvent) {
        sceneSwitch("EdgeEditing");
    }

}
