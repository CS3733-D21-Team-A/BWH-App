package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class FacilitiesMaintenance extends GenericServiceRequest {

    @FXML private JFXComboBox locationDropdown;
    @FXML private JFXRadioButton yesRadioB;
    @FXML private JFXRadioButton noRadioB;
    @FXML private JFXTextArea description;


    public void initialize() {
        final ToggleGroup radioButtons = new ToggleGroup();
        yesRadioB.setToggleGroup(radioButtons);
        noRadioB.setToggleGroup(radioButtons);
        requestFieldList.add(new FieldTemplate<ToggleGroup>(
                "URGENT",
                radioButtons,
                (a) -> Boolean.toString(radioButtons.getSelectedToggle().isSelected()),
                (a) ->a.getSelectedToggle() != null
        ));
        requestFieldList.add(new FieldTemplate<JFXTextArea>(
                "DESCRIPTION",
                description,
                (a) -> a.getText(),
                (a) -> !a.getText().isEmpty()
        ));
        serviceRequestType = SERVICEREQUEST.FACILITIES_MAINTENANCE;

        ArrayList<String> locations = new ArrayList<>();
        try {
            for (Map<String, String> oneNode: db.getNodes()) {
                locations.add(oneNode.get("LONGNAME"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        locationDropdown.setItems(FXCollections
            .observableArrayList(locations)
        );
    }

    @FXML
    void submitFacilities() throws SQLException, IOException {
        String errorVals = "";
        if(locationDropdown.getSelectionModel().getSelectedItem().toString().isEmpty()) errorVals = "\n  -LOCATIONID";
        sharedValues.put("LOCATIONID", db.getNodesByValue("LONGNAME",locationDropdown.getSelectionModel().getSelectedItem().toString()).get(0).get("NODEID"));
        submit(errorVals);
    }

}
