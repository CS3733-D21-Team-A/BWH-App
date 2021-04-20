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


    // credit : https://www.youtube.com/watch?v=tgV8dDP9DtM
    public void initialize() throws IOException {
        menuDrawer.setSidePane(box);
        transition = new HamburgerBasicCloseTransition(burger);
        transition.setRate(-1);
        menuDrawer.close();

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
    @FXML
    public void edgeP(ActionEvent actionEvent) {
        sceneSwitch("EdgeEditing");
    }




}
