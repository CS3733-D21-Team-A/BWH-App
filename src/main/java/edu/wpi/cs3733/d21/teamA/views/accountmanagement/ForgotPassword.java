package edu.wpi.cs3733.d21.teamA.views.accountmanagement;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.cs3733.d21.teamA.db.DatabaseController;
import edu.wpi.cs3733.d21.teamA.db.DatabaseUtil;
import edu.wpi.cs3733.d21.teamA.db.enums.USERTYPE;
import edu.wpi.cs3733.d21.teamA.extras.EmailService;
import edu.wpi.cs3733.d21.teamA.extras.Security;
import edu.wpi.cs3733.d21.teamA.views.GenericPage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static edu.wpi.cs3733.d21.teamA.Settings.*;

public class ForgotPassword extends GenericPage {
	@FXML
	private JFXTextField username;
	@FXML
	private JFXTextField email;
	@FXML
	private JFXPasswordField password;
	@FXML
	private JFXPasswordField confirmPassword;
	@FXML
	private GridPane gridPane;
	@FXML
	private Pane newPassPane;
	@FXML
	private Pane verfPane;
	@FXML
	private JFXButton first;
	@FXML
	private JFXButton second;
	@FXML
	private JFXButton finalSubmit;
	@FXML
	private JFXTextField verifyEmail;
	@FXML
	private Label label;

	private String otp;
	private final DatabaseController db = DatabaseController.getInstance();

	String resetUsername = PREFERENCES.get(USER_NAME, null);
	String usertype = PREFERENCES.get(USER_TYPE, null);

	public void initialize() {
		System.out.println(usertype);
		verfPane.setVisible(false);
		newPassPane.setVisible(false);
		if (!usertype.equals(DatabaseUtil.USER_TYPE_NAMES.get(USERTYPE.GUEST))) {
			resetPassword();
		}
	}


	public void userCheck() {
		resetUsername = username.getText();
		String eml = email.getText();

		verfPane.setVisible(true);
		gridPane.setVisible(false);
		first.setVisible(false);
		second.setVisible(true);
		label.setText("Please enter the verification code sent to your email");

		try {
			if (db.checkUserExistsByUsername(resetUsername) && db.getUserByUsername(resetUsername).get("EMAIL").equals(eml)) {
				otp = Security.generateOneTimeSecurityCode();
				EmailService.sendAccountResetEmail(email.getText(), username.getText(), otp);
			}
		} catch (SQLException | IOException throwables) {
			throwables.printStackTrace();
		}
	}

	public void verifyEmailConf() {
		if (verifyEmail.getText().equals(otp)) {
			verfPane.setVisible(false);
			second.setVisible(false);
			finalSubmit.setVisible(true);
			newPassPane.setVisible(true);
			label.setText("Please enter your new password");
		} else {
			popUp("Invalid Request", "The email verification code that you entered was not correct. Please try again");
		}
	}

	public void finalSubmit() throws SQLException, IOException {
		if (!password.getText().isEmpty() && !confirmPassword.getText().isEmpty()) {
			if (password.getText().equals(confirmPassword.getText())) {
				Map<String, String> updated = new HashMap<>();
				Security.addHashedPassword(updated, password.getText());

				db.editUser(resetUsername,updated);
				Map<String,String> user = db.getUserByUsername(resetUsername);
				EmailService.sendPasswordChangeConfirmation(user.get("EMAIL"),resetUsername,user.get("FIRSTNAME"));

				if (usertype.equals(DatabaseUtil.USER_TYPE_NAMES.get(USERTYPE.GUEST))) {
					popUp("New Password", "\n\n\n\n\nYour password has been successfully created.\nPlease log in using your new credentials.");
					sceneSwitch("LogIn");
				} else  {
					popUp("New Password", "\n\n\n\n\nYour password has been reset.");
					sceneSwitch("UserSettings");
				}
			} else {
				popUp("Invalid Request", "The passwords did not match. Please try again");
			}
		} else {
			popUp("Invalid Request", "Password fields must be filled");
		}
	}

	public void resetPassword() {
		gridPane.setVisible(false);
		first.setVisible(false);
		verfPane.setVisible(false);
		second.setVisible(false);
		finalSubmit.setVisible(true);
		newPassPane.setVisible(true);
		label.setText("Please enter your new password");
	}

	public void cancelVerf() {
		verfPane.setVisible(false);
		gridPane.setVisible(true);
		first.setVisible(true);
		second.setVisible(false);

	}
}

