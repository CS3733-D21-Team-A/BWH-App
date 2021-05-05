package edu.wpi.aquamarine_axolotls.views.homepages;

import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;

public class PatientMainPage extends GuestMainPage{

    public void initialize() throws IOException, SQLException {
        startUp();
    }

    @Override
    public void startUp() throws SQLException, IOException {
        super.startUp();
        userNameText.setText ( Aapp.userType + ": " + Aapp.userFirstName );
    }

    @FXML
    public void serviceReqP(ActionEvent actionEvent) {
        sceneSwitch("DefaultServicePage");
    }

    @FXML
    public void signOutPage(){
        popUp("Sign Out", "\n\n\n\n\nYou have been signed out of your account.");
        Aapp.username = null;
        Aapp.userType = "Guest";
        Aapp.userFirstName = null;
        goHome();
    }

}
