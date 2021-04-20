package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import edu.wpi.aquamarine_axolotls.Aapp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;


public class FloralDelivery extends SServiceRequest {

    @FXML
    private JFXTextField firstName;

    @FXML
    private JFXTextField lastName;

    @FXML
    private JFXTextField deliveryTime;

    @FXML
    private JFXComboBox roomNumber;

    @FXML
    private JFXTextField persMessage;
    @FXML
    JFXHamburger burger;

    @FXML
    JFXDrawer menuDrawer;

    @FXML
    VBox box;

    HamburgerBasicCloseTransition transition;


    @FXML
    private AnchorPane myAnchorPane;

    private ArrayList<String> nodeIDS;


    @FXML
    public void initialize() {
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");
        roomNumber.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        );
    }




    @FXML
    public void handleButtonAction(javafx.event.ActionEvent actionEvent) throws IOException {
        if(roomNumber.getSelectionModel().getSelectedItem() == null){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Patient Room Number");
            return;
        }

        String fn = firstName.getText();
        String ln = lastName.getText();
        String dt = deliveryTime.getText();
        int room = roomNumber.getSelectionModel().getSelectedIndex();
        String pmsg = persMessage.getText();

        //TODO: make pop up here
        if(!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+") || dt.isEmpty()){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Patient Room Number");
            return;
        }

        try {
            DatabaseController db = new DatabaseController();
            Map<String, String> shared = new HashMap<String, String>();
            Random r = new Random();
            int id = Math.abs(r.nextInt());
            shared.put("REQUESTID", String.valueOf(id));
            shared.put("STATUS", "Unassigned");
            shared.put("LOCATIONID", nodeIDS.get(room));
            shared.put("FIRSTNAME", fn);
            shared.put("LASTNAME", ln);
            shared.put("REQUESTTYPE", "Floral Delivery");

            Map<String, String> floral = new HashMap<String, String>();

            floral.put("REQUESTID", String.valueOf(id));
            floral.put("DELIVERYTIME", dt);
            floral.put("NOTE", pmsg);
            db.addServiceRequest(shared, floral);
            db.close();
            submit();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
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


