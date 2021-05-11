package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static edu.wpi.aquamarine_axolotls.Settings.USER_NAME;
import static edu.wpi.aquamarine_axolotls.Settings.PREFERENCES;

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
    //startUp ();
    }

    @FXML
    public void submitButton() {
        goHome();
        Map<String, String> survey = new HashMap<> ();
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

        String username = PREFERENCES.get(USER_NAME,null);

        survey.put("USERNAME", username);
        try {
            db.addSurvey(survey);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        popUp("Submission Success!", "\n\n\nYour Covid-19 Survey has been submitted. ");
    }

}



