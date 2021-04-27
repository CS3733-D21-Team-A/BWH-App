package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
public class CovidSurvey extends SPage {


    @FXML
    JFXHamburger burger;

    @FXML
    JFXDrawer menuDrawer;

    @FXML
    VBox box;

    @FXML
    private JFXRadioButton yes1;

    @FXML
    private JFXRadioButton yes2;
    @FXML
    private JFXRadioButton yes3;
    @FXML
    private JFXRadioButton yes4;
    @FXML
    private JFXRadioButton yes5;
    @FXML
    private JFXRadioButton yes6;
    @FXML
    private JFXRadioButton yes7;
    @FXML
    private JFXRadioButton yes8;
    @FXML
    private JFXRadioButton yes9;
    @FXML
    private JFXRadioButton yes10;
    @FXML
    private JFXRadioButton yes11;
    @FXML
    private JFXRadioButton no1;
    @FXML
    private JFXRadioButton no2;
    @FXML
    private JFXRadioButton no3;
    @FXML
    private JFXRadioButton no4;
    @FXML
    private JFXRadioButton no5;
    @FXML
    private JFXRadioButton no6;
    @FXML
    private JFXRadioButton no7;
    @FXML
    private JFXRadioButton no8;
    @FXML
    private JFXRadioButton no9;
    @FXML
    private JFXRadioButton no10;
    @FXML
    private JFXRadioButton no11;


    @FXML
    private GridLayout grid;

    @FXML
    private JFXScrollPane scrollPane;

    HamburgerBasicCloseTransition transition;
    private ArrayList<String> nodeIDS;


    @FXML
    public void initialize() {
        ToggleGroup group1 = new ToggleGroup();
        final ToggleGroup group2 = new ToggleGroup();
        final ToggleGroup group3 = new ToggleGroup();
        final ToggleGroup group4 = new ToggleGroup();
        final ToggleGroup group5 = new ToggleGroup();
        final ToggleGroup group6 = new ToggleGroup();
        final ToggleGroup group7 = new ToggleGroup();
        final ToggleGroup group8 = new ToggleGroup();
        final ToggleGroup group9 = new ToggleGroup();
        final ToggleGroup group10 = new ToggleGroup();
        final ToggleGroup group11 = new ToggleGroup();




        yes1.setToggleGroup(group1);
        no1.setToggleGroup(group1);

        yes2.setToggleGroup(group2);
        no2.setToggleGroup(group2);

        yes3.setToggleGroup(group3);
        no3.setToggleGroup(group3);

        yes4.setToggleGroup(group4);
        no4.setToggleGroup(group4);

        yes5.setToggleGroup(group5);
        no5.setToggleGroup(group5);

        yes6.setToggleGroup(group6);
        no6.setToggleGroup(group6);

        yes7.setToggleGroup(group7);
        no7.setToggleGroup(group7);

        yes8.setToggleGroup(group8);
        no8.setToggleGroup(group8);

        yes9.setToggleGroup(group9);
        no9.setToggleGroup(group9);

        yes10.setToggleGroup(group10);
        no10.setToggleGroup(group10);

        yes11.setToggleGroup(group11);
        no11.setToggleGroup(group11);


        nodeIDS = new ArrayList<String>();
        nodeIDS.add("FINFO00101");
        nodeIDS.add("EINFO00101");

    }

    @FXML
    public void handleButtonAction(javafx.event.ActionEvent actionEvent) throws IOException {
           /* if(roomNumber.getSelectionModel().getSelectedItem() == null || deliveryTime.getValue() == null){
                return;
            }

            String fn = firstName.getText();
            String ln = lastName.getText();
            String dt = deliveryTime.getValue().format(DateTimeFormatter.ofPattern("HH.mm"));
            int room = roomNumber.getSelectionModel().getSelectedIndex();
            String pmsg = persMessage.getText();


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
                shared.put("REQUESTTYPE", "Floral Delivery");

                Map<String, String> floral = new HashMap<String, String>();

                floral.put("REQUESTID", String.valueOf(id));
                floral.put("DELIVERYTIME", dt);
                floral.put("NOTE", pmsg);
                db.addServiceRequest(shared, floral);
                db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
*/
    }
    public void helpButton(ActionEvent actionEvent) {
        popUp("CovidSurvey", "\n\n\nPlease fill out this survey to the best of your ability ");
    }

    public void submitButton(ActionEvent actionEvent) {
        popUp("Submission Success!", "\n\n\nYour Covid-19 Survey has been submitted. ");
        sceneSwitch ( "" );


    }

    public void menu() {
        if (transition.getRate() == -1) menuDrawer.open();
        else menuDrawer.close();
        transition.setRate(transition.getRate() * -1);
        transition.play();
    }
}



