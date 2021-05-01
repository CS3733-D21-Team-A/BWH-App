package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseUtil;
import edu.wpi.aquamarine_axolotls.db.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Sanitation extends SServiceRequest {

	@FXML
	private JFXComboBox<String> biohazard;

	@FXML
	private JFXTextField firstName;

	@FXML
	private JFXTextField lastName;

	@FXML
	private JFXComboBox<String> sanitLocation; //note: location is a reserved keyword and cannot be used

	@FXML
	private JFXTextArea description;

	@FXML
	JFXHamburger burger;

	@FXML
	JFXDrawer menuDrawer;

	@FXML
	private AnchorPane myAnchorPane;

	private ArrayList<String> nodeIDS;


	@FXML
	public void initialize() {
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
		int loc = sanitLocation.getSelectionModel().getSelectedIndex();
		String bioh = biohazard.getSelectionModel().getSelectedItem();
		String desc = description.getText();

		//TODO: make pop up here
		if (!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+")) { //TODO: this what if names have hyphens or apostrophes?
			errorFields("- First Name\n- Last Name\n- Location\n- Biohazard");
			return;
		}

		try {
			DatabaseController db = DatabaseController.getInstance();
			Map<String, String> shared = new HashMap<>();
			Random r = new Random();
			int id = Math.abs(r.nextInt());
			shared.put("REQUESTID", String.valueOf(id));
			shared.put("STATUS", "Unassigned");
			shared.put("LOCATIONID", String.valueOf(nodeIDS.get(loc)));
			shared.put("FIRSTNAME", fn);
			shared.put("LASTNAME", ln);
			shared.put("REQUESTTYPE", DatabaseUtil.SERVICEREQUEST_NAMES.get(SERVICEREQUEST.SANITATION));

			Map<String, String> sanitation = new HashMap<>();

			sanitation.put("REQUESTID", String.valueOf(id));
			sanitation.put("BIOHAZARD", bioh);
			sanitation.put("NOTE", desc);
			db.addServiceRequest(shared, sanitation);
			db.close();
			submit();
		} catch (SQLException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void menu() {
		if (transition.getRate() == -1) menuDrawer.open();
		else menuDrawer.close();
		transition.setRate(transition.getRate() * -1);
		transition.play();
	}


}


