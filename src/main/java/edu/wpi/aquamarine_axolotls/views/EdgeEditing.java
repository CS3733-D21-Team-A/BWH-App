package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.CSVHandler;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseInfo;
import edu.wpi.aquamarine_axolotls.pathplanning.Edge;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EdgeEditing extends SEditing{
    @FXML public JFXButton deleteButton;
    @FXML private JFXButton addButton;
    @FXML private JFXButton editButton;
    @FXML public MenuItem importButton;
    @FXML public MenuItem exportButton;
    @FXML private JFXComboBox edgeDropdown;
    @FXML private JFXTextField edgeIDtextbox;
    @FXML private JFXComboBox startNodeDropdown;
    @FXML private JFXComboBox endNodeDropdown;
    @FXML private Label submissionlabel;
    @FXML private JFXButton submissionButton;
    @FXML private TableView table;
    @FXML private TableColumn<Edge,String> edgeIdCol;
    @FXML private TableColumn<Edge,String> startNodeCol;
    @FXML private TableColumn<Edge,String> endNodeCol;

    String state = "";

    DatabaseController db;
    CSVHandler csvHandler;

    @FXML
    public void initialize() {
        //table.setEditable(false);
        //table.getItems().clear();

        ObservableList<String> edgeOptions = FXCollections.observableArrayList();               // making dropdown options
        ObservableList<String> nodeOptions = FXCollections.observableArrayList();

        //edgeIdCol.setCellValueFactory(new PropertyValueFactory<Edge,String>("edgeID"));         // setting data to table
        //startNodeCol.setCellValueFactory(new PropertyValueFactory<Edge,String>("startNode"));
        //endNodeCol.setCellValueFactory(new PropertyValueFactory<Edge,String>("endNode"));

        edgeDropdown.setVisible(false);         //making fields invisible
        edgeIDtextbox.setVisible(false);
        startNodeDropdown.setVisible(false);
        endNodeDropdown.setVisible(false);

        deleteButton.setStyle("-fx-background-color: #003da6; ");       //setting buttons to default color
        addButton.setStyle("-fx-background-color: #003da6; ");
        editButton.setStyle("-fx-background-color: #003da6; ");

        try {
            db = new DatabaseController();
            csvHandler = new CSVHandler(db);
            List<Map<String, String>> edges = db.getEdges();
            List<Map<String, String>> nodes = db.getNodes();

            for (Map<String, String> edge : edges) {
                edgeOptions.add(edge.get("EDGEID"));    //getting edge options
             //   table.getItems().add(new Edge(edge.get("EDGEID"), edge.get("STARTNODE"), edge.get("ENDNODE")));
            }
            for (Map<String, String> node : nodes) {
                nodeOptions.add(node.get("NODEID"));
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

        deleteButton.setStyle("-fx-background-color: #91b7fa; ");
        addButton.setStyle("-fx-background-color: #003da6; ");
        editButton.setStyle("-fx-background-color: #003da6; ");

        state = "delete";
    }

    public void pressAddButton() {
        edgeDropdown.setVisible(false);
        edgeIDtextbox.setVisible(true);
        startNodeDropdown.setVisible(true);
        endNodeDropdown.setVisible(true);

        deleteButton.setStyle("-fx-background-color: #003da6; ");
        addButton.setStyle("-fx-background-color: #91b7fa; ");
        editButton.setStyle("-fx-background-color: #003da6; ");

        state = "add";
    }

    public void pressEditButton() {
        edgeDropdown.setVisible(true);
        edgeIDtextbox.setVisible(false);
        startNodeDropdown.setVisible(true);
        endNodeDropdown.setVisible(true);

        deleteButton.setStyle("-fx-background-color: #003da6; ");
        addButton.setStyle("-fx-background-color: #003da6; ");
        editButton.setStyle("-fx-background-color: #91b7fa; ");

        state = "edit";
    }

    public void loadCSV() { //still in the works
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showOpenDialog(addButton.getScene().getWindow());
        try{
            db.emptyEdgeTable();
            csvHandler.importCSV(csv, DatabaseInfo.TABLES.EDGES);
        }catch(IOException ie){
            ie.printStackTrace();
        }catch(SQLException sq){
            sq.printStackTrace();
        }

        initialize(); //REFRESH TABLE
    }

    public void exportCSV() { //still in the works
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showSaveDialog(addButton.getScene().getWindow());
        try{
            csvHandler.exportCSV(csv, DatabaseInfo.TABLES.EDGES);
        }catch(IOException ie){
            ie.printStackTrace();
        }catch(SQLException sq){
            sq.printStackTrace();
        }
    }

    public void delete(){
        String edgeID = edgeDropdown.getSelectionModel().getSelectedItem().toString();
        System.out.println(edgeID);
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

        if (edgeID.equals("") || startNode.equals("") || endNode.equals("")){
            submissionlabel.setText("Did not fill out all required fields");
            return;
        }else {
            try {
                if (!db.edgeExists(edgeID)) {
                    Map<String, String> edge = new HashMap<String, String>();
                    edge.put("EDGEID", edgeID);
                    edge.put("STARTNODE", startNode);
                    edge.put("ENDNODE", endNode);

                    db.addEdge(edge);
                    submissionlabel.setText("You have added " + edgeID);
                } else {
                    submissionlabel.setText("Edge already exist");
                }
            } catch (SQLException sq) {
                sq.printStackTrace();
            }
            return;
        }
    }

    public void edit(){
        String edgeID = edgeDropdown.getSelectionModel().getSelectedItem().toString();
        String startNode = startNodeDropdown.getSelectionModel().getSelectedItem().toString();
        String endNode = endNodeDropdown.getSelectionModel().getSelectedItem().toString();

        try{
            if (startNode.equals("")){
                startNode = db.getNode(edgeID).get("STARTNODE");
            }
            if (endNode.equals("")){
                endNode = db.getNode(edgeID).get("ENDNODE");
            }

            if (db.edgeExists(edgeID)){
                Map<String, String> edge = new HashMap<String, String>();
                edge.put("EDGEID", edgeID);
                edge.put("STARTNODE", startNode);
                edge.put("ENDNODE", endNode);

                db.editEdge(edgeID, edge);
                submissionlabel.setText("You have edited "+ edgeID);
            }else{
                submissionlabel.setText("Edge does not exist");
            }
        }catch (SQLException sq){
            sq.printStackTrace();
        }
        return;

    }

    public void submitfunction() {
        switch(state){
            case "delete":
                delete();
                break;
            case "add":
                add();
                break;
            case "edit":
                edit();
                break;
            case "":
                edgeIDtextbox.clear();
                submissionlabel.setText("Invalid submission");
        }
        initialize();
    }

    public void pressNodeButton() {
    }
}