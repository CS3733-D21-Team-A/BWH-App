package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.db.CSVHandler;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
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
    @FXML public JFXButton deleteButton;
    @FXML private JFXButton addButton;
    @FXML private JFXButton editButton;
    @FXML private ComboBox nodeDropdown;
    @FXML private Label label;
    @FXML private Label submissionlabel;
    @FXML private JFXTextField longName;
    @FXML private JFXTextField shortName;
    @FXML private JFXTextField xCoor;
    @FXML private JFXTextField yCoor;
    @FXML private JFXTextField nodeType;
    @FXML private JFXTextField nodeID;
    @FXML private JFXTextField floor;
    @FXML private JFXTextField building;
    @FXML private JFXButton export_button;
    @FXML private JFXButton import_button;
    @FXML private JFXButton findPathButton2;

    ObservableList<String> options = FXCollections.observableArrayList();
    DatabaseController db;
    CSVHandler csvHandler;

    @FXML
    public void initialize() {
        try {
            db = new DatabaseController();
            List<Map<String, String>> nodes = db.getNodes();
            for (Map<String, String> node : nodes) {
                options.add(node.get("NODEID"));
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

    options = FXCollections.observableArrayList();
    try {
        List<Map<String, String>> nodes = db.getNodes();
        for (Map<String, String> node : nodes) {
            options.add(node.get("NODEID"));
        }
        nodeDropdown.setItems(options);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    @FXML
    public void pressAddButton(){
        clearfields();
        nodeDropdown.setVisible(false);
        longName.setVisible(true);
        shortName.setVisible(true);
        xCoor.setVisible(true);
        yCoor.setVisible(true);
        floor.setVisible(true);
        building.setVisible(true);
        nodeID.setVisible(true);
        nodeType.setVisible(true);

        label.setText("Add");
    }



    @FXML
    public void PressdeleteButton(){
        clearfields();
        nodeDropdown.setVisible(true);
        longName.setVisible(false);
        shortName.setVisible(false);
        xCoor.setVisible(false);
        yCoor.setVisible(false);
        floor.setVisible(false);
        building.setVisible(false);
        nodeID.setVisible(false);
        nodeType.setVisible(false);
        label.setText("Delete");
    }


    @FXML
    public void pressEditButton(){
        clearfields();
        nodeDropdown.setVisible(true);
        longName.setVisible(true);
        shortName.setVisible(true);
        xCoor.setVisible(true);
        yCoor.setVisible(true);
        floor.setVisible(true);
        building.setVisible(true);
        nodeID.setVisible(false);
        nodeType.setVisible(true);
        label.setText("Edit");

    }



    public void export_CSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showSaveDialog(export_button.getScene().getWindow());
        try{
            csvHandler.exportCSV(csv, DatabaseInfo.TABLES.EDGES);
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
            csvHandler.importCSV(csv, DatabaseInfo.TABLES.EDGES);
        }catch(IOException ie){
            ie.printStackTrace();
        }catch(SQLException sq){
            sq.printStackTrace();
        }
    }

    @FXML
    public void submitfunction() throws SQLException {
        String current_nodeID = nodeDropdown.getSelectionModel().getSelectedItem().toString();

        if(label.getText() == "Edit") {
            int x=Integer.parseInt(xCoor.getText());
            int y=Integer.parseInt(yCoor.getText());
            if (db.nodeExists(current_nodeID)&& (x<5000 &&  x>0)&&(y<3400 && y>0)) {
                Map<String, String> newNode = new HashMap<String, String>();
                newNode.put("XCOORD", xCoor.getText());
                newNode.put("YCOORD", yCoor.getText());
                newNode.put("FLOOR", floor.getText());
                newNode.put("BUILDING", building.getText());
                newNode.put("NODETYPE", nodeType.getText());
                newNode.put("LONGNAME", longName.getText());
                newNode.put("SHORTNAME", shortName.getText());
                db.editNode(current_nodeID, newNode);
                Map<String, String> edited = new HashMap<String, String>();
                submissionlabel.setText("You have edited " + current_nodeID);
            }
            else{
                submissionlabel.setText(  "Invalid submission: cannot edit node");
            }




        }
        else if(label.getText() == "Delete"){
            if (db.nodeExists(current_nodeID) ){
                db.deleteNode(current_nodeID);
                submissionlabel.setText("You have deleted " + current_nodeID);
            }
            else{
                submissionlabel.setText("deletion error, node does not exist");
            }
        }


        //this is breaking idk why!!!!!!!!!!!!!!!
        //check if nodeID exists , XCoor 0-5000, Y 0-3400,
        else if(label.getText() == "Add") {
            int x=Integer.parseInt(xCoor.getText());
            int y=Integer.parseInt(yCoor.getText());

            if (db.nodeExists(current_nodeID) && (x<5000 &&  x>0)&&(y<3400 && y>0)){

            Map<String, String> newNode = new HashMap<String, String>();
            newNode.put("NODEID", nodeID.getText());

            newNode.put("XCOORD", xCoor.getText());
            newNode.put("YCOORD", yCoor.getText());
            newNode.put("FLOOR", floor.getText());
            newNode.put("BUILDING", building.getText());
            newNode.put("NODETYPE", nodeType.getText());
            newNode.put("LONGNAME", longName.getText());
            newNode.put("SHORTNAME", shortName.getText());

            try{
                db.addNode(newNode);
                submissionlabel.setText("You have added " + nodeID.getText());

            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
            else{
                submissionlabel.setText(  "Invalid submission: cannot add node.");

            }
        }
        else{
            submissionlabel.setText(  "Invalid submission: add, edit, delete not selected");
        }
        label.setText("Another Node?");
        clearfields();

        return;
    }


}
