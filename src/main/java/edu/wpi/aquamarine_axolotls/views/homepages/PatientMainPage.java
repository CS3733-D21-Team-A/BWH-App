package edu.wpi.aquamarine_axolotls.views.homepages;

import edu.wpi.aquamarine_axolotls.db.DatabaseUtil;
import edu.wpi.aquamarine_axolotls.db.enums.USERTYPE;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

import java.awt.event.ActionEvent;

import static edu.wpi.aquamarine_axolotls.Settings.*;

public class PatientMainPage extends GuestMainPage{
    @FXML StackPane serviceRequestPane;

    public void initialize() {
        startUp();
    }

    public void startUp() {
        userNameText.setText(PREFERENCES.get(USER_TYPE,null) + ": " + PREFERENCES.get(USER_FIRST_NAME,null));

        serviceRequestPane.setOnMouseExited(mouseEvent -> {
            if (serviceRequestPane.isVisible()) {
                serviceRequestPane.setVisible(false);
                serviceRequestPane.toBack();
            }
        });
    }

    public void serviceReqP() {
        serviceRequestPane.setVisible(true);
        serviceRequestPane.toFront();
    }

    @FXML
    public void foodDeliveryP() {
        sceneSwitch("FoodDelivery");
    }
    @FXML
    public void floralDelivP() {
        sceneSwitch("FloralDelivery");
    }
    @FXML
    public void externalTransP() {
        sceneSwitch("ExternalTransport");
    }
    @FXML
    public void medicineDelivP() {
        sceneSwitch("MedicineDelivery");
    }
    @FXML
    public void giftDelivP() {
        sceneSwitch("GiftDelivery");
    }
    @FXML
    public void facilityMainP() {
        sceneSwitch ( "FacilitiesMaintenance" );
    }
    @FXML
    public void viewReqP() {
        sceneSwitch("EmployeeRequests");
    }
    @FXML
    public void internalTransport() {
        sceneSwitch("InternalTransportation");
    }
    @FXML
    public void sanitationP() {
        sceneSwitch("Sanitation");
    }
    @FXML
    public void laundryP() {
        sceneSwitch("LaundryService");
    }
    @FXML
    public void languageP() {
        sceneSwitch("LanguageInterpreter");
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
