package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.time.format.DateTimeFormatter;

public class FloralDelivery extends GenericServiceRequest {

	@FXML
	private JFXTimePicker deliveryTime;

	@FXML
	private JFXTextArea persMessage;
	@FXML
	private JFXComboBox<String> flowerOptions;
	@FXML
	private JFXDatePicker deliveryDate;
	@FXML
	private JFXTextField contactNumber;
	@FXML
	private JFXComboBox<String> vaseOptions;

	@FXML
	public void initialize() {
		super.initialize();
		requestFieldList.add(new FieldTemplate<>(
			"DELIVERYTIME",
			deliveryTime,
			(a) -> a.getValue().format(DateTimeFormatter.ofPattern("HH.mm")),
			(a) -> a.getValue() != null));
		requestFieldList.add(new FieldTemplate<>(
			"DELIVERYDATE",
			deliveryDate,
			(a) -> a.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
			(a) -> a.getValue() != null
		));
		requestFieldList.add(new FieldTemplate<>(
			"FLOWEROPTION",
			flowerOptions,
			(a) -> a.getSelectionModel().getSelectedItem(),
			(a) -> a.getSelectionModel().getSelectedItem() != null
		));
		requestFieldList.add(new FieldTemplate<>(
			"VASEOPTION",
			vaseOptions,
			(a) -> a.getSelectionModel().getSelectedItem(),
			(a) -> a.getSelectionModel().getSelectedItem() != null
		));
		requestFieldList.add(new FieldTemplate<>(
			"CONTACTNUMBER",
			contactNumber,
			(a) -> a.getText(),
			(a) -> !a.getText().isEmpty()
		));
		requestFieldList.add(new FieldTemplate<>(
			"NOTE",
			persMessage,
			(a) -> a.getText(),
			(a) -> !a.getText().isEmpty()
		));

		serviceRequestType = SERVICEREQUEST.FLORAL_DELIVERY;

		flowerOptions.setItems(FXCollections.observableArrayList("Roses", "Sunflowers", "Peruvian Lilies", "Hydrangeas", "Orchids"));
		vaseOptions.setItems(FXCollections.observableArrayList("Bouquet Vase", "Square Vase", "Cylinder Vase", "Milk Bottle", "Pedestal Vase"));
	}

}


