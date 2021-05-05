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

    @FXML
    public void initialize() {
        /*
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "AREQUAR",
                yes1,
                (a) -> Boolean.toString(yes1.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "NAUSADIRRHEA",
                yes2,
                (a) -> Boolean.toString(yes2.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "SHORTBREATH",
                yes3,
                (a) -> Boolean.toString(yes3.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "HASCOUGH",
                yes4,
                (a) -> Boolean.toString(yes4.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "HASFEVER",
                yes5,
                (a) -> Boolean.toString(yes5.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "NEWCHILLS",
                yes6,
                (a) -> Boolean.toString(yes6.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "LOSSTASTESMELL",
                yes7,
                (a) -> Boolean.toString(yes7.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "SORETHROAT",
                yes8,
                (a) -> Boolean.toString(yes8.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "NASALCONGEST",
                yes9,
                (a) -> Boolean.toString(yes9.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "MORETIRED",
                yes10,
                (a) -> Boolean.toString(yes10.isSelected()),
                (a) -> true));
        requestFieldList.add(new FieldTemplate<JFXCheckBox>(
                "MUSCLEACHES",
                yes11,
                (a) -> Boolean.toString(yes11.isSelected()),
                (a) -> true));



         */


    startUp ();
    }

    @FXML
    public void helpButton() {
        popUp("CovidSurvey", "\n\n\nPlease fill out this survey to the best of your ability. ");
    }

    @FXML //TODO: Submit to database
    public void submitButton() {
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
            if(Aapp.username!=null){
                survey.put("USERNAME", Aapp.username);
                try {
                        db.addSurvey ( survey );
                        System.out.println ( survey.toString () );
                } catch (SQLException throwables) {
                    throwables.printStackTrace ( );
                }
                popUp("Submission Success!", "\n\n\nYour Covid-19 Survey has been submitted. ");
                goHome();
            }
            else{
                    try {
                        db.addSurvey ( survey );
                    } catch (SQLException throwables) {
                        throwables.printStackTrace ( );
                    }
                    Aapp.guestHasTaken = true;
                    popUp("Submission Success!", "\n\n\nYour Covid-19 Survey has been submitted. ");
                    goHome();

            }

        }

    }



