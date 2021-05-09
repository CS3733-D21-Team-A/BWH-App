package edu.wpi.aquamarine_axolotls.views.homepages;

import edu.wpi.aquamarine_axolotls.db.DatabaseUtil;
import edu.wpi.aquamarine_axolotls.db.enums.USERTYPE;
import javafx.fxml.FXML;

import static edu.wpi.aquamarine_axolotls.Settings.*;

public class PatientMainPage extends GuestMainPage{

    public void initialize() {
        startUp();
    }

    public void startUp() {
        userNameText.setText(PREFERENCES.get(USER_TYPE,null) + ": " + PREFERENCES.get(USER_FIRST_NAME,null));
    }

    @FXML
    public void serviceReqP() {
        sceneSwitch("DefaultServicePage");
    }

    @FXML
    public void signOutPage(){
        popUp("Sign Out", "\n\n\n\n\nYou have been signed out of your account.");
        PREFERENCES.put(USER_TYPE, DatabaseUtil.USER_TYPE_NAMES.get(USERTYPE.GUEST));
        PREFERENCES.put(USER_NAME, PREFERENCES.get(INSTANCE_ID,null));
        PREFERENCES.remove(USER_FIRST_NAME);
        goHome();
    }

    @FXML
    public void settingsP() {
        sceneSwitch("UserSettings");
    }

}
