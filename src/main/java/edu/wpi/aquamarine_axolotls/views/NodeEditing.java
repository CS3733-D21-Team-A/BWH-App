package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.db.CSVHandler;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseInfo;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NodeEditing extends SEditing {
    @FXML public JFXButton deleteButton;
    @FXML private JFXButton addButton;
    @FXML private JFXButton editButton;

    @FXML private JFXButton edgesButton;
    @FXML private JFXComboBox nodeDropdown;
    @FXML private JFXTextField nodeID;
    @FXML private JFXTextField longName;
    @FXML private JFXTextField shortName;
    @FXML private JFXTextField xCoor;
    @FXML private JFXTextField yCoor;
    @FXML private JFXTextField nodeType;
    @FXML private JFXTextField floor;
    @FXML private JFXTextField building;

    @FXML private RadioMenuItem exportButton;
    @FXML private RadioMenuItem importButton;
    @FXML private Label submissionlabel;
    @FXML private JFXButton submissionButton;

    @FXML ImageView groundFloor;
    @FXML ImageView floor1;

    @FXML private AnchorPane anchor;
    @FXML private AnchorPane nodeGridAnchor;

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

        table.setEditable(false);
        table.getItems().clear();

        ObservableList<String> options = FXCollections.observableArrayList();
        submissionlabel.setVisible(true);
        anchor.setVisible(false);
        groundFloor.setVisible(true);
        floor1.setVisible(false);

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
        longName.setVisible(false);
        shortName.setVisible(false);
        xCoor.setVisible(false);
        yCoor.setVisible(false);
        nodeType.setVisible(false);
        floor.setVisible(false);
        building.setVisible(false);

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
            drawNodes();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void changeGroundFloor(){
        groundFloor.setVisible(true);
        floor1.setVisible(false);
    }

    public void changeFloor1(){
        groundFloor.setVisible(false);
        floor1.setVisible(true);
        System.out.println("got floor 1");
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
        longName.setVisible(false);
        shortName.setVisible(false);
        xCoor.setVisible(false);
        yCoor.setVisible(false);
        nodeType.setVisible(false);
        floor.setVisible(false);
        building.setVisible(false);

        state = "delete";
    }

    @FXML
    public void pressAddButton(){
        nodeDropdown.setVisible(false);
        nodeID.setVisible(true);
        longName.setVisible(true);
        shortName.setVisible(true);
        xCoor.setVisible(true);
        yCoor.setVisible(true);
        nodeType.setVisible(true);
        floor.setVisible(true);
        building.setVisible(true);

        state = "add";
    }

    @FXML
    public void pressEditButton(){
        nodeDropdown.setVisible(true);
        nodeID.setVisible(false);
        longName.setVisible(true);
        shortName.setVisible(true);
        xCoor.setVisible(true);
        yCoor.setVisible(true);
        nodeType.setVisible(true);
        floor.setVisible(true);
        building.setVisible(true);

        state = "edit";
    }

    public void exportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showSaveDialog(addButton.getScene().getWindow());
        try{
            csvHandler.exportCSV(csv, DatabaseInfo.TABLES.NODES);
        }catch(IOException ie){
            ie.printStackTrace();
        }catch(SQLException sq){
            sq.printStackTrace();
        }
    }

    public void loadCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showOpenDialog(addButton.getScene().getWindow());
        try{
            db.emptyNodeTable();
            csvHandler.importCSV(csv, DatabaseInfo.TABLES.NODES);
        }catch(IOException ie){
            ie.printStackTrace();
        }catch(SQLException sq){
            sq.printStackTrace();
        }

        initialize(); //REFRESH TABLE
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

    public void add(String current_nodeID){
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
                    fl = db.getNode(current_nodeID).get("FLOOR");
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

                if (!(x<5000 && x>0 && y<3400 && y>0)) {
                    submissionlabel.setText(  "Invalid submission: cannot edit node.");
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
    public void pressEdgeButton(ActionEvent actionEvent) {
        sceneSwitch("EdgeEditing");
    }

    public void drawNodes() {
        nodeGridAnchor.getChildren().clear();
        int count = 0;
        try {
            List<Map<String, String>> edges = db.getEdges();
            List<String> nodesList = new ArrayList<>();
            for (Map<String, String> edge : edges) {
                Circle circ1 = new Circle();
                Circle circ2 = new Circle();

                String startNode = edge.get("STARTNODE");
                String endNode = edge.get("ENDNODE");
                String bothNodes = startNode.concat(endNode);
                if (!nodesList.contains(bothNodes) || (!nodesList.contains(endNode.concat(startNode)))) { //??
                    try {
                        Map<String, String> snode = db.getNode(startNode);
                        Map<String, String> enode = db.getNode(endNode);
                        Double startX = xScale((int) Double.parseDouble(snode.get("XCOORD")));
                        Double startY = yScale((int) Double.parseDouble(snode.get("YCOORD")));
                        Double endX = xScale((int) Double.parseDouble(enode.get("XCOORD")));
                        Double endY = yScale((int) Double.parseDouble(enode.get("YCOORD")));

                        circ1.setCenterX(startX);
                        circ1.setCenterY(startY);
                        circ2.setCenterX(endX);
                        circ2.setCenterY(endY);
                        circ1.setRadius(2);
                        circ2.setRadius(2);
                        circ1.setFill(Color.RED);
                        circ2.setFill(Color.RED);

                        Line line = new Line();
                        line.setStartX(startX);
                        line.setStartY(startY);
                        line.setEndX(endX);
                        line.setEndY(endY);
                        line.setStroke(Color.WHITE);
                        nodeGridAnchor.getChildren().addAll(circ1, circ2, line);
                        nodesList.add(startNode + endNode);
                        count++;
                    } catch (SQLException sq) {
                        sq.printStackTrace();
                    }
                }
            }
        }catch (SQLException sq) {
            sq.printStackTrace();
        } System.out.println(count);
    }
}
