package edu.wpi.aquamarine_axolotls.views.mapping;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Edge;
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

public class EdgePopUp {

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
        edge.put("ENDNODE", startNodeDropdown.getSelectionModel().getSelectedItem());

        switch(mapController.state) {
            case "Edit":
                if(!edgeIDLabel.getText().equals(mapController.currentID)) db.deleteEdge(mapController.currentID);
                break;
        }
        db.addEdge(edge);
        mapController.drawFloor(mapController.FLOOR);
        edgeIDLabel.getScene().getWindow().hide();



    }

    public void loadHelp(ActionEvent actionEvent) {
    }

    public void cancel(ActionEvent actionEvent) {
    }
//
//    /**
//     * Deletes a edge based on user selection
//     */
//    public void delete() {
//        String edgeID = edgeDropdown.getSelectionModel().getSelectedItem().toString();
//        System.out.println(edgeID);
//        try {
//            if (db.edgeExists(edgeID)) {
//                db.deleteEdge(edgeID);
//                drawFloor(FLOOR);
//                submissionlabel.setText("You have deleted " + edgeID);
//            } else {
//                submissionlabel.setText("Edge does not exist");
//            }
//        } catch (SQLException sq) {
//            sq.printStackTrace();
//        }
//        return;
//    }
//
//    /**
//     * Adds an edge based on user selection
//     */
//    public void add() {
//        String edgeID = edgeIDtextbox.getText();
//        String startNode = startNodeDropdown.getSelectionModel().getSelectedItem().toString();
//        String endNode = endNodeDropdown.getSelectionModel().getSelectedItem().toString();
//
//        if (edgeID.equals("") || startNode.equals("") || endNode.equals("")) {
//            submissionlabel.setText("Did not fill out all required fields");
//            return;
//        } else {
//            try {
//                if (!db.edgeExists(edgeID)) {
//                    Map<String, String> edge = new HashMap<String, String>();
//                    edge.put("EDGEID", edgeID);
//                    edge.put("STARTNODE", startNode);
//                    edge.put("ENDNODE", endNode);
//
//                    db.addEdge(edge);
//                    drawFloor(FLOOR);
//                    submissionlabel.setText("You have added " + edgeID);
//                } else {
//                    submissionlabel.setText("Edge already exist");
//                }
//            } catch (SQLException sq) {
//                sq.printStackTrace();
//            }
//            return;
//        }
//    }
//
//    /**
//     * Edits an edge based on user selection
//     */
//    public void edit() {
//        String edgeID = edgeDropdown.getSelectionModel().getSelectedItem().toString(); //TODO: GIVE WARNING ON NULL EDGEID
//        String startNode = startNodeDropdown.getSelectionModel().getSelectedItem() == null ? "" : startNodeDropdown.getSelectionModel().getSelectedItem().toString();
//        String endNode = endNodeDropdown.getSelectionModel().getSelectedItem() == null ? "" : endNodeDropdown.getSelectionModel().getSelectedItem().toString();
//
//        try {
//            if (startNode.equals("")) {
//                startNode = db.getEdge(edgeID).get("STARTNODE");
//            }
//            if (endNode.equals("")) {
//                endNode = db.getEdge(edgeID).get("ENDNODE");
//            }
//
//            if (db.edgeExists(edgeID)) {
//                Map<String, String> edge = new HashMap<String, String>();
//                edge.put("EDGEID", edgeID);
//                edge.put("STARTNODE", startNode);
//                edge.put("ENDNODE", endNode);
//
//                drawFloor(FLOOR);
//                db.editEdge(edgeID, edge);
//                submissionlabel.setText("You have edited " + edgeID);
//            } else {
//                submissionlabel.setText("Edge does not exist");
//            }
//        } catch (SQLException sq) {
//            sq.printStackTrace();
//        }
//        return;
//
//    }
//
//
}
