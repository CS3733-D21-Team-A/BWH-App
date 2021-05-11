package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import com.jfoenix.controls.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseUtil;
import edu.wpi.aquamarine_axolotls.db.enums.USERTYPE;
import edu.wpi.aquamarine_axolotls.extras.Security;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.swing.text.html.ImageView;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static edu.wpi.aquamarine_axolotls.Settings.*;

public class UserSettings extends GenericPage {

	@FXML
	private JFXButton reloadDbButton;
	@FXML
	private JFXCheckBox databasePicker;

	@FXML
	private JFXTextField firstName;
	@FXML
	private JFXTextField lastName;
	@FXML
	private JFXTextField pronouns;
	@FXML
	private JFXTextField gender;
	@FXML
	private JFXTextField userName;
	@FXML
	private JFXTextField email;
	@FXML
	private GridPane gridPane;
	@FXML
	private JFXTextField phoneNumber;
	@FXML
	private JFXTextField verification;
	@FXML
	private ImageView view;

	@FXML
	private JFXTextField hostLabel;
	@FXML
	private Pane databasePane;
	@FXML
	public javafx.scene.image.ImageView tfaView;
	@FXML
	private Label tfaSource;

	@FXML
	private Label passwordMatchLabel;

	@FXML
	private Label verfIncorrect;
	@FXML
	private Pane tfaPane;

	@FXML
	private JFXCheckBox tfa;

	@FXML
	private StackPane stackPane;


	private final DatabaseController db = DatabaseController.getInstance();

	private String username;

	public void initialize() {
		databasePicker.setVisible(PREFERENCES.get(USER_TYPE,null).equals("Admin"));
		reloadDbButton.setVisible(PREFERENCES.get(USER_TYPE,null).equals("Admin"));
		databasePicker.setSelected(PREFERENCES.get(DATABASE_HOSTNAME,null) != null);

		databasePane.setVisible(PREFERENCES.get(USER_TYPE,null).equals("Admin") && PREFERENCES.get(DATABASE_HOSTNAME,null) != null);

		hostLabel.setText(PREFERENCES.get(DATABASE_HOSTNAME,"localhost"));

		username = PREFERENCES.get(USER_NAME, null);
		try {
			databasePicker.setVisible(db.getUserByUsername(username).get("USERTYPE").equals("Admin"));

			userName.setText(username);
			firstName.setText(db.getUserByUsername(username).get("FIRSTNAME"));
			lastName.setText(db.getUserByUsername(username).get("LASTNAME"));
			email.setText(db.getUserByUsername(username).get("EMAIL"));
			pronouns.setText(db.getUserByUsername(username).get("PRONOUNS"));
			gender.setText(db.getUserByUsername(username).get("GENDER"));
			if (db.getUserByUsername(username).get("MFAENABLED").equals("true")) {
				tfa.setSelected(true);
			} else {
				tfa.setSelected(false);
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

		userName.setEditable(false);
		firstName.setEditable(false);
		lastName.setEditable(false);
		email.setEditable(false);
		pronouns.setEditable(false);
		phoneNumber.setEditable(false);
		gender.setEditable(false);
		tfa.setDisable(true);
		stackPane.setVisible(false);

	}

	public void editAccount() {

		firstName.setEditable(true);
		lastName.setEditable(true);
		email.setEditable(true);
		phoneNumber.setEditable(true);
		pronouns.setEditable(true);
		gender.setEditable(true);
		tfa.setDisable(false);

	}

	public void saveEdits() {

		firstName.setEditable(false);
		lastName.setEditable(false);
		email.setEditable(false);
		pronouns.setEditable(false);
		gender.setEditable(false);
		phoneNumber.setEditable(false);
		tfa.setDisable(true);


		try {
			Map<String, String> user = new HashMap<String, String>();

			user.put("FIRSTNAME", firstName.getText());
			user.put("LASTNAME", lastName.getText());
			user.put("EMAIL", email.getText());
			user.put("PRONOUNS", pronouns.getText());
			user.put("GENDER", gender.getText());

			db.editUser(username, user);
			PREFERENCES.put(USER_FIRST_NAME, firstName.getText());
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}


	public void tfaSelected() {
		if (tfa.isSelected()) {
			tfaPane.setVisible(true);
			gridPane.setVisible(false);
			verfIncorrect.setVisible(false);
			try {
				Pair<String, byte[]> TOTPinformation = Security.enableTOTP(username);
				readByteArray(TOTPinformation.getValue());
				tfaSource.setText("Secret: \n" + TOTPinformation.getKey());

			} catch (QrGenerationException | SQLException | IOException e) {
				e.printStackTrace();
			}
		} else {
			stackPane.setVisible(true);
			JFXDialogLayout content = new JFXDialogLayout();
			JFXDialog tfadisable = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.TOP);
			content.setHeading(new Text("Disabling Two-Factor"));
			content.setBody(new Text("Are you sure you would like to disable two-factor authentication."));
			JFXButton disable = new JFXButton("Disable");
			JFXButton cancel_button = new JFXButton("Cancel");
			disable.setOnAction(event1 -> {
				tfadisable.close();
				cancel2fa();
			});
			cancel_button.setOnAction(event -> {
				tfa.setSelected(true);
				tfadisable.close();
			});
			content.setActions(cancel_button, disable);
			tfadisable.show();
		}

	}


	public void readByteArray(byte[] pair) throws IOException {
		Image img = new Image(new ByteArrayInputStream(pair));
		tfaView.setImage(img);
	}


	public void tfasubmit_button() throws SQLException {
		boolean verificationSuccess = Security.verifyTOTP(username, verification.getText());
		if (verificationSuccess) {
			popUp("Two Factor Authentication Success", "\n\n\n\n\n\n Two-Factor Authentication successfully enabled");
			tfaPane.setVisible(false);
			gridPane.setVisible(true);
		} else {
			verfIncorrect.setVisible(true);
		}
	}

	public void cancel2fa() {
		tfaPane.setVisible(false);
		try {
			Security.disableTOTP(username);
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		gridPane.setVisible(true);
		tfa.setSelected(false);
	}


	public void aboutP() {
		sceneSwitch("AboutPage");
	}

	public void creditsP() {
		sceneSwitch("CreditsPage");
	}

	public void reloadDB() {
		if (databasePicker.isSelected()) PREFERENCES.put(DATABASE_HOSTNAME, hostLabel.getText());
		else PREFERENCES.remove(DATABASE_HOSTNAME);

		boolean usingEmbedded;

		try {
			usingEmbedded = db.updateConnection();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			popUp("Database Update", "An error occurred when updating the database connection. THIS SHOULD NEVER HAPPEN FIGURE OUT WHAT HAPPENED AAAAAAAAAAAAAAAAAAA");
			return;
		}

		String instanceID = PREFERENCES.get(INSTANCE_ID, null);
		try {
			if (!db.checkUserExistsByUsername(instanceID)) {
				Map<String, String> instanceUser = new HashMap<>();
				instanceUser.put("USERTYPE", DatabaseUtil.USER_TYPE_NAMES.get(USERTYPE.GUEST));
				instanceUser.put("USERNAME", instanceID);
				instanceUser.put("EMAIL", instanceID); //This is because email must be unique and not null
				instanceUser.put("PASSWORD", instanceID); //this should never be used, but it's a thing
				instanceUser.put("SALT", instanceID); //THIS SHOULD NEVER BE USED

				db.addUser(instanceUser);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (PREFERENCES.get(DATABASE_HOSTNAME, null) != null) {
			popUp("Database Update", usingEmbedded ? "Unable to connect to client-server database. Falling back to embedded database." : "Connected to client-server database!");
		} else {
			popUp("Database Update", "Connected to embedded database!");
		}
	}

	public void databaseCheckbox() {
		databasePane.setVisible(databasePicker.isSelected());
		hostLabel.setText(PREFERENCES.get(DATABASE_HOSTNAME,"localhost"));
	}

	public void apiEdit() {
		final Stage myDialog = new Stage();
		myDialog.centerOnScreen();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/ApiPopup.fxml"));

			myDialog.setScene(new Scene(loader.load()));
			myDialog.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void forgotPasswordP(){
		sceneSwitch ( "ForgotPassword" );
	}
}
