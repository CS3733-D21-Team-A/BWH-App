package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import javafx.fxml.FXML;

import java.time.format.DateTimeFormatter;


public class MedicineDelivery extends GenericServiceRequest {
	@FXML
	private JFXTextField docFirstName;
	@FXML
	private JFXTextField docLastName;
	@FXML
	private JFXTextField medication;
	@FXML
	private JFXTextField doseSize;
	@FXML
	private JFXTimePicker deliveryTime;


	@FXML
	public void initialize() {
		super.initialize();
		requestFieldList.add(new FieldTemplate<>(
			"MEDICATION",
			medication,
			(a) -> a.getText(),
			(a) -> !a.getText().isEmpty()
		));

		requestFieldList.add(new FieldTemplate<>(
			"DOSAGE",
			doseSize,
			(a) -> a.getText(),
			(a) -> !a.getText().isEmpty()
		));

		requestFieldList.add(new FieldTemplate<>(
			"DELIVERYTIME",
			deliveryTime,
			(a) -> a.getValue().format(DateTimeFormatter.ofPattern("HH.mm")),
			(a) -> a.getValue() != null
		));

		requestFieldList.add(new FieldTemplate<>(
			"DOCFIRSTNAME",
			docFirstName,
			(a) -> a.getText(),
			(a) -> !a.getText().isEmpty()
		));

		requestFieldList.add(new FieldTemplate<>(
			"DOCLASTNAME",
			docLastName,
			(a) -> a.getText(),
			(a) -> !a.getText().isEmpty()
		));

		serviceRequestType = SERVICEREQUEST.MEDICINE_DELIVERY;
	}
}
