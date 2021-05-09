package edu.wpi.aquamarine_axolotls.views.homepages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.SQLException;

import static edu.wpi.aquamarine_axolotls.Settings.USER_NAME;
import static edu.wpi.aquamarine_axolotls.Settings.PREFERENCES;


public class GuestMainPage extends GenericPage {

    DatabaseController db = DatabaseController.getInstance();

    @FXML Text userNameText;

    @FXML
    public void signInP(ActionEvent actionEvent) {
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