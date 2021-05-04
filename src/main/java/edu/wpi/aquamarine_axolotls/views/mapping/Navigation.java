package edu.wpi.aquamarine_axolotls.views.mapping;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.aquamarine_axolotls.pathplanning.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sun.font.TextLabel;
import javafx.scene.shape.Polygon;

import javax.naming.Context;
import java.sql.SQLException;
import java.util.*;

public class Navigation extends GenericMap {

    @FXML private Label startLabel;
    @FXML private Label endLabel;
    @FXML private JFXButton findPathButton;
    @FXML private JFXButton cancelPath;
    @FXML private Label etaLabel;
    @FXML private Label curDirection;
    @FXML private ImageView arrow;
    @FXML private VBox stepByStep;
    @FXML private VBox listDirVBox;
    @FXML private VBox listOfDirections;
    @FXML private Group textDirectionsGroup;
    @FXML private TreeTableView<String> treeTable;

    ObservableList<String> options = FXCollections.observableArrayList();
    private int firstNodeSelect = 0;
    private String firstNode;
    private List<String> stopList = new ArrayList<>();
    private List<Node> currPath = new ArrayList<>();
    private int activePath = 0;
    private List<List<String>> currPathDir = new ArrayList<>();
    static int dirIndex = 0;
    private List<Map<String,String>> intermediatePoints = new ArrayList<>();
    private Map<String,String> endPoint;

