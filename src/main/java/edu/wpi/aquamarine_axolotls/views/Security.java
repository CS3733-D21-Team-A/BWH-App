package edu.wpi.aquamarine_axolotls.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Security extends SServiceRequest {
	ObservableList<String> secList = FXCollections
			  .observableArrayList("High", "Medium", "Low", "Other (enter special instructions)");

	@FXML
	private Label text;

	@FXML
	private Label formName;

	@FXML
	private Label description;

	@FXML
	private TextField firstName;

	@FXML
	private TextField lastName;

	@FXML
	private TextField roomNumber;

	@FXML
	private TextField newRoomNum;

	@FXML
	private TextField reason;

	@FXML
	private ComboBox secLevel;

	@FXML
	private TextField specInstruct;

	@FXML
	public void initialize(){
		secLevel.setItems(secList);
	}
}
