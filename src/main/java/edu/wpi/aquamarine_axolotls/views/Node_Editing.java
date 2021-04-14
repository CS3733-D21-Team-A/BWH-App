package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.CSVHandler;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseInfo;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Node_Editing {
    @FXML private JFXButton homeButton;
    @FXML private JFXButton helpButton;
    @FXML public JFXButton deleteButton;
    @FXML private JFXButton addButton;
    @FXML private JFXButton editButton;
    @FXML private ComboBox nodeDropdown;
    @FXML private JFXTextField nodeID;
    @FXML private ScrollPane otherFields;
    @FXML private JFXTextField longName;
    @FXML private JFXTextField shortName;
    @FXML private JFXTextField xCoor;
    @FXML private JFXTextField yCoor;
    @FXML private JFXTextField nodeType;
    @FXML private JFXTextField floor;
    @FXML private JFXTextField building;
    @FXML private JFXButton export_button;
    @FXML private JFXButton import_button;
    @FXML private Label submissionlabel;
    @FXML private JFXButton submissionButton;
    @FXML private TableView table;
    @FXML private TableColumn nodeIDCol;
    @FXML private TableColumn lNameCol;
    @FXML private TableColumn sNameCol;
    @FXML private TableColumn xCol;
    @FXML private TableColumn yCol;
    @FXML private TableColumn floorCol;
    @FXML private TableColumn buildingCol;
    @FXML private TableColumn typeCol;

    String state = "";

    DatabaseController db;
    CSVHandler csvHandler;

    @FXML
    public void initialize() {
        ObservableList<String> options = FXCollections.observableArrayList();
        submissionlabel.setVisible(true);

        table.setEditable(false);
        table.getItems().clear();
        nodeIDCol.setCellValueFactory(new PropertyValueFactory<Node, String>("nodeID"));
        lNameCol.setCellValueFactory(new PropertyValueFactory<Node, String>("longName"));
        sNameCol.setCellValueFactory(new PropertyValueFactory<Node, String>("shortName"));
        xCol.setCellValueFactory(new PropertyValueFactory<Node, String>("xcoord"));
        yCol.setCellValueFactory(new PropertyValueFactory<Node, String>("ycoord"));
        floorCol.setCellValueFactory(new PropertyValueFactory<Node, String>("floor"));
        buildingCol.setCellValueFactory(new PropertyValueFactory<Node, String>("building"));
        typeCol.setCellValueFactory(new PropertyValueFactory<Node, String>("nodeType"));

        nodeDropdown.setVisible(false);
        nodeID.setVisible(false);
        otherFields.setVisible(false);

        deleteButton.setStyle("-fx-background-color: #003da6; ");       //setting buttons to default color
        addButton.setStyle("-fx-background-color: #003da6; ");
        editButton.setStyle("-fx-background-color: #003da6; ");

        try {
            db = new DatabaseController();
            csvHandler = new CSVHandler(db);
            List<Map<String, String>> nodes = db.getNodes();
            for (Map<String, String> node : nodes) {
                options.add(node.get("NODEID"));
                table.getItems().add(new Node(
                        node.get("NODEID"), Integer.parseInt(node.get("XCOORD")), Integer.parseInt(node.get("YCOORD")),
                        node.get("FLOOR"), node.get("BUILDING"), node.get("NODETYPE"), node.get("LONGNAME"), node.get("SHORTNAME")
                ));
            }
            nodeDropdown.setItems(options);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void clearfields(){
        longName.clear();
        shortName.clear();
        xCoor.clear();
        yCoor.clear();
        nodeID.clear();
        nodeType.clear();
        floor.clear();
        building.clear();
    }

    @FXML
    public void pressDeleteButton(){
        nodeDropdown.setVisible(true);
        nodeID.setVisible(false);
        otherFields.setVisible(false);

        deleteButton.setStyle("-fx-background-color: #91b7fa; ");
        addButton.setStyle("-fx-background-color: #003da6; ");
        editButton.setStyle("-fx-background-color: #003da6; ");

        state = "delete";
    }

    @FXML
    public void pressAddButton(){
        nodeDropdown.setVisible(false);
        nodeID.setVisible(true);
        otherFields.setVisible(true);

        deleteButton.setStyle("-fx-background-color: #003da6; ");
        addButton.setStyle("-fx-background-color: #91b7fa; ");
        editButton.setStyle("-fx-background-color: #003da6; ");

        state = "add";
    }

    @FXML
    public void pressEditButton(){
        nodeDropdown.setVisible(true);
        nodeID.setVisible(false);
        otherFields.setVisible(true);

        deleteButton.setStyle("-fx-background-color: #003da6; ");
        addButton.setStyle("-fx-background-color: #003da6; ");
        editButton.setStyle("-fx-background-color: #91b7fa; ");

        state = "edit";
    }

    public void export_CSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showSaveDialog(export_button.getScene().getWindow());
        try{
            csvHandler.exportCSV(csv, DatabaseInfo.TABLES.NODES);
        }catch(IOException ie){
            ie.printStackTrace();
        }catch(SQLException sq){
            sq.printStackTrace();
        }
    }

    public void import_CSV() { //still in the works
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showOpenDialog(import_button.getScene().getWindow());
        try{
            db.emptyEdgeTable();
            csvHandler.importCSV(csv, DatabaseInfo.TABLES.NODES);
        }catch(IOException ie){
            ie.printStackTrace();
        }catch(SQLException sq){
            sq.printStackTrace();
        }
    }

    public void delete(String current_nodeID){
        try{
            if (db.nodeExists(current_nodeID) ) {
                db.deleteNode(current_nodeID);
                submissionlabel.setText("You have deleted " + current_nodeID);
            }else{
                submissionlabel.setText("Edge does not exist");
            }
        }catch (SQLException sq){
              sq.printStackTrace();
        }
        return;
    }

    public void add(String current_nodeID){ //not working idk why
        int x=Integer.parseInt(xCoor.getText());
        int y=Integer.parseInt(yCoor.getText());
        if (!(x<5000 &&  x>0)&&(y<3400 && y>0)){
            submissionlabel.setText("X or Y coordinates are out of bounds");
            return;
        }
        try{
            if (!db.nodeExists(current_nodeID)) {
                Map<String, String> newNode = new HashMap<String, String>();
                newNode.put("NODEID", current_nodeID);
                newNode.put("XCOORD", xCoor.getText());
                newNode.put("YCOORD", yCoor.getText());
                newNode.put("FLOOR", floor.getText());
                newNode.put("BUILDING", building.getText());
                newNode.put("NODETYPE", nodeType.getText());
                newNode.put("LONGNAME", longName.getText());
                newNode.put("SHORTNAME", shortName.getText());
                db.addNode(newNode);
                submissionlabel.setText("You have added " + nodeID.getText());
            }else{
                submissionlabel.setText("Node already exists");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return;
    }

    public void edit(String current_nodeID){
        try {
            if (db.nodeExists(current_nodeID)) {
                String xcoord = xCoor.getText();
                String ycoord = yCoor.getText();
                String fl = floor.getText();
                String b = building.getText();
                String type = nodeType.getText();
                String longN = longName.getText();
                String shortN = shortName.getText();

                if (xcoord.equals("")){
                    xcoord = db.getNode(current_nodeID).get("XCOORD");
                }
                if (ycoord.equals("")){
                    ycoord = db.getNode(current_nodeID).get("YCOORD");
                }
                if (fl.equals("")){
                    fl = db.getNode(current_nodeID).get("FLORR");
                }
                if (b.equals("")){
                    b = db.getNode(current_nodeID).get("BUILDING");
                }
                if (type.equals("")){
                    type = db.getNode(current_nodeID).get("NODETYPE");
                }
                if (longN.equals("")){
                    longN = db.getNode(current_nodeID).get("LONGNAME");
                }
                if (shortN.equals("")){
                    shortN = db.getNode(current_nodeID).get("SHORTNAME");
                }

                int x = Integer.parseInt(xcoord);
                int y = Integer.parseInt(ycoord);

                if ((x<5000 &&  x>0)&&(y<3400 && y>0)) {
                    submissionlabel.setText(  "Invalid submission: cannot add node.");
                    return;
                }

                Map<String, String> newNode = new HashMap<String, String>();
                newNode.put("XCOORD", xcoord);
                newNode.put("YCOORD", ycoord);
                newNode.put("FLOOR", fl);
                newNode.put("BUILDING", b);
                newNode.put("NODETYPE", type);
                newNode.put("LONGNAME", longN);
                newNode.put("SHORTNAME", shortN);
                db.editNode(current_nodeID, newNode);
                submissionlabel.setText("You have edited " + current_nodeID);
            }
        }catch (SQLException sq){
            sq.printStackTrace();
        }
    }

    @FXML
    public void submitfunction() throws SQLException {
        switch (state){
            case "delete":
                delete(nodeDropdown.getSelectionModel().getSelectedItem().toString());
                break;
            case "add":
                add(nodeID.getText());
                break;
            case "edit":
                edit(nodeDropdown.getSelectionModel().getSelectedItem().toString());
                break;
            case "":
                submissionlabel.setText("Invalid submission");
        }
        clearfields();
        initialize();

        return;
    }
    @FXML
    public void return_home(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/Admin_Main_Page.fxml"));
            Aapp.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
