package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.time.format.DateTimeFormatter;


public class FoodDelivery extends GenericServiceRequest {

	@FXML
	private JFXTimePicker deliveryTime;
	@FXML
	private JFXComboBox<String> foodOptions;
	@FXML
	private JFXComboBox<String> drinkOptions;
	@FXML
	private JFXComboBox<String> numberServings;
	@FXML
	private JFXTextArea dietaryRestA;
	@FXML
	private JFXTextField contactNumber;


	@FXML
	public void initialize() {
		super.initialize();
		requestFieldList.add(new FieldTemplate<>(
			"DELIVERYTIME",
			deliveryTime,
			(a) -> a.getValue().format(DateTimeFormatter.ofPattern("HH.mm")),
			(a) -> a.getValue() != null));
		requestFieldList.add(new FieldTemplate<>(
			"FOODOPTION",
			foodOptions,
			(a) -> a.getSelectionModel().getSelectedItem(),
			(a) -> a.getSelectionModel().getSelectedItem() != null
		));
		requestFieldList.add(new FieldTemplate<>(
			"DIETARYRESTRICTIONS",
			dietaryRestA,
			(a) -> a.getText(),
			(a) -> !a.getText().isEmpty()
		));
		requestFieldList.add(new FieldTemplate<>(
			"NUMBEROFSERVINGS",
			numberServings,
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
			"DRINKOPTIONS",
			drinkOptions,
			(a) -> a.getSelectionModel().getSelectedItem(),
			(a) -> a.getSelectionModel().getSelectedItem() != null
		));

		serviceRequestType = SERVICEREQUEST.FOOD_DELIVERY;

		foodOptions.setItems(FXCollections.observableArrayList("Mac and Cheese", "Salad", "Pizza"));
		drinkOptions.setItems(FXCollections.observableArrayList("Water", "Coca-Cola", "Sprite", "Milk", "Orange Juice"));
		numberServings.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5"));
	}
}

