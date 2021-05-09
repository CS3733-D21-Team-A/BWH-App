package edu.wpi.aquamarine_axolotls.views.homepages;

import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;

public class EmployeeMainPage extends PatientMainPage {

    public void initialize() throws IOException, SQLException {
        startUp();
    }

    @FXML
    public void requestP() {
        sceneSwitch("EmployeeRequests");
    }

    @FXML
    public void serviceReqEP() {
        sceneSwitch("EmployeeServiceRequestPage");
    }
}
