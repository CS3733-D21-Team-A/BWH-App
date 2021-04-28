package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseUtil;
import edu.wpi.aquamarine_axolotls.db.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import org.apache.derby.iapi.db.Database;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FacilitiesMaintenance extends SServiceRequest {

    @FXML private JFXTextField firstName;
    @FXML private JFXTextField lastName;
    @FXML private JFXComboBox locationDropdown;
    @FXML private JFXRadioButton yesRadioB;
    @FXML private JFXRadioButton noRadioB;
    @FXML private JFXTextArea description;

    DatabaseController db;


    public void initialize() {
        try {
            db = new DatabaseController();
        } catch (SQLException | IOException | URISyntaxException throwables) {
            throwables.printStackTrace();
        }
        final ToggleGroup radioButtons = new ToggleGroup();
        yesRadioB.setToggleGroup(radioButtons);
        noRadioB.setToggleGroup(radioButtons);
        try {
            DatabaseController db = new DatabaseController();
            List<Map<String, String>> nodes = db.getNodes();
            ObservableList<String> locos = FXCollections.observableArrayList();
            for (Map<String, String> node : nodes) {
                locos.add(node.get("NODEID"));
            }
            locationDropdown.setItems(locos);
        } catch (SQLException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }

    }


    public void loadHelp(ActionEvent actionEvent) {
        popUp("Helpful information:", "\nPlease provide your...\n first name\n  Last name\n   the location of service\n   if the maintenance is urgent\n  and a brief description");
    }


    public void handleButtonAction(ActionEvent actionEvent) {
        if (locationDropdown.getSelectionModel().getSelectedItem() == null) {
            errorFields("Please Select a location!");
            return;
        }
        String fn = firstName.getText();
        String ln = lastName.getText();
        String loc = locationDropdown.getSelectionModel().getSelectedItem().toString();
        boolean yes = yesRadioB.isSelected();
        boolean no = noRadioB.isSelected();

        if (!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+") || (!yes && !no)) {
            errorFields("\n first name\n  Last name\n   the location of service\n   if the maintenance is urgent\n  and a brief description");
            return;
        }

        Random r = new Random();
        int id = Math.abs(r.nextInt());
        Map<String, String> shared = new HashMap<String, String>();
        shared.put("REQUESTID", String.valueOf(id));
        shared.put("AUTHORID", Aapp.username);
        shared.put("STATUS", "Unassigned");
        shared.put("LOCATIONID", loc);
        shared.put("FIRSTNAME", fn);
        shared.put("LASTNAME", ln);
        shared.put("REQUESTTYPE", DatabaseUtil.SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FACILITIES_MAINTENANCE));

        Map<String, String> facilities = new HashMap<String, String>();
        facilities.put("REQUESTID", String.valueOf(id));
        facilities.put("URGENT", Boolean.toString(yes));
        facilities.put("DESCRIPTION", description.toString());


        try {
            db.addServiceRequest(shared, facilities);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        submit();
    }
}
