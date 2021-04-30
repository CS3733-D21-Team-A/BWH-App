package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class FloralDelivery extends GenericServiceRequest {

    @FXML
    private JFXTimePicker deliveryTime;
    @FXML private JFXComboBox roomNumber;
    @FXML
    private JFXTextArea persMessage;
    @FXML
    private JFXComboBox flowerOptions;
    @FXML
    private JFXDatePicker deliveryDate;
    @FXML
    private JFXTextField contactNumber;
    @FXML
    private JFXComboBox vaseOptions;


    @FXML
    public void initialize() {
        startUp();
      roomNumber.setItems(FXCollections
                .observableArrayList("75 Lobby Information Desk","Connors Center Security Desk Floor 1")
        );
        flowerOptions.setItems(FXCollections
                .observableArrayList("Roses", "Sunflowers", "Peruvian Lilies", "Hydrangeas", "Orchids")
        );

        vaseOptions.setItems(FXCollections
                .observableArrayList("Bouquet Vase", "Square Vase", "Cylinder Vase", "Milk Bottle", "Pedestal Vase")
        );
    }


    @FXML
    public void handleButtonAction(javafx.event.ActionEvent actionEvent) throws IOException {
        if ( roomNumber.getSelectionModel ( ).getSelectedItem ( ) == null || flowerOptions.getSelectionModel().getSelectedItem() == null || vaseOptions.getSelectionModel().getSelectedItem() == null || firstName.getText().isEmpty() || contactNumber.getText().isEmpty() || lastName.getText().isEmpty() || deliveryTime.getValue() == null) {
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Patient Room Number\n- Contact Number\n- Delivery Date\n- Flower Options\n- Vase Options");
            return;
        }
        String fn = firstName.getText();
        String ln = lastName.getText();

        if (!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+")) {
            errorFields("- First Name\n- Last Name\n-Delivery Time\n- Patient Room Number");
            return;
        }

        ArrayList<String> fields = new ArrayList<String>();
        fields.add(contactNumber.getText());
        fields.add(deliveryDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        fields.add(deliveryTime.getValue().format(DateTimeFormatter.ofPattern("HH.mm")));
        fields.add(flowerOptions.getSelectionModel().getSelectedItem().toString());
        fields.add(persMessage.getText());
        fields.add(vaseOptions.getSelectionModel().getSelectedItem().toString());

        try {
            createServiceRequest(SERVICEREQUEST.FLORAL_DELIVERY, fields);
            submit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


