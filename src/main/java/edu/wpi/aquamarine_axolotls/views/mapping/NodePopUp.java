
package edu.wpi.aquamarine_axolotls.views.mapping;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.derby.iapi.db.Database;
import org.apache.derby.iapi.db.DatabaseContext;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class NodePopUp extends MapEditing{

    @FXML
    private JFXTextField nodeID;
    @FXML
    private JFXTextField longName;
    @FXML
    private JFXTextField shortName;
    @FXML
    private JFXTextField xCoor;
    @FXML
    private JFXTextField yCoor;
    @FXML
    private JFXComboBox<String> nodeType;
    @FXML
    private JFXComboBox<String> floor;
    @FXML
    private JFXComboBox<String> building;
    @FXML
    private Label submissionStatusLabel;
    // Make Required fields


    @FXML
    public void initialize() throws SQLException{

        floor.setItems(FXCollections
                .observableArrayList("L2","L1","1","2","3"));
        nodeType.setItems(FXCollections
                .observableArrayList("PARK","WALK","HALL",
                        "ELEV","REST", "STAI", "DEPT",
                        "LABS", "INFO", "CONF", "EXIT",
                        "RETL", "SERV"));

        switch(state){
            case "New":
                xCoor.setText(String.valueOf(contextMenuX));
                yCoor.setText(String.valueOf(contextMenuY));
                break;
            case "Edit":
                Map<String, String> node = db.getNode(currentNodeID);
                nodeID.setText(currentNodeID);
                longName.setText(node.get("LONGNAME"));
                shortName.setText(node.get("SHORTNAME"));
                xCoor.setText(node.get("XCOORD"));
                yCoor.setText(node.get("YCOORD"));
                nodeType.getSelectionModel().select(node.get("NODETYPE"));
                floor.getSelectionModel().select(node.get("FLOOR"));
                building.getSelectionModel().select(node.get("BUILDING"));
                nodeID.setEditable(false);
                break;
        }
    }

    @FXML
    public void submit() throws SQLException {

        Map<String, String> node = new HashMap<>();

        String longNameText = longName.getText();
        String shortNameText = shortName.getText();
        String xCoorText = xCoor.getText();
        String yCoorText = yCoor.getText();
        String nodeTypeText = nodeType.getSelectionModel().getSelectedItem();
        String floorText = floor.getSelectionModel().getSelectedItem();
        String buildingText = building.getSelectionModel().getSelectedItem();

        if (longNameText.isEmpty() ||
        shortNameText.isEmpty() ||
        xCoorText.isEmpty() ||
        yCoorText.isEmpty() ||
        nodeTypeText.isEmpty() ||
        floorText.isEmpty() ||
        buildingText.isEmpty()) {
            submissionStatusLabel.setText("Invalid submission");
            return;
        }
        else {
            node.put("LONGNAME", longNameText);
            node.put("SHORTNAME", shortNameText);
            node.put("XCOORD", xCoorText);
            node.put("YCOORD", yCoorText);
            node.put("NODETYPE", nodeTypeText);
            node.put("FLOOR", floorText);
            node.put("BUILDING", buildingText);
        }
        switch(state){
            case "New":
                db.addNode(node);
                break;
            case "Edit":
                db.editNode(currentNodeID, node);
                break;
        }
    }


    @FXML
    public void clear(){
        longName.clear();
        shortName.clear();
        xCoor.clear();
        yCoor.clear();
        nodeType.getSelectionModel().clearSelection();
        floor.getSelectionModel().clearSelection();
        building.getSelectionModel().clearSelection();
    }

}

