package edu.wpi.aquamarine_axolotls.views.mapping;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.aquamarine_axolotls.db.CSVHandler;
import edu.wpi.aquamarine_axolotls.db.enums.TABLES;
import edu.wpi.aquamarine_axolotls.pathplanning.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapEditing extends GenericMap {

    @FXML
    private JFXComboBox algoSelectBox;

    @FXML
    public RadioMenuItem newCSVButton;
    @FXML
    public RadioMenuItem mergeCSVButton;
    @FXML
    public RadioMenuItem exportButton;

    ObservableList<String> searchAlgorithms = FXCollections.observableArrayList();
    CSVHandler csvHandler;

    private Map<String, String> anchor1 = new HashMap<>();
    private Map<String, String> anchor2 = new HashMap<>();

    MenuItem newNode;
    MenuItem alignVertical;
    MenuItem alignHorizontal;
    MenuItem addAnchorPoint;
    MenuItem alignSlope;
    MenuItem makeEdge;
    MenuItem deleteNodes;
    MenuItem deleteEdges;
    MenuItem deselect;

    @FXML
    public void initialize() throws SQLException, IOException {
        startUp();

    //====SET UP SEARCH ALGORITHM SELECTION====//

        if(searchAlgorithms.size() == 0){
            searchAlgorithms.add("A Star");
            searchAlgorithms.add("Dijkstra");
            searchAlgorithms.add("Breadth First");
            searchAlgorithms.add("Depth First");
            searchAlgorithms.add("Best First");
            algoSelectBox.setItems(searchAlgorithms);
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



    //====CONTEXT MENU SETUP====//

        // TODO: Rewrite context menu stuff
        newNode = new MenuItem(("New Node Here"));
        alignVertical = new MenuItem(("Align Vertical"));
        alignHorizontal = new MenuItem(("Align Horizontal"));
        addAnchorPoint = new MenuItem(("Add Anchor Point"));
        alignSlope = new MenuItem("Align w/ Anchors 1 and 2");
        makeEdge = new MenuItem("Make Edge Between Selection");
        deleteNodes = new MenuItem("Delete Selected Nodes");
        deleteEdges = new MenuItem("Delete Selected Edges");
        deselect = new MenuItem(("Deselect All"));

        //Handler for the button that adds a new node
        newNode.setOnAction((ActionEvent e) -> {
            state = "New";
            currentID = "";
            editPopUp();
        });

        //Handler for the button that adds an anchor point
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
            }
            else {
                anchor2 = selectedNode;
                addAnchorPoint.setText("Change 2nd Anchor Point");
            }
            changeNodeColorOnImage(selectedNode.get("NODEID"), Color.PURPLE);

        });

        //Handler for the button that deselects everything
        deselect.setOnAction((ActionEvent e) -> {
            alignHorizontal.setVisible(false);
            alignVertical.setVisible(false);
            alignSlope.setVisible(false);
            makeEdge.setVisible(false);
            deleteNodes.setVisible(false);
            deleteEdges.setVisible(false);
            addAnchorPoint.setVisible(false);
            for (Map<String, String> node: selectedNodesList){
                try {
                    nodesOnImage.get(node.get("NODEID")).setFill(darkBlue);
                    if (!db.getNode(node.get("NODEID")).get("FLOOR").equals(FLOOR)) {
                        removeNodeOnImage(node.get("NODEID"));
                    }
                } catch(SQLException throwables){
                    throwables.printStackTrace();
                }
            }
            for (Map<String, String> edge: selectedEdgesList){
                try {
                    String edgeID = edge.get("EDGEID");
                    linesOnImage.get(edgeID).setStroke(Color.BLACK);
                    if (!db.getNode(db.getEdge(edgeID).get("STARTNODE")).get("FLOOR").equals(FLOOR)) {
                        removeEdgeOnImage(edgeID);
                    }
                } catch (SQLException throwables){
                    throwables.printStackTrace();
                }
            }
            selectedNodesList.clear();
            selectedEdgesList.clear();
            addAnchorPoint.setText("Add Anchor Point");
            if (!anchor1.isEmpty()) drawSingleNode(anchor1.get("NODEID"), darkBlue);
            if (!anchor2.isEmpty()) drawSingleNode(anchor2.get("NODEID"), darkBlue);
            anchor1.clear();
            anchor2.clear();
            deselect.setVisible(false);
        });

        //Handler for the button that aligns the selection vertically
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

        //Handler for the button that aligns the selection horizontally
        alignHorizontal.setOnAction((ActionEvent e) -> {
            try {
                if(!selectedNodesList.isEmpty()){
                    alignNodesHorizontal(selectedNodesList, anchor1);
                    deselect.fire();
                }
            }
            catch (SQLException se){
                se.printStackTrace();
            }
        });

        //Handler for the button that aligns everything on a slope between two points
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

        //Handler for the button that makes a new edge
        makeEdge.setOnAction((ActionEvent e) -> {

            Map<String, String> edge = new HashMap<>();

            String startNodeID = selectedNodesList.get(0).get("NODEID");
            String endNodeID = selectedNodesList.get(1).get("NODEID");
            String edgeID = startNodeID + "_" + endNodeID;

            edge.put("STARTNODE", startNodeID);
            edge.put("ENDNODE", endNodeID);
            edge.put("EDGEID", edgeID);

            try {
                if(!db.edgeExists(edge.get("EDGEID")) &&
                        !db.edgeExists(edge.get("ENDNODE") + "_" + edge.get("STARTNODE"))){
                    db.addEdge(edge);
                } else {
                    popUp("ERROR", "That edge already exists.");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            drawSingleEdge(edgeID, Color.BLACK);
            deselect.fire();

        });

        deleteNodes.setOnAction((ActionEvent e) -> {
            for (Map<String, String> node : selectedNodesList){
                try {
                    String nodeID = node.get("NODEID");
                    removeNodeOnImage(nodeID);
                    for (Map<String, String> edge : db.getEdgesConnectedToNode(nodeID)){
                        selectedEdgesList.remove(edge);
                        removeEdgeOnImage(edge.get("EDGEID"));
                    }
                    db.deleteNode(nodeID);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            selectedNodesList.clear();
            //drawFloor(FLOOR);
        });

        deleteEdges.setOnAction((ActionEvent e) ->{
            for (Map<String, String> edge: selectedEdgesList){
                try {
                    String edgeID = edge.get("EDGEID");
                    removeEdgeOnImage(edgeID);
                    db.deleteEdge(edgeID);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            selectedEdgesList.clear();
            //drawFloor(FLOOR);
        });

        //Aligning and deselecting shouldn't be initially visible since they require a selection to work
        newNode.setVisible(false);
        addAnchorPoint.setVisible(false);
        alignHorizontal.setVisible(false);
        alignVertical.setVisible(false);
        alignSlope.setVisible(false);
        makeEdge.setVisible(false);
        deleteNodes.setVisible(false);
        deleteEdges.setVisible(false);
        deselect.setVisible(false);

        //Add everything to the context menu
        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(newNode, addAnchorPoint, alignHorizontal, alignVertical, alignSlope, makeEdge, deleteNodes, deleteEdges, deselect);

        //Update the context menu when it's requested
        mapView.setOnContextMenuRequested(event -> {
            contextMenu.show(mapView, event.getScreenX(), event.getScreenY()); //Show the menu
            contextMenuX = event.getX(); //Update X and Y coords of contextmenu
            contextMenuY = event.getY();

            Map<String, String> nearestNode = null;
            try { //Get the nearest node to the context menu -- will be used in subsequent operatinos
                nearestNode = getNearestNode(contextMenuX, contextMenuY);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            newNode.setVisible(true);

            if (nearestNode != null) { //If there's nothing nearby, set add anchor point to be invisible
                addAnchorPoint.setVisible(true);
            }
            else {
                addAnchorPoint.setVisible(false);
            }

            if (!anchor1.isEmpty() && anchor2.isEmpty()){
                alignHorizontal.setVisible(true);
            } else {
                alignHorizontal.setVisible(false);
            }

            if (!anchor1.isEmpty() && anchor2.isEmpty()){
                alignVertical.setVisible(true);
            } else {
                alignVertical.setVisible(false);
            }

            if (!anchor1.isEmpty() && !anchor2.isEmpty()){
                alignSlope.setVisible(true);
            } else {
                alignSlope.setVisible(false);
            }

            if (selectedNodesList.size() == 2){
                makeEdge.setVisible(true);
            }
            else {
                makeEdge.setVisible(false);
            }

            if(!selectedNodesList.isEmpty()){
                deleteNodes.setVisible(true);
            } else{
                deleteNodes.setVisible(false);
            }

            if(!selectedEdgesList.isEmpty()){
                deleteEdges.setVisible(true);
            } else{
                deleteEdges.setVisible(false);
            }

            if (!selectedNodesList.isEmpty() || !anchor1.isEmpty() || !anchor2.isEmpty() || !selectedEdgesList.isEmpty()){ //If there's nothing selected, set deselect to be invisible
                deselect.setVisible(true);
            }
            else {
                deselect.setVisible(false);
            }

        });

        //Event handler for when the mouse is clicked and dragged, used for dragging nodes around
        mapView.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if(nodeBeingDragged != null){ //If we've double-clicked and dragged, move the node
                changeNodeCoordinatesOnImage(nodeBeingDragged, event.getX(), event.getY());
                mapScrollPane.setPannable(false);
            }
        });

        //Event handler for when the mouse is released
        mapView.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (nodeBeingDragged != null) { //If we were dragging a node, update the database with its new position
                try {
                    Map<String, String> newCoords = new HashMap<>();
                    newCoords.put("XCOORD", String.valueOf(Math.round(inverseXScale(event.getX()))));
                    newCoords.put("YCOORD", String.valueOf(Math.round(inverseYScale(event.getY()))));
                    db.editNode(nodeBeingDragged, newCoords);
                    deselect.fire();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                updateEdgesConnectedToNode(nodeBeingDragged); //Visually update the edges connected to that node
                nodeBeingDragged = null; //We're no longer dragging anything
                mapScrollPane.setPannable(true);
            }
        });
    }



    /**
     * Brings up the editing popup for users to change values of an edge/node
     */
    public void editPopUp(){
        final Stage myDialog = new Stage();
        myDialog.initModality(Modality.APPLICATION_MODAL);
        myDialog.centerOnScreen();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/NodePopUp.fxml"));

            NodePopUp controller = new NodePopUp(this);
            loader.setController(controller);

            myDialog.setScene(new Scene(loader.load()));
            myDialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Switches to a given floor, then draws all nodes and edges on it
     * @param floor Floor to move to
     */
    @Override
    public void drawFloor(String floor) {
        changeFloorImage(floor);
        drawEdges();
        drawNodes(darkBlue);
        /*if(contextMenu.getItems().contains(deselect)) {
            ObservableList<MenuItem> items = contextMenu.getItems();
            items.get(items.indexOf(deselect)).fire(); // targets deselect
        }*/
    }

    public void drawNodes(Color colorOfNodes) {
        try {
            for (Map<String, String> node: db.getNodesByValue("FLOOR", FLOOR)) {
                drawSingleNode(node.get("NODEID"), colorOfNodes);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Draws all edges on the current floor, check for floor is in drawTwoNodesWithEdge
     */
    public void drawEdges() {
        try {
            for (Map<String, String> edge : db.getEdges())
                drawSingleEdge(edge.get("EDGEID"), Color.BLACK);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Circle setEventHandler(Circle node, String nodeID) {
        node.setOnMousePressed((MouseEvent e) ->{
            if (e.getButton().equals(MouseButton.PRIMARY)){
                if(e.getClickCount() == 2) nodeBeingDragged = nodeID;
            }
        });

        node.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY)){

                //System.out.println(e.getClickCount());
                if (e.getClickCount() == 2) {
                    node.setFill(yellow);
                    System.out.println("Successfully clicked node");
                    currentID = nodeID;
                    state = "Edit";
                    editPopUp();
                }
                //Otherwise, single clicks will select/deselect nodes
                else {
                    Circle currentCircle = nodesOnImage.get(nodeID);
                    if (currentCircle.getFill().equals(yellow)) {
                        try {
                            selectedNodesList.remove(db.getNode(nodeID));
                            //if (selectedNodesList.size() == 0) contextMenu.getItems().get(1).setVisible(false);
                            node.setFill(darkBlue);
                            //setNodeOnImage(currentCircle, nodeID);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    } else {
                        try {
                            selectedNodesList.add(db.getNode(nodeID));
                            //contextMenu.getItems().get(1).setVisible(true);
                            currentCircle.setFill(yellow);
                            //setNodeOnImage(currentCircle, nodeID);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }
            }
        });

        node.setOnMouseEntered((MouseEvent e) -> node.setRadius(5));

        node.setOnMouseExited((MouseEvent e) -> node.setRadius(magicNumber));

        return node;
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
     * Aligns the current selection horizontally relative to a selected anchor point
     * @param nodes List of nodes to align
     * @param anchorPoint Anchor point to align relative to
     * @throws SQLException
     */
    public void alignNodesHorizontal(List<Map<String, String>> nodes, Map<String, String> anchorPoint) throws SQLException {
        String anchorY = anchorPoint.get("YCOORD");
        updateNodeOnImage(anchorPoint.get("NODEID"));
        for (Map<String, String> node : nodes) {
            node.replace("YCOORD", anchorY); // meh it works CHANGE TO NEW NODE IF NOT WORKING
            db.editNode(node.get("NODEID"), node); // IF THIS ISNT WORKING YOU HAVE TO CHANGE TO new node
//            changeNodeCoordinatesOnImage(node.get("NODEID"), Integer.parseInt(node.get("XCOORD")), Integer.parseInt(anchorY));
//            changeNodeColorOnImage(node.get("NODEID"), darkBlue);
            updateNodeOnImage(node.get("NODEID"));
        }
        selectedNodesList.clear();
        anchor1.clear();
        drawFloor(FLOOR);
    }


    /**
     * Aligns the current selection in a vertical line relative to a selected anchor point
     * @param nodes List of nodes to align
     * @param anchorPoint Anchor point to align relative to
     * @throws SQLException
     */
    public void alignNodesVertical(List<Map<String, String>> nodes, Map<String, String> anchorPoint) throws SQLException {
        String anchorX = anchorPoint.get("XCOORD");
        updateNodeOnImage(anchorPoint.get("NODEID"));
        for (Map<String, String> node : nodes) {
            node.replace("XCOORD", anchorX);
            db.editNode(node.get("NODEID"), node);
            updateNodeOnImage(node.get("NODEID"));
        }

        selectedNodesList.clear();
        anchor1.clear();
        drawFloor(FLOOR);
    }


    /**
     * Aligns the current selection in a line between two anchors
     * @param nodes Nodes to align
     * @param anchorPoint1 First anchor
     * @param anchorPoint2 Second anchor
     * @throws SQLException
     */
    //TODO : loss of accuarcy causing errors
    public void alignNodesBetweenTwoNodes(List<Map<String, String>> nodes, Map<String, String> anchorPoint1, Map<String, String> anchorPoint2) throws SQLException {

        double anchorX1 = Integer.parseInt(anchorPoint1.get("XCOORD"));
        double anchorY1 = Integer.parseInt(anchorPoint1.get("YCOORD"));
        double anchorX2 = Integer.parseInt(anchorPoint2.get("XCOORD"));
        double anchorY2 = Integer.parseInt(anchorPoint2.get("YCOORD"));

        double anchorSlope = (anchorX2 - anchorX1) / (anchorY2 - anchorY1);

        for (Map<String, String> node : nodes) {

            double originalX = Integer.parseInt(node.get("XCOORD"));
            double originalY = Integer.parseInt(node.get("YCOORD"));

            double newY = (originalY + Math.pow(anchorSlope, 2) * anchorY1 - anchorSlope * anchorX1 + anchorSlope * originalX) / (1 + Math.pow(anchorSlope, 2));
            double newX = (anchorSlope * newY + anchorX1 - anchorSlope * anchorY1);

            node.replace("XCOORD", String.valueOf( (int) Math.round(newX)));
            node.replace("YCOORD", String.valueOf((int) Math.round(newY)));
            db.editNode(node.get("NODEID"), node);
            updateNodeOnImage(node.get("NODEID"));
        }
        selectedNodesList.clear();
        anchor1.clear();
        anchor2.clear();
        drawFloor(FLOOR);
    }


    /** CSV Stuff **/

    /**
     * Overwrites current csv with the selected csv
     */
    public void newCSV() { //still in the works
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showOpenDialog(algoSelectBox.getScene().getWindow());
        try {
            csvHandler.importCSV(csv, TABLES.EDGES, true);
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (SQLException sq) {
            sq.printStackTrace();
        }

        try {
            initialize(); //REFRESH TABLE
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Merges the selected CSV together with the existing one
     */
    public void mergeCSV() { //still in the works
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showOpenDialog(algoSelectBox.getScene().getWindow());
        try {
            csvHandler.importCSV(csv, TABLES.EDGES, false);
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (SQLException sq) {
            sq.printStackTrace();
        }

        try {
            initialize(); //REFRESH TABLE
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Downloads the current list of edges to a CSV
     */
    public void exportCSV() { //still in the works
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showSaveDialog(algoSelectBox.getScene().getWindow()); //TODO: what isthis?
        try {
            csvHandler.exportCSV(csv, TABLES.EDGES);
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (SQLException sq) {
            sq.printStackTrace();
        }
    }
}