package edu.wpi.aquamarine_axolotls.views.mapping;

import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GenericMap extends GenericPage {
    static final Color darkBlue = Color.web("#003DA6");
    static final Color lightBlue = Color.web("#7D99C9");
    static final Color lightGray = Color.web("#F0F0F0");
    static final Color darkGray = Color.web("#646464");
    static final Color yellow = Color.web("#F4BA47");
    static Map<String, String> floors = new HashMap<String,String>() {{
        put("L2", "edu/wpi/aquamarine_axolotls/img/lowerLevel2.png");
        put("L1", "edu/wpi/aquamarine_axolotls/img/lowerLevel1.png");
        put("1", "edu/wpi/aquamarine_axolotls/img/firstFloor.png");
        put("2", "edu/wpi/aquamarine_axolotls/img/secondFloor.png");
        put("3", "edu/wpi/aquamarine_axolotls/img/thirdFloor.png");
    }};


    // valid nodes list
    //canvas stuff
    @FXML
    ImageView mapImage;
    @FXML
    Pane mapView;
    @FXML
    ScrollPane mapScrollPane;
    @FXML
    private Polygon directionArrow;
    @FXML
    public Slider zoomSlider;
    @FXML private Menu curFloor;

    String nodeBeingDragged;
    Map<String, Circle> nodesOnImage = new HashMap<>();
    Map<String, Line> linesOnImage = new HashMap<>();
    List<Map<String, String>> selectedNodesList = new ArrayList<>();
    List<Map<String, String>> selectedEdgesList = new ArrayList<>();
    String FLOOR = "1";
    Group zoomGroup;
    int zoomLevel;
    double contextMenuX = 0;
    double contextMenuY = 0;
    ContextMenu contextMenu = new ContextMenu();

    DatabaseController db;
    String state = "";
    String currentID;
    double magicNumber = (Math.PI + Math.E) / 2.0; //this is used as the radius for the nodes because Chris likes it. I don't know why

    /**
     * Responsible for setting up the map
     */
    public void startUp(){
        db = DatabaseController.getInstance();
        mapScrollPane.pannableProperty().set(true);
        mapView.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if(event.getButton() == MouseButton.PRIMARY) mapScrollPane.pannableProperty().set(true);
            else mapScrollPane.pannableProperty().set(false);
        });

        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(mapImage);
        zoomGroup.getChildren().add(mapView);
        mapScrollPane.setContent(contentGroup);
        mapImage.setPreserveRatio(false);
        mapImage.setImage(new Image(floors.get(FLOOR)));
        curFloor = new Menu();


        zoomSlider.addEventHandler(MouseEvent.ANY, event -> {
            double tick = zoomSlider.getValue();
            zoomGroup.setScaleX(tick);
            zoomGroup.setScaleY(tick);
        });

        drawFloor(FLOOR);

        zoomLevel = 1;
    }


//=== BUTTON PRESSES ===//
    /**
     * Change the active floor
     */
    public void changeFloor3() throws SQLException { drawFloor("3"); }
    public void changeFloor2() throws SQLException { drawFloor("2"); }
    public void changeFloor1() throws SQLException { drawFloor("1"); }
    public void changeFloorL1() throws SQLException { drawFloor("L1"); }
    public void changeFloorL2() throws SQLException { drawFloor("L2"); }


//=== SCALING FUNCTIONS ===//
    /**
     * Scales an x-coordinate to be in proportion to the dimensions of the map images
     * @param xCoord int, the x-coordinate to be scaled
     * @return Double, the coordinate post-scaling
     */
    public Double xScale(int xCoord) { return (mapImage.getFitWidth()/5000) * xCoord; }

    /**
     * Scales a y-coordinate to be in proportion to the dimensions of the map images
     * @param yCoord int, the y-coordinate to be scaled
     * @return Double, the coordinate post-scaling
     */
    public Double yScale(int yCoord) { return (mapImage.getFitHeight()/3400) * yCoord; }

    /**
     * Scales an x-coordinate to be in proportion to the dimensions of the map images
     * @param xCoord int, the x-coordinate to be scaled
     * @return Double, the coordinate post-scaling
     */
    public Double inverseXScale(double xCoord) { return (5000/mapImage.getFitWidth()) * xCoord; }

    /**
     * Scales a y-coordinate to be in proportion to the dimensions of the map images
     * @param yCoord int, the y-coordinate to be scaled
     * @return Double, the coordinate post-scaling
     */
    public Double inverseYScale(double yCoord) { return (3400/mapImage.getFitHeight()) * yCoord; }


    //=== ZOOM FUNCTIONS ===//
    public void zoom(){
        double tick = zoomSlider.getValue();
        System.out.println(tick);
        zoomGroup.setScaleX(tick);
        zoomGroup.setScaleY(tick);
    }

    public void resetZoom(){ // TODO : implement this

    }


