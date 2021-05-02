package edu.wpi.aquamarine_axolotls.views.mapping;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.db.*;
import edu.wpi.aquamarine_axolotls.pathplanning.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


public class NodeEditing extends GenericMap {
    @FXML
    private JFXButton addButton;
    @FXML
    private JFXComboBox algoSelectBox;
    @FXML
    private HBox nodeT;
    @FXML
    private HBox nodeD;
    @FXML
    private JFXComboBox nodeDropdown;
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
    private JFXTextField nodeType;
    @FXML
    private JFXTextField floor;
    @FXML
    private JFXTextField building;

    @FXML
    private Label submissionlabel;
    @FXML
    private JFXButton submissionButton;

    @FXML
    private TableView table;
    @FXML
    private TableColumn nodeIDCol;
    @FXML
    private TableColumn lNameCol;
    @FXML
    private TableColumn sNameCol;
    @FXML
    private TableColumn xCol;
    @FXML
    private TableColumn yCol;
    @FXML
    private TableColumn floorCol;
    @FXML
    private TableColumn buildingCol;
    @FXML
    private TableColumn typeCol;

    @FXML
    private JFXButton clearButton;
    private Map<String, String> prevSelected;
    private Map<String, String> currSelected;

    private

    ObservableList<String> options = FXCollections.observableArrayList();
    ArrayList<String> nodeIDS = new ArrayList<>();
    ObservableList<String> searchAlgorithms = FXCollections.observableArrayList();

    String state = "";

    CSVHandler csvHandler;
    private List<Map<String, String>> selectedNodesList = new ArrayList<Map<String, String>>();
    private Map<String, String> anchor1 = new HashMap<>();
    private Map<String, String> anchor2 = new HashMap<>();

    /**
     * Initializes the node editing page by filling in dropdown boxes and importing data from the database
     */
    @FXML
    public void initialize() throws SQLException {

        startUp();

        table.setEditable(false);
        table.getItems().clear();

        submissionlabel.setVisible(true);
        if(searchAlgorithms.size() == 0){
            searchAlgorithms.add("A Star");
            searchAlgorithms.add("Dijkstra");
            searchAlgorithms.add("Breadth First");
            searchAlgorithms.add("Depth First");
            searchAlgorithms.add("Best First");
            algoSelectBox.setItems(searchAlgorithms);
        }

        List<Map<String, String>> nodes = db.getNodes();

        for (Map<String, String> node : nodes) {
            options.add(node.get("LONGNAME"));
            nodeIDS.add(node.get("NODEID"));
            Node cur = new Node(node.get("NODEID"), Integer.parseInt(node.get("XCOORD")), Integer.parseInt(node.get("YCOORD")), node.get("LONGNAME"), node.get("SHORTNAME"), node.get("FLOOR"), node.get("BUILDING"), node.get("NODETYPE"));
            table.getItems().add(cur);
        }
        if (SearchAlgorithmContext.getSearchAlgorithmContext().context == null) {
            SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
        }

        String algo = SearchAlgorithmContext.getSearchAlgorithmContext().context.toString();

        if (algo.contains("AStar")) algoSelectBox.getSelectionModel().select(0);
        else if (algo.contains("Dijkstra")) algoSelectBox.getSelectionModel().select(1);
        else if (algo.contains("BreadthFirstSearch")) algoSelectBox.getSelectionModel().select(2);
        else if (algo.contains("DepthFirstSearch")) algoSelectBox.getSelectionModel().select(3);
        else if (algo.contains("BestFirstSearch")) algoSelectBox.getSelectionModel().select(4);


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


        MenuItem newNode = new MenuItem(("New Node Here"));
        MenuItem editNode = new MenuItem(("Edit Node"));
        MenuItem deleteNode = new MenuItem(("Delete Node"));
        MenuItem selectNode = new MenuItem(("Select Node"));
        MenuItem alignVertical = new MenuItem(("Align Vertical"));
        MenuItem alignHorizontal = new MenuItem(("Align Horizontal"));
        MenuItem deselect = new MenuItem(("Deselect Nodes"));
        MenuItem addAnchorPoint = new MenuItem(("Add Anchor Point"));
        MenuItem alignSlope = new MenuItem("Align w/ Anchor 1 and Anchor 2");

        newNode.setOnAction((ActionEvent e) -> {
            pressAddButton();
            int xint = (int) Math.floor(contextMenuX / (mapImage.getFitWidth() / 5000));
            int yint = (int) Math.floor(contextMenuY / (mapImage.getFitHeight() / 3400));
            xCoor.setText(Integer.toString(xint));
            yCoor.setText(Integer.toString(yint));
        });

        editNode.setOnAction((ActionEvent e) -> {
            pressEditButton();
            try {
                getCoordsFromMap(contextMenuX, contextMenuY);
            } catch (SQLException se) {
                se.printStackTrace();
            }

        });
        deleteNode.setOnAction((ActionEvent e) -> {
            pressDeleteButton();
            try {
                getCoordsFromMap(contextMenuX, contextMenuY);
            } catch (SQLException se) {
                se.printStackTrace();
            }
        });
        addAnchorPoint.setOnAction((ActionEvent e) -> {
            Map<String, String> selectedNode = null;
            try {
                selectedNode = getNearestNode(contextMenuX, contextMenuY);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            if(selectedNode == null) return;
            if(anchor1.isEmpty()){
                anchor1 = selectedNode;
                addAnchorPoint.setText("Add 2nd Anchor Point");
                alignVertical.setVisible(true);
                alignHorizontal.setVisible(true);
            }
            else {
                alignSlope.setVisible(true);
                anchor2 = selectedNode;
                addAnchorPoint.setText("Change 2nd Anchor Point");
            }
            drawSingleNode( Integer.parseInt(selectedNode.get("XCOORD")), Integer.parseInt(selectedNode.get("YCOORD")), Color.PURPLE);

        });
        selectNode.setOnAction((ActionEvent e) -> {
            try {
                Map<String, String> selectedNode = getNearestNode(contextMenuX, contextMenuY);
                if(selectedNode == null) return;

                drawSingleNode(Integer.parseInt(selectedNode.get("XCOORD")), Integer.parseInt(selectedNode.get("YCOORD")), Color.GREEN);
                selectedNodesList.add(getNearestNode(contextMenuX, contextMenuY));

            }
            catch (SQLException se){
                se.printStackTrace();
            }
        });
        deselect.setOnAction((ActionEvent e) -> {
            alignHorizontal.setVisible(false);
            alignVertical.setVisible(false);
            alignSlope.setVisible(false);
            selectedNodesList.clear();
            try {
                drawNodesAndFloor(FLOOR, Color.BLUE);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            addAnchorPoint.setText("Add Anchor Point");
            anchor1.clear();
            anchor2.clear();

        });
        alignVertical.setOnAction((ActionEvent e) -> {
            try {
                if(!selectedNodesList.isEmpty()) {
                    alignNodesVertical(selectedNodesList, anchor1);
                    deselect.fire();
                }
            }
            catch (SQLException se){
                se.printStackTrace();
            }

        });
        alignHorizontal.setOnAction((ActionEvent e) -> {
            try {
                if(!selectedNodesList.isEmpty()){
                    alignNodesHorizontal(selectedNodesList, anchor2);
                    deselect.fire();
                }
            }
            catch (SQLException se){
                se.printStackTrace();
            }
        });
        alignSlope.setOnAction((ActionEvent e) -> {
            try {
                if(!selectedNodesList.isEmpty()){
                    alignNodesBetweenTwoNodes(selectedNodesList, anchor1, anchor2);
                }
            }
            catch (SQLException se){
                se.printStackTrace();
            }
        });
        alignHorizontal.setVisible(false);
        alignVertical.setVisible(false);
        alignSlope.setVisible(false);

        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(newNode, editNode, deleteNode, addAnchorPoint, selectNode, alignVertical, alignHorizontal, alignSlope, deselect);

//        mapImage.setOnContextMenuRequested(new EventHandler() {
//            @Override
//            public void handle(ContextMenuEvent event) {
//                contextMenu.show(mapImage, event.getScreenX(), event.getScreenY());
//            }
//        });
        //mapView.setOnContextMenuRequested(e -> contextMenu.show(mapView, e.getScreenX(), e.getScreenY()));
        mapView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

            public void handle(ContextMenuEvent event) {
                contextMenu.show(mapView, event.getScreenX(), event.getScreenY());
                contextMenuX = event.getX();
                contextMenuY = event.getY();
                try {
                    if (getNearestNode(contextMenuX, contextMenuY) == null) {
                        selectNode.setVisible(false);
                        editNode.setVisible(false);
                        deleteNode.setVisible(false);
                        addAnchorPoint.setVisible(false);
                    }
                    else {
                        selectNode.setVisible(true);
                        editNode.setVisible(true);
                        deleteNode.setVisible(true);
                        addAnchorPoint.setVisible(true);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    /**
     * Clears all JFXTextFields in the node editing menu
     */
    @FXML
    public void clearfields() {
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
    public void pressDeleteButton() {
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
    public void pressAddButton() {
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
    public void pressEditButton() {
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

    public void changeFloor(String floor) throws SQLException{
        ObservableList<MenuItem> items = contextMenu.getItems();
        items.get(items.size()-1).fire(); // targets deselect
        drawFloor(floor);
        drawNodes(Color.BLUE);
    }

    /**
     * Handles the user pressing the Import and Overwrite button while in the node editing page by grabbing
     * the node data from the CSV file and using that data to replace the existing node table
     */
    public void newCSV() throws SQLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showOpenDialog(addButton.getScene().getWindow());
        try {
            csvHandler.importCSV(csv, TABLES.NODES, false);
        } catch (IOException | SQLException ie) {
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
        try {
            csvHandler.exportCSV(csv, TABLES.NODES);
        } catch (IOException | SQLException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Deletes a given node from the database and map.
     *
     * @param current_nodeID The ID of the node to be deleted
     */
    public void delete(String current_nodeID) {
        try {

            if (db.nodeExists(current_nodeID)) {
                db.deleteNode(current_nodeID);
                submissionlabel.setText("You have deleted " + current_nodeID);
            } else {
                submissionlabel.setText("Node does not exist");
            }
        } catch (SQLException sq) {
            sq.printStackTrace();
        }
        return;
    }

    /**
     * Adds a node to the map and database
     *
     * @param current_nodeID The ID of the node to be added
     */
    public void add(String current_nodeID) {
        int x = Integer.parseInt(xCoor.getText());
        int y = Integer.parseInt(yCoor.getText());
        if (!(x < 5000 && x > 0) && (y < 3400 && y > 0)) {
            submissionlabel.setText("X or Y coordinates are out of bounds");
            return;
        }
        try {
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
            } else {
                submissionlabel.setText("Node already exists");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * Updates the values of row in the node table based on the current values of the text field and dropdown box
     * selections
     *
     * @param current_nodeID The ID of the node to be edited
     */
    public void edit(String current_nodeID) {
        try {
            if (db.nodeExists(current_nodeID)) {
                String xcoord = xCoor.getText();
                String ycoord = yCoor.getText();
                String fl = floor.getText();
                String b = building.getText();
                String type = nodeType.getText();
                String longN = longName.getText();
                String shortN = shortName.getText();

                if (xcoord.equals("")) {
                    xcoord = db.getNode(current_nodeID).get("XCOORD");
                }
                if (ycoord.equals("")) {
                    ycoord = db.getNode(current_nodeID).get("YCOORD");
                }
                if (fl.equals("")) {
                    fl = db.getNode(current_nodeID).get("FLOOR");
                }
                if (b.equals("")) {
                    b = db.getNode(current_nodeID).get("BUILDING");
                }
                if (type.equals("")) {
                    type = db.getNode(current_nodeID).get("NODETYPE");
                }
                if (longN.equals("")) {
                    longN = db.getNode(current_nodeID).get("LONGNAME");
                }
                if (shortN.equals("")) {
                    shortN = db.getNode(current_nodeID).get("SHORTNAME");
                }

                int x = Integer.parseInt(xcoord);
                int y = Integer.parseInt(ycoord);

                if (!(x < 5000 && x > 0 && y < 3400 && y > 0)) {
                    submissionlabel.setText("Invalid submission: cannot edit node.");
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
        } catch (SQLException sq) {
            sq.printStackTrace();
        }
    }

    /**
     * Handles the user pressing the Submit button while in the node editing page based on whether the user was
     * adding, editing, or deleting a node
     *
     * @throws SQLException If there is an error getting node data from the database
     */
    @FXML
    public void submitfunction() throws SQLException {
        switch (state) {
            case "delete":
                delete(nodeIDS.get(nodeDropdown.getSelectionModel().getSelectedIndex()));
                drawNodesAndFloor(FLOOR, Color.BLUE);
                break;
            case "add":
                if (nodeID.getText().equals("") ||
                        longName.getText().equals("") ||
                        floor.getText().equals("") ||
                        xCoor.getText().equals("") ||
                        yCoor.getText().equals("")) {
                    submissionlabel.setText("Invalid submission");
                    return;
                }
                else {
                   add(nodeID.getText());
                   drawNodesAndFloor(FLOOR, Color.BLUE);
                   break;
                }

            case "edit":
                if (nodeID.getText().equals("") ||
                        longName.getText().equals("") ||
                        floor.getText().equals("") ||
                        xCoor.getText().equals("") ||
                        yCoor.getText().equals("")) {
                    submissionlabel.setText("Invalid submission");
                    return;
                }
                else {
                    edit(nodeIDS.get(nodeDropdown.getSelectionModel().getSelectedIndex()));
                    drawNodesAndFloor(FLOOR, Color.BLUE);
                    break;
                }

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
        if (algoSelectBox.getSelectionModel() != null && algoSelectBox.getSelectionModel() != null) {
            if (algoSelectBox.getSelectionModel().getSelectedItem().equals("A Star")) {
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
            } else if (algoSelectBox.getSelectionModel().getSelectedItem().equals("Dijkstra")) {
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new Dijkstra());
            } else if (algoSelectBox.getSelectionModel().getSelectedItem().equals("Breadth First")) {
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new BreadthFirstSearch());
            } else if (algoSelectBox.getSelectionModel().getSelectedItem().equals("Depth First")) {
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new DepthFirstSearch());
            } else if (algoSelectBox.getSelectionModel().getSelectedItem().equals("Best First")) {
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new BestFirstSearch());
            }
        }
    }

    /**
     * Highlights the node selected by the user in the dropdown box
     */
    @FXML
    public void highLightNodeFromSelector() throws SQLException {
        if (nodeDropdown.getSelectionModel().getSelectedItem() != null) {
            Map<String, String> node = db.getNode(nodeIDS.get(nodeDropdown.getSelectionModel().getSelectedIndex()));
            prevSelected = currSelected;
            currSelected = node;
            if (prevSelected != null) {
                drawSingleNode(prevSelected, Color.BLUE);
                selectedNodesList.remove(prevSelected);
            }
            drawSingleNode(currSelected, Color.RED);
            //nodeDropdown.setValue(currSelected.get("LONGNAME"));
            nodeID.setText(currSelected.get("NODEID"));
            shortName.setText(currSelected.get("SHORTNAME"));
            longName.setText(currSelected.get("LONGNAME"));
            xCoor.setText(currSelected.get("XCOORD"));
            yCoor.setText(currSelected.get("YCOORD"));
            floor.setText(currSelected.get("FLOOR"));
            building.setText(currSelected.get("BUILDING"));
            nodeType.setText(currSelected.get("NODETYPE"));
            selectedNodesList.add(currSelected); // TODO: part of selection
        }
    }

    public void alignNodesHorizontal(List<Map<String, String>> nodes, Map<String, String> anchorPoint) throws SQLException {
        drawFloor(FLOOR);
        String anchorY = anchorPoint.get("YCOORD");
        for (Map<String, String> node : nodes) {
            Map<String, String> newNode = new HashMap<String, String>();
            newNode.put("YCOORD", anchorY);
            db.editNode(node.get("NODEID"), newNode);
        }
        drawNodesAndFloor(FLOOR, Color.BLUE);

    }

    public void alignNodesVertical(List<Map<String, String>> nodes, Map<String, String> anchorPoint) throws SQLException {
        drawFloor(FLOOR);
        String anchorX = anchorPoint.get("XCOORD");
        drawSingleNode(Double.parseDouble(anchorPoint.get("XCOORD")), Double.parseDouble(anchorPoint.get("YCOORD")), Color.RED);
        for (Map<String, String> node : nodes) {
            Map<String, String> newNode = new HashMap<String, String>();
            newNode.put("XCOORD", anchorX);
            db.editNode(node.get("NODEID"), newNode);
        }
        drawNodesAndFloor(FLOOR, Color.BLUE);


    }

    //TODO : loss of accuarcy causing errors
    public void alignNodesBetweenTwoNodes(List<Map<String, String>> nodes, Map<String, String> anchorPoint1, Map<String, String> anchorPoint2) throws SQLException {
        drawFloor(FLOOR); //TODO : remove this
        double anchorX1 = (Integer.parseInt(anchorPoint1.get("XCOORD")));
        double anchorY1 = -1 * (Integer.parseInt(anchorPoint1.get("YCOORD")));
        double anchorX2 = (Integer.parseInt(anchorPoint2.get("XCOORD")));
        double anchorY2 = -1 * (Integer.parseInt(anchorPoint2.get("YCOORD")));
        double anchorSlope = (anchorY2 - anchorY1) / (anchorX2 - anchorX1);
        for (Map<String, String> node : nodes) {
            double originalX = (Integer.parseInt(node.get("XCOORD")));
            double originalY = -1 * (Integer.parseInt(node.get("YCOORD")));
            //System.out.println("Current Node coords \n" + "X3 = " + originalX + "  Y3 = " + originalY * -1 + "\n");
            double newX = (originalX + Math.pow(anchorSlope, 2) * anchorX1 - anchorSlope * anchorY1 + anchorSlope * originalY) / (1 + Math.pow(anchorSlope, 2));
            double newY = -1 * (anchorSlope * newX + anchorY1 - anchorSlope * anchorX1);
            //System.out.println("New current Node coords \n" + "X3 = " + newX + "  Y3 = " + newY);
            //drawSingleNode(newX, newY, Color.GREEN);
            Map<String, String> newNode = new HashMap<String, String>();
            newNode.put("XCOORD", String.valueOf( (int) Math.round(newX)));
            newNode.put("YCOORD", String.valueOf((int) Math.round(newY)));
            db.editNode(node.get("NODEID"), newNode);
        }
        drawNodesAndFloor(FLOOR, Color.BLUE);
    }

    /**
     * Switches the active page to EdgeEditing, allowing the user to edit edges instead of nodes
     *
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
     *
     * @param x
     * @param y
     * @throws SQLException
     */
    public void getCoordsFromMap(double x, double y) throws SQLException {

        double radius = 20;

        Map<String, String> currClosest = null;
        double currLeastDist = 100000;

        for (Map<String, String> n : db.getNodesByValue("FLOOR", FLOOR)) {
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
            nodeID.setText(currClosest.get("NODEID"));
            shortName.setText(currClosest.get("SHORTNAME"));
            longName.setText(currClosest.get("LONGNAME"));
            xCoor.setText(currClosest.get("XCOORD"));
            yCoor.setText(currClosest.get("YCOORD"));
            floor.setText(currClosest.get("FLOOR"));
            building.setText(currClosest.get("BUILDING"));
            nodeType.setText(currClosest.get("NODETYPE"));
        }

//                if(selectedNodesList.contains(currClosest)){
//                    drawSingleNode(currClosest, Color.BLUE);
//                    selectedNodesList.remove(currClosest);
//                } else {
//                    drawSingleNode(currClosest, Color.RED);
//                    selectedNodesList.add(currClosest);
//                }

        prevSelected = currSelected;
        currSelected = currClosest;
        drawSingleNode(prevSelected, Color.BLUE);
        drawSingleNode(currSelected, Color.RED);
    }


}