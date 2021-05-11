package edu.wpi.aquamarine_axolotls.views.homepages;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import static edu.wpi.aquamarine_axolotls.Settings.*;

public class ApiPopupController {
	@FXML
	private JFXTextField gmapsField;
	@FXML
	private JFXTextField sendGridField;

	@FXML
	public void initialize() {
		gmapsField.setText(PREFERENCES.get(GOOGLE_MAPS_API_KEY,""));
		sendGridField.setText(PREFERENCES.get(EMAIL_API_KEY,""));
	}

	@FXML
	public void submitKeys() {
		PREFERENCES.put(GOOGLE_MAPS_API_KEY,gmapsField.getText());
		PREFERENCES.put(EMAIL_API_KEY,sendGridField.getText());
		((Stage) gmapsField.getScene().getWindow()).close();
	}
}
