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


public class GiftDelivery extends GenericServiceRequest {

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private JFXTimePicker deliveryTime;

    @FXML
    private JFXComboBox locationDropdown;

    @FXML
    private JFXComboBox giftOptions;

    @FXML
    private ArrayList<String> nodeIDS;

    @FXML
    public void initialize()  {
        startUp();
        giftOptions.setItems(FXCollections
                .observableArrayList("Hospital T-Shirt", "Teddy Bear", "Hospital Mug"));
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");
        locationDropdown.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        );
    }




    @FXML
    public void handleButtonAction(ActionEvent actionEvent) throws IOException {

        if(giftOptions.getSelectionModel().getSelectedItem() == null
                || locationDropdown.getSelectionModel().getSelectedItem() == null){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number\n- Gift Option");
            return;
        }
        String fn = firstName.getText();
        String ln = lastName.getText();
        String dt = deliveryTime.getValue().format(DateTimeFormatter.ofPattern("HH.mm"));
        int room = locationDropdown.getSelectionModel().getSelectedIndex();
        String gift = giftOptions.getSelectionModel().getSelectedItem().toString();

        if(!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+")
                || dt.isEmpty()){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number\n- Gift Option");
            return;
        }

        ArrayList<String> fields = new ArrayList<String>();
        fields.add(dt);
        fields.add(gift);
        fields.add("NOTE"); // TODO: add field for Note


        try {
            createServiceRequest(SERVICEREQUEST.GIFT_DELIVERY, fields);
            submit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
