
package edu.wpi.aquamarine_axolotls.views.mapping;

import com.jfoenix.controls.*;
import edu.wpi.aquamarine_axolotls.db.CSVHandler;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
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

    @FXML
    private TableView table;
    @FXML
    private TableColumn edgeIdCol;
    @FXML
    private TableColumn startNodeCol;
    @FXML
    private TableColumn endNodeCol;

    List<Edge> validEdges = new ArrayList<>();

    String state = "";

    DatabaseController db;
    CSVHandler csvHandler;

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

        if (algo.contains("AStar")) algoSelectBox.getSelectionModel().select(0);
        else if (algo.contains("Dijkstra")) algoSelectBox.getSelectionModel().select(1);
        else if (algo.contains("BreadthFirstSearch")) algoSelectBox.getSelectionModel().select(2);
        else if (algo.contains("DepthFirstSearch")) algoSelectBox.getSelectionModel().select(3);
        else if (algo.contains("BestFirstSearch")) algoSelectBox.getSelectionModel().select(4);

        table.setEditable(false);
        table.getItems().clear();

        ObservableList<String> edgeOptions = FXCollections.observableArrayList();
        ObservableList<String> nodeOptions = FXCollections.observableArrayList();

        submissionlabel.setVisible(true);

        edgeIdCol.setCellValueFactory(new PropertyValueFactory<Edge, String>("edgeID"));
        startNodeCol.setCellValueFactory(new PropertyValueFactory<Edge, String>("startNode"));
        endNodeCol.setCellValueFactory(new PropertyValueFactory<Edge, String>("endNode"));

        edgeDropdown.setVisible(false);
        edgeIDtextbox.setVisible(false);
        startNodeDropdown.setVisible(false);
        endNodeDropdown.setVisible(false);
        submissionButton.setVisible(false);

        try {
            db = new DatabaseController();
            csvHandler = new CSVHandler(db);
            List<Map<String, String>> edges = db.getEdges();
            List<Map<String, String>> nodes = db.getNodes();

            for (Map<String, String> edge : edges) {
                edgeOptions.add(edge.get("EDGEID"));
                Edge cur = new Edge(edge.get("EDGEID"),
                        edge.get("STARTNODE"),
                        edge.get("ENDNODE"));

                table.getItems().add(cur);
                validEdges.add(cur);
                drawTwoNodesWithEdge(db.getNode(edge.get("STARTNODE")), db.getNode(edge.get("ENDNODE")),Color.BLUE , Color.BLUE , Color.BLACK);
            }
            for (Map<String, String> node : nodes) nodeOptions.add(node.get("NODEID"));

            startNodeDropdown.setItems(nodeOptions);
            endNodeDropdown.setItems(nodeOptions);
            edgeDropdown.setItems(edgeOptions);
            floors = new HashMap<>();
        } catch (SQLException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }

//        table.setEditable(false);
//        table.getItems().clear();
//
//        ObservableList<String> edgeOptions = FXCollections.observableArrayList();               // making dropdown options
//        ObservableList<String> nodeOptions = FXCollections.observableArrayList();
//        submissionlabel.setVisible(true);
//        anchor.setVisible(false);
//        //groundFloor.setVisible(true);
//        //floor1.setVisible(false);
//
//        edgeIdCol.setCellValueFactory(new PropertyValueFactory<Edge,String>("edgeID"));         // setting data to table
//        startNodeCol.setCellValueFactory(new PropertyValueFactory<Edge,String>("startNode"));
//        endNodeCol.setCellValueFactory(new PropertyValueFactory<Edge,String>("endNode"));
//
//        edgeDropdown.setVisible(false);         //making fields invisible
//        edgeIDtextbox.setVisible(false);
//        startNodeDropdown.setVisible(false);
//        endNodeDropdown.setVisible(false);
//
//        try {
//            db = new DatabaseController();
//            csvHandler = new CSVHandler(db);
//            List<Map<String, String>> edges = db.getEdges();
//            List<Map<String, String>> nodes = db.getNodes();
//            for (Map<String, String> edge : edges) {
//                edgeOptions.add(edge.get("EDGEID"));    //getting edge options
//                table.getItems().add(new Edge(edge.get("EDGEID"), edge.get("STARTNODE"), edge.get("ENDNODE")));
//            }
//            for (Map<String, String> node : nodes) {
//                nodeOptions.add(node.get("NODEID"));
//            }
//            edgeDropdown.setItems(edgeOptions);
//            startNodeDropdown.setItems(nodeOptions);
//            endNodeDropdown.setItems(nodeOptions);
//            changeFloorNodes();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
    }


/*    private void drawSingleEdge(Edge edge) {
        Line line = new Line();
        try {
            Map<String, String> startNode = db.getNode(edge.getStartNode());
            Map<String, String> endNode = db.getNode(edge.getEndNode());
            if(startNode.get("FLOOR").equals(FLOOR) && endNode.get("FLOOR").equals(FLOOR)){// TODO: set up triangle floor thing
                line.setStartX(Integer.parseInt(startNode.get("XCOORD")));
                line.setStartY(Integer.parseInt(startNode.get("YCOORD")));
                line.setEndX(Integer.parseInt(endNode.get("XCOORD")));
                line.setEndY(Integer.parseInt(endNode.get("YCOORD")));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        line.setStroke(Color.RED);
    }*/

/*    private void drawSingleEdge(Map<String, String> edge) {
        Line line = new Line();
        try {
            Map<String, String> startNode = db.getNode(edge.get("STARTNODE"));
            Map<String, String> endNode = db.getNode(edge.get("ENDNODE"));
            if(startNode.get("FLOOR").equals(FLOOR) && endNode.get("FLOOR").equals(FLOOR)){// TODO: set up triangle floor thing
                GraphicsContext gc = mapCanvas.getGraphicsContext2D();
                gc.strokeLine(xScale(Integer.parseInt(startNode.get("XCOORD"))), yScale(Integer.parseInt(startNode.get("YCOORD"))),
                        xScale(Integer.parseInt(endNode.get("XCOORD"))), yScale(Integer.parseInt(endNode.get("YCOORD"))));
                drawSingleNode(startNode);
                drawSingleNode(endNode);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        line.setStroke(Color.RED);
    }*/
/*
    public void drawSingleNode(Map<String, String> node) {
        double x = xScale(Integer.parseInt(node.get("XCOORD")));
        double y = yScale(Integer.parseInt(node.get("YCOORD")));
        double radius = 3;
        x = x - (radius / 2);
        y = y - (radius / 2);
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLUE);
        gc.fillOval(x, y, radius, radius);
    }*/

    /**
     * Button press that sets unused fields to be hidden, and needed fields to be visible. Also sets
     * state so that the desired add action will happen.
     */
    public void pressAddButton() {
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
    public void pressEditButton() {
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
    public void pressDeleteButton() {
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
        if (algoSelectBox.getSelectionModel() != null && algoSelectBox.getSelectionModel() != null) {
            if (algoSelectBox.getSelectionModel().getSelectedItem().equals("A Star")) {
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
            } else if (algoSelectBox.getSelectionModel().getSelectedItem().equals("Dijkstra")) {
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new Dijkstra());
            } else if (algoSelectBox.getSelectionModel().getSelectedItem().equals("Breadth First")) {
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new BreadthFirstSearch());
            } else if (algoSelectBox.getSelectionModel().getSelectedItem().equals("Depth First")) {
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new DepthFirstSearch());
            }else if (algoSelectBox.getSelectionModel().getSelectedItem().equals("Best First")) {
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
    public void submitfunction() {
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
        initialize();
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

