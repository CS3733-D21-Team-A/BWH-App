package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.SERVICEREQUEST;
import edu.wpi.aquamarine_axolotls.views.servicerequests.GenericServiceRequest;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class MedicineDelivery extends GenericServiceRequest {

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField docFirstName;

    @FXML
    private TextField docLastName;

    @FXML
    private TextField medication;

    @FXML
    private TextField doseSize;

    @FXML
    private JFXTimePicker deliveryTime;

    @FXML
    private JFXComboBox roomNumber;

    @FXML
    private ArrayList<String> nodeIDS;




    @FXML
    public void initialize() {
        startUp();
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");
        roomNumber.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        );
    }




    @FXML
    public void handleButtonAction(ActionEvent actionEvent) throws IOException {

        if(roomNumber.getSelectionModel().getSelectedItem() == null){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }
        String fn = firstName.getText();
        String ln = lastName.getText();
        String dfn = docFirstName.getText();
        String dln = docLastName.getText();
        String med = medication.getText();
        String dose = doseSize.getText();
        String dt = deliveryTime.getValue().format(DateTimeFormatter.ofPattern("HH.mm"));
        int room = roomNumber.getSelectionModel().getSelectedIndex();

        if(!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+") // TODO : better checks
                || dt.isEmpty()){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }

        ArrayList<String> fields = new ArrayList<String>();
        fields.add(dt);
        fields.add(dfn);
        fields.add(dln);
        fields.add(dose);
        fields.add(med);

        try {
            createServiceRequest(SERVICEREQUEST.MEDICINE_DELIVERY, fields);
            submit();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}
