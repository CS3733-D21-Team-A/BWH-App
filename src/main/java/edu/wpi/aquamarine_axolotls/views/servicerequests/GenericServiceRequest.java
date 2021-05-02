package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseUtil;
import edu.wpi.aquamarine_axolotls.db.SERVICEREQUEST;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.fxml.FXML;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.*;

public class GenericServiceRequest extends GenericPage {
    @FXML
    JFXTextField firstName;
    @FXML
    JFXTextField lastName;
   // @FXML
   // JFXComboBox location;
    @FXML
    JFXTextArea note;

    DatabaseController db;

    Map<String,String> sharedValues = new HashMap<>();
    Map<String,String> requestValues = new HashMap<>();
    SERVICEREQUEST serviceRequestType = null;



    class FieldTemplate<T> {
        private String column;
        private Function<T,String> valueGetter;
        private Predicate<T> syntaxChecker; //TODO: Syntax Checking , Predicate<String> syntaxChecker
        private T field;

        public FieldTemplate(String column, T field, Function<T,String> valueGetter, Predicate<T> syntaxChecker) {
            this.column = column;
            this.valueGetter = valueGetter;
            this.field = field;
            this.syntaxChecker = syntaxChecker;
        }

       String getColumn() {
            return column;
       }
       String getValue() {
            return valueGetter.apply(field);
       }
       boolean checkSyntax() {
           return syntaxChecker.test(field);
       }
    }

    List<FieldTemplate> requestFieldList = new ArrayList<>();

    /*
    *   shared.put("REQUESTID", reqID);
        shared.put("AUTHORID", Aapp.username);
        shared.put("STATUS", "Unassigned");
        shared.put("LOCATIONID", "aPARK002GG");//location.get(room)); // TODO: change around location
        shared.put("FIRSTNAME", firstName.getText());
        shared.put("LASTNAME", lastName.getText());
        shared.put("REQUESTTYPE", DatabaseUtil.SERVICEREQUEST_NAMES.get(serviceRequestType));*/

    @FXML
    public void submit() throws SQLException {
        StringBuilder errorMessage = new StringBuilder();
        for(FieldTemplate field : requestFieldList){
            if(!field.checkSyntax()) errorMessage.append("\n  -" + field.getColumn());
        }
        if(errorMessage.length() != 0){
            errorFields(errorMessage.toString());
            return;
        }

        for (FieldTemplate field : requestFieldList) {
            requestValues.put(field.getColumn(), field.getValue());
        }

        sharedValues.put("AUTHORID", Aapp.username);
        sharedValues.put("LOCATIONID", "aPARK002GG");//location.get(room)); // TODO: change around location
        sharedValues.put("FIRSTNAME", firstName.getText());
        sharedValues.put("LASTNAME", lastName.getText());
        sharedValues.put("REQUESTTYPE", DatabaseUtil.SERVICEREQUEST_NAMES.get(serviceRequestType));

        String requestID = generateRequestID();
        sharedValues.put("REQUESTID", requestID);
        requestValues.put("REQUESTID",requestID);

        db.addServiceRequest(sharedValues,requestValues);
        System.out.println(db.getServiceRequest(serviceRequestType, requestID).toString());

        popUp("Submission Successful" ,"\nSubmission Success!\nYour information has successfully been submitted.\n");
        sceneSwitch("DefaultServicePage");
    }

    @FXML
    public void errorFields(String reqFields) {
        popUp("ERROR" ,"\nThe submission has not been made...\nPlease fill in the following fields." + reqFields);
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
     * @return a unique id
     */
    private String generateRequestID(){ // giving neg values?
        StringBuilder sb = new StringBuilder();

        BiConsumer<String,String> appender = (k, v) -> {
            sb.append(k);
            sb.append(v);
        };

        sharedValues.forEach(appender);

        requestValues.forEach(appender);

        sb.append(System.currentTimeMillis());

        return Integer.toString(sb.toString().hashCode());
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
        String reqID = generateRequestID();
        db.addServiceRequest(createGeneric(serviceRequestType, reqID), createRequest(serviceRequestType, fields, reqID));
       // Map<String, String> serviceRequest = db.getServiceRequest(serviceRequestType, reqID);
/*        for(String s : serviceRequest.keySet()){
            System.out.println(s + " " + serviceRequest.get(s));
        }*/
    }

}
