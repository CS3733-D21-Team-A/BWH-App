package edu.wpi.aquamarine_axolotls.views.homepages;

import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import edu.wpi.aquamarine_axolotls.views.mapping.NodePopUp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

import static edu.wpi.aquamarine_axolotls.Settings.*;


public class GuestMainPage extends GenericPage {

    DatabaseController db = DatabaseController.getInstance();

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
	public void covidSurveyPage() {
		sceneSwitch("CovidSurvey");
	}

}