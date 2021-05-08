package edu.wpi.aquamarine_axolotls.views.homepages;

import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.sql.SQLException;

public class PatientMainPage extends GuestMainPage{
    @FXML StackPane serviceRequestPane;

    public void initialize() throws IOException, SQLException {
        startUp();
    }

    @Override
    public void startUp() throws SQLException, IOException {
        super.startUp();
        userNameText.setText ( Aapp.userType + ": " + Aapp.userFirstName );

        serviceRequestPane.setOnMouseExited(mouseEvent -> {
            if (serviceRequestPane.isVisible()) {
                serviceRequestPane.setVisible(false);
                serviceRequestPane.toBack();
            }
        });
    }

    @FXML
    public void serviceReqP(ActionEvent actionEvent) {
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
        Aapp.username = null;
        Aapp.userType = "Guest";
        Aapp.userFirstName = null;
        goHome();
    }



}
