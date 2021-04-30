package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.*;
import edu.wpi.aquamarine_axolotls.views.servicerequests.GenericServiceRequest;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class LaundryService extends GenericServiceRequest {

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private JFXTimePicker deliveryTime;

    @FXML
    private JFXComboBox roomNumber;

    @FXML
    private JFXComboBox loadOptions;

    @FXML
    private JFXComboBox detergentType;

    @FXML
    private JFXComboBox articlesOfClothing;


    @FXML
    private JFXTextArea specialRequest;

    @FXML
    private ArrayList<String> nodeIDS;



    @FXML
    public void initialize() { //TODO: fill these out
        startUp();
        loadOptions.setItems(FXCollections
                .observableArrayList("Delicates", "Light", "Heavy"));
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");
        roomNumber.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        );
        detergentType.setItems(FXCollections
                .observableArrayList("Tide","All", "Gain")
        );
        articlesOfClothing.setItems(FXCollections
                .observableArrayList("Work Clothes","T-Shirts", "Under Garments", "Clerical Garb")
        );
    }




    @FXML
    public void handleButtonAction(ActionEvent actionEvent) throws IOException {

        if(loadOptions.getSelectionModel().getSelectedItem() == null
                || roomNumber.getSelectionModel().getSelectedItem() == null
                || detergentType.getSelectionModel().getSelectedItem() == null
                || articlesOfClothing.getSelectionModel().getSelectedItem() == null){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }
        String fn = firstName.getText();
        String ln = lastName.getText();
        String dt = deliveryTime.getValue().format(DateTimeFormatter.ofPattern("HH.mm"));
        int room = roomNumber.getSelectionModel().getSelectedIndex();
        String ac = articlesOfClothing.getSelectionModel().getSelectedItem().toString();
        String ldo = loadOptions.getSelectionModel().getSelectedItem().toString();
        String sprq = specialRequest.getText();

        if(!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+")
                || dt.isEmpty()){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }

        ArrayList<String> fields = new ArrayList<String>();
        fields.add(ac);
        fields.add(dt);
        fields.add(sprq); // TODO add other fields to DB

        try {
            createServiceRequest(SERVICEREQUEST.LAUNDRY, fields);
            submit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

