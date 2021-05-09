package edu.wpi.aquamarine_axolotls.views.mapping;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.db.CSVHandler;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.enums.TABLES;
import edu.wpi.aquamarine_axolotls.pathplanning.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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

    MenuItem newNode = new MenuItem(("New Node Here"));
    MenuItem alignVertical = new MenuItem(("Align Vertical"));
    MenuItem alignHorizontal = new MenuItem(("Align Horizontal"));
    MenuItem deselect = new MenuItem(("Deselect Nodes"));
    MenuItem addAnchorPoint = new MenuItem(("Add Anchor Point"));
    MenuItem alignSlope = new MenuItem("Align w/ Anchors 1 and 2");
    MenuItem makeEdge = new MenuItem("Make edge between selection");



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
        deselect = new MenuItem(("Deselect Nodes"));
        addAnchorPoint = new MenuItem(("Add Anchor Point"));
        alignSlope = new MenuItem("Align w/ Anchors 1 and 2");
        makeEdge = new MenuItem("Make edge between selection");

        //Handler for the button that adds a new node
        newNode.setOnAction((ActionEvent e) -> {
            state = "New";
            currentID = "";
            nodePopUp();
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
                alignVertical.setVisible(true);
                alignHorizontal.setVisible(true);
            }
            else {
                alignSlope.setVisible(true);
                anchor2 = selectedNode;
                addAnchorPoint.setText("Change 2nd Anchor Point");
            }
            drawSingleNode(selectedNode.get("NODEID"), Color.PURPLE);

        });

        //Handler for the button that deselects everything
        deselect.setOnAction((ActionEvent e) -> {
            alignHorizontal.setVisible(false);
            alignVertical.setVisible(false);
            alignSlope.setVisible(false);
            deselect.setVisible(false);
            for (Map<String, String> node: selectedNodesList){
                nodesOnImage.get(node.get("NODEID")).setFill(darkBlue);
            }
            selectedNodesList.clear();
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
            state = "New";
            currentID = "";
            edgePopUp();
        });

        //Aligning and deselecting shouldn't be initially visible since they require a selection to work
        alignHorizontal.setVisible(false);
        alignVertical.setVisible(false);
        alignSlope.setVisible(false);
        deselect.setVisible(false);

        //Add everything to the context menu
        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(newNode, addAnchorPoint, alignVertical, alignHorizontal, alignSlope, deselect, makeEdge);

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

            if (selectedNodesList.size() == 2){
                makeEdge.setVisible(true);
            }
            else {
                makeEdge.setVisible(false);
            }

            if (nearestNode == null) { //If there's nothing nearby, set add anchor point to be invisible
                addAnchorPoint.setVisible(false);
            }
            else {
                addAnchorPoint.setVisible(true);
            }

            if (selectedNodesList.isEmpty() && anchor1.isEmpty() && anchor2.isEmpty()){ //If there's nothing selected, set deselect to be invisible
                deselect.setVisible(false);
            }
            else {
                deselect.setVisible(true);
            }


            /*if (findDistance(event.getX(), event.getY(), Integer.parseInt(nearestNode.get("XCOORD")), Integer.parseInt(nearestNode.get("YCOORD"))) > magicNumber){
                newNode.setVisible(true);
            }
            else {
                newNode.setVisible(false);
            }*/
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
     * @param isNode Whether this is a node or edge
     */
    public void editPopUp(boolean isNode){
        final Stage myDialog = new Stage();
        myDialog.initModality(Modality.APPLICATION_MODAL);
        myDialog.centerOnScreen();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/" + (isNode ? "Node" : "Edge") + "PopUp" + ".fxml"));
            if(isNode){
                NodePopUp controller = new NodePopUp(this);
                loader.setController(controller);
            }
            else{
                EdgePopUp controller = new EdgePopUp(this);
                loader.setController(controller);
            }

            myDialog.setScene(new Scene(loader.load()));
            myDialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void nodePopUp() {
        editPopUp(true);
    }

    @FXML  //TODO: make this look better
    public void edgePopUp() {
        editPopUp(false);
    }

    /**
     * Switches to a given floor, then draws all nodes and edges on it
     * @param floor Floor to move to
     */
    @Override
    public void drawFloor(String floor) {
        try {
            changeFloorImage(floor);
            drawEdges(Color.BLACK);
            drawNodes(darkBlue);
            /*if(contextMenu.getItems().contains(deselect)) {
                ObservableList<MenuItem> items = contextMenu.getItems();
                items.get(items.indexOf(deselect)).fire(); // targets deselect
            }*/
            //deselect.fire(); //TODO: Figure out why an invocationTargetException() is happening here
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
            node.replace("YCOORD", anchorY); // CHANGE TO NEW NODE IF NOT WORKING
            db.editNode(node.get("NODEID"), node); // IF THIS ISNT WORKING YOU HAVE TO CHANGE TO new node
            updateNodeOnImage(node.get("NODEID"));
        }
        drawFloor(FLOOR);
        selectedNodesList.clear();
        anchor1.clear();
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
        drawFloor(FLOOR);
        selectedNodesList.clear();
        anchor1.clear();

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
        drawFloor(FLOOR);
        selectedNodesList.clear();
        anchor1.clear();
        anchor2.clear();
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