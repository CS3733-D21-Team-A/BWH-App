package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.CSVHandler;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javafx.stage.Modality;

public class FoodDelivery extends SServiceRequest {
    ObservableList<String> foodOptionList = FXCollections
            .observableArrayList("Vegetarian", "Salad", "Pizza");

    DatabaseController db;
    CSVHandler csvHandler;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField deliveryTime;

    @FXML
    private TextField roomNumber;

    @FXML
    private ComboBox foodOptions;

    @FXML
    private ComboBox locationDropdown;
    @FXML
    private AnchorPane myAnchorPane;

    @FXML
    public void initialize() {
        ObservableList<String> options = FXCollections.observableArrayList();
        foodOptions.setItems(foodOptionList);

        try {
            db = new DatabaseController();
            csvHandler = new CSVHandler(db);
            List<Map<String, String>> nodes = db.getNodes();
            for (Map<String, String> node : nodes) {
                options.add(node.get("LONGNAME"));
            }
            locationDropdown.setItems(options);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleButtonAction(ActionEvent actionEvent) throws IOException {

        if(foodOptions.getSelectionModel().getSelectedItem() == null) return;
        String fn = firstName.getText();
        String ln = lastName.getText();
        String dt = deliveryTime.getText();
        String room = roomNumber.getText();
        String food = foodOptions.getSelectionModel().getSelectedItem().toString();

        if(!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+")
                || dt.isEmpty() || room.isEmpty()){
            return;
        }

        Map<String, String> service = new HashMap<String, String>();
        service.put("SERVICENAME", "Food Delivery");
        service.put("FIRSTNAME", fn);
        service.put("LASTNAME", ln);
        service.put("DELIVERYTIME", dt);
        service.put("ROOMID", room);
        service.put("FOODOPTIONS", food);
        Aapp.serviceRequests.add(service);
        submit(actionEvent);
    }
}

