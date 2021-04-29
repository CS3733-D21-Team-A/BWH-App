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

public class FacilitiesMaintenance extends GenericServiceRequest {

    @FXML private JFXComboBox locationDropdown;
    @FXML private JFXRadioButton yesRadioB;
    @FXML private JFXRadioButton noRadioB;
    @FXML private JFXTextArea description;


    public void initialize() {
        startUp();
        final ToggleGroup radioButtons = new ToggleGroup();
        yesRadioB.setToggleGroup(radioButtons);
        noRadioB.setToggleGroup(radioButtons);
        try {
            List<Map<String, String>> nodes = db.getNodes();
            ObservableList<String> locos = FXCollections.observableArrayList();
            for (Map<String, String> node : nodes) {
                locos.add(node.get("NODEID"));
            }
            locationDropdown.setItems(locos);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

        Map<String, String> shared = getSharedValues(SERVICEREQUEST.FACILITIES_MAINTENANCE);
        Map<String, String> facilities = new HashMap<String, String>();
        facilities.put("REQUESTID", shared.get("REQUESTID"));
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
