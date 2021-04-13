package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EdgeEditing {
    @FXML private JFXButton homeButton;
    @FXML private JFXButton helpButton;
    @FXML public JFXButton deleteButton;
    @FXML private JFXButton addButton;
    @FXML private JFXButton editButton;
    @FXML private JFXComboBox edgeDropdown;
    @FXML private JFXTextField edgeIDtextbox;
    @FXML private JFXComboBox startNodeDropdown;
    @FXML private JFXComboBox endNodeDropdown;
    @FXML private Label submissionlabel;
    @FXML private JFXButton submissionButton;

    int label = 3; // 0 means delete, 1 means add, 2 means edit, 3 means invalid


    DatabaseController db;

    @FXML
    public void initialize() {
        ObservableList<String> edgeOptions = FXCollections.observableArrayList();
        ObservableList<String> nodeOptions = FXCollections.observableArrayList();
        edgeDropdown.setVisible(false);
        edgeIDtextbox.setVisible(false);
        startNodeDropdown.setVisible(false);
        endNodeDropdown.setVisible(false);

        try {
            db = new DatabaseController();
            List<Map<String, String>> edges = db.getEdges();
            List<Map<String, String>> nodes = db.getNodes();

            for (Map<String, String> edge : edges) {
                edgeOptions.add(edge.get("EDGEID"));
            }

            for (Map<String, String> node : nodes) {
                if (node.get("NODETYPE").equals("EXIT") || node.get("NODETYPE").equals("PARK")) {
                    nodeOptions.add(node.get("NODEID"));
                }
            }
            edgeDropdown.setItems(edgeOptions);
            startNodeDropdown.setItems(nodeOptions);
            endNodeDropdown.setItems(nodeOptions);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public void pressDeleteButton() {
        edgeDropdown.setVisible(true);
        edgeIDtextbox.setVisible(false);
        startNodeDropdown.setVisible(false);
        endNodeDropdown.setVisible(false);
        label = 0;
    }

    public void pressAddButton() {
        edgeDropdown.setVisible(false);
        edgeIDtextbox.setVisible(true);
        startNodeDropdown.setVisible(true);
        endNodeDropdown.setVisible(true);
        label = 1;
    }

    public void pressEditButton() {
        edgeDropdown.setVisible(true);
        edgeIDtextbox.setVisible(false);
        startNodeDropdown.setVisible(true);
        endNodeDropdown.setVisible(true);
        label = 2;
    }


    public void viewDatabase(){
        try{
            System.out.println(db.getEdges());
        }catch (SQLException sq){
            sq.printStackTrace();
        }
    }

    public void delete(){
        String edgeID = edgeDropdown.getSelectionModel().getSelectedItem().toString();
        try{
            if (db.edgeExists(edgeID)){
                db.deleteEdge(edgeID);
                submissionlabel.setText("You have deleted "+ edgeID);
            }else{
                submissionlabel.setText("Edge does not exist");
            }
        }catch (SQLException sq){
            sq.printStackTrace();
        }
        return;
    }

    public void add(){
        String edgeID = edgeIDtextbox.getText();
        String startNode = startNodeDropdown.getSelectionModel().getSelectedItem().toString();
        String endNode = endNodeDropdown.getSelectionModel().getSelectedItem().toString();
        try{
            if (!db.edgeExists(edgeID)){
                Map<String, String> edge = new HashMap<String, String>();
                edge.put("EDGEID", edgeID);
                edge.put("STARTNODE", startNode);
                edge.put("ENDNODE", endNode);

                db.addEdge(edge);
                submissionlabel.setText("You have added "+ edgeID);
            }else{
                submissionlabel.setText("Edge already exist");
            }
        }catch (SQLException sq){
            sq.printStackTrace();
        }
        return;
    }

    public void edit(){
        String edgeID = edgeIDtextbox.getText();
        String startNode = startNodeDropdown.getSelectionModel().getSelectedItem().toString();
        String endNode = endNodeDropdown.getSelectionModel().getSelectedItem().toString();
        try{
            if (db.edgeExists(edgeID)){
                Map<String, String> edge = new HashMap<String, String>();
               // edge.put("EDGEID", edgeID);
                edge.put("STARTNODE", startNode);
                edge.put("ENDNODE", endNode);

                db.editEdge(edgeID, edge);
                submissionlabel.setText("You have edit "+ edgeID);
            }else{
                submissionlabel.setText("Edge does not exist");
            }
        }catch (SQLException sq){
            sq.printStackTrace();
        }
        return;

    }

    public void submitfunction() {
        switch(label){
            case 0:
                delete();
                break;
            case 1:
                add();
                edgeIDtextbox.clear();
                break;
            case 2:
                edit();
                edgeIDtextbox.clear();
                break;
            case 3:
                submissionlabel.setText("Invalid submission");
        }
        initialize();
    }
    private void getText() {}

}