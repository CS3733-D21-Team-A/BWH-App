package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.SERVICEREQUEST;
import edu.wpi.aquamarine_axolotls.views.servicerequests.GenericServiceRequest;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;


public class Sanitation extends GenericServiceRequest {

	@FXML
	private JFXComboBox<String> biohazard, sanitLocation;
	@FXML
	private JFXTextArea description;

	private ArrayList<String> nodeIDS;


	@FXML
	public void initialize() {
		startUp();
		nodeIDS = new ArrayList<>();
		nodeIDS.add("FINFO00101");
		nodeIDS.add("EINFO00101");
		sanitLocation.setItems(FXCollections.observableArrayList("75 Lobby Information Desk", "Connors Center Security Desk Floor 1"));
		biohazard.setItems(FXCollections.observableArrayList("Yes", "No"));
	}


	@FXML
	public void handleButtonAction(javafx.event.ActionEvent actionEvent) throws IOException {
		if (sanitLocation.getSelectionModel().getSelectedItem() == null || biohazard.getSelectionModel().getSelectedItem() == null) {
			errorFields("- First Name\n- Last Name\n- Location\n- Biohazard");
			return;
		}

		String fn = firstName.getText();
		String ln = lastName.getText();

		if (!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+")) { //TODO: this what if names have hyphens or apostrophes?
			errorFields("- First Name\n- Last Name\n- Location\n- Biohazard");
			return;
		}

		int loc = sanitLocation.getSelectionModel().getSelectedIndex();
		String bioh = biohazard.getSelectionModel().getSelectedItem();
		String desc = description.getText();

		//TODO: make pop up here

		ArrayList<String> fields = new ArrayList<String>();
		fields.add(biohazard.getSelectionModel().getSelectedItem());
		fields.add(description.getText());
		try {
			createServiceRequest(SERVICEREQUEST.SANITATION, fields);
			submit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



}


