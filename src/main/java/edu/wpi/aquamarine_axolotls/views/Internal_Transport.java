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
	public Button back_button;

	@FXML
	public Label text;

	@FXML
	public Label formName;

	@FXML
	public Label description;

	@FXML
	public TextField firstName;

	@FXML
	public TextField lastName;

	@FXML
	public TextField roomNumber;

	@FXML
	public TextField newRoomNum;

	@FXML
	public TextField moveReason;

	@FXML
	public ComboBox urgency;

	@FXML
	public TextField specInstruct;

	@FXML
	public Button submitButton;

	@FXML
	public void initialize(){
		urgency.setItems(urgencyList);
	}
}
