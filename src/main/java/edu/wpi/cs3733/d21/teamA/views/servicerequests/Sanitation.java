package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;


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

		ArrayList<String> locations = new ArrayList<>();
		try {
			for (Map<String, String> oneNode: db.getNodes()) {
				locations.add(oneNode.get("LONGNAME"));
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		sanitLocation.setItems(FXCollections.observableArrayList(locations));
		biohazard.setItems(FXCollections.observableArrayList("Yes", "No"));

	}

	// new method for getting the submit information regarding the location id into submit
	@FXML
	void submitSanitation() throws SQLException, IOException {
		String errorVals = "";
		if(sanitLocation.getSelectionModel().getSelectedItem().toString().isEmpty()) errorVals = "\n  -LOCATIONID";
		sharedValues.put("LOCATIONID", db.getNodesByValue("LONGNAME",sanitLocation.getSelectionModel().getSelectedItem().toString()).get(0).get("NODEID"));
		submit(errorVals);
	}


}


