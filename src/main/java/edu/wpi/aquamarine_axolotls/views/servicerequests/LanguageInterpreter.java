package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.*;
import edu.wpi.aquamarine_axolotls.views.servicerequests.GenericServiceRequest;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


public class LanguageInterpreter extends GenericServiceRequest {

    @FXML
    public JFXComboBox languageSelect;

    @FXML
    public JFXTextArea preferences;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private JFXComboBox roomNumber;

    @FXML
    private ArrayList<String> nodeIDS;

    @FXML
    private JFXTextField contactNumber;



    @FXML
    public void initialize() {
        startUp();
        languageSelect.setItems(FXCollections
                .observableArrayList("Espanol", "Portugues", "Francais", "Polskie"));
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");
        roomNumber.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        );
    }


    @FXML
    public void handleButtonAction(ActionEvent actionEvent) throws IOException {

        if(languageSelect.getSelectionModel().getSelectedItem() == null
                || roomNumber.getSelectionModel().getSelectedItem() == null
                ||firstName.getText ().isEmpty ()|| lastName.getText ().isEmpty ()||
                preferences.getText ().isEmpty ()||
                contactNumber.getText ().isEmpty ())
        {
            errorFields("- First Name\n- Last Name\n-Contact Number\n- Room Number\n- Language");
            return;
        }
        String fn = firstName.getText();
        String ln = lastName.getText();
        String lang = languageSelect.getSelectionModel().getSelectedItem().toString();
        int room = roomNumber.getSelectionModel().getSelectedIndex();
        String prefs = preferences.getText();
        String cn = contactNumber.getText();
        if(!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+")){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }
        ArrayList<String> fields = new ArrayList<String>();
        fields.add(cn); // contact number
        fields.add(lang); // language
        fields.add(prefs); //Note

        try {
            createServiceRequest(SERVICEREQUEST.LANGUAGE_INTERPRETER, fields);
            submit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}