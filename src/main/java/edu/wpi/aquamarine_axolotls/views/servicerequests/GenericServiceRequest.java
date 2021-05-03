package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseUtil;
import edu.wpi.aquamarine_axolotls.db.enums.SERVICEREQUEST;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.*;

public class GenericServiceRequest extends GenericPage {
    @FXML
    JFXTextField firstName;
    @FXML
    JFXTextField lastName;
   // @FXML
   // JFXComboBox location; //TODO: Don't all requests need a location?

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


    public void startUp(){ //TODO: why startUp and not initialize?
        try {
            db = DatabaseController.getInstance();
        } catch (SQLException | IOException e) {
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

}
