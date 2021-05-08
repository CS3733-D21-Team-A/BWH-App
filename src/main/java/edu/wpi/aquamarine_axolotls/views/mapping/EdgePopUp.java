package edu.wpi.aquamarine_axolotls.views.mapping;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Edge;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import edu.wpi.aquamarine_axolotls.views.observerpattern.Observer;
import edu.wpi.aquamarine_axolotls.views.observerpattern.Subject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EdgePopUp extends GenericPage{

    @FXML
    public JFXButton deleteButton;
    @FXML
    public JFXButton cancelButton;
    @FXML
    public JFXButton confirmButton;
    @FXML
    private JFXComboBox<String> startNodeDropdown;
    @FXML
    private JFXComboBox<String> endNodeDropdown;
    @FXML
    private Label edgeIDLabel;
    @FXML
    private Label submissionStatusLabel;

    MapEditing mapController;

    DatabaseController db;

    // TODO : not working
    public EdgePopUp(MapEditing mapController){
        this.mapController = mapController;
    }

    public void initialize() throws SQLException, IOException {
        db = mapController.db;
        ArrayList<String> nodes = new ArrayList<>();
        for(Map<String, String> node : db.getNodes()) nodes.add(node.get("NODEID"));
        startNodeDropdown.setItems(FXCollections
                .observableArrayList(nodes));
        endNodeDropdown.setItems(FXCollections
                .observableArrayList(nodes));



        Subject subject = new Subject(2);


        Observer edgeIDObserver = new Observer(subject, edgeIDLabel, (a) -> a.size() == 2 && !a.get(0).equals(a.get(1)),
                (a) -> a.get(0) + "_" + a.get(1), "ERROR : Unable to create the current edge");

        startNodeDropdown.getSelectionModel().selectedItemProperty().addListener(observable -> {
            subject.setItem(0, startNodeDropdown.getSelectionModel().getSelectedItem());
        });

        endNodeDropdown.getSelectionModel().selectedItemProperty().addListener(observable -> {
            subject.setItem(1, endNodeDropdown.getSelectionModel().getSelectedItem());
        });

        subject.attach(edgeIDObserver);


        try{
            switch(mapController.state) {
                case "Edit":
                    Map<String, String> edge = db.getEdge(mapController.currentID);
                    subject.setItem(0, edge.get("STARTNODE"));
                    subject.setItem(1, edge.get("ENDNODE"));
                    startNodeDropdown.getSelectionModel().select(edge.get("STARTNODE"));
                    endNodeDropdown.getSelectionModel().select(edge.get("ENDNODE"));
                    break;
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

    }


    @FXML
    public void delete(){

    }

    @FXML
    public void submit() throws SQLException{
        if (startNodeDropdown.getSelectionModel().getSelectedItem().isEmpty() ||
                endNodeDropdown.getSelectionModel().getSelectedItem().isEmpty()) {
            submissionStatusLabel.setText("Invalid submission");
            return;
        }

        Map<String, String> edge = new HashMap<>();
        edge.put("EDGEID", edgeIDLabel.getText());
        edge.put("STARTNODE", startNodeDropdown.getSelectionModel().getSelectedItem());
        edge.put("ENDNODE", endNodeDropdown.getSelectionModel().getSelectedItem());

        switch(mapController.state) {
            case "Edit":
                if(!edgeIDLabel.getText().equals(mapController.currentID)) db.deleteEdge(mapController.currentID);
                break;
        }
        if(!db.edgeExists(edge.get("EDGEID"))) db.addEdge(edge);
        else popUp("ERROR", "That edge already exists. Please try again.");
        mapController.drawFloor(mapController.FLOOR);
        edgeIDLabel.getScene().getWindow().hide();

    }

    public void loadHelp(ActionEvent actionEvent) {
    }

    public void cancel(ActionEvent actionEvent) {
    }

}
