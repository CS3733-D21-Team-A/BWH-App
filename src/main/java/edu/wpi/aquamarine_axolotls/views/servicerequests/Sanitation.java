package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.util.ArrayList;


public class Sanitation extends GenericServiceRequest {

	@FXML
	private JFXComboBox<String> biohazard, sanitLocation;
	@FXML
	private JFXTextArea description;

	private ArrayList<String> nodeIDS;


	@FXML
	public void initialize() {
		requestFieldList.add(new FieldTemplate<JFXComboBox<String>>(
				"BIOHAZARD",
				biohazard,
				(a) -> a.getSelectionModel().getSelectedItem(),
				(a) -> a.getSelectionModel().getSelectedItem() != null
		));

		requestFieldList.add(new FieldTemplate<JFXTextArea>(
				"NOTE",
				description,
				(a) -> a.getText(),
				(a) -> !a.getText().isEmpty()
		));

		serviceRequestType = SERVICEREQUEST.SANITATION;
		startUp();
		nodeIDS = new ArrayList<>();
		nodeIDS.add("FINFO00101");
		nodeIDS.add("EINFO00101");
		sanitLocation.setItems(FXCollections.observableArrayList("75 Lobby Information Desk", "Connors Center Security Desk Floor 1"));
		biohazard.setItems(FXCollections.observableArrayList("Yes", "No"));

	}



}


