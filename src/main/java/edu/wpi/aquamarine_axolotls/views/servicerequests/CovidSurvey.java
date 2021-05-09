package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.enums.SERVICEREQUEST;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CovidSurvey extends GenericServiceRequest {
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

    @FXML
    public void initialize() {
    startUp ();
    }

    @FXML //TODO: Submit to database
    public void submitButton() {
        goHome();
        Map<String, String> survey = new HashMap<> (); // TODO: username is null for guests
        survey.put("AREQUAR", Boolean.toString(yes1.isSelected()));
        survey.put("NAUSEADIARRHEA", Boolean.toString(yes2.isSelected()));
        survey.put("SHORTBREATH", Boolean.toString(yes3.isSelected()));
        survey.put("HASCOUGH", Boolean.toString(yes4.isSelected()));
        survey.put("HASFEVER", Boolean.toString(yes5.isSelected()));
        survey.put("NEWCHILLS", Boolean.toString(yes6.isSelected()));
        survey.put("LOSSTASTESMELL", Boolean.toString(yes7.isSelected()));
        survey.put("SORETHROAT", Boolean.toString(yes8.isSelected()));
        survey.put("NASALCONGEST", Boolean.toString(yes9.isSelected()));
        survey.put("MORETIRED", Boolean.toString(yes10.isSelected()));
        survey.put("MUSCLEACHES", Boolean.toString(yes11.isSelected()));

        survey.put("USERNAME", Aapp.username != null ? Aapp.username : "guest"); //TODO: THIS IS A WORKAROUND CHANGE THIS PLEASE
        try {
            db.addSurvey(survey);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        popUp("Submission Success!", "\n\n\nYour Covid-19 Survey has been submitted. ");
    }

}



