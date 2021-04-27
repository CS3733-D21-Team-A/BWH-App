package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.db.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class FloralDelivery extends SServiceRequest {

    @FXML
    private JFXTextField firstName;

    @FXML
    private JFXTextField lastName;

    @FXML
    private JFXTimePicker deliveryTime;

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

    @FXML
    private JFXComboBox flowerOptions;

    @FXML private JFXDatePicker deliveryDate;

    @FXML private JFXTextField contactNumber;
    @FXML
    private JFXComboBox vaseOptions;
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
        flowerOptions.setItems(FXCollections
                .observableArrayList("Roses","Sunflowers", "Peruvian Lilies", "Hydrangeas", "Orchids")
        );

        vaseOptions.setItems(FXCollections
                .observableArrayList("Bouquet Vase","Square Vase", "Cylinder Vase", "Milk Bottle", "Pedestal Vase")
        );
    }




    @FXML
    public void handleButtonAction(javafx.event.ActionEvent actionEvent) throws IOException {
        if ( roomNumber.getSelectionModel ( ).getSelectedItem ( ) == null || firstName.getText ().isEmpty () || contactNumber.getText ().isEmpty () ||lastName.getText ().isEmpty() || deliveryTime.getValue ( ) == null ) {
            errorFields ( "- First Name\n- Last Name\n-Delivery Time\n- Patient Room Number\n- Contact Number\n- Delivery Date\n- Flower Options\n- Vase Options"  );
            return;
        }
        String fn = firstName.getText ( );
        String ln = lastName.getText ( );
        String dt = deliveryTime.getValue ( ).format ( DateTimeFormatter.ofPattern ( "HH.mm" ) );
        String dd = deliveryDate.getValue ().format ( DateTimeFormatter.ofPattern ( "yyyy-MM-dd" ) );
        String fo = flowerOptions.getSelectionModel ().getSelectedItem ().toString ();
        int room = roomNumber.getSelectionModel ( ).getSelectedIndex ( );
        String vo = vaseOptions.getSelectionModel ().getSelectedItem ().toString ();
        String pmsg = persMessage.getText ( );
        String  co = contactNumber.getText();

        //TODO: make pop up here
        if ( !fn.matches ( "[a-zA-Z]+" ) || !ln.matches ( "[a-zA-Z]+" ) || dt.isEmpty ( ) ) {
            errorFields ( "- First Name\n- Last Name\n-Delivery Time\n- Patient Room Number" );
            return;
        }

        try {
            DatabaseController db = new DatabaseController();
            Map<String, String> shared = new HashMap<String, String>();
            Random r = new Random();
            int id = Math.abs(r.nextInt());
            shared.put("REQUESTID", String.valueOf(id));
            shared.put("STATUS", "Unassigned");
            shared.put("LOCATIONID", String.valueOf(nodeIDS.get(room)));
            shared.put("FIRSTNAME", fn);
            shared.put("LASTNAME", ln);
            shared.put("REQUESTTYPE", DatabaseUtil.SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FLORAL_DELIVERY));

            Map<String, String> floral = new HashMap<String, String>();

            floral.put("REQUESTID", String.valueOf(id));
            floral.put("DELIVERYTIME", dt);
            floral.put("NOTE", pmsg);
            floral.put("DELIVERYDATE", dd);
            floral.put ( "FLOWEROPTION", fo );
            floral.put ( "VASEOPTION", vo );
            floral.put ( "CONTACTNUMBER", co );

            db.addServiceRequest(shared, floral);
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


