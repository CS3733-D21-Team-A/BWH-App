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


public class NodeEditing extends GenericMap {
    @FXML private JFXButton addButton;
    @FXML private JFXComboBox algoSelectBox;
    @FXML private HBox nodeT;
    @FXML private HBox nodeD;
    @FXML private JFXComboBox nodeDropdown;
    @FXML private JFXTextField nodeID;
    @FXML private JFXTextField longName;
    @FXML private JFXTextField shortName;
    @FXML private JFXTextField xCoor;
    @FXML private JFXTextField yCoor;
    @FXML private JFXTextField nodeType;
    @FXML private JFXTextField floor;
    @FXML private JFXTextField building;

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
    private Map<String, String> prevSelected;
    private Map<String, String> currSelected;

    private


    String state = "";

    CSVHandler csvHandler;

    /**
     * Initializes the node editing page by filling in dropdown boxes and importing data from the database
     */
    @FXML
    public void initialize() throws SQLException{

        startUp();

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

        List<Map<String, String>> nodes = db.getNodesByValue("FLOOR", FLOOR);

        for (Map<String, String> node : nodes) {
            options.add ( node.get ( "LONGNAME" ) );
            Node cur = new Node(node.get("NODEID"), Integer.parseInt(node.get("XCOORD")), Integer.parseInt(node.get("YCOORD")), node.get("LONGNAME"), node.get("SHORTNAME"),  node.get("FLOOR"), node.get("BUILDING"), node.get("NODETYPE"));
            table.getItems().add(cur);
        }
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

        nodeDropdown.setItems(options);


    }

    /**
     * Clears all JFXTextFields in the node editing menu
     */
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

    /**
     * Handles the input of the user pressing the Delete button in the node editing screen by making appropriate
     * text fields and dropdown boxes visible.
     */
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

    /**
     * Handles the event of the user pressing the Add button in the node editing screen by making appropriate
     * text fields and dropdown boxes visible.
     */
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

    /**
     * Handles the event of the user pressing the Add button in the node editing screen by making appropriate
     * text fields and dropdown boxes visible.
     */
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

    /**
     * Handles the user pressing the Import and Overwrite button while in the node editing page by grabbing
     * the node data from the CSV file and using that data to replace the existing node table
     */
    public void newCSV() throws SQLException{
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

    /**
     * Handles the user pressing the Export button while on the node editing page by extracting the node data from the
     * database and saving it as a CSV file.
     */
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

    /**
     * Deletes a given node from the database and map.
     * @param current_nodeID The ID of the node to be deleted
     */
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

    /**
     * Adds a node to the map and database
     * @param current_nodeID The ID of the node to be added
     */
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

    /**
     * Updates the values of row in the node table based on the current values of the text field and dropdown box
     * selections
     * @param current_nodeID The ID of the node to be edited
     */
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

    /**
     * Handles the user pressing the Submit button while in th enode editing page based on whether the user was
     * adding, editing, or deleting a node
     * @throws SQLException If there is an error getting node data from the database
     */
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

    /**
     * Sets the strategy to be used when pathfinding based on the current state of the algorithm selectio dropdown box
     */
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

    /**
     * Highlights the node selected by the user in the dropdown box
     */
    @FXML
    public void highLightNodeFromSelector() throws SQLException{
        if (nodeDropdown.getSelectionModel().getSelectedItem() != null) {
            for (Map<String, String> node: db.getNodesByValue("FLOOR", FLOOR)) { // TODO : maybe preformance issue?
                if (node.get("LONGNAME").equals(nodeDropdown.getSelectionModel().getSelectedItem())) {
                    prevSelected = currSelected;
                    currSelected = node;
                    if (prevSelected != null) drawSingleNode(prevSelected, Color.BLUE);
                    drawSingleNode(currSelected, Color.RED);
                }
            }
        }
    }

    /**
     * Switches the active page to EdgeEditing, allowing the user to edit edges instead of nodes
     * @param actionEvent The user's mouse click on the dropdown box
     */
    @FXML
    public void pressEdgeButton(ActionEvent actionEvent) {
        sceneSwitch("EdgeEditing");
    }

/*    public void drawSingleNode(Map<String, String> node) {
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
    }*/

    /**
     * Calculates the coordinates (rounded to the nearest int) of the location f the cursor when the mouse is clicked
     * @param event The mouse click that triggers this action
     */
    public void getCoordsFromMap(javafx.scene.input.MouseEvent event) throws SQLException{
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (state.equals("add")) {
                //System.out.println("Clicked map");

                //double x = xScale(event.getX());
                //double y = yScale(event.getY());
                int x = (int) Math.floor(event.getX() / (mapImage.getFitWidth()/5000));
                int y = (int) Math.floor(event.getY() / (mapImage.getFitHeight()/3400));

                if (xCoor.isVisible()) xCoor.setText(Integer.toString(x));
                if (yCoor.isVisible()) yCoor.setText(Integer.toString(y));
            }
            else if (state.equals("edit") || state.equals("delete")) {
                double x = event.getX();
                double y = event.getY();

                double radius = 20;

                Map<String, String> currClosest = null;
                double currLeastDist = 100000;

                for (Map<String, String> n : db.getNodes()) {
                    //if ((FLOOR == "G" && n.getFloor().equals("G"))
                    //|| (FLOOR == "1" && n.getFloor().equals("1"))) {
                    //Get the x and y of that node
                    double currNodeX = xScale(Integer.parseInt(n.get("XCOORD")));
                    double currNodeY = yScale(Integer.parseInt(n.get("YCOORD")));

                    //Get the difference in x and y between input coords and current node coords
                    double xOff = x - currNodeX;
                    double yOff = y - currNodeY;

                    //Give 'em the ol' pythagoras maneuver
                    double dist = (Math.pow(xOff, 2) + Math.pow(yOff, 2));
                    dist = Math.sqrt(dist);

                    //If the distance is LESS than the given radius...
                    if (dist < radius) {
                        //...AND the distance is less than the current min, update current closest node
                        if (dist < currLeastDist) {
                            currClosest = n;
                            currLeastDist = dist;
                        }
                    }
                }

                if (currClosest == null) return;
                else {
                    nodeDropdown.setValue(currClosest.get("LONGNAME"));
                }

                prevSelected = currSelected;
                currSelected = currClosest;
                drawSingleNode(prevSelected, Color.BLUE);
                drawSingleNode(currSelected, Color.RED);
            }
        }
    }
}