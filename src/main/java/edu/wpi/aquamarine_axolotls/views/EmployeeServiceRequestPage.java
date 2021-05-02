package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class EmployeeServiceRequestPage extends GenericPage {

        // Button Presses for Default Service Page, each button leads to its service request form
        // NOTE: code feels redundant, maybe make method?

        @FXML
        public void foodDelivery(ActionEvent actionEvent) { sceneSwitch("FoodDelivery"); }

        @FXML
        public void floralDelivP(ActionEvent actionEvent) {
            sceneSwitch("FloralDelivery");
        }

        @FXML
        public void viewReqP(ActionEvent actionEvent) {
            sceneSwitch("EmployeeRequests");
        }
    }


