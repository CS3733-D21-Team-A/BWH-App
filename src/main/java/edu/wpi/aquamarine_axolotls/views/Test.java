package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class Test {

    @FXML
    JFXHamburger burger;

    @FXML
    JFXDrawer menuDrawer;

    @FXML
    VBox box;

    HamburgerBasicCloseTransition transition;


    // credit : https://www.youtube.com/watch?v=tgV8dDP9DtM
    public void initialize() throws IOException {
/*        try {
            transition = new HamburgerBasicCloseTransition(burger);
            transition.setRate(-1);
            //VBox box = FXMLLoader.load(getClass().getResource(""));
            //menuDrawer.setSidePane(box);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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

}
