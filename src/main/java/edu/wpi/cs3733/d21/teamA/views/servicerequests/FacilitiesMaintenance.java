package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;

public class FacilitiesMaintenance extends GenericServiceRequest {

    @FXML private JFXRadioButton yesRadioB;
    @FXML private JFXRadioButton noRadioB;
    @FXML private JFXTextArea description;


    public void initialize() {
        super.initialize();
        final ToggleGroup radioButtons = new ToggleGroup();
        yesRadioB.setToggleGroup(radioButtons);
        noRadioB.setToggleGroup(radioButtons);
        requestFieldList.add(new FieldTemplate<>(
            "URGENT",
            radioButtons,
            (a) -> Boolean.toString(radioButtons.getSelectedToggle().isSelected()),
            (a) -> a.getSelectedToggle() != null
        ));
        requestFieldList.add(new FieldTemplate<>(
            "DESCRIPTION",
            description,
            (a) -> a.getText(),
            (a) -> !a.getText().isEmpty()
        ));
        serviceRequestType = SERVICEREQUEST.FACILITIES_MAINTENANCE;
    }

}
