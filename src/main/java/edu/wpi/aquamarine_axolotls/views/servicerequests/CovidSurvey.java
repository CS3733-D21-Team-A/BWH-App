package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CovidSurvey extends GenericServiceRequest {


    @FXML
    JFXHamburger burger;

    @FXML
    JFXDrawer menuDrawer;

    @FXML
    VBox box;

    @FXML
    private JFXCheckBox yes1;
    @FXML
    private JFXCheckBox yes2;
    @FXML
    private JFXCheckBox yes3;
    @FXML
    private JFXCheckBox yes4;
    @FXML
    private JFXCheckBox yes5;
    @FXML
    private JFXCheckBox yes6;
    @FXML
    private JFXCheckBox yes7;
    @FXML
    private JFXCheckBox yes8;
    @FXML
    private JFXCheckBox yes9;
    @FXML
    private JFXCheckBox yes10;
    @FXML
    private JFXCheckBox yes11;
    DatabaseController db;

    HamburgerBasicCloseTransition transition;


    @FXML
    public void initialize() {
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "DELIVERYTIME",
                yes1,
                (a) -> Boolean.toString(yes1.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "DELIVERYTIME",
                yes2,
                (a) -> Boolean.toString(yes2.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "DELIVERYTIME",
                yes3,
                (a) -> Boolean.toString(yes3.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "DELIVERYTIME",
                yes4,
                (a) -> Boolean.toString(yes4.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "DELIVERYTIME",
                yes5,
                (a) -> Boolean.toString(yes5.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "DELIVERYTIME",
                yes6,
                (a) -> Boolean.toString(yes6.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "DELIVERYTIME",
                yes7,
                (a) -> Boolean.toString(yes7.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "DELIVERYTIME",
                yes8,
                (a) -> Boolean.toString(yes8.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "DELIVERYTIME",
                yes9,
                (a) -> Boolean.toString(yes9.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "DELIVERYTIME",
                yes10,
                (a) -> Boolean.toString(yes10.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "DELIVERYTIME",
                yes11,
                (a) -> Boolean.toString(yes11.isSelected()),
                (a) -> true));


    }

    @FXML
    public void helpButton() {
        popUp("CovidSurvey", "\n\n\nPlease fill out this survey to the best of your ability. ");
    }

    @FXML //TODO: Submit to database
    public void submitButton() {
        popUp("Submission Success!", "\n\n\nYour Covid-19 Survey has been submitted. ");
            if(Aapp.username!=null){
                //db.hasFilledOutSurvey(Aapp.username)
        }
        goHome();
    }

    public void menu() {
        if (transition.getRate() == -1) menuDrawer.open();
        else menuDrawer.close();
        transition.setRate(transition.getRate() * -1);
        transition.play();
    }
}



