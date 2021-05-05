
package edu.wpi.aquamarine_axolotls.views.mapping;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.CSVHandler;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.*;
import edu.wpi.aquamarine_axolotls.db.enums.TABLES;
import edu.wpi.aquamarine_axolotls.pathplanning.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EdgeEditing extends GenericMap {
    @FXML
    public JFXButton deleteButton;
    @FXML
    public JFXComboBox algoSelectBox;
    @FXML
    private JFXButton addButton;
    @FXML
    private JFXButton editButton;

    @FXML
    private HBox edgeT;
    @FXML
    private HBox edgeD;
    @FXML
    private JFXComboBox edgeDropdown;
    @FXML
    private JFXTextField edgeIDtextbox;
    @FXML
    private JFXComboBox startNodeDropdown;
    @FXML
    private JFXComboBox endNodeDropdown;

    @FXML
    public RadioMenuItem importButton; // TODO: whats going on?
    @FXML
    public RadioMenuItem exportButton;
    @FXML
    private Label submissionlabel;
    @FXML
    private JFXButton submissionButton;


    String state = "";

    DatabaseController db;
    CSVHandler csvHandler;
    List<Map<String, String>> selectedNodesList = new ArrayList<>();
    private Map<String, String> prevSelectedEdge = null;
    private Map<String, String> prevSelectedNodeStart = null;
    private Map<String, String> prevSelectedNodeEnd = null;

    @FXML
    public void initialize() {

        startUp();

        ObservableList<String> options = FXCollections.observableArrayList();
        ObservableList<String> searchAlgorithms = FXCollections.observableArrayList();

        searchAlgorithms.add("A-Star");
        searchAlgorithms.add("Dijkstra");
        searchAlgorithms.add("Breadth First");
        searchAlgorithms.add("Depth First");
        searchAlgorithms.add("Best First");
        algoSelectBox.setItems(searchAlgorithms);


        if (SearchAlgorithmContext.getSearchAlgorithmContext().context == null) {
            SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
        }

        String algo = SearchAlgorithmContext.getSearchAlgorithmContext().context.toString();

        if (algo.contains("AStar")) algoSelectBox.getSelectionModel().select(0); //TODO: SWITCH CASE
        else if (algo.contains("Dijkstra")) algoSelectBox.getSelectionModel().select(1);
        else if (algo.contains("BreadthFirstSearch")) algoSelectBox.getSelectionModel().select(2);
        else if (algo.contains("DepthFirstSearch")) algoSelectBox.getSelectionModel().select(3);
        else if (algo.contains("BestFirstSearch")) algoSelectBox.getSelectionModel().select(4);

        ObservableList<String> edgeOptions = FXCollections.observableArrayList();
        ObservableList<String> nodeOptions = FXCollections.observableArrayList();

        submissionlabel.setVisible(true);

        edgeDropdown.setVisible(false);
        edgeIDtextbox.setVisible(false);
        startNodeDropdown.setVisible(false);
        endNodeDropdown.setVisible(false);
        submissionButton.setVisible(false);

        MenuItem selectNode = new MenuItem("Select Node");
        MenuItem makeEdge = new MenuItem("Make edge between selection");
        MenuItem deselect = new MenuItem("Deselect nodes");

        selectNode.setOnAction((ActionEvent e) -> {
            try {
                Map<String, String> select = getNearestNode(contextMenuX, contextMenuY);
                drawSingleNode(select, Color.GREEN);
                selectedNodesList.add(select);
                if (selectedNodesList.size() >= 2) {
                    selectNode.setVisible(false);
                }
            }
            catch(SQLException se) {
                se.printStackTrace();
            }
        });

        makeEdge.setOnAction((ActionEvent e) -> {
            try {
                pressAddButton();
            }
            catch (SQLException se) {
                se.printStackTrace();
            }
            startNodeDropdown.setValue(selectedNodesList.get(0).get("NODEID"));
            endNodeDropdown.setValue(selectedNodesList.get(1).get("NODEID"));
        });

        deselect.setOnAction((ActionEvent e) -> {
            selectedNodesList.clear();
            selectNode.setVisible(true);
            try {
                changeFloor(FLOOR);
            }
            catch (SQLException se) {
                se.printStackTrace();
            }
        });


        try {
            db = DatabaseController.getInstance();
            csvHandler = new CSVHandler(db);
            List<Map<String, String>> edges = db.getEdges();
            List<Map<String, String>> nodes = db.getNodes();

            for (Map<String, String> edge : edges) {
                edgeOptions.add(edge.get("EDGEID"));
                drawTwoNodesWithEdge(db.getNode(edge.get("STARTNODE")), db.getNode(edge.get("ENDNODE")), Color.BLUE, Color.BLUE, Color.BLACK);
            }
            for (Map<String, String> node : nodes) nodeOptions.add(node.get("NODEID"));

            startNodeDropdown.setItems(nodeOptions);
            endNodeDropdown.setItems(nodeOptions);
            edgeDropdown.setItems(edgeOptions);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(selectNode, makeEdge, deselect);

        mapView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

            public void handle(ContextMenuEvent event) {
                contextMenu.show(mapView, event.getScreenX(), event.getScreenY());
                contextMenuX = event.getX();
                contextMenuY = event.getY();
                try {
                    if (getNearestNode(contextMenuX, contextMenuY) == null) {
                        selectNode.setVisible(false);
                    }
                    else {
                        selectNode.setVisible(true);
                    }
                    if (selectedNodesList.size() >= 2) {
                        selectNode.setVisible(false);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void changeFloor(String floor) throws SQLException {
        super.changeFloor(floor);
        for (Map<String, String> edge : db.getEdges()) {
            drawTwoNodesWithEdge(db.getNode(edge.get("STARTNODE")), db.getNode(edge.get("ENDNODE")), Color.BLUE, Color.BLUE, Color.BLACK);
        }
    }

    /**
     * Button press that sets unused fields to be hidden, and needed fields to be visible. Also sets
     * state so that the desired add action will happen.
     */
    public void pressAddButton() throws SQLException{
        changeFloor("1");
        clearfields();
        prevSelectedEdge = null;
        prevSelectedNodeEnd = null;
        prevSelectedNodeStart = null;
        edgeT.toFront();
        edgeDropdown.setVisible(false);
        edgeIDtextbox.setVisible(true);
        startNodeDropdown.setVisible(true);
        endNodeDropdown.setVisible(true);
        submissionButton.setVisible(true);

        state = "add";
    }

    /**
     * Button press that sets unused fields to be hidden, and needed fields to be visible. Also sets
     * state so that upon submition the desired edit will happen.
     */
    public void pressEditButton() throws SQLException{
        changeFloor("1");
        clearfields();
        prevSelectedEdge = null;
        prevSelectedNodeEnd = null;
        prevSelectedNodeStart = null;
        edgeD.toFront();
        edgeDropdown.setVisible(true);
        edgeIDtextbox.setVisible(false);
        startNodeDropdown.setVisible(true);
        endNodeDropdown.setVisible(true);
        submissionButton.setVisible(true);

        state = "edit";
    }

    /**
     * Button press that sets unused fields to be hidden, and needed fields to be visible. Also sets
     * state so that upon submission the desired deletion will happen.
     */
    @FXML
    public void pressDeleteButton() throws SQLException{
        changeFloor("1");
        clearfields();
        prevSelectedEdge = null;
        prevSelectedNodeEnd = null;
        prevSelectedNodeStart = null;
        edgeD.toFront();
        edgeDropdown.setVisible(true);
        edgeIDtextbox.setVisible(false);
        startNodeDropdown.setVisible(false);
        endNodeDropdown.setVisible(false);
        submissionButton.setVisible(true);
        state = "delete";
    }

    /**
     * Clears user inputs from all editable fields
     */
    @FXML
    public void clearfields() {
        edgeIDtextbox.clear();
        startNodeDropdown.getSelectionModel().clearSelection();
        endNodeDropdown.getSelectionModel().clearSelection();
    }

    /**
     * switches to node editing
     */
    public void pressNodeButton() {
        sceneSwitch("NodeEditing");
    }

    /**
     * Changes the algorithm that will be used for navigation.
     */
    public void selectAlgorithm() {
        if (algoSelectBox.getSelectionModel() != null && algoSelectBox.getSelectionModel() != null) { //TODO: switch case
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
     * Overwrites current csv with the selected csv
     */
    public void newCSV() { //still in the works
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showOpenDialog(addButton.getScene().getWindow());
        try {
            csvHandler.importCSV(csv, TABLES.EDGES, true);
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (SQLException sq) {
            sq.printStackTrace();
        }

        initialize(); //REFRESH TABLE
    }

    /**
     * Merges the selected CSV together with the existing one
     */
    public void mergeCSV() { //still in the works
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showOpenDialog(addButton.getScene().getWindow());
        try {
            csvHandler.importCSV(csv, TABLES.EDGES, false);
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (SQLException sq) {
            sq.printStackTrace();
        }

        initialize(); //REFRESH TABLE
    }

    /**
     * Downloads the current list of edges to a CSV
     */
    public void exportCSV() { //still in the works
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        File csv = fileChooser.showSaveDialog(addButton.getScene().getWindow());
        try {
            csvHandler.exportCSV(csv, TABLES.EDGES);
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (SQLException sq) {
            sq.printStackTrace();
        }
    }

    /**
     * Deletes a edge based on user selection
     */
    public void delete() {
        String edgeID = edgeDropdown.getSelectionModel().getSelectedItem().toString();
        System.out.println(edgeID);
        try {
            if (db.edgeExists(edgeID)) {
                db.deleteEdge(edgeID);
                drawFloor(FLOOR);
                submissionlabel.setText("You have deleted " + edgeID);
            } else {
                submissionlabel.setText("Edge does not exist");
            }
        } catch (SQLException sq) {
            sq.printStackTrace();
        }
        return;
    }

    /**
     * Adds an edge based on user selection
     */
    public void add() {
        String edgeID = edgeIDtextbox.getText();
        String startNode = startNodeDropdown.getSelectionModel().getSelectedItem().toString();
        String endNode = endNodeDropdown.getSelectionModel().getSelectedItem().toString();

        if (edgeID.equals("") || startNode.equals("") || endNode.equals("")) {
            submissionlabel.setText("Did not fill out all required fields");
            return;
        } else {
            try {
                if (!db.edgeExists(edgeID)) {
                    Map<String, String> edge = new HashMap<String, String>();
                    edge.put("EDGEID", edgeID);
                    edge.put("STARTNODE", startNode);
                    edge.put("ENDNODE", endNode);

                    db.addEdge(edge);
                    drawFloor(FLOOR);
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

    /**
     * Edits an edge based on user selection
     */
    public void edit() {
        String edgeID = edgeDropdown.getSelectionModel().getSelectedItem().toString(); //TODO: GIVE WARNING ON NULL EDGEID
        String startNode = startNodeDropdown.getSelectionModel().getSelectedItem() == null ? "" : startNodeDropdown.getSelectionModel().getSelectedItem().toString();
        String endNode = endNodeDropdown.getSelectionModel().getSelectedItem() == null ? "" : endNodeDropdown.getSelectionModel().getSelectedItem().toString();

        try {
            if (startNode.equals("")) {
                startNode = db.getEdge(edgeID).get("STARTNODE");
            }
            if (endNode.equals("")) {
                endNode = db.getEdge(edgeID).get("ENDNODE");
            }

            if (db.edgeExists(edgeID)) {
                Map<String, String> edge = new HashMap<String, String>();
                edge.put("EDGEID", edgeID);
                edge.put("STARTNODE", startNode);
                edge.put("ENDNODE", endNode);

                drawFloor(FLOOR);
                db.editEdge(edgeID, edge);
                submissionlabel.setText("You have edited " + edgeID);
            } else {
                submissionlabel.setText("Edge does not exist");
            }
        } catch (SQLException sq) {
            sq.printStackTrace();
        }
        return;

    }

    /**
     * Facilitates changing the edge map based on the current state of the program
     */
    @FXML
    public void submitfunction() throws SQLException {
        switch (state) {
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
        clearfields();
        contextMenu.getItems().get(contextMenu.getItems().size() - 1).fire();
        initialize();
        changeFloor("1");
        prevSelectedEdge = null;
        prevSelectedNodeEnd = null;
        prevSelectedNodeStart = null;

    }

    /**
     * Highlight the edge in edge drop down with color gold
     */
    @FXML
    public void edgeEditHighLight() throws SQLException {
        if (edgeDropdown.getSelectionModel().getSelectedItem() != null) {
            Map<String, String> edge = db.getEdge(edgeDropdown.getSelectionModel().getSelectedItem().toString()); //get the edge from selection
            String floor = db.getNode(edge.get("STARTNODE")).get("FLOOR"); //get the floor of the startint node
            changeFloor(floor); //draw the floor of the edge should exit on
            if (prevSelectedNodeStart != null) {
                if (prevSelectedNodeStart.get("FLOOR").equals(floor)) {
                    drawSingleNodeHighLight(prevSelectedNodeStart, Color.PURPLE);
                }
            }
            if (prevSelectedNodeEnd != null) {
                if (prevSelectedNodeEnd.get("FLOOR").equals(floor)) {
                    drawSingleNodeHighLight(prevSelectedNodeEnd, Color.PURPLE);
                }
            }           //change floor will erase the previous selected nodes and edges, draw the previous selected back to the canvas
            if (prevSelectedEdge != null) {
                drawTwoNodesWithEdge(db.getNode(prevSelectedEdge.get("STARTNODE")), db.getNode(prevSelectedEdge.get("ENDNODE")), Color.BLUE, Color.BLUE, Color.BLACK);
            }
            prevSelectedEdge = edge;
            drawTwoNodesWithEdge(db.getNode(edge.get("STARTNODE")), db.getNode(edge.get("ENDNODE")), Color.GOLD, Color.GOLD, Color.GOLD); // highlight the currently selected edge/nodes
        }
    }
    /**
     * Highlight the nodes in startNode drop down with color purple
     */
    @FXML
    public void startNodeHighLight() throws SQLException {
        if (startNodeDropdown.getSelectionModel().getSelectedItem() != null) {
            Map<String, String> node = db.getNode(startNodeDropdown.getSelectionModel().getSelectedItem().toString());//get the node from selection
            String floor = node.get("FLOOR"); //get floor
            changeFloor(floor); //draw the floor the node should be at
            if (prevSelectedNodeEnd != null) {
                if (prevSelectedNodeEnd.get("FLOOR").equals(floor)) {
                    drawSingleNodeHighLight(prevSelectedNodeEnd, Color.PURPLE);
                }
            }
            if (prevSelectedEdge != null) {
                if (db.getNode(prevSelectedEdge.get("STARTNODE")).get("FLOOR").equals(floor)) {
                    drawTwoNodesWithEdge(db.getNode(prevSelectedEdge.get("STARTNODE")), db.getNode(prevSelectedEdge.get("ENDNODE")), Color.GOLD, Color.GOLD, Color.GOLD);
                }
            }  //change floor will erase the previous selected nodes and edges, draw the previous selected back to the canvas
            if (prevSelectedNodeStart != null) {
                drawSingleNode(prevSelectedNodeStart, Color.BLUE);
            } //if there is previously selected, change it back
            prevSelectedNodeStart = node;
            drawSingleNodeHighLight(node, Color.PURPLE); // highlight the currently selected edge/nodes
        }
    }
    /**
     * Highlight the nodes in endNode drop down with color purple
     */
    @FXML
    public void endNodeHighLight() throws SQLException {
        if (startNodeDropdown.getSelectionModel().getSelectedItem() != null) {
            Map<String, String> node = db.getNode(endNodeDropdown.getSelectionModel().getSelectedItem().toString());
            String floor = node.get("FLOOR");
            changeFloor(floor);
            if (prevSelectedNodeStart != null) {
                if (prevSelectedNodeStart.get("FLOOR").equals(floor)) {
                    drawSingleNodeHighLight(prevSelectedNodeStart, Color.PURPLE);
                }
            }
            if (prevSelectedEdge != null) {
                if (db.getNode(prevSelectedEdge.get("STARTNODE")).get("FLOOR").equals(floor)) {
                    drawTwoNodesWithEdge(db.getNode(prevSelectedEdge.get("STARTNODE")), db.getNode(prevSelectedEdge.get("ENDNODE")), Color.GOLD, Color.GOLD, Color.GOLD);
                }
            }
            if (prevSelectedNodeEnd != null) {
                drawSingleNode(prevSelectedNodeEnd, Color.BLUE);
            }
            prevSelectedNodeEnd = node;
            drawSingleNodeHighLight(node, Color.PURPLE);
        }
    }
}


//    public void changeFloorNodes(){
//        edgeGridAnchor.getChildren().clear();
//        int count = 0;
//        try {
//            List<Map<String, String>> edges = db.getEdges();
//            List<String> nodesList = new ArrayList<>();
//            for (Map<String, String> edge : edges) {
//                String startNode = edge.get("STARTNODE");
//                String endNode = edge.get("ENDNODE");
//                String bothNodes = startNode.concat(endNode);
//                if (!nodesList.contains(bothNodes) || (!nodesList.contains(endNode.concat(startNode)))) { //??
//                    try {
//                        Map<String, String> snode = db.getNode(startNode);
//                        Map<String, String> enode = db.getNode(endNode);
//
//                        if (floor1.isVisible() && (
//                                ( snode.get("FLOOR").equals("1")) && enode.get("FLOOR").equals("1")) ){
//                            drawNodes(snode,enode);
//                            nodesList.add(startNode + endNode);
//                            count++;
//                        }else if (groundFloor.isVisible() && (
//                                ( snode.get("FLOOR").equals("G")) && enode.get("FLOOR").equals("G")) ){
//                            drawNodes(snode,enode);
//                            nodesList.add(startNode + endNode);
//                            count++;
//
//                        }
//
//                    } catch (SQLException sq) {
//                        sq.printStackTrace();
//                    }
//                }
//            }
//        }catch (SQLException sq) {
//            sq.printStackTrace();
//        } System.out.println(count);
//    }

//    public void drawNodes(Map<String, String> snode, Map<String,String> enode) {
//        Circle circ1 = new Circle();
//        Circle circ2 = new Circle();
//
//        Double startX = xScale((int) Double.parseDouble(snode.get("XCOORD")));
//        Double startY = yScale((int) Double.parseDouble(snode.get("YCOORD")));
//        Double endX = xScale((int) Double.parseDouble(enode.get("XCOORD")));
//        Double endY = yScale((int) Double.parseDouble(enode.get("YCOORD")));
//
//        circ1.setCenterX(startX);
//        circ1.setCenterY(startY);
//        circ2.setCenterX(endX);
//        circ2.setCenterY(endY);
//        circ1.setRadius(1);
//        circ2.setRadius(1);
//        circ1.setFill(Color.RED);
//        circ2.setFill(Color.RED);
//
//        Line line = new Line();
//        line.setStartX(startX);
//        line.setStartY(startY);
//        line.setEndX(endX);
//        line.setEndY(endY);
//        line.setStroke(Color.RED);
//        nodeGridAnchor.getChildren().addAll(circ1, circ2, line);
//
//    }


