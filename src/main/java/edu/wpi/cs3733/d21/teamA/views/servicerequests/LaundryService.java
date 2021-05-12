package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class LaundryService extends GenericServiceRequest {

	@FXML
	public JFXTextField phone;
	@FXML
	private JFXTimePicker deliveryTime;
	@FXML
	private JFXComboBox<String> loadOptions;
	@FXML
	private JFXComboBox<String> detergentType;
	@FXML
	private JFXComboBox<String> articlesOfClothing;
	@FXML
	private JFXTextArea specialRequest;


	@FXML
	public void initialize() {
		super.initialize();
		requestFieldList.add(new FieldTemplate<>(
			"DELIVERYTIME",
			deliveryTime,
			(a) -> a.getValue().format(DateTimeFormatter.ofPattern("HH.mm")),
			(a) -> a.getValue() != null));
		requestFieldList.add(new FieldTemplate<>(
			"ARTICLESOFCLOTHING",
			articlesOfClothing,
			(a) -> a.getSelectionModel().getSelectedItem(),
			(a) -> a.getSelectionModel().getSelectedItem() != null
		));
		requestFieldList.add(new FieldTemplate<>(
			"DETERGENTTYPE",
			detergentType,
			(a) -> a.getSelectionModel().getSelectedItem(),
			(a) -> a.getSelectionModel().getSelectedItem() != null
		));
		requestFieldList.add(new FieldTemplate<>(
			"LOADOPTION",
			loadOptions,
			(a) -> a.getSelectionModel().getSelectedItem(),
			(a) -> a.getSelectionModel().getSelectedItem() != null
		));
		requestFieldList.add(new FieldTemplate<>(
			"NOTE",
			specialRequest,
			(a) -> a.getText(),
			(a) -> !a.getText().isEmpty()
		));
		requestFieldList.add(new FieldTemplate<>(
			"CONTACTNUMBER",
			phone,
			(a) -> a.getText(),
			(a) -> !a.getText().isEmpty()
		));

		serviceRequestType = SERVICEREQUEST.LAUNDRY;

		loadOptions.setItems(FXCollections.observableArrayList("Delicates", "Light", "Heavy"));
		detergentType.setItems(FXCollections.observableArrayList("Tide", "All", "Gain"));
		articlesOfClothing.setItems(FXCollections.observableArrayList("Work Clothes", "T-Shirts", "Under Garments", "Clerical Garb"));
	}


}

