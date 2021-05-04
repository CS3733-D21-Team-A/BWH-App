package edu.wpi.aquamarine_axolotls.views.servicerequests;

import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.fxml.FXML;

public class DefaultServicePage extends GenericPage { //TODO: Please change name of this class and page

    // Button Presses for Default Service Page, each button leads to its service request form
    // NOTE: code feels redundant, maybe make method?

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
}

