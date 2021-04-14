package edu.wpi.aquamarine_axolotls.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Internal_Transport extends Service_Request{
	ObservableList<String> urgencyList = FXCollections
			  .observableArrayList("Critical", "High", "Medium", "Low");

	@FXML
	private Button back_button;

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
	private TextField moveReason;

	@FXML
	private ComboBox urgency;

	@FXML
	private TextField specInstruct;

	@FXML
	private Button submitButton;

	@FXML
	public void initialize(){
		urgency.setItems(urgencyList);
	}
}