    @FXML
    public void initialize() throws SQLException {

        startUp();
        if(SearchAlgorithmContext.getSearchAlgorithmContext().context == null) {
            SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
        }

        TreeItem<String> park = new TreeItem<>("Parking Spots");
        TreeItem<String> rest = new TreeItem<>("Restrooms");
        TreeItem<String> stai = new TreeItem<>("Stairs");
        TreeItem<String> dept = new TreeItem<>("Departments");
        TreeItem<String> labs = new TreeItem<>("Laboratories");
        TreeItem<String> info = new TreeItem<>("Help Desks");
        TreeItem<String> conf = new TreeItem<>("Conference Rooms");
        TreeItem<String> exit = new TreeItem<>("Entrances");
        TreeItem<String> retl = new TreeItem<>("Non Medical Commercial Areas");
        TreeItem<String> serv = new TreeItem<>("Non Medical Services");

        for (Map<String, String> node: db.getNodes()) { // TODO : make db method to get nodes that arent hall/walk
            if(!(node.get("NODETYPE").equals("HALL") || node.get("NODETYPE").equals("WALK"))){
                options.add(node.get("LONGNAME"));
                String nodeType = node.get("NODETYPE");
                String longName = node.get("LONGNAME");
                switch (nodeType){
                    case "PARK":
                        park.getChildren().add(new TreeItem<>(longName));
                        break;
                    case "REST":
                        rest.getChildren().add(new TreeItem<>(longName));
                        break;
                    case "STAI":
                        stai.getChildren().add(new TreeItem<>(longName));
                        break;
                    case "DEPT":
                        dept.getChildren().add(new TreeItem<>(longName));
                        break;
                    case "LABS":
                        labs.getChildren().add(new TreeItem<>(longName));
                        break;
                    case "INFO":
                        info.getChildren().add(new TreeItem<>(longName));
                        break;
                    case "CONF":
                        conf.getChildren().add(new TreeItem<>(longName));
                        break;
                    case "EXIT":
                        exit.getChildren().add(new TreeItem<>(longName));
                        break;
                    case "RETL":
                        retl.getChildren().add(new TreeItem<>(longName));
                        break;
                    case "SERV":
                        serv.getChildren().add(new TreeItem<>(longName));
                        break;
                }
            }
        }
        TreeItem<String> root = new TreeItem<>("Hello");
        //root.setExpanded(true);
        root.getChildren().addAll(park, rest, stai, dept, labs, info, conf, exit, retl, serv);
        TreeTableColumn<String, String> treeTableColumn1 = new TreeTableColumn<>("Locations");
        treeTableColumn1.setCellValueFactory((TreeTableColumn.CellDataFeatures<String, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue()));
        treeTable.setRoot(root);
        treeTable.setShowRoot(false);
        treeTable.getColumns().add(treeTableColumn1);

        drawNodesAndFloor("1", Color.BLUE);

        stepByStep.setVisible(false);
        listDirVBox.setVisible(false);
        listDirVBox.toFront();
        treeTable.setVisible(true);
        treeTable.toFront();

        MenuItem item1 = new MenuItem(("Add Stop"));
        MenuItem item2 = new MenuItem(("Add to Favorites"));

        treeTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    TreeItem<String> selectedFromTreeView = treeTable.getSelectionModel().getSelectedItem();
                    if (selectedFromTreeView.getChildren().isEmpty()) {
                        System.out.println(selectedFromTreeView.getValue());

                        if (selectedFromTreeView != null) {
                            if (firstNodeSelect == 0) {
                                stopList.add(selectedFromTreeView.getValue());
                                startLabel.setText(stopList.get(0));
                                firstNodeSelect = 1;
                            }
                            else if (firstNodeSelect == 1) {
                                stopList.add(selectedFromTreeView.getValue());
                                endLabel.setText(stopList.get(1));
                                firstNodeSelect = 0;
                                try {
                                    findPathSingleSegment(stopList.get(0), stopList.get(1));
                                    drawPath(FLOOR);
                                }
                                catch(SQLException se) { //oh FINE we'll handle the damn exception
                                    se.printStackTrace(); //guess we can't keep throwing it forever
                                }
                            }
                        }
                    }
                }
            }
        });

        item1.setOnAction((ActionEvent e)->{
            try {
                addDestination(contextMenuX, contextMenuY);
            }
            catch(SQLException se) {
                se.printStackTrace();
            }
        });
        item2.setOnAction((ActionEvent e)->{

        });
        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(item1,item2);


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
            }
        });

    }

    public void changeFloor(String floor) throws SQLException{
        drawFloor(floor);
        if(activePath == 1) {
            drawPath(floor);
            for (Map<String, String> intermediatePointToDraw : intermediatePoints) {
                drawSingleNodeHighLight(intermediatePointToDraw, Color.ORANGE);
            }
            drawSingleNodeHighLight(intermediatePoints.get(intermediatePoints.size()-1),Color.MAGENTA);
        }
        else drawNodes(Color.BLUE);
    }

    /**
     * Clears out everything relating to navigation, including the stop list and path, and redraws the current floor
     */
    public void clearNav() throws SQLException{
        stopList.clear();
        currPath.clear();
        currPathDir.clear();
        intermediatePoints.clear();
        activePath = 0;
        etaLabel.setText("");
        startLabel.setText("");
        endLabel.setText("");
        drawNodesAndFloor(FLOOR, Color.BLUE);
        startLabel.setText("");
        endLabel.setText("");

        listDirVBox.setVisible(false);
        treeTable.setVisible(true);
        treeTable.toFront();
    }


    /**
     * Takes the selections from the start and end dropdowns and uses them to find the full path from start to finish
     */
    public void findPath() throws SQLException{
        currPath.clear();
        stopList.clear();
        intermediatePoints.clear();

        activePath = 0;
        if (startLabel.getText().equals("") || endLabel.getText().equals("")) {
            return;
        }
        String start = startLocation.getSelectionModel().getSelectedItem().toString();
        String end = destination.getSelectionModel().getSelectedItem().toString();
        stopList.add(start);
        stopList.add(end);
        drawNodesAndFloor(db.getNodesByValue("LONGNAME", start).get(0).get("FLOOR"), Color.BLUE); // TODO : this is weird
        findPathSingleSegment(start, end);
        drawPath(FLOOR);
        intermediatePoints.add(db.getNodesByValue("LONGNAME",end).get(0));
        //drawFloor(FLOOR); // do we need this?
    }


    // draw path method
    public void drawPath(String floor) throws SQLException{
        if(currPath.isEmpty()) return;
        FLOOR = floor;
        drawNodesAndFloor(FLOOR, new Color(0.0 , 0.0, 1.0, 0.4));
        for (int i = 0; i < currPath.size() - 1; i++) {
            if (currPath.get(i).getFloor().equals(FLOOR) &&
                    currPath.get(i + 1).getFloor().equals(FLOOR)) {
                drawTwoNodesWithEdge(currPath.get(i), currPath.get(i + 1), Color.BLUE, Color.BLUE, Color.BLACK);
            }
            if (!(currPath.get(i).getFloor().equals(currPath.get(i+1).getFloor()))){
                drawArrow(currPath.get(i), currPath.get(i+1));
            }
            drawSingleNodeHighLight(currPath.get(0),Color.GREEN);
            drawSingleNodeHighLight(currPath.get(currPath.size()-1),Color.MAGENTA);
        }
    }
    // draw floor that makes everything transparent


    /**
     * Uses the A-star algorithm to find the shortest path between a given start and end node
     * @param start String, long name of start node
     * @param end   String, long name of end node
     */
    public void findPathSingleSegment(String start, String end) throws SQLException {
        double etaTotal, minutes, seconds;

        currPath.addAll(SearchAlgorithmContext.getSearchAlgorithmContext().getPath(start, end));
        System.out.println(SearchAlgorithmContext.getSearchAlgorithmContext().context);

        etaTotal = SearchAlgorithmContext.getSearchAlgorithmContext().getETA(currPath);
        minutes = Math.floor(etaTotal);
        seconds = Math.floor((etaTotal - minutes) * 60);
        etaLabel.setText((int) minutes + " min " + (int) seconds + " sec");

        if (currPath.isEmpty()) return;

        firstNodeSelect = 0;
        activePath = 1;
        List<Node> toRemove = new ArrayList<>();
        for(Node n : currPath){ // TODO : move this to
            if(SearchAlgorithmContext.getSearchAlgorithmContext().nodeIsUnimportant(currPath, n)) toRemove.add(n);
        }
        currPath.removeAll(toRemove);
        //drawFloor(FLOOR);
        //drawSingleNode(getNodeFromValid(stopList.get(stopList.size() - 1)));
        currPathDir.clear();
        currPathDir = SearchAlgorithmContext.getSearchAlgorithmContext().getTextDirections(currPath);
//        List<String> textDir = new ArrayList<String>();
//        textDir.add("left 1");
//        textDir.add("left 2");
//         = textDir;
        initializeDirections();
    }

    /**
     * Cancels the current set of text directions
     */
    public void cancelDir() throws SQLException {
        stepByStep.setVisible(false);
        listDirVBox.setVisible(true);
        treeTable.setVisible(false);
        listDirVBox.toFront();

        startLocation.setDisable(false);
        destination.setDisable(false);
        findPathButton.setDisable(false);
        cancelPath.setDisable(false);

        //unHighlightDirection();
    }

    /**
     * Starts the text directions once they're initialized
     */
    public void startDir() throws SQLException{
        stepByStep.setVisible(true);
        listDirVBox.setVisible(false);
        treeTable.setVisible(false);
        stepByStep.toFront();

        startLocation.setDisable(true);
        destination.setDisable(true);
        findPathButton.setDisable(true);
        cancelPath.setDisable(true);

        dirIndex = 0;
        changeArrow(currPathDir.get(0).get(dirIndex));
        String nodeID = currPathDir.get(1).get(0);
        if(currPathDir.get(1).get(0).contains(",")) nodeID = nodeID.substring(0, currPathDir.get(1).get(0).indexOf(","));
        changeFloor(db.getNode(nodeID).get("FLOOR"));
        curDirection.setText(currPathDir.get(0).get(dirIndex)); //get first direction
        highlightDirection();
    }

    /**
     * Progresses to the next step in the text directions
     */
    public void progress() throws SQLException {
        if (dirIndex < currPathDir.get(0).size() - 1){
            unHighlightDirection();
            dirIndex += 1;
            String curNode = currPathDir.get(1).get(dirIndex);
            String curFloor = getInstructionsFloor(curNode);

            if(!curFloor.equals(FLOOR)) drawPath(curFloor);

            changeArrow(currPathDir.get(0).get(dirIndex));
            curDirection.setText(currPathDir.get(0).get(dirIndex)); //get next direction
            highlightDirection();
        }
    }

    /**
     * Moves back to the previous step in the text directions
     */
    public void regress() throws SQLException{
        if (dirIndex == 0){
            return;
        }else{
            unHighlightDirection();
            dirIndex -= 1;
            String curNode = currPathDir.get(1).get(dirIndex);
            String curFloor = getInstructionsFloor(curNode);
            if(!curFloor.equals(FLOOR)){
                drawPath(curFloor);
            }
            changeArrow(currPathDir.get(0).get(dirIndex));
            curDirection.setText(currPathDir.get(0).get(dirIndex));
            highlightDirection();
        }
    }

    /**
     * Extracts the floor from the provided node or nodes corresponding to a text direction.
     * @param nodeInstruction The node or nodes passed in are of the form NODEID or NODEID,NODEID where the second represents the
     * action of traveling between two nodes.
     * @return A floor that corresponds to the map
     */
    private String getInstructionsFloor(String nodeInstruction){
        String curFloor = null;
        if (nodeInstruction.contains(",")) {
            int index = nodeInstruction.indexOf(",");
            try {
                curFloor = db.getNode(nodeInstruction.substring(0, index)).get("FLOOR");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                curFloor = db.getNode(nodeInstruction).get("FLOOR");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return curFloor;
    }

    /**
     * Changes the icon for the current text direction
     * @param direction The current text direction, used to determine what icon is needed
     */
    public void changeArrow(String direction){ //update arrow
        Image arrowImg;
        if (direction.contains("left")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/leftArrow.png");
        else if (direction.contains("right")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/rightArrow.png");
        else if (direction.contains("elevator")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/elevator.png");
        else if (direction.contains("stairs")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/stairs.png");
        else if (direction.contains("Turn around")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/turn around.png");
        else if (direction.contains("You have arrived at your destination.")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/arrived.png");
        else arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/straight.png");

        arrow.setImage(arrowImg);
    }

    /**
     * Adds all of the text directions to the visual list of directions on the screen
     */
    public void initializeDirections() throws SQLException { // adds each step to the list of direction
        cancelDir();
        listOfDirections.getChildren().clear();
        for (int i = 0; i < currPathDir.get(0).size(); i++) {

            ImageView arrowImage = new ImageView();     //fitHeight="69.0" fitWidth="73.0"
            Image img;
            String direction = currPathDir.get(0).get(i);

            arrowImage.setFitWidth(50.0);
            arrowImage.setFitHeight(50.0);

            if (direction.contains("left")) img = new Image("/edu/wpi/aquamarine_axolotls/img/leftArrow.png");
            else if (direction.contains("right")) img = new Image("/edu/wpi/aquamarine_axolotls/img/rightArrow.png");
            else if (direction.contains("elevator")) img = new Image("/edu/wpi/aquamarine_axolotls/img/elevator.png");
            else if (direction.contains("stairs")) img = new Image("/edu/wpi/aquamarine_axolotls/img/stairs.png");
            else if (direction.contains("Turn around")) img = new Image("/edu/wpi/aquamarine_axolotls/img/turn around.png");
            else if (direction.contains("You have arrived at your destination.")) img = new Image("/edu/wpi/aquamarine_axolotls/img/arrived.png");
            else img = new Image("/edu/wpi/aquamarine_axolotls/img/straight.png");

            arrowImage.setImage(img);

            Label l = new Label(direction);
            l.setWrapText(true);

            HBox hbox = new HBox(10, arrowImage, l);
            hbox.setAlignment(Pos.CENTER_LEFT);

            listOfDirections.getChildren().add(hbox);
        }
    }

    /**
     * Highlights the current portion of the map that the current direction is on
     */
    public void highlightDirection() throws SQLException{
        String curNode = currPathDir.get(1).get(dirIndex);
        // draws an edge on map
        if (curNode.contains(",")) {
            int index = curNode.indexOf(",");
            Map<String, String> start = db.getNode(curNode.substring(0,index));
            //System.out.println(start);
            Map<String, String> end = db.getNode(curNode.substring(index+1));
            //System.out.println(end);
            drawTwoNodesWithEdge(start, end, Color.RED, Color.BLUE, Color.RED );
            //drawSingleEdge(getNodeFromValidID(start), getNodeFromValidID(end), Color.RED);

            double X1 = xScale(Integer.parseInt(start.get("XCOORD")));
            double Y1 = yScale(Integer.parseInt(start.get("YCOORD")));
            double X2 = xScale(Integer.parseInt(end.get("XCOORD")));
            double Y2 = yScale(Integer.parseInt(end.get("YCOORD")));

            double centerX = (X1 + X2) / 2.0;
            double centerY = (Y1 + Y2) / 2.0;

            double rotationAngle = Math.atan2(Y2-Y1, X2-X1) * 180 / Math.PI + 90.0;

            if(start.get("FLOOR").equals(end.get("FLOOR"))){
                drawArrow(centerX, centerY + 1, start.get("FLOOR"), rotationAngle);
            } else {
                removeDirectionArrow();
            }

        } else {
            Map<String, String> node = db.getNode(curNode);
            drawSingleNode(node, Color.RED);
            if (dirIndex == currPathDir.get(1).size() - 1){
                double X1 = xScale(Integer.parseInt(node.get("XCOORD")));
                double Y1 = yScale(Integer.parseInt(node.get("YCOORD")));

                drawArrow(X1, Y1, node.get("FLOOR"), 0);
            }
            if (dirIndex != currPathDir.get(1).size() - 1){
                String nextNodes = currPathDir.get(1).get(dirIndex + 1);
                String nextNodeID = nextNodes.substring(nextNodes.indexOf(",")+1);
                Map<String, String> nextNode = db.getNode(nextNodeID);

                double X1 = xScale(Integer.parseInt(node.get("XCOORD")));
                double Y1 = yScale(Integer.parseInt(node.get("YCOORD")));
                double X2 = xScale(Integer.parseInt(nextNode.get("XCOORD")));
                double Y2 = yScale(Integer.parseInt(nextNode.get("YCOORD")));


                double rotationAngle = Math.atan2(Y2-Y1, X2-X1) * 180 / Math.PI + 90.0;

                if(node.get("FLOOR").equals(nextNode.get("FLOOR"))){
                    drawArrow(X1, Y1, node.get("FLOOR"), rotationAngle);
                }else {
                    removeDirectionArrow();
                }
            }
        }
    }

    /**
     * Removes the highlight from the current part of the map
     */
    public void unHighlightDirection() throws SQLException{
        String curNode = currPathDir.get(1).get(dirIndex);
        if (curNode.contains(",")){
            int index = curNode.indexOf(",");
            Map<String, String> start = db.getNode(curNode.substring(0,index));
            Map<String, String> end = db.getNode(curNode.substring(index+1));
            drawTwoNodesWithEdge(start, end, Color.BLUE, Color.BLUE, Color.BLACK );
        }
    }

    /**
     * Gets the current closest node to the mouse and uses it to navigate
     * If there's no active path, this function will define a new one -- otherwise, it will add more stops
     */
    public void addDestination(double x, double y) throws SQLException{

        //if(event.getButton().equals(MouseButton.PRIMARY)) {
        Map<String, String> newDestination = getNearestNode(x, y);

        if (newDestination == null) return;

        else {
            String currCloseName = newDestination.get("LONGNAME");

            if (activePath == 0) { //if there's no active path, we'll handle that
                if ( firstNodeSelect == 0 ) {
                    firstNode = currCloseName;
                    firstNodeSelect = 1;
                    drawSingleNodeHighLight(newDestination,Color.GREEN);
                }
                else if ( firstNodeSelect == 1 ) {
                    stopList.clear ( );
                    stopList.add ( firstNode );
                    stopList.add ( currCloseName );
                    currPath.clear ( );
                    findPathSingleSegment ( stopList.get ( 0 ) ,stopList.get ( 1 ) );
                    drawPath ( FLOOR );
                    intermediatePoints.add(newDestination);
                    drawSingleNodeHighLight(newDestination,Color.MAGENTA);
                }
            }
            else if (activePath == 1) {
                stopList.add(stopList.size(), currCloseName);
                currPath.clear();
                for (int i = 0; i < stopList.size() - 1; i++) {
                    findPathSingleSegment(stopList.get(i), stopList.get(i + 1));
                }
                drawPath(FLOOR);
                intermediatePoints.add(newDestination); // store the intermediate points, not erased when drawing new intermediate path
                for (Map<String,String> intermediatePointToDraw : intermediatePoints){
                    drawSingleNodeHighLight(intermediatePointToDraw,Color.ORANGE);
                }
                drawSingleNodeHighLight(newDestination,Color.MAGENTA);
                //drawSingleNodeHighLight(endPoint,Color.MAGENTA);
            }
        }
    }
}


