package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class DefaultServicePage extends SPage {
    @FXML
    private JFXButton foodDeliveryB;

    @FXML
    private JFXButton languageInterpretersB;

    @FXML
    private JFXButton giftDeliveryButton;

    @FXML
    private JFXButton sanitationB;

    @FXML
    private JFXButton laundryB;

    @FXML
    private JFXButton medicineB;
    @FXML
    private JFXButton floralDeliveryB;

    @FXML
    private JFXButton religiousReqB;

    @FXML
    private JFXButton internalTranB;

    @FXML
    private JFXButton externalTranB;

    @FXML
    private JFXButton backB;

    @FXML
    private JFXHamburger burger;

    @FXML
    private JFXButton shutDownB;
    @FXML
    private VBox box;

    @FXML
    HamburgerBasicCloseTransition transition;

    @FXML
    private JFXDrawer menuDrawer;
    @FXML
    private JFXButton viewReqB;

    // Button Presses for Default Service Page, each button leads to its service request form
    // NOTE: code feels redundant, maybe make method?

    @FXML
    public void foodDeliveryP(ActionEvent actionEvent) { sceneSwitch("FoodDelivery"); }

    public void floralDelivP(ActionEvent actionEvent) { sceneSwitch("FloralDelivery"); }

    public void externalTransP(ActionEvent actionEvent) { sceneSwitch("ExternalTransport"); }

    public void medicineDelivP(ActionEvent actionEvent) {
        sceneSwitch("MedicineDelivery");
    }

    public void giftDelivP(ActionEvent actionEvent) {
        sceneSwitch("GiftDelivery");
    }

    public void facilityMainP(ActionEvent actionEvent) {
        sceneSwitch ( "FacilitiesMaintenance" );
    }

    public void viewReqP(ActionEvent actionEvent) {
        sceneSwitch("EmployeeRequests");
    }

    public void internalTransport(ActionEvent actionEvent) {
        sceneSwitch("InternalTransportation");
    }

    public void sanitationP(ActionEvent actionEvent) {
        sceneSwitch("Sanitation");
    }

    public void laundryP(ActionEvent actionEvent) {
        sceneSwitch("LaundryService");
    }

    public void languageP(ActionEvent actionEvent) {
        sceneSwitch("LanguageInterpreter");
    }


    public void menu(){
        if(transition.getRate() == -1) menuDrawer.open();
        else menuDrawer.close();
        transition.setRate(transition.getRate() * -1);
        transition.play();
    }
}

