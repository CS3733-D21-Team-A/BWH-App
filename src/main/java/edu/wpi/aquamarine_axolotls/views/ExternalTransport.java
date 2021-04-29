package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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

import static edu.wpi.aquamarine_axolotls.db.DatabaseUtil.SERVICEREQUEST_NAMES;

public class ExternalTransport extends GenericServiceRequest{
    @FXML
    private TextField docFirstName;

    @FXML
    private TextField docLastName;

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
                docFirstName.getText().isEmpty()||
                docLastName.getText().isEmpty()||
                destination.getText().isEmpty()||
                transpTime.getValue().format(DateTimeFormatter.ofPattern("HH.mm")).isEmpty()) {
            errorFields("- First Name\n- Last Name\n-Transport Time\n- Room Number");
            return;
        }

        String fn = firstName.getText();
        String ln = lastName.getText();
        String dfn = docFirstName.getText();
        String dln = docLastName.getText();
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

        try {
            Map<String, String> shared = getSharedValues(SERVICEREQUEST.EXTERNAL_TRANSPORT);
            Map<String, String> medicineR = new HashMap<String, String>();
            medicineR.put("REQUESTID", shared.get("REQUESTID"));
            medicineR.put("TRANSPORTTIME", dt);
            medicineR.put("DESTINATION", med);
            medicineR.put("DOCFIRSTNAME", dfn);
            medicineR.put("DOCLASTNAME", dln);
            medicineR.put("MODEOFTRANSPORT",modtrans);
            medicineR.put("EMERGENCYLEVEL",lemr);
            db.addServiceRequest(shared, medicineR);
            submit();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
