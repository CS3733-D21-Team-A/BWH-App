package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class ExternalTransport extends GenericServiceRequest {

	@FXML
	private JFXTextField patientFirstName;
	@FXML
	private JFXTextField patientLastName;
	@FXML
	private JFXTextField destination;
	@FXML
	private JFXComboBox<String> modeOfTrans;
	@FXML
	private JFXTimePicker transpTime;
	@FXML
	private JFXComboBox<String> levelOfEmergency;


	@FXML
	public void initialize() {
		super.initialize();
		requestFieldList.add(new FieldTemplate<>(
			"TRANSPORTTIME",
			transpTime,
			(a) -> a.getValue().format(DateTimeFormatter.ofPattern("HH.mm")),
			(a) -> a.getValue() != null));
		requestFieldList.add(new FieldTemplate<>(
			"DESTINATION",
			destination,
			(a) -> a.getText(),
			(a) -> !a.getText().isEmpty()
		));
		requestFieldList.add(new FieldTemplate<>(
			"PATIENTFIRSTNAME",
			patientFirstName,
			(a) -> a.getText(),
			(a) -> !a.getText().isEmpty()
		));
		requestFieldList.add(new FieldTemplate<>(
			"PATIENTLASTNAME",
			patientLastName,
			(a) -> a.getText(),
			(a) -> !a.getText().isEmpty()
		));
		requestFieldList.add(new FieldTemplate<>(
			"MODEOFTRANSPORT",
			modeOfTrans,
			(a) -> a.getSelectionModel().getSelectedItem(),
			(a) -> a.getSelectionModel().getSelectedItem() != null
		));
		requestFieldList.add(new FieldTemplate<>(
			"EMERGENCYLEVEL",
			levelOfEmergency,
			(a) -> a.getSelectionModel().getSelectedItem(),
			(a) -> a.getSelectionModel().getSelectedItem() != null
		));

		serviceRequestType = SERVICEREQUEST.EXTERNAL_TRANSPORT;

		levelOfEmergency.setItems(FXCollections.observableArrayList("Very Critical", "Critical", "Moderately Critical", "Non-Emergent"));
		modeOfTrans.setItems(FXCollections.observableArrayList("Helicopter", "Ambulance", "Non-Emergent Vehicle Transport"));
	}
}
