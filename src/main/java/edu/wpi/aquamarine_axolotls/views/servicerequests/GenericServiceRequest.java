package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseUtil;
import edu.wpi.aquamarine_axolotls.db.SERVICEREQUEST;
import edu.wpi.aquamarine_axolotls.views.SPage;
import javafx.fxml.FXML;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

import static java.util.Objects.hash;

public class GenericServiceRequest extends SPage {
    @FXML
    JFXTextField firstName;
    @FXML
    JFXTextField lastName;
   // @FXML
   // JFXComboBox location;
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
        //previousPage = "DefaultServicePage";
        try {
            db = new DatabaseController();
        //    firstName.setText(db.getUserByUsername(Aapp.username).get("FIRSTNAME"));
          //  lastName.setText(db.getUserByUsername(Aapp.username).get("LASTNAME"));
        } catch (SQLException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns a hash value of the service requests fields with the current date and time
     * @param fields all values the service request contains
     * @return a unique id
     */
    private String getID(List<String> fields){ // giving neg values?
        String hash = fields.toString() + java.time.LocalDateTime.now().toString(); // ask Nyoma
        return Integer.toString(hash.hashCode());
    }


    /**
     * Creates a Map<String,String> representation of the generic service request
     * @param serviceRequestType the type of service request the generic links to
     * @param reqID the requests unique id
     * @return
     */
    private Map<String, String> createGeneric(SERVICEREQUEST serviceRequestType, String reqID){
        Map<String, String> shared = new HashMap<String, String>();
        shared.put("REQUESTID", reqID);
        shared.put("AUTHORID", Aapp.username);
        shared.put("STATUS", "Unassigned");
        shared.put("LOCATIONID", "aPARK002GG");//location.get(room)); // TODO: change around location
        shared.put("FIRSTNAME", firstName.getText());
        shared.put("LASTNAME", lastName.getText());
        shared.put("REQUESTTYPE", DatabaseUtil.SERVICEREQUEST_NAMES.get(serviceRequestType));
        return shared;
    }

    /**
     * Creates a Map<String,String> representation of the appropriate service request in the database
     * @param serviceRequestType type of service request being added
     * @param fields the required fields to add a given service request
     * @param reqID the requests unique id
     * @return
     * @throws SQLException
     */
    private Map<String, String> createRequest(SERVICEREQUEST serviceRequestType, ArrayList<String> fields, String reqID) {
        ArrayList<String> sortedKeys = new ArrayList<>(db.getServiceRequestColumns(serviceRequestType).keySet());
        sortedKeys.remove("REQUESTID");
        //if(fields.size() != sortedKeys.size()) throw new Error("ERROR in GENERIC SERVICE REQUEST SIZE OF VALUES AND COLUMNS" + fields.size() + " " + sortedKeys.size());
        Collections.sort(sortedKeys);


        Map<String, String> serviceRequest = new HashMap<String, String>();
        for(int i = 0; i < sortedKeys.size(); i++){
            serviceRequest.put(sortedKeys.get(i), fields.get(i));
        }
        serviceRequest.put("REQUESTID", reqID);
        return serviceRequest;
    }


    /**
     * Creates and adds a service request to the database
     * @param serviceRequestType the type of service request
     * @param fields the fields for the service request
     * @throws SQLException problem with SQL query or DB methods
     */
    public void createServiceRequest(SERVICEREQUEST serviceRequestType, ArrayList<String> fields) throws SQLException{
        String reqID = getID(fields);
        db.addServiceRequest(createGeneric(serviceRequestType, reqID), createRequest(serviceRequestType, fields, reqID));
        Map<String, String> serviceRequest = db.getServiceRequest(serviceRequestType, reqID);
/*        for(String s : serviceRequest.keySet()){
            System.out.println(s + " " + serviceRequest.get(s));
        }*/
    }

}
