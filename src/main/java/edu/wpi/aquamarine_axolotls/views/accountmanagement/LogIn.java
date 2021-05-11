package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import com.jfoenix.controls.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.extras.Security;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Map;

import static edu.wpi.aquamarine_axolotls.Settings.*;


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
	@FXML private Label tfaSource;
	@FXML private JFXTextField verification;

	private DatabaseController db = DatabaseController.getInstance();



	public void confirmUser() throws SQLException {
		String CUusername = username.getText();
		String CUpassword = password.getText();


		if (username.getText().isEmpty() || password.getText().isEmpty()) {
			popUp("Submission Failed!", "\n\n\n\n\n\nYou have not filled in both the username and password fields");
			return;
		}
		if (!db.checkUserMatchesPass(CUusername, CUpassword)) {
			popUp("Submission Failed!", "\n\n\n\n\n\nYou have entered either an incorrect username and password combination"
				+ "or the account does not exist");
			return;
		}
		try {
			Map<String, String> usr = db.getUserByUsername(CUusername);
			PREFERENCES.put(USER_TYPE,usr.get("USERTYPE"));
			PREFERENCES.put(USER_NAME,usr.get("USERNAME"));
			PREFERENCES.put(USER_FIRST_NAME,usr.get("FIRSTNAME"));
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		String tfaEnable = db.getUserByUsername ( CUusername ).get ( "MFAENABLED" );
		if(tfaEnable.equals ( "true")){
			tfaPane.setVisible ( true );
			verfIncorrect.setVisible ( false );


		}
		else{
			goHome();
		}

	}


	public void tfasubmit_button() throws SQLException {
		boolean verificationSuccess = Security.verifyTOTP(username.getText(),verification.getText ( ) );
		if(verificationSuccess){
			popUp ( "Two Factor Authentication Success" ,"\n\n\n\n\n\n You have successfully completed Two-Factor Authentication" );
			goHome();
		}
		else{
			verfIncorrect.setVisible ( true );

		}
	}

	public void cancel2fa(){
		tfaPane.setVisible ( false );
	}


	public void forgottenPassword() {
		sceneSwitch("ForgotPassword");
	}

	public void createNewAccount() {
		sceneSwitch("CreateNewAccount");
	}

}

