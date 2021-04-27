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
    private AnchorPane myAnchorPane;

    @FXML
    private ArrayList<String> nodeIDS;


    @FXML
    JFXHamburger burger;

    @FXML
    JFXDrawer menuDrawer;

    @FXML
    VBox box;

    HamburgerBasicCloseTransition transition;


    @FXML
    public void initialize() { //TODO: fill these out
        loadOptions.setItems(FXCollections
                .observableArrayList("Mac and Cheese", "Salad", "Pizza"));
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");
        roomNumber.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        );
        detergentType.setItems(FXCollections
                .observableArrayList("Water","Coca-Cola", "Sprite", "Milk", "Orange Juice")
        );
        articlesOfClothing.setItems(FXCollections
                .observableArrayList("1","2", "3", "4", "5")
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
            DatabaseController db = new DatabaseController();
            //   Aapp.num++; // TODO: better way of establishing request ID
            Map<String, String> shared = new HashMap<String, String>();
            Random r = new Random();
            String id = String.valueOf(Math.abs(r.nextInt()));
            shared.put("REQUESTID", id);
            shared.put("STATUS", "Unassigned");
            shared.put("LOCATIONID", nodeIDS.get(room));
            shared.put("FIRSTNAME", fn);
            shared.put("LASTNAME", ln);
            shared.put("REQUESTTYPE", DatabaseUtil.SERVICEREQUEST_NAMES.get(SERVICEREQUEST.LAUNDRY));

            Map<String, String> foodR = new HashMap<String, String>();
            foodR.put("REQUESTID", id);
            foodR.put("DELIVERYTIME", dt);
            foodR.put("ARTICLESOFCLOTHING", rest);
            foodR.put("NOTE", rest);
            db.addServiceRequest(shared, foodR);
            db.close();
            submit();
        } catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
        }
    }



    public void menu(){
        if(transition.getRate() == -1) menuDrawer.open();
        else menuDrawer.close();
        transition.setRate(transition.getRate() * -1);
        transition.play();
    }
}

