package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseUtil;
import edu.wpi.aquamarine_axolotls.db.SERVICEREQUEST;
import javafx.fxml.FXML;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GenericServiceRequest extends GenericPage{
    @FXML
    JFXTextField firstName;
    @FXML
    JFXTextField lastName;
    @FXML
    JFXComboBox location;
    @FXML
    JFXTextArea note;




    DatabaseController db;

    @FXML
    public void submit() {
        popUp("Submission Successful" ,"\nSubmission Success!\nYour information has successfully been submitted.\n");
        sceneSwitch("DefaultServicePage");
    }

    @FXML
    public void errorFields(String reqFields) {
        popUp("ERROR" ,"\nThe submission has not been made...\nPlease fill in the following fields.\n" + reqFields);
    }


    @FXML
    public void loadHelp() {
        popUp("Helpful information:","\n\n\n\nPlease provide your first name, last name, " +
                "time you would like to receive the request patient's room number, and an optional message ");
    }


    public void startUp(){
        previousPage = "DefaultServicePage";
        try {
            db = new DatabaseController();
            firstName.setText(db.getUserByUsername(Aapp.username).get("FIRSTNAME"));
            lastName.setText(db.getUserByUsername(Aapp.username).get("LASTNAME"));
        } catch (SQLException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private String getID(){
        Random r = new Random();
        return String.valueOf(Math.abs(r.nextInt())); // TODO : change to be current time hashed with Strings
    }


    public Map<String, String> getSharedValues(SERVICEREQUEST serviceRequestType){
        Map<String, String> shared = new HashMap<String, String>();
        shared.put("REQUESTID", getID());
        shared.put("AUTHORID", Aapp.username);
        shared.put("STATUS", "Unassigned");
        shared.put("LOCATIONID", "EINFO00101");//location.get(room)); // TODO: change around location
        shared.put("FIRSTNAME", firstName.getText());
        shared.put("LASTNAME", lastName.getText());
        shared.put("REQUESTTYPE", DatabaseUtil.SERVICEREQUEST_NAMES.get(serviceRequestType));
        return shared;

    }

}
