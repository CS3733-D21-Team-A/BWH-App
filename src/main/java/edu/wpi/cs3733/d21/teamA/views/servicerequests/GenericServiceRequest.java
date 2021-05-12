package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d21.teamA.db.DatabaseController;
import edu.wpi.cs3733.d21.teamA.db.DatabaseUtil;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import edu.wpi.cs3733.d21.teamA.extras.EmailService;
import edu.wpi.cs3733.d21.teamA.views.GenericPage;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import static edu.wpi.cs3733.d21.teamA.Settings.USER_NAME;
import static edu.wpi.cs3733.d21.teamA.Settings.PREFERENCES;

public class GenericServiceRequest extends GenericPage {
	@FXML
	JFXTextField firstName;
	@FXML
	JFXTextField lastName;
	@FXML
	JFXComboBox<String> locationPicker;

	DatabaseController db = DatabaseController.getInstance();

	Map<String, String> sharedValues = new HashMap<>();
	Map<String, String> requestValues = new HashMap<>();
	SERVICEREQUEST serviceRequestType = null;

	@FXML
	public void initialize() {
		try {
			locationPicker.setItems(FXCollections.observableArrayList(db.getNodes().stream().map(node -> node.get("LONGNAME")).collect(Collectors.toList())));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	class FieldTemplate<T> {
		private String column;
		private Function<T, String> valueGetter;
		private Predicate<T> syntaxChecker; //TODO: Syntax Checking , Predicate<String> syntaxChecker
		private T field;

		public FieldTemplate(String column, T field, Function<T, String> valueGetter, Predicate<T> syntaxChecker) {
			this.column = column;
			this.valueGetter = valueGetter;
			this.field = field;
			this.syntaxChecker = syntaxChecker;
		}

		String getColumn() {
			return column;
		}

		String getValue() {
			return valueGetter.apply(field);
		}

		boolean checkSyntax() {
			return syntaxChecker.test(field);
		}
	}

	List<FieldTemplate> requestFieldList = new ArrayList<>();


	@FXML
	void submit() throws SQLException, IOException {
		StringBuilder errorMessage = new StringBuilder();
		for (FieldTemplate field : requestFieldList) {
			if (!field.checkSyntax()) errorMessage.append("\n  -").append(field.getColumn());
		}

		if (firstName.getText().isEmpty()) errorMessage.append("\n  -FIRSTNAME");
		if (lastName.getText().isEmpty()) errorMessage.append("\n  -LASTNAME");
		if (locationPicker.getSelectionModel().getSelectedItem().isEmpty()) errorMessage.append("\n  -LOCATIONID");

		if (errorMessage.length() != 0) {
			errorFields(errorMessage.toString());
			return;
		}

		for (FieldTemplate field : requestFieldList) {
			requestValues.put(field.getColumn(), field.getValue());
		}

		sharedValues.put("AUTHORID", PREFERENCES.get(USER_NAME, null));
		sharedValues.put("FIRSTNAME", firstName.getText());
		sharedValues.put("LASTNAME", lastName.getText());
		sharedValues.put("LOCATIONID", db.getNodesByValue("LONGNAME", locationPicker.getSelectionModel().getSelectedItem()).get(0).get("NODEID")); //there is a better way to do this but we don't have time :/
		sharedValues.put("REQUESTTYPE", DatabaseUtil.SERVICEREQUEST_NAMES.get(serviceRequestType));

		String requestID = generateRequestID();
		sharedValues.put("REQUESTID", requestID);
		requestValues.put("REQUESTID", requestID);

		db.addServiceRequest(sharedValues, requestValues);
		String user = PREFERENCES.get(USER_NAME, null);


		EmailService.sendServiceRequestConfirmation(
			db.getUserByUsername(user).get("EMAIL"),
			db.getUserByUsername(user).get("USERNAME"),
			new HashMap<String, String>() {{
				putAll(sharedValues);
				putAll(requestValues);
			}}
		);

		popUp("Submission Successful", "\nSubmission Success!\nYour information has successfully been submitted.\n");

		goHome();
	}

	@FXML
	void errorFields(String reqFields) {
		popUp("ERROR", "\nThe submission has not been made...\nPlease fill in the following fields." + reqFields);
	}

	/**
	 * Returns a hash value of the service requests fields with the current date and time
	 *
	 * @return a unique id
	 */
	private String generateRequestID() { // giving neg values?
		StringBuilder sb = new StringBuilder();

		sharedValues.forEach((k, v) -> sb.append(k).append(v));
		requestValues.forEach((k, v) -> sb.append(k).append(v));
		sb.append(System.currentTimeMillis());

		return Integer.toString(sb.toString().hashCode());
	}

}
