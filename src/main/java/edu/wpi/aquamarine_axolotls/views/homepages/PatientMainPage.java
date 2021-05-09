package edu.wpi.aquamarine_axolotls.views.homepages;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;

import static edu.wpi.aquamarine_axolotls.Settings.*;

public class PatientMainPage extends GuestMainPage{

    public void initialize() throws IOException, SQLException {
        startUp();
    }

    @Override
    public void startUp() throws SQLException, IOException {
        super.startUp();
        userNameText.setText(prefs.get(USER_TYPE,null) + ": " + prefs.get(USER_FIRST_NAME,null));
    }

    @FXML
    public void serviceReqP(ActionEvent actionEvent) {
        sceneSwitch("DefaultServicePage");
    }

    @FXML
    public void signOutPage(){
        popUp("Sign Out", "\n\n\n\n\nYou have been signed out of your account.");
        prefs.remove(USER_NAME);
        prefs.remove(USER_TYPE);
        prefs.remove(USER_FIRST_NAME);
        goHome();
    }

}
