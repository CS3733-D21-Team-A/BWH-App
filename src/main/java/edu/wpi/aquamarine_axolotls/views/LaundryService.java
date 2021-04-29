package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.db.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class LaundryService extends SServiceRequest {

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
                || roomNumber.getSelectionModel().getSelectedItem() == null){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }
        String fn = firstName.getText();
        String ln = lastName.getText();
        String dt = deliveryTime.getValue().format(DateTimeFormatter.ofPattern("HH.mm"));
        int room = roomNumber.getSelectionModel().getSelectedIndex();
        String food = loadOptions.getSelectionModel().getSelectedItem().toString();
        String rest = specialRequest.getText();

        if(!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+")
                || dt.isEmpty()){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }

        try {
            //   Aapp.num++; // TODO: better way of establishing request ID
            Map<String, String> shared = getSharedValues(SERVICEREQUEST.LAUNDRY);
            Map<String, String> foodR = new HashMap<String, String>();
            foodR.put("REQUESTID", shared.get("REQUESTID"));
            foodR.put("DELIVERYTIME", dt);
            foodR.put("ARTICLESOFCLOTHING", rest);
            foodR.put("NOTE", rest);
            db.addServiceRequest(shared, foodR);
            submit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

