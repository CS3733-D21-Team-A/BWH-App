package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseUtil;
import edu.wpi.aquamarine_axolotls.db.SERVICEREQUEST;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
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


public class InternalTransportation extends SServiceRequest {

    @FXML
    private TextField doctorFirstName;

    @FXML
    private TextField doctorLastName;

    @FXML
    private TextField patientFirstName;

    @FXML
    private TextField patientLastName;

    @FXML
    private TextField currentRoom;

    @FXML
    private TextField newRoom;

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
    DatabaseController db;


    @FXML
    public void initialize() throws SQLException, IOException, URISyntaxException {
        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");

        db = DatabaseController.getInstance();

    }




    @FXML
    public void handleButtonAction(ActionEvent actionEvent) throws IOException {
        String dfn = doctorFirstName.getText();
        String dln = doctorLastName.getText();
        String pfn = patientFirstName.getText();
        String pln = patientLastName.getText();
        String crn = currentRoom.getText();
        String nrn = newRoom.getText();


        if(!dfn.matches("[a-zA-Z]+") || !dln.matches("[a-zA-Z]+")){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }

        try {
         //   Aapp.num++; // TODO: better way of establishing request ID
            Map<String, String> shared = new HashMap<String, String>();
            Random r = new Random();
            String id = String.valueOf(Math.abs(r.nextInt()));
            shared.put("REQUESTID", id);
            shared.put("STATUS", "Unassigned");
            shared.put("LOCATIONID", nodeIDS.get(0));
            shared.put("FIRSTNAME", dfn);
            shared.put("LASTNAME", dln);
            shared.put("REQUESTTYPE", DatabaseUtil.SERVICEREQUEST_NAMES.get(SERVICEREQUEST.INTERNAL_TRANSPORT));

            Map<String, String> internalTransportR = new HashMap<String, String>();
            internalTransportR.put("REQUESTID", id);
            internalTransportR.put("CURRENTLOCATION", crn);
            internalTransportR.put("NEWLOCATION", nrn);
            db.addServiceRequest(shared, internalTransportR);
            db.close();
            submit();


        } catch (SQLException e) {
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
