package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
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
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class FoodDelivery extends SServiceRequest {

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField deliveryTime;

    @FXML
    private JFXComboBox roomNumber;

    @FXML
    private ComboBox foodOptions;

    @FXML
    private JFXTextArea dietaryRestA;

    @FXML
    private AnchorPane myAnchorPane;

    private ArrayList<String> nodeIDS;


    @FXML
    public void initialize() {
        foodOptions.setItems(FXCollections
                .observableArrayList("Mac and Cheese", "Salad", "Pizza"));
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");
        roomNumber.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
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
        String dt = deliveryTime.getText();
        int room = roomNumber.getSelectionModel().getSelectedIndex();
        String food = foodOptions.getSelectionModel().getSelectedItem().toString();
        String rest = dietaryRestA.getText();

        if(!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+")
                || dt.isEmpty()){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }

        try {
            DatabaseController db = new DatabaseController();
            Aapp.num++; // TODO: better way of establishing request ID
            Map<String, String> shared = new HashMap<String, String>();
            Random r = new Random();
            String id = String.valueOf(Math.abs(r.nextInt()));
            shared.put("REQUESTID", id);
            shared.put("STATUS", "Unassigned");
            shared.put("EMPLOYEEID", "");
            shared.put("LOCATIONID", nodeIDS.get(room));
            shared.put("FIRSTNAME", fn);
            shared.put("LASTNAME", ln);
            shared.put("REQUESTTYPE", "Food Delivery");

            Map<String, String> foodR = new HashMap<String, String>();
            foodR.put("REQUESTIDFOOD", id);
            foodR.put("DELIVERYTIME", dt);
            foodR.put("DIETARYRESTRICTIONS", rest);
            foodR.put("NOTE", rest);
            db.addServiceRequest(shared, foodR);
            db.close();
            submit();


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
