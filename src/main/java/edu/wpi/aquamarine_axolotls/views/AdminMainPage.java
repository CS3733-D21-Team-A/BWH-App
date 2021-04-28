package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    private JFXHamburger burger;
    @FXML
    private JFXDrawer menuDrawer;

    @FXML
    private VBox box;

    private HamburgerBasicCloseTransition transition;


    public void initialize() throws IOException {
    }


    public void menu(){
        if(transition.getRate() == -1) menuDrawer.open();
        else menuDrawer.close();
        transition.setRate(transition.getRate() * -1);
        transition.play();
    }

    @FXML
    public void nodeP(ActionEvent actionEvent) {
        sceneSwitch("NodeEditing");
    }

    public void requestP(ActionEvent actionEvent) {
        sceneSwitch("EmployeeRequests");
    }

    public void addUser(ActionEvent actionEvent) {
        sceneSwitch("AdminNewUser");
    }




}
