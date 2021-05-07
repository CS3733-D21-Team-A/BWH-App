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
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        MenuItem newNode = new MenuItem(("New Node Here"));
        MenuItem alignVertical = new MenuItem(("Align Vertical"));
        MenuItem alignHorizontal = new MenuItem(("Align Horizontal"));
        MenuItem deselect = new MenuItem(("Deselect Nodes"));
        MenuItem addAnchorPoint = new MenuItem(("Add Anchor Point"));
        MenuItem alignSlope = new MenuItem("Align w/ Anchors 1 and 2");
        MenuItem makeEdge = new MenuItem("Make edge between selection");

        newNode.setOnAction((ActionEvent e) -> {
            state = "New";
            currentID = "";
            nodePopUp();
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
            drawSingleNode( Integer.parseInt(selectedNode.get("XCOORD")), Integer.parseInt(selectedNode.get("YCOORD")), selectedNode.get("NODEID"), Color.PURPLE);

        });

        deselect.setOnAction((ActionEvent e) -> {
            alignHorizontal.setVisible(false);
            alignVertical.setVisible(false);
            alignSlope.setVisible(false);
            selectedNodesList.clear();
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
        makeEdge.setOnAction((ActionEvent e) -> {

        });
        alignHorizontal.setVisible(false);
        alignVertical.setVisible(false);
        alignSlope.setVisible(false);

        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(newNode, addAnchorPoint, alignVertical, alignHorizontal, alignSlope, deselect);

        mapView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

            public void handle(ContextMenuEvent event) {
                contextMenu.show(mapView, event.getScreenX(), event.getScreenY());
                contextMenuX = event.getX();
                contextMenuY = event.getY();
                try {
                    if (getNearestNode(contextMenuX, contextMenuY) == null) {
                        addAnchorPoint.setVisible(false);
                    }
                    else {
                        addAnchorPoint.setVisible(true);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });



//        mapView.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->{
//            if(!event.isControlDown() || selectedNodesList.isEmpty()) return;
//            Map<String, String> node = selectedNodesList.get(0);
//            String nodeID = node.get("NODEID");
//            int index = mapView.getChildren().indexOf(nodesOnImage.get(nodeID));
//            if(index != -1){
//                Circle c = new Circle(event.getX(), event.getY(), 3, Color.LIGHTCORAL);
//                mapView.getChildren().set(index, c);
//                nodesOnImage.put(nodeID, c);
//                try {
//                    node.put("XCOORD", String.valueOf((int) ((5000/mapImage.getFitWidth()) * event.getX())));
//                    node.put("YCOORD", String.valueOf((int) ((3400/mapImage.getFitHeight()) * event.getY())));
//                    db.editNode(nodeID,node);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }


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

    @Override
    public void drawFloor(String floor) throws SQLException{
        super.drawFloor(floor);
        if(contextMenu.getItems().size() != 0) {
            ObservableList<MenuItem> items = contextMenu.getItems();
            items.get(items.size()-1).fire(); // targets deselect
        }
        drawEdges(Color.BLACK);
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
        drawSingleNode(Double.parseDouble(anchorPoint.get("XCOORD")), Double.parseDouble(anchorPoint.get("YCOORD")), anchorPoint.get("NODEID"), Color.RED);
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