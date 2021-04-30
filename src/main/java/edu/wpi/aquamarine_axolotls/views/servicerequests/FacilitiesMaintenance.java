package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.aquamarine_axolotls.db.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;

import java.sql.SQLException;
import java.util.*;

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
        locationDropdown.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1"));

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

        ArrayList<String> fields = new ArrayList<String>();
        fields.add(description.getText());
        fields.add(Boolean.toString(yes));
        // TODO : add location here

        try {
            createServiceRequest(SERVICEREQUEST.FACILITIES_MAINTENANCE, fields);
            submit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
