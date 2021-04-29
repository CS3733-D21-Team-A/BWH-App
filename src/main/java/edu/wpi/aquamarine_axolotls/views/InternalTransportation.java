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


public class InternalTransportation extends GenericServiceRequest {

    @FXML
    private TextField doctorFirstName;
    @FXML
    private TextField doctorLastName;
    @FXML
    private TextField currentRoom;
    @FXML
    private TextField newRoom;


    @FXML
    public void initialize() throws SQLException, IOException, URISyntaxException {
     startUp();
    }

    @FXML
    public void handleButtonAction(ActionEvent actionEvent) throws IOException {
        String dfn = doctorFirstName.getText();
        String dln = doctorLastName.getText();
        String pfn = firstName.getText();
        String pln = lastName.getText();
        String crn = currentRoom.getText();
        String nrn = newRoom.getText();


        if(!dfn.matches("[a-zA-Z]+") || !dln.matches("[a-zA-Z]+")){
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Room Number");
            return;
        }

        try {
         //   Aapp.num++; // TODO: better way of establishing request ID
            Map<String, String> shared = getSharedValues(SERVICEREQUEST.INTERNAL_TRANSPORT);
            Map<String, String> internalTransportR = new HashMap<String, String>();
            internalTransportR.put("REQUESTID", shared.get("REQUESTID"));
            internalTransportR.put("CURRENTLOCATION", crn);
            internalTransportR.put("NEWLOCATION", nrn);
            db.addServiceRequest(shared, internalTransportR);;
            submit();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
