
package edu.wpi.aquamarine_axolotls.views.mapping;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.derby.iapi.db.Database;
import org.apache.derby.iapi.db.DatabaseContext;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class NodePopUp extends GenericPage {

    @FXML
    public JFXTextField nodeIDTextField;
    @FXML
    public JFXComboBox nodeTypeDropdown;
    @FXML
    public JFXTextField xCoordField;
    @FXML
    public JFXTextField yCoordField;
    @FXML
    public JFXComboBox buildingDropdown;
    @FXML
    public JFXComboBox floorDropdown;
    @FXML
    public JFXTextField shortNameField;
    @FXML
    public JFXButton deleteButton;
    @FXML
    public JFXButton cancelButton;
    @FXML
    public JFXButton confirmButton;
    @FXML
    public JFXTextField longNameField;
    @FXML
    public Label submissionStatusLabel;
    @FXML
    public Label titleLabel;
    MapEditing mapController;
    DatabaseController db;

    public NodePopUp(MapEditing mapController){
        this.mapController = mapController;
    }

    public void initialize() throws SQLException, IOException {
          db = mapController.db;

        floorDropdown.setItems(FXCollections
                .observableArrayList("L2","L1","1","2","3"));
        nodeTypeDropdown.setItems(FXCollections
                .observableArrayList("PARK","WALK","HALL",
                        "ELEV","REST", "STAI", "DEPT",
                        "LABS", "INFO", "CONF", "EXIT",
                        "RETL", "SERV"));
        buildingDropdown.setItems(FXCollections
                .observableArrayList("Parking", "BTM", "45 Francis", "Tower", "15 Francis", "Shapiro"));
        try{
            switch(mapController.state) {
                case "New" :
                    titleLabel.setText("Add Node");
                    xCoordField.setText(String.valueOf((mapController.inverseXScale((int) mapController.contextMenuX)).intValue()));
                    yCoordField.setText(String.valueOf((mapController.inverseYScale((int) mapController.contextMenuY)).intValue()));
                    deleteButton.setVisible(false);
                    break;
                case "Edit":
                    titleLabel.setText("Edit Node");
                    Map<String, String> node = db.getNode(mapController.currentID);
                    nodeIDTextField.setText(mapController.currentID);
                    longNameField.setText(node.get("LONGNAME"));
                    shortNameField.setText(node.get("SHORTNAME"));
                    xCoordField.setText(node.get("XCOORD"));
                    yCoordField.setText(node.get("YCOORD"));
                    nodeTypeDropdown.getSelectionModel().select(node.get("NODETYPE"));
                    floorDropdown.getSelectionModel().select(node.get("FLOOR"));
                    buildingDropdown.getSelectionModel().select(node.get("BUILDING"));
                    nodeIDTextField.setEditable(false);
                    deleteButton.setVisible(true);
                    break;
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

    }

    @FXML
    public void submit() throws SQLException {

        Map<String, String> node = new HashMap<>();

        if (longNameField.getText().isEmpty() ||
        shortNameField.getText().isEmpty() ||
        xCoordField.getText().isEmpty() ||
        yCoordField.getText().isEmpty() ||
        nodeTypeDropdown.getSelectionModel().getSelectedItem() == null ||
        floorDropdown.getSelectionModel().getSelectedItem() == null ||
        buildingDropdown.getSelectionModel().getSelectedItem() == null ||
        nodeIDTextField.getText().isEmpty()){
            submissionStatusLabel.setText("Invalid submission");
            return;
        }

        String longNameText = longNameField.getText();
        String shortNameText = shortNameField.getText();
        String xCoorText = xCoordField.getText();
        String yCoorText = yCoordField.getText();
        String nodeTypeText = nodeTypeDropdown.getSelectionModel().getSelectedItem().toString();
        String floorText = floorDropdown.getSelectionModel().getSelectedItem().toString();
        String buildingText = buildingDropdown.getSelectionModel().getSelectedItem().toString();
        String nodeIDText = nodeIDTextField.getText();

        node.put("LONGNAME", longNameText);
        node.put("SHORTNAME", shortNameText);
        node.put("XCOORD", xCoorText);
        node.put("YCOORD", yCoorText);
        node.put("NODETYPE", nodeTypeText);
        node.put("FLOOR", floorText);
        node.put("BUILDING", buildingText);

        switch(mapController.state){
            case "New":
                node.put("NODEID", nodeIDText);
                if(!db.nodeExists(nodeIDText)) db.addNode(node);
                else popUp("ERROR", "That Node ID already exists. Please enter a new Node ID.");
                break;
            case "Edit":
                db.editNode(mapController.currentID, node);
                break;
        }
        mapController.drawFloor(mapController.FLOOR);
        submissionStatusLabel.getScene().getWindow().hide();
    }


    @FXML
    public void clear(){
        longNameField.clear();
        shortNameField.clear();
        xCoordField.clear();
        yCoordField.clear();
        nodeTypeDropdown.getSelectionModel().clearSelection();
        floorDropdown.getSelectionModel().clearSelection();
        buildingDropdown.getSelectionModel().clearSelection();
    }

    public void delete() {
        String nodeID = nodeIDTextField.getText();
        try {
            mapController.selectedNodesList.clear();
            mapController.removeNodeOnImage(nodeID);
            db.deleteNode(nodeID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mapController.drawFloor(mapController.FLOOR);
        submissionStatusLabel.getScene().getWindow().hide();
    }

    @FXML
    public void cancel() {
        clear();
        submissionStatusLabel.getScene().getWindow().hide();
        mapController.selectedNodesList.remove(nodeIDTextField.getText());
        mapController.changeNodeColorOnImage(nodeIDTextField.getText(), GenericMap.darkBlue);
    }


    public void loadHelp() {

    }
}

