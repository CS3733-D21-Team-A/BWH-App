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
    JFXHamburger burger;

    HamburgerBasicCloseTransition transition;


    @FXML
    public void initialize() {
        languageSelect.setItems(FXCollections
                .observableArrayList("Español", "Português", "Français", "عربى", "עִברִית", "Polskie"));
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
            DatabaseController db = new DatabaseController();
            //   Aapp.num++; // TODO: better way of establishing request ID
            Map<String, String> shared = new HashMap<String, String>();
            Random r = new Random();
            String id = String.valueOf(Math.abs(r.nextInt()));
            shared.put("REQUESTID", id);
            shared.put("STATUS", "Unassigned");
            shared.put("LOCATIONID", nodeIDS.get(room));
            shared.put("FIRSTNAME", fn);
            shared.put("LASTNAME", ln);
            shared.put("REQUESTTYPE", DatabaseUtil.SERVICEREQUEST_NAMES.get(SERVICEREQUEST.LANGUAGE_INTERPRETER));

            Map<String, String> langR = new HashMap<String, String>();
            langR.put("REQUESTID", id);
            langR.put("NOTE", prefs);
            langR.put ( "CONTACTNUMBER", cn );
            langR.put("LANGUAGE", lang);

            db.addServiceRequest(shared, langR);
            db.close();
            submit();
        } catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void menu(){
        if(transition.getRate() == -1) menuDrawer.open();
        else menuDrawer.close();
        transition.setRate(transition.getRate() * -1);
        transition.play();
    }
}