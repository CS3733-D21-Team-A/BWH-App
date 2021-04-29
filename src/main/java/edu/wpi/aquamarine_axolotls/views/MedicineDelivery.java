package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static edu.wpi.aquamarine_axolotls.db.DatabaseUtil.SERVICEREQUEST_NAMES;


public class MedicineDelivery extends GenericServiceRequest {

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
    public void initialize() {
        startUp();
        //roomNumber.setItems(FXCollections
          //      .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        //);
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

        if(!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+")
                || dt.isEmpty()){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }

        try {
            DatabaseController db = new DatabaseController();
            //   Aapp.num++; // TODO: better way of establishing request ID
            Map<String, String> shared = getSharedValues(SERVICEREQUEST.MEDICINE_DELIVERY);
            Map<String, String> medicineR = new HashMap<String, String>();
            medicineR.put("REQUESTID", shared.get("REQUESTID"));
            medicineR.put("DELIVERYTIME", dt);
            medicineR.put("MEDICATION", med);
            medicineR.put("DOSAGE", dose);
            medicineR.put("DOCFIRSTNAME", dfn);
            medicineR.put("DOCLASTNAME", dln);

            db.addServiceRequest(shared, medicineR);
            submit();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


}
