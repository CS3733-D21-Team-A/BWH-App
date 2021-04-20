package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.db.CSVHandler;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseInfo;
import edu.wpi.aquamarine_axolotls.pathplanning.Edge;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

public class EdgeEditing extends SEditing{
    @FXML public JFXButton deleteButton;
    @FXML private JFXButton addButton;
    @FXML private JFXButton editButton;

    @FXML private HBox edgeD;
    @FXML private HBox edgeT;
    @FXML private JFXComboBox edgeDropdown;
    @FXML private JFXTextField edgeIDtextbox;
    @FXML private JFXComboBox startNodeDropdown;
    @FXML private JFXComboBox endNodeDropdown;

    @FXML public RadioMenuItem importButton;
    @FXML public RadioMenuItem exportButton;
    @FXML private Label submissionlabel;
    @FXML private JFXButton submissionButton;

    @FXML private AnchorPane anchor;
    @FXML private AnchorPane nodeGridAnchor;

    @FXML private TableView table;
    @FXML private TableColumn<Edge,String> edgeIdCol;
    @FXML private TableColumn<Edge,String> startNodeCol;
    @FXML private TableColumn<Edge,String> endNodeCol;


    @FXML
    private JFXHamburger burger;

    @FXML
    private JFXDrawer menuDrawer;

    @FXML
    private VBox box;


    @FXML ImageView groundFloor;
    @FXML ImageView floor1;

    String state = "";

    DatabaseController db;
    CSVHandler csvHandler;

    @FXML
    public void initialize() {
        menuDrawer.setSidePane(box);
        transition = new HamburgerBasicCloseTransition(burger);
        transition.setRate(-1);
        menuDrawer.close();

        table.setEditable(false);
        table.getItems().clear();

        ObservableList<String> edgeOptions = FXCollections.observableArrayList();               // making dropdown options
        ObservableList<String> nodeOptions = FXCollections.observableArrayList();
        submissionlabel.setVisible(true);
        anchor.setVisible(false);
        //groundFloor.setVisible(true);
        floor1.setVisible(false);

        edgeIdCol.setCellValueFactory(new PropertyValueFactory<Edge,String>("edgeID"));         // setting data to table
        startNodeCol.setCellValueFactory(new PropertyValueFactory<Edge,String>("startNode"));
        endNodeCol.setCellValueFactory(new PropertyValueFactory<Edge,String>("endNode"));

        edgeDropdown.setVisible(false);         //making fields invisible
        edgeIDtextbox.setVisible(false);
        startNodeDropdown.setVisible(false);
        endNodeDropdown.setVisible(false);

        try {
            db = new DatabaseController();
            csvHandler = new CSVHandler(db);
            List<Map<String, String>> edges = db.getEdges();
            List<Map<String, String>> nodes = db.getNodes();
            for (Map<String, String> edge : edges) {
                edgeOptions.add(edge.get("EDGEID"));    //getting edge options
                table.getItems().add(new Edge(edge.get("EDGEID"), edge.get("STARTNODE"), edge.get("ENDNODE")));
            }
            for (Map<String, String> node : nodes) {
                nodeOptions.add(node.get("NODEID"));
            }
            edgeDropdown.setItems(edgeOptions);
            startNodeDropdown.setItems(nodeOptions);
            endNodeDropdown.setItems(nodeOptions);
            changeFloorNodes();

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
        changeFloorNodes();
    }

    public void changeFloor1(){
        groundFloor.setVisible(false);
        floor1.setVisible(true);
        changeFloorNodes();
    }

    public void pressDeleteButton() {
        edgeD.toFront();
        edgeDropdown.setVisible(true);
        edgeIDtextbox.setVisible(false);
        startNodeDropdown.setVisible(false);
        endNodeDropdown.setVisible(false);

        state = "delete";
    }

    public void pressAddButton() {
        edgeT.toFront();
        edgeDropdown.setVisible(false);
        edgeIDtextbox.setVisible(true);
        startNodeDropdown.setVisible(true);
        endNodeDropdown.setVisible(true);

        state = "add";
    }

    public void pressEditButton() {
        edgeD.toFront();
        edgeDropdown.setVisible(true);
        edgeIDtextbox.setVisible(false);
        startNodeDropdown.setVisible(true);
        endNodeDropdown.setVisible(true);

        state = "edit";
    }

    public void loadCSV() { //still in the works
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showOpenDialog(addButton.getScene().getWindow());
        try{
            csvHandler.importCSV(csv, DatabaseInfo.TABLES.EDGES, true);
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
        sceneSwitch("NodeEditing");
    }

    public void changeFloorNodes(){
        nodeGridAnchor.getChildren().clear();
        int count = 0;
        try {
            List<Map<String, String>> edges = db.getEdges();
            List<String> nodesList = new ArrayList<>();
            for (Map<String, String> edge : edges) {
                String startNode = edge.get("STARTNODE");
                String endNode = edge.get("ENDNODE");
                String bothNodes = startNode.concat(endNode);
                if (!nodesList.contains(bothNodes) || (!nodesList.contains(endNode.concat(startNode)))) { //??
                    try {
                        Map<String, String> snode = db.getNode(startNode);
                        Map<String, String> enode = db.getNode(endNode);

                        if (floor1.isVisible() && (
                                ( snode.get("FLOOR").equals("1")) && enode.get("FLOOR").equals("1")) ){
                            drawNodes(snode,enode);
                            nodesList.add(startNode + endNode);
                            count++;
                        }else if (groundFloor.isVisible() && (
                                ( snode.get("FLOOR").equals("G")) && enode.get("FLOOR").equals("G")) ){
                            drawNodes(snode,enode);
                            nodesList.add(startNode + endNode);
                            count++;

                        }

                    } catch (SQLException sq) {
                        sq.printStackTrace();
                    }
                }
            }
        }catch (SQLException sq) {
            sq.printStackTrace();
        } System.out.println(count);
    }

    public void drawNodes(Map<String, String> snode, Map<String,String> enode) {
        Circle circ1 = new Circle();
        Circle circ2 = new Circle();

        Double startX = xScale((int) Double.parseDouble(snode.get("XCOORD")));
        Double startY = yScale((int) Double.parseDouble(snode.get("YCOORD")));
        Double endX = xScale((int) Double.parseDouble(enode.get("XCOORD")));
        Double endY = yScale((int) Double.parseDouble(enode.get("YCOORD")));

        circ1.setCenterX(startX);
        circ1.setCenterY(startY);
        circ2.setCenterX(endX);
        circ2.setCenterY(endY);
        circ1.setRadius(1);
        circ2.setRadius(1);
        circ1.setFill(Color.RED);
        circ2.setFill(Color.RED);

        Line line = new Line();
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);
        line.setStroke(Color.RED);
        nodeGridAnchor.getChildren().addAll(circ1, circ2, line);

    }

    public void menu(){
        if(transition.getRate() == -1) menuDrawer.open();
        else menuDrawer.close();
        transition.setRate(transition.getRate() * -1);
        transition.play();
    }
}