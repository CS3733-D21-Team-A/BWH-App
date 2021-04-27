package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
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


public class MedicineDelivery extends SServiceRequest {

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
    public void initialize() {
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
            shared.put("REQUESTTYPE", "Medicine Delivery");

            Map<String, String> medicineR = new HashMap<String, String>();
            medicineR.put("REQUESTID", id);
            medicineR.put("DELIVERYTIME", dt);
            medicineR.put("MEDICATION", med);
            medicineR.put("DOSESIZE", dose);
            medicineR.put("DOCFIRSTNAME", dfn);
            medicineR.put("DOCLASTNAME", dln);
            db.addServiceRequest(shared, medicineR);
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