package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
public class CovidSurvey extends GenericPage {


    @FXML
    JFXHamburger burger;

    @FXML
    JFXDrawer menuDrawer;

    @FXML
    VBox box;

    @FXML
    private JFXRadioButton yes1; //TODO: Improve this

    @FXML
    private JFXRadioButton yes2;
    @FXML
    private JFXRadioButton yes3;
    @FXML
    private JFXRadioButton yes4;
    @FXML
    private JFXRadioButton yes5;
    @FXML
    private JFXRadioButton yes6;
    @FXML
    private JFXRadioButton yes7;
    @FXML
    private JFXRadioButton yes8;
    @FXML
    private JFXRadioButton yes9;
    @FXML
    private JFXRadioButton yes10;
    @FXML
    private JFXRadioButton yes11;
    @FXML
    private JFXRadioButton no1;
    @FXML
    private JFXRadioButton no2;
    @FXML
    private JFXRadioButton no3;
    @FXML
    private JFXRadioButton no4;
    @FXML
    private JFXRadioButton no5;
    @FXML
    private JFXRadioButton no6;
    @FXML
    private JFXRadioButton no7;
    @FXML
    private JFXRadioButton no8;
    @FXML
    private JFXRadioButton no9;
    @FXML
    private JFXRadioButton no10;
    @FXML
    private JFXRadioButton no11;


    HamburgerBasicCloseTransition transition;


    @FXML
    public void initialize() {
        ToggleGroup group1 = new ToggleGroup();
        final ToggleGroup group2 = new ToggleGroup();
        final ToggleGroup group3 = new ToggleGroup();
        final ToggleGroup group4 = new ToggleGroup();
        final ToggleGroup group5 = new ToggleGroup();
        final ToggleGroup group6 = new ToggleGroup();
        final ToggleGroup group7 = new ToggleGroup();
        final ToggleGroup group8 = new ToggleGroup();
        final ToggleGroup group9 = new ToggleGroup();
        final ToggleGroup group10 = new ToggleGroup();
        final ToggleGroup group11 = new ToggleGroup();

        yes1.setToggleGroup(group1);
        no1.setToggleGroup(group1);

        yes2.setToggleGroup(group2);
        no2.setToggleGroup(group2);

        yes3.setToggleGroup(group3);
        no3.setToggleGroup(group3);

        yes4.setToggleGroup(group4);
        no4.setToggleGroup(group4);

        yes5.setToggleGroup(group5);
        no5.setToggleGroup(group5);

        yes6.setToggleGroup(group6);
        no6.setToggleGroup(group6);

        yes7.setToggleGroup(group7);
        no7.setToggleGroup(group7);

        yes8.setToggleGroup(group8);
        no8.setToggleGroup(group8);

        yes9.setToggleGroup(group9);
        no9.setToggleGroup(group9);

        yes10.setToggleGroup(group10);
        no10.setToggleGroup(group10);

        yes11.setToggleGroup(group11);
        no11.setToggleGroup(group11);
    }

    @FXML
    public void helpButton() {
        popUp("CovidSurvey", "\n\n\nPlease fill out this survey to the best of your ability. ");
    }

    @FXML //TODO: Submit to database
    public void submitButton() {
        popUp("Submission Success!", "\n\n\nYour Covid-19 Survey has been submitted. ");
        goHome();
    }

    public void menu() {
        if (transition.getRate() == -1) menuDrawer.open();
        else menuDrawer.close();
        transition.setRate(transition.getRate() * -1);
        transition.play();
    }
}



