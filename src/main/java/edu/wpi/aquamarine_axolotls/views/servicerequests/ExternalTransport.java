package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ExternalTransport extends GenericServiceRequest {

    @FXML
    private TextField patientFirstName;

    @FXML
    private TextField patientLastName;

    @FXML
    private TextField destination;

    @FXML
    private JFXComboBox modeOfTrans;

    @FXML
    private JFXTimePicker transpTime;

    @FXML
    private JFXComboBox levelOfEmergency;

    @FXML
    private JFXComboBox roomNumber;

    @FXML
    private ArrayList<String> nodeIDS;


    @FXML
    public void initialize() {
        startUp();
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");
        levelOfEmergency.setItems(FXCollections
                .observableArrayList("Very Critical","Critical","Moderately Critical","Non-Emergent")
        );
        modeOfTrans.setItems(FXCollections
                .observableArrayList("Helicopter","Ambulance","Non-Emergent Vehicle Transport")
        );
        roomNumber.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        );
    }




    @FXML
    public void handleButtonAction(ActionEvent actionEvent) throws IOException {

        if (roomNumber.getSelectionModel().getSelectedItem() == null ||
            levelOfEmergency.getSelectionModel().getSelectedItem() == null||
            modeOfTrans.getSelectionModel().getSelectedItem() == null||
                firstName.getText().isEmpty()||
                lastName.getText().isEmpty()||
                patientFirstName.getText().isEmpty()||
                patientLastName.getText().isEmpty()||
                destination.getText().isEmpty()||
                transpTime.getValue().format(DateTimeFormatter.ofPattern("HH.mm")).isEmpty()) {
            errorFields("- First Name\n- Last Name\n-Transport Time\n- Room Number");
            return;
        }

        String fn = firstName.getText();
        String ln = lastName.getText();
        String pfn = patientFirstName.getText();
        String pln = patientLastName.getText();
        String med = destination.getText();
        String dt = transpTime.getValue().format(DateTimeFormatter.ofPattern("HH.mm"));
        int room = roomNumber.getSelectionModel().getSelectedIndex();
        String lemr = levelOfEmergency.getSelectionModel().getSelectedItem().toString();
        String modtrans = modeOfTrans.getSelectionModel().getSelectedItem().toString();

        if (!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+")
                || dt.isEmpty()) {
            errorFields("- First Name\n- Last Name\n-Transport Time\n- Room Number");
            return;
        }

        ArrayList<String> fields = new ArrayList<String>();
        fields.add(med); // destination
        fields.add(pfn); // doctor first name
        fields.add(pln); // doctor last name //TODO : update SQL TABLE TO BE patient name
        fields.add(lemr); // level of emergency
        fields.add(modtrans); // mode of transport
        fields.add(dt); // delivery time

        try {
            createServiceRequest(SERVICEREQUEST.EXTERNAL_TRANSPORT, fields);
            submit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
