package edu.wpi.aquamarine_axolotls.views.servicerequests;

import edu.wpi.aquamarine_axolotls.db.SERVICEREQUEST;
import edu.wpi.aquamarine_axolotls.views.servicerequests.GenericServiceRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


public class InternalTransportation extends GenericServiceRequest {

    @FXML
    private TextField patientFirstName;

    @FXML
    private TextField patientLastName;

    @FXML
    private TextField currentRoom;

    @FXML
    private TextField newRoom;

    @FXML
    private ArrayList<String> nodeIDS;


    @FXML
    public void initialize() {
        startUp(); // TODO : whos name should eb on the request?
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");

    }




    @FXML
    public void handleButtonAction(ActionEvent actionEvent) throws IOException {
        String dfn = firstName.getText();
        String dln = lastName.getText();
        String pfn = patientFirstName.getText();
        String pln = patientLastName.getText();
        String crn = currentRoom.getText();
        String nrn = newRoom.getText();


        if(!dfn.matches("[a-zA-Z]+") || !dln.matches("[a-zA-Z]+")){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }

        ArrayList<String> fields = new ArrayList<String>();
        fields.add(crn); // current locaion
        fields.add(nrn); // new location


        try {
            createServiceRequest(SERVICEREQUEST.INTERNAL_TRANSPORT, fields);
            submit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
