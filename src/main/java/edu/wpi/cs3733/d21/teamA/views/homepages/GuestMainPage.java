package edu.wpi.cs3733.d21.teamA.views.homepages;

import edu.wpi.cs3733.d21.teamA.db.DatabaseController;
import edu.wpi.cs3733.d21.teamA.views.GenericPage;
import javafx.fxml.FXML;

import java.sql.SQLException;

import static edu.wpi.cs3733.d21.teamA.Settings.*;


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