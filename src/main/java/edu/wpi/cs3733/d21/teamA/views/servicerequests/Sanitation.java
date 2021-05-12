package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;


public class Sanitation extends GenericServiceRequest {

	@FXML
	private JFXComboBox<String> biohazard;
	@FXML
	private JFXTextArea description;


	@FXML
	public void initialize() {
		super.initialize();
		requestFieldList.add(new FieldTemplate<>(
			"BIOHAZARD",
			biohazard,
			(a) -> a.getSelectionModel().getSelectedItem(),
			(a) -> a.getSelectionModel().getSelectedItem() != null
		));

		requestFieldList.add(new FieldTemplate<>(
			"NOTE",
			description,
			(a) -> a.getText(),
			(a) -> !a.getText().isEmpty()
		));

		serviceRequestType = SERVICEREQUEST.SANITATION;
		biohazard.setItems(FXCollections.observableArrayList("Yes", "No"));
	}

}


