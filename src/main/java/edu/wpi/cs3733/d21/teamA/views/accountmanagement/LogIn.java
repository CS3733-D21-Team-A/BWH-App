package edu.wpi.cs3733.d21.teamA.views.accountmanagement;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d21.teamA.db.DatabaseController;
import edu.wpi.cs3733.d21.teamA.extras.Security;
import edu.wpi.cs3733.d21.teamA.views.GenericPage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.sql.SQLException;
import java.util.Map;

import static edu.wpi.cs3733.d21.teamA.Settings.*;


public class LogIn extends GenericPage {

	@FXML
	private JFXTextField username;
	@FXML
	private JFXPasswordField password;
	@FXML
	private Label verfIncorrect;
	@FXML
	private Pane tfaPane;
	@FXML
	public javafx.scene.image.ImageView tfaView;
	@FXML
	private Label tfaSource;
	@FXML
	private JFXTextField verification;

	private DatabaseController db = DatabaseController.getInstance();


	public void confirmUser() throws SQLException {
		String CUusername = username.getText();
		String CUpassword = password.getText();


		if (username.getText().isEmpty() || password.getText().isEmpty()) {
			popUp("Submission Failed!", "\n\n\n\n\n\nYou have not filled in both the username and password fields");
			return;
		}
		if (!Security.secureVerifyAccount(CUusername, CUpassword)) {
			popUp("Submission Failed!", "\n\n\n\n\n\nYou have entered either an incorrect username and password combination"
				+ "or the account does not exist");
			return;
		}
		String tfaEnable = db.getUserByUsername(CUusername).get("MFAENABLED");
		if (tfaEnable.equals("true")) {
			tfaPane.setVisible(true);
			verfIncorrect.setVisible(false);
		} else {
			try {
				Map<String, String> usr = db.getUserByUsername(CUusername);
				PREFERENCES.put(USER_TYPE, usr.get("USERTYPE"));
				PREFERENCES.put(USER_NAME, usr.get("USERNAME"));
				PREFERENCES.put(USER_FIRST_NAME, usr.get("FIRSTNAME"));
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
			goHome();
		}

	}


	public void tfasubmit_button() throws SQLException {
		boolean verificationSuccess = Security.verifyTOTP(username.getText(), verification.getText());
		if (verificationSuccess) {
			popUp("Two Factor Authentication Success", "\n\n\n\n\n\n You have successfully completed Two-Factor Authentication");
			try {
				Map<String, String> usr = db.getUserByUsername(username.getText());
				PREFERENCES.put(USER_TYPE, usr.get("USERTYPE"));
				PREFERENCES.put(USER_NAME, usr.get("USERNAME"));
				PREFERENCES.put(USER_FIRST_NAME, usr.get("FIRSTNAME"));
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
			goHome();
		} else {
			verfIncorrect.setVisible(true);

		}
	}

	public void cancel2fa() {
		tfaPane.setVisible(false);
	}


	public void forgottenPassword() {
		sceneSwitch("ForgotPassword");
	}

	public void createNewAccount() {
		sceneSwitch("CreateNewAccount");
	}

}