//=== MAP CONTENT FUNCTIONS ===//

    /**
     * Switches to a given floor and draws its image on the screen
     * @param floor the floor that will be drawn
     * @throws SQLException error with database
     */
    public void changeFloorImage(String floor) throws SQLException {
        FLOOR = floor;
        mapImage.setImage(new Image(floors.get(FLOOR)));
        mapView.getChildren().clear();
        nodesOnImage.clear();
        linesOnImage.clear();
    }


    /**
     * Replaces the circle on the map associated with nodeID with newCircle
     * @param newCircle the new representation of nodeID on the map
     * @param nodeID a ID that links to a node in the database
     */
    public void setNodeOnImage(Circle newCircle, String nodeID){
        nodesOnImage.put(nodeID, newCircle);
        if (!mapView.getChildren().contains(newCircle)) mapView.getChildren().add(newCircle);
        else mapView.getChildren().set(getNodeIndexOnImage(nodeID), newCircle);
    }


    /**
     *
     * @param nodeID
     */
    public void updateNodeOnImage(String nodeID){
        try {
            Map<String, String> node = db.getNode(nodeID);
            Circle updatedNode = new Circle();
            updatedNode.setCenterX(Double.parseDouble(node.get("XCOORD")));
            updatedNode.setCenterY(Double.parseDouble(node.get("YCOORD")));
            updatedNode.setRadius(3);
            updatedNode.setFill(darkBlue); // could be changed
            updatedNode.toFront();
            updatedNode.setStroke(darkBlue);
            updatedNode.setVisible(true);
            setNodeOnImage(updatedNode, nodeID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param nodeID
     */
    public void changeNodeCoordinatesOnImage(String nodeID, double x, double y){
        Circle currentNode = nodesOnImage.get(nodeID);
        currentNode.setCenterX(x);
        currentNode.setCenterY(y);
        setNodeOnImage(currentNode, nodeID);
    }


    public void setMultipleNodesOnImage(List<Map<String, String>> nodes, Color colorOfNodes){
        for(Map<String, String> node : nodes){
            node.get("NODEID");
        }
    }


    /**
     * Gets the index of the circle on the map that corresponds to nodeID
     * @param nodeID a ID that links to a node in the database
     * @return
     */
    public int getNodeIndexOnImage(String nodeID){
        return mapView.getChildren().indexOf(nodesOnImage.get(nodeID));
    }

    /**
     * Gets the index of the line on the map that corresponds to edgeID
     * @param edgeID a ID that links to an edge in the database
     * @return
     */
    public int getEdgeIndexOnImage(String edgeID){
        return mapView.getChildren().indexOf(linesOnImage.get(edgeID));
    }

    /**
     * Changes the color of the circle representing the nodeID on the map
     * @param nodeID a ID that links to a node in the database
     * @param color color to change the circle to be
     */
    public void changeNodeColorOnImage(String nodeID, Color color){ // WILL BE USED IN NAVIGATION
        Circle currentNode = nodesOnImage.get(nodeID);
        currentNode.setFill(color);
        setNodeOnImage(currentNode, nodeID);
    }


    /**
     * Removes a given nodeID from the map
     * @param nodeID a ID that links to a node in the database
     */
    public void removeNodeOnImage(String nodeID){
        int index = getNodeIndexOnImage(nodeID);
        nodesOnImage.remove(nodeID);
        mapView.getChildren().remove(index);
    }

    /**
     * Removes a given edgeID from the map
     * @param edgeID a ID that links to a edge in the database
     */
    public void removeEdgeOnImage(String edgeID){
        int index = getEdgeIndexOnImage(edgeID);
        linesOnImage.remove(edgeID);
        mapView.getChildren().remove(index);
    }

    /**
     * Re-draws all the edges connected to a particular node
     * @param nodeID
     */
    public void updateEdgesConnectedToNode(String nodeID) {
        try {
            List<Map<String, String>> connectedEdges = db.getEdgesConnectedToNode(nodeID);
            for (Map<String, String> edge : connectedEdges) {
                drawSingleEdge(edge.get("EDGEID"), Color.BLACK);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    /**
     * Pop up that happens when user clicks a node
     */
    public abstract void nodePopUp();


//=== DRAW FUNCTIONS ===//

    //====NODE FUNCTIONS

    public abstract void drawFloor(String floor);
    /**
     * Draws all nodes on the current floor
     * @param colorOfNodes the color the nodes will be displayed as on the map
     * @throws SQLException error with database
     */
    public void drawNodes(Color colorOfNodes) throws SQLException{
        for (Map<String, String> node: db.getNodesByValue("FLOOR", FLOOR)) {
            drawSingleNode(node.get("NODEID"), colorOfNodes);
        }
        for (Map<String, String> node: selectedNodesList){
            drawSingleNode(node.get("NODEID"), yellow);
        }
    }


    /**
     * Draws a single node as a colored dot
     * This version takes a map of string to string
     * @param nodeID the ID of the node to draw
     * @param color the color to fill the node
     */
    public void drawSingleNode(String nodeID, Color color) {
        Map<String, String> node;
        try {
            node = db.getNode(nodeID);
            drawSingleNode(xScale(Integer.parseInt(node.get("XCOORD"))), yScale(Integer.parseInt(node.get("YCOORD"))), node.get("NODEID"), color);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    /**
     * Draws a single circle of radius 3 at the given x and y coordinates
     * @param x x coord
     * @param y y coord
     * @param color color to fill the cicle
     */
    private void drawSingleNode(double x, double y, String nodeID, Color color){
        double radius = magicNumber;

        Circle node = new Circle();
        node.setCenterX(x);
        node.setCenterY(y);
        node.setRadius(radius);
        node.setFill(color);
        node.toFront();
        node.setStroke(darkBlue);

        node.setOnMousePressed((MouseEvent e) ->{
            if (e.getButton().equals(MouseButton.PRIMARY)){
                if(e.getClickCount() == 2) nodeBeingDragged = nodeID;
            }
        });

        node.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY)){

                //System.out.println(e.getClickCount());
                if (e.getClickCount() == 2) {
                    if(e.isStillSincePress()) {
                        node.setFill(yellow);
                        System.out.println("Successfully clicked node");
                        currentID = nodeID;
                        state = "Edit";
                        nodePopUp();
                    }
                }
                //Otherwise, single clicks will select/deselect nodes
                else {
                    try {
                        if (selectedNodesList.contains(db.getNode(nodeID))) {
                            selectedNodesList.remove(db.getNode(nodeID));
                            node.setFill(darkBlue);
                            if(!db.getNode(nodeID).get("FLOOR").equals(FLOOR)){
                                removeNodeOnImage(nodeID);
                            }
                        } else {
                            selectedNodesList.add(db.getNode(nodeID));
                            node.setFill(yellow);
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });

        node.setOnMouseEntered((MouseEvent e) -> {
            node.setRadius(5);
        });

        node.setOnMouseExited((MouseEvent e) -> {
            node.setRadius(magicNumber);
        });

        setNodeOnImage(node, nodeID);

    }


    //====EDGE FUNCTIONS
    /**
     * Draws all edges on the current floor, check for floor is in drawTwoNodesWithEdge
     * @param colorOfNodes
     * @throws SQLException
     */
    public void drawEdges(Color colorOfNodes) throws SQLException {
        for (Map<String, String> edge : db.getEdges())
            drawSingleEdge(edge.get("EDGEID"), Color.BLACK);
    }


    /**
     * Draws a line on the map that represents an edge.
     * @param edgeID a key that corresponds to an edge in the database
     * @param edgeColor the color the edge will be drawn in
     */
    public void drawSingleEdge(String edgeID, Color edgeColor) {
        try {
            Map<String, String> edge = db.getEdge(edgeID);
            Map<String, String> startNode = db.getNode(edge.get("STARTNODE"));
            Map<String, String> endNode = db.getNode(edge.get("ENDNODE"));

            if (startNode.get("FLOOR").equals(FLOOR) && endNode.get("FLOOR").equals(FLOOR)){
                double startX = xScale(Integer.parseInt(startNode.get("XCOORD")));
                double startY = yScale(Integer.parseInt(startNode.get("YCOORD")));
                String startID = startNode.get("NODEID");
                double endX = xScale(Integer.parseInt(endNode.get("XCOORD")));
                double endY = yScale(Integer.parseInt(endNode.get("YCOORD")));
                String endID = endNode.get("NODEID");
                drawSingleEdge(startX, startY, startID, endX, endY, endID, edgeColor);
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /**
     * Draws a line on the map that represents an edge.
     * @param startX
     * @param startY
     * @param startID
     * @param endX
     * @param endY
     * @param endID
     * @param edgeCol
     */
    private void drawSingleEdge(double startX, double startY, String startID, double endX, double endY, String endID, Color edgeCol) {

        Line edge = new Line();
        edge.setStartX(startX);
        edge.setStartY(startY);
        edge.setEndX(endX);
        edge.setEndY(endY);
        edge.toBack();
        edge.setStroke(edgeCol);
        edge.setStrokeWidth(magicNumber);
        edge.setFill(edgeCol);

        String edgeID = startID + "_" + endID;

        //Opening the popup menu
        edge.setOnMouseClicked((MouseEvent e) ->{

            try {
                if (selectedEdgesList.contains(db.getEdge(edgeID))) {
                    selectedEdgesList.remove(db.getEdge(edgeID));
                    edge.setStroke(Color.BLACK);
                } else {
                    selectedEdgesList.add(db.getEdge(edgeID));
                    edge.setStroke(yellow);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

//
//            if(e.getClickCount() == 2){
//                state = "Edit";
//                currentID = startID+"_"+endID;
//                edgePopUp();
//            }
        });

        // Hover over edge to make it thicker
        edge.setOnMouseEntered((MouseEvent e) ->{
            edge.setStrokeWidth(5);
            edge.toBack();
        });

        //Moving mouse off edge will make it stop highlighting
        edge.setOnMouseExited((MouseEvent e) ->{
            edge.setStrokeWidth(magicNumber);
        });

        if(linesOnImage.containsKey(edgeID)){
            Line key = linesOnImage.get(edgeID);
            mapView.getChildren().set(mapView.getChildren().indexOf(key), edge);
            linesOnImage.get(edgeID).setStroke(yellow);
        }
        else mapView.getChildren().add(edge);

        linesOnImage.put(edgeID, edge);
    }


    //====ARROWS

    /**
     * Draws up and down arrows to signify floor change for a given edge
     * @param edgeID representation of an edge in the database
     */
    void drawArrow(String edgeID) { // TODO : investigate stairs arrows not being drawn
        try {
            Map<String, String> edge = db.getEdge(edgeID);
            Map<String, String> startNode = db.getNode(edge.get("STARTNODE"));
            Map<String, String> endNode = db.getNode(edge.get("ENDNODE"));
            double startX = xScale(Integer.parseInt(startNode.get("XCOORD")));
            double startY = yScale(Integer.parseInt(startNode.get("YCOORD")));
            String startFloor = startNode.get("FLOOR");
            String endFloor = endNode.get("FLOOR");
            if(startFloor.equals(FLOOR) && !endFloor.equals(FLOOR)){
                drawArrow(startX, startY, startFloor, endFloor, 0.0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Draws up and down arrows to signify floor change for a given edge (two nodes)
     * This is a private method that takes coordinates and floors derived from the Node or Map passed to the public
     * methods.
     * @param centerX x-coordinate of the start node
     * @param centerY y-coordinate of the start node
     * @param startFloor floor of the start node
     * @param endFloor floor of the end node
     */
    private void drawArrow(double centerX, double centerY, String startFloor, String endFloor, double rotationAngle){

        if(mapView.getChildren().contains(directionArrow)) mapView.getChildren().remove(directionArrow);
        directionArrow = new Polygon();
        directionArrow.setFill(darkBlue);
        directionArrow.setStroke(darkBlue);
        directionArrow.setVisible(false);

        Double points[] = new Double[6];
        points[0] = centerX;
        points[2] = centerX + 7 * Math.sqrt(2.0) / 2.0;
        points[4] = centerX - 7 * Math.sqrt(2.0) / 2.0;

        points[1] = centerY - 7;
        points[3] = centerY + 7 * Math.sqrt(2.0) / 2.0;
        points[5] = centerY + 7 * Math.sqrt(2.0) / 2.0;

        if (startFloor.equals("G")) startFloor = "0";
        if (startFloor.equals("L1")) startFloor = "-1";
        if (startFloor.equals("L2")) startFloor = "-2";
        if (endFloor.equals("G")) endFloor = "0";
        if (endFloor.equals("L1")) endFloor = "-1";
        if (endFloor.equals("L2")) endFloor = "-2";

        if (Integer.parseInt(startFloor) == Integer.parseInt(endFloor)){
            directionArrow.getPoints().removeAll();
            directionArrow.getPoints().addAll(points);
            directionArrow.setScaleX(5.0/7.0);
            directionArrow.setScaleY(5.0/7.0);
            directionArrow.setRotate(rotationAngle);
            directionArrow.setVisible(true);
            mapView.getChildren().add(directionArrow);
        } else {

            Polygon floorChangeArrow = new Polygon();
            floorChangeArrow.toFront();

            if (Integer.parseInt(startFloor) < Integer.parseInt(endFloor)) {
                floorChangeArrow.setFill(Color.GREEN);
                floorChangeArrow.setRotate(0);
            } else if (Integer.parseInt(startFloor) > Integer.parseInt(endFloor)) {
                floorChangeArrow.setFill(Color.RED);
                floorChangeArrow.setRotate(180);
            }

            for (int i = 0; i < points.length; i++) {
                floorChangeArrow.getPoints().add(points[i]);
            }
            mapView.getChildren().add(floorChangeArrow);

            String finalEndFloor = endFloor;
            floorChangeArrow.setOnMousePressed((MouseEvent e) ->{
                FLOOR = finalEndFloor;
                drawFloor(finalEndFloor);
            });

        }
    }


//=== CONTEXT MENU FUNCTIONS ===//

    /**
     * Hides context menu when left mouse button is pressed
     * @param event the mouse click event
     */
    @FXML
    public void hideContextMenu(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) contextMenu.hide();
    }


//=== CLICK FUNCTIONALITY ==//

    /**
     * Finds the node closest to a given location on the map, if such a node exists within a radius of 20 pixels
     * @param x The x-coordinate about which the search is centered
     * @param y The y-coordinate about which the search is centered
     * @return The node closest to the given coordinates, if such a node exists within a radius of 20 pixels
     */
    public Map<String, String> getNearestNode(double x, double y) throws SQLException {
        double radius = 20;

        //Establish current closest recorded node and current least distance
        Map<String, String> currClosest = null;
        double currLeastDist = 100000;

        //Loop through nodes
        for (Map<String, String> node: db.getNodesByValue("FLOOR", FLOOR)) {
            //Get the x and y of that node
            double currNodeX = xScale(Integer.parseInt(node.get("XCOORD")));
            double currNodeY = yScale(Integer.parseInt(node.get("YCOORD")));
            double dist = findDistance(x, y, currNodeX, currNodeY);

            //If the distance is LESS than the given radius...
            if (dist < radius) {
                //...AND the distance is less than the current min, update current closest node
                if (dist < currLeastDist) {
                    currClosest = node;
                    currLeastDist = dist;
                }
            }
        }
        return currClosest;
    }

    /**
     * Gets Euclidean distance between two pairs of coordinates
     * @param startX The first pairs x value
     * @param startY The first pairs y value
     * @param endX The second pairs x value
     * @param endY The second pairs y value
     * @return The Euclidean distance between the two pairs of coordinates
     */
    protected double findDistance(double startX, double startY, double endX, double endY){
        // find differnce between two coordinates
        double xOff = endX - startX;
        double yOff = endY - startY;

        //Give 'em the ol' pythagoras maneuver
        double dist = (Math.pow(xOff, 2) + Math.pow(yOff, 2));
        dist = Math.sqrt(dist);

        return dist;
    }



}
