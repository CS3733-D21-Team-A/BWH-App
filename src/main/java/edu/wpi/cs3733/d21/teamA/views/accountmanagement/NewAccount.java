package edu.wpi.cs3733.d21.teamA.views.accountmanagement;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import dev.samstevens.totp.exceptions.QrGenerationException;
import edu.wpi.cs3733.d21.teamA.db.DatabaseController;
import edu.wpi.cs3733.d21.teamA.extras.EmailService;
import edu.wpi.cs3733.d21.teamA.extras.Security;
import edu.wpi.cs3733.d21.teamA.views.GenericPage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class NewAccount extends GenericPage {
	@FXML
	public javafx.scene.image.ImageView tfaView;
	@FXML
	private JFXTextField firstName;
	@FXML
	private JFXTextField lastName;
	@FXML
	private JFXTextField emailAddress;
	@FXML
	private JFXTextField userName;
	@FXML
	private JFXPasswordField password;
	@FXML
	private JFXPasswordField confirmPassword;

	@FXML
	private JFXTextField verification;

	@FXML
	private Label tfaSource;

	@FXML
	private Label verfIncorrect;
	@FXML
	private Pane tfaPane;
	@FXML
	private JFXButton submitButton;
	@FXML
	private JFXCheckBox tfa;

	private DatabaseController db = DatabaseController.getInstance();

	@FXML
	public void submit_button() throws SQLException, IOException {
		String email = emailAddress.getText();

		if (firstName.getText().isEmpty() || lastName.getText().isEmpty() ||
			emailAddress.getText().isEmpty() || userName.getText().isEmpty()) {
			popUp("Failed Submission.", "\n\n\n\n\nPlease fill out all fields listed.");
			return;
		}

		if (!password.getText().equals(confirmPassword.getText())) {
			popUp("Account Creation Failed", "\n\n\n\n\n\nThe two passwords do not match, Try again.");
			return;
		}

		if (db.checkUserExistsByUsername(userName.getText())) {
			popUp("Account Creation Failed", "\n\n\n\n\n\nUsername already exists.");
			return;
		}

		if (db.checkUserExistsByEmail(email)) {
			popUp("Account Creation Failed", "\n\n\n\n\n\nUser with this email already exists.");
			return;
		}
		Map<String, String> user = new HashMap<>();
		user.put("USERNAME", userName.getText());
		user.put("FIRSTNAME", firstName.getText());
		user.put("LASTNAME", lastName.getText());
		user.put("EMAIL", emailAddress.getText());
		user.put("USERTYPE", "Patient");

		Security.addHashedPassword(user, password.getText());

		db.addUser(user);

		if (tfa.isSelected()) {
			tfaSelected();
		} else {
			popUp("Account Success", "\n\n\n\n\n\nThe account you submitted was successfully created.");
			sceneSwitch("LogIn");
		}
		EmailService.sendAccountCreationEmail(email, userName.getText(), firstName.getText());
	}

	public void tfaSelected() {
		tfaPane.setVisible(true);
		verfIncorrect.setVisible(false);
		submitButton.setVisible(false);
		try {
			Pair<String, byte[]> TOTPinformation = Security.enableTOTP(userName.getText());
			readByteArray(TOTPinformation.getValue());
			tfaSource.setText("Secret: \n" + TOTPinformation.getKey());

		} catch (SQLException | IOException | QrGenerationException e) {
			e.printStackTrace();
		}

	}


	public void readByteArray(byte[] pair) throws IOException {
		Image img = new Image(new ByteArrayInputStream(pair));

		tfaView.setImage(img);
	}


	public void tfasubmit_button() throws SQLException {
		boolean verificationSuccess = Security.verifyTOTP(userName.getText(), verification.getText());
		if (verificationSuccess) {
			popUp("Account Success", "\n\n\n\n\n\nThe account you submitted was successfully created.");
			sceneSwitch("LogIn");
		} else {
			verfIncorrect.setVisible(true);
		}
	}

	public void cancel2fa() {
		tfaPane.setVisible(false);
		try {
			db.deleteUser(userName.getText());
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		submitButton.setVisible(true);
	}
}
