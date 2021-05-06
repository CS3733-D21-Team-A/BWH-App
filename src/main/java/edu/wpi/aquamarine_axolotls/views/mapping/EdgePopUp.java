package edu.wpi.aquamarine_axolotls.views.mapping;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class EdgePopUp {

    @FXML
    private JFXComboBox startNodeDropdown;
    @FXML
    private JFXComboBox endNodeDropdown;
    @FXML
    private Label edgeIDLabel;
    @FXML
    private Label submissionStatusLabel;

    @FXML
    public void delete(){

    }

    @FXML
    public void submit(){

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
