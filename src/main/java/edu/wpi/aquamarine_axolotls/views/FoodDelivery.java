package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.Aapp;
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


public class FoodDelivery extends GenericServiceRequest {

    @FXML private JFXTimePicker deliveryTime;
    @FXML private JFXComboBox roomNumber;
    @FXML private JFXComboBox foodOptions;
    @FXML private JFXComboBox drinkOptions;
    @FXML private JFXComboBox numberServings;
    @FXML private JFXTextArea dietaryRestA;
    @FXML private ArrayList<String> nodeIDS;
    @FXML private JFXTextField contactNumber;


    @FXML
    public void initialize() {
        startUp();

        foodOptions.setItems(FXCollections
                .observableArrayList("Mac and Cheese", "Salad", "Pizza"));
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");
        roomNumber.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        );
        drinkOptions.setItems(FXCollections
                .observableArrayList("Water","Coca-Cola", "Sprite", "Milk", "Orange Juice")
        );
        numberServings.setItems(FXCollections
                .observableArrayList("1","2", "3", "4", "5")
        );
    }




    @FXML
    public void handleButtonAction(ActionEvent actionEvent) throws IOException {

        if(foodOptions.getSelectionModel().getSelectedItem() == null
                || roomNumber.getSelectionModel().getSelectedItem() == null){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }
        String fn = firstName.getText();
        String ln = lastName.getText();
       String dt = deliveryTime.getValue().format(DateTimeFormatter.ofPattern("HH.mm"));
        int room = roomNumber.getSelectionModel().getSelectedIndex();
        String food = foodOptions.getSelectionModel().getSelectedItem().toString();
        String rest = dietaryRestA.getText();
        String servings = numberServings.getSelectionModel ().getSelectedItem ().toString ();
        String cn = contactNumber.getText();
        String dop = drinkOptions.getSelectionModel ().getSelectedItem ().toString ();
        if(!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+")
                || dt.isEmpty()){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }

        try {
            Map<String, String> shared = getSharedValues(SERVICEREQUEST.FOOD_DELIVERY);
            Map<String, String> foodR = new HashMap<String, String>();
            foodR.put("REQUESTID", shared.get("REQUESTID"));
            foodR.put("DELIVERYTIME", dt);
            foodR.put("DIETARYRESTRICTIONS", rest);
            foodR.put("NOTE", rest);
            foodR.put ("FOODOPTION", food);
            foodR.put ("NUMBEROFSERVINGS", servings);
            foodR.put ("CONTACTNUMBER", cn );
            foodR.put ("DRINKOPTIONS", dop);

            db.addServiceRequest(shared, foodR);
            submit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

