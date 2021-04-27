package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.db.*;
import edu.wpi.aquamarine_axolotls.pathplanning.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    @FXML private JFXComboBox algoSelectBox;
    @FXML private HBox nodeT;
    @FXML private HBox nodeD;
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
    @FXML private RadioMenuItem newCSVButton;
    @FXML private RadioMenuItem mergeCSVButton;
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

    @FXML private JFXButton clearButton;

    @FXML private ScrollPane mapScrollPane;
    @FXML private Canvas mapCanvas;



    private Map<String, String> floors;
    static String FLOOR = "G";
    private Group zoomGroup;
    private int zoom;
    List<Node> validNodes = new ArrayList<>();


    String state = "";
    DatabaseController db;
    CSVHandler csvHandler;

    @FXML
    public void initialize() {

        table.setEditable(false);
        table.getItems().clear();

        ObservableList<String> options = FXCollections.observableArrayList();
        ObservableList<String> searchAlgorithms = FXCollections.observableArrayList();

        submissionlabel.setVisible(true);

        searchAlgorithms.add("A-Star");
        searchAlgorithms.add("Dijkstra");
        searchAlgorithms.add("Breadth First");
        searchAlgorithms.add("Depth First");
        algoSelectBox.setItems(searchAlgorithms);


        if(SearchAlgorithmContext.getSearchAlgorithmContext().context == null){
            SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
        }

        String algo = SearchAlgorithmContext.getSearchAlgorithmContext().context.toString();

        if(algo.contains("AStar")) algoSelectBox.getSelectionModel().select(0);
        else if(algo.contains("Dijkstra")) algoSelectBox.getSelectionModel().select(1);
        else if(algo.contains("BreadthFirstSearch")) algoSelectBox.getSelectionModel().select(2);
        else if(algo.contains("DepthFirstSearch")) algoSelectBox.getSelectionModel().select(3);




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
        submissionButton.setVisible(false);
        clearButton.setVisible(false);

        try {
            db = new DatabaseController();
            csvHandler = new CSVHandler(db);
            List<Map<String, String>> nodes = db.getNodes();

            for (Map<String, String> node : nodes) {
                options.add(node.get("NODEID"));
                Node cur = new Node(node.get("NODEID"),
                        Integer.parseInt(node.get("XCOORD")),
                        Integer.parseInt(node.get("YCOORD")),
                        node.get("FLOOR"), node.get("BUILDING"),
                        node.get("NODETYPE"), node.get("LONGNAME"),
                        node.get("SHORTNAME")
                );
                table.getItems().add(cur);
                validNodes.add(cur);
                drawSingleNode(cur);
            }
            nodeDropdown.setItems(options);
            floors = new HashMap<>();
            floors.put("G", "edu/wpi/aquamarine_axolotls/img/groundFloor.png");
            floors.put("1", "edu/wpi/aquamarine_axolotls/img/firstFloor.png");

            mapScrollPane.pannableProperty().set(true);
            Group contentGroup = new Group();
            zoomGroup = new Group();
            contentGroup.getChildren().add(zoomGroup);
            zoomGroup.getChildren().add(mapCanvas);
            mapScrollPane.setContent(contentGroup);
            mapCanvas.getGraphicsContext2D().drawImage(new Image(floors.get(FLOOR)), 0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
            drawFloor(FLOOR);
            zoom = 1;

        } catch (SQLException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Double xScale(int xCoord) { return (mapCanvas.getWidth()/5000) * xCoord; }
    public Double yScale(int yCoord) { return (mapCanvas.getHeight()/3400) * yCoord; }

    public void zoomIn(ActionEvent actionEvent) {
        if(zoom < 3){
            zoomGroup.setScaleX(++zoom);
            zoomGroup.setScaleY(zoom);
        }
    }

    public void resetZoom(){
        zoom = 1;
        zoomGroup.setScaleX(1);
        zoomGroup.setScaleY(1);
    }

    public void zoomOut(ActionEvent actionEvent) {
        if(zoom > 1){
            zoomGroup.setScaleX(--zoom);
            zoomGroup.setScaleY(zoom);
        }
    }

    public void resetMap(String floor) {
        resetZoom();
        mapCanvas.getGraphicsContext2D().clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        mapCanvas.getGraphicsContext2D().drawImage(new Image(floors.get(floor)), 0,0, mapCanvas.getWidth(), mapCanvas.getHeight());
        FLOOR = floor;
    }

    public void resetMapAndDraw(String floor) {
        resetMap(floor);
        drawFloor(floor);
    }

    public void drawFloor(String floor){
        resetMap(floor);

        try {
            for( Map<String, String> node : db.getNodes()){ if(node.get("FLOOR").equals(floor)) drawSingleNode(node); }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void changeFloor1() {
        resetMapAndDraw("1");
    }

    public void changeGroundFloor() {
        resetMapAndDraw("G");
    }

    @FXML
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
        nodeD.toFront();
        nodeDropdown.setVisible(true);
        nodeID.setVisible(false);
        longName.setVisible(false);
        shortName.setVisible(false);
        xCoor.setVisible(false);
        yCoor.setVisible(false);
        nodeType.setVisible(false);
        floor.setVisible(false);
        building.setVisible(false);
        submissionButton.setVisible(true);
        clearButton.setVisible(false);

        state = "delete";
    }

    @FXML
    public void pressAddButton(){
        nodeT.toFront();
        nodeDropdown.setVisible(false);
        nodeID.setVisible(true);
        longName.setVisible(true);
        shortName.setVisible(true);
        xCoor.setVisible(true);
        yCoor.setVisible(true);
        nodeType.setVisible(true);
        floor.setVisible(true);
        building.setVisible(true);
        submissionButton.setVisible(true);
        clearButton.setVisible(true);

        state = "add";
    }

    @FXML
    public void pressEditButton(){
        nodeD.toFront();
        nodeDropdown.setVisible(true);
        nodeID.setVisible(false);
        longName.setVisible(true);
        shortName.setVisible(true);
        xCoor.setVisible(true);
        yCoor.setVisible(true);
        nodeType.setVisible(true);
        floor.setVisible(true);
        building.setVisible(true);
        submissionButton.setVisible(true);
        clearButton.setVisible(true);

        state = "edit";
    }


    public void newCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showOpenDialog(addButton.getScene().getWindow());
        try{
            csvHandler.importCSV(csv, TABLES.NODES, true);
        }catch(IOException | SQLException ie){
            ie.printStackTrace();
        }

        initialize(); //REFRESH TABLE
    }

    public void mergeCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showOpenDialog(addButton.getScene().getWindow());
        try{
            csvHandler.importCSV(csv, TABLES.NODES, false);
        }catch(IOException | SQLException ie){
            ie.printStackTrace();
        }

        initialize(); //REFRESH TABLE
    }

    public void exportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showSaveDialog(addButton.getScene().getWindow());
        try{
            csvHandler.exportCSV(csv, TABLES.NODES);
        }catch(IOException | SQLException ie){
            ie.printStackTrace();
        }
    }

    public void delete(String current_nodeID){
        try{
            if (db.nodeExists(current_nodeID) ) {
                db.deleteNode(current_nodeID);
                submissionlabel.setText("You have deleted " + current_nodeID);
            }else{
                submissionlabel.setText("Node does not exist");
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
                db.editNode(current_nodeID, newNode); //TODO: YOU DON'T NEED TO SET ALL OF THEM. YOU CAN JUST LEAVE THEM BLANK
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
                drawFloor(FLOOR);
                break;
            case "add":
                add(nodeID.getText());
                drawFloor(FLOOR);
                break;
            case "edit":
                edit(nodeDropdown.getSelectionModel().getSelectedItem().toString());
                drawFloor(FLOOR);
                break;
            case "":
                submissionlabel.setText("Invalid submission");
        }
        clearfields();
        initialize();

        return;
    }

    @FXML
    public void selectAlgorithm() {
        if(algoSelectBox.getSelectionModel() != null && algoSelectBox.getSelectionModel() != null){
            if(algoSelectBox.getSelectionModel().getSelectedItem().equals("A Star")){
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
            } else if (algoSelectBox.getSelectionModel().getSelectedItem().equals("Dijkstra")){
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new Dijkstra());
            } else if (algoSelectBox.getSelectionModel().getSelectedItem().equals("Breadth First")){
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new BreadthFirstSearch());
            } else if (algoSelectBox.getSelectionModel().getSelectedItem().equals("Depth First")){
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new DepthFirstSearch());
            }
        }
    }

    @FXML
    public void pressEdgeButton(ActionEvent actionEvent) {
        sceneSwitch("EdgeEditing");
    }

    public void drawSingleNode(Map<String, String> node) {
        double x = xScale(Integer.parseInt(node.get("XCOORD")));
        double y = yScale(Integer.parseInt(node.get("YCOORD")));
        double radius = 3;
        x = x - (radius / 2);
        y = y - (radius / 2);
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLUE);
        gc.fillOval(x, y, radius, radius);
    }

    public void drawSingleNode(Node node) {
        double x = xScale(node.getXcoord());
        double y = yScale(node.getYcoord());
        double radius = 3;
        x = x - (radius / 2);
        y = y - (radius / 2);
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLUE);
        gc.fillOval(x, y, radius, radius);
    }


    public void getCoordsFromMap(javafx.scene.input.MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            //System.out.println("Clicked map");

            //double x = xScale(event.getX());
            //double y = yScale(event.getY());
            double x = Math.floor(event.getX());
            double y = Math.floor(event.getY());

            if (xCoor.isVisible()) xCoor.setText(Double.toString(x));
            if (yCoor.isVisible()) yCoor.setText(Double.toString(y));
        }
    }
}