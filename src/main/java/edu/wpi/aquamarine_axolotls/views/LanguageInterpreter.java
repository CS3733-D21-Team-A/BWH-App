package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.db.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class LanguageInterpreter extends SServiceRequest {

    @FXML
    public JFXComboBox languageSelect;

    @FXML
    public JFXTextArea preferences;

    @FXML
    private JFXComboBox roomNumber;

    @FXML
    private JFXTextField contactNumber;

    @FXML
    public void initialize() {
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

        try {
            Map<String, String> shared = getSharedValues(SERVICEREQUEST.LANGUAGE_INTERPRETER);
            Map<String, String> langR = new HashMap<String, String>();
            langR.put("REQUESTID", shared.get("REQUESTID"));
            langR.put("NOTE", prefs);
            langR.put("CONTACTNUMBER", cn );
            langR.put("LANGUAGE", lang);
            db.addServiceRequest(shared, langR);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}