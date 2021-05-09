package edu.wpi.aquamarine_axolotls.views.homepages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.SQLException;

import static edu.wpi.aquamarine_axolotls.Settings.USER_NAME;
import static edu.wpi.aquamarine_axolotls.Settings.PREFERENCES;


public class GuestMainPage extends GenericPage {

	@FXML
	StackPane stackPane;

	public DatabaseController db;

	@FXML
	Text userNameText;

	@FXML
	public void initialize() throws SQLException, IOException {
		startUp();
	}

	void startUp() throws IOException, SQLException {
		db = DatabaseController.getInstance();
	}

	@FXML
	public void signInP() {
		sceneSwitch("LogIn");
	}

	@FXML
	public void mapP() {
		try {
			String username = PREFERENCES.get(USER_NAME,null);
			if (!db.hasUserTakenCovidSurvey(username)) {
				popUp("Covid Survey", "\n\n\n\n\nTaking the Covid-19 Survey is necessary before completing this action.");
			} else if (db.getUserByUsername(username).get("COVIDLIKELY") == null) {
				popUp("Covid Survey", "\n\n\n\n\nWait for an employee to approve your survey.");
			} else {
				sceneSwitch("Navigation");
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}

	}

	@FXML
	public void stop() {
		JFXDialogLayout content = new JFXDialogLayout();
		JFXDialog help = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.BOTTOM);
		content.setHeading(new Text("Shut Down Application"));
		content.setBody(new Text("Clicking the Shut Down button will close the application."));
		JFXButton shutDownB = new JFXButton("Shut Down");
		JFXButton cancel_button = new JFXButton("Cancel");
		shutDownB.setOnAction(event1 -> {
            help.close();
            Platform.exit();
        });
		cancel_button.setOnAction(event12 -> help.close());
		content.setActions(cancel_button, shutDownB);
		help.show();
	}

	@FXML
	public void covidSurveyPage() {
		sceneSwitch("CovidSurvey");
	}


}