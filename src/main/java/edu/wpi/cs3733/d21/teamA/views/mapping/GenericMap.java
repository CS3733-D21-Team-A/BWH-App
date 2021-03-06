package edu.wpi.cs3733.d21.teamA.views.mapping;

import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d21.teamA.db.DatabaseController;
import edu.wpi.cs3733.d21.teamA.views.GenericPage;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

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
        put("L2", "edu/wpi/cs3733/d21/teamA/img/lowerLevel2.png");
        put("L1", "edu/wpi/cs3733/d21/teamA/img/lowerLevel1.png");
        put("1", "edu/wpi/cs3733/d21/teamA/img/firstFloor.png");
        put("2", "edu/wpi/cs3733/d21/teamA/img/secondFloor.png");
        put("3", "edu/wpi/cs3733/d21/teamA/img/thirdFloor.png");
    }};

    @FXML
    public JFXButton floorL2Button;
    @FXML
    public JFXButton floorL1Button;
    @FXML
    public JFXButton floor1Button;
    @FXML
    public JFXButton floor2Button;
    @FXML
    public JFXButton floor3Button;
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
    ArrayList<ArrayList<Polygon>> arrowsOnImage = new ArrayList<>();

    Polygon currentPathArrow;

    List<Map<String, String>> selectedNodesList = new ArrayList<>();
    List<Map<String, String>> selectedEdgesList = new ArrayList<>();
    String FLOOR = "1";
    Group zoomGroup;
    int zoomLevel;
    double contextMenuX = 0;
    double contextMenuY = 0;
    ContextMenu contextMenu = new ContextMenu();

    DatabaseController db = DatabaseController.getInstance();
    String state = "";
    String currentID;
    double magicNumber = (Math.PI + Math.E) / 2.0; //this is used as the radius for the nodes because Chris likes it. I don't know why


    /**
     * Responsible for setting up the map
     */
    public void startUp(){
        mapScrollPane.pannableProperty().set(true);
        mapView.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> mapScrollPane.pannableProperty().set(event.getButton() == MouseButton.PRIMARY));

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

        changeFloor1();

        zoomLevel = 1;
    }


//=== BUTTON PRESSES ===//
    /**
     * Change the active floor
     */
    public void changeFloor3() {
        resetButtons();
        floor3Button.setTextFill(Color.WHITE);
        floor3Button.setStyle("-fx-background-color: #f4ba47");
        drawFloor("3");
    }
    public void changeFloor2() {
        resetButtons();
        floor2Button.setTextFill(Color.WHITE);
        floor2Button.setStyle("-fx-background-color: #f4ba47");
        drawFloor("2");
    }
    public void changeFloor1() {
        resetButtons();
        floor1Button.setTextFill(Color.WHITE);
        floor1Button.setStyle("-fx-background-color: #f4ba47");
        drawFloor("1");
    }
    public void changeFloorL1() {
        resetButtons();
        floorL1Button.setTextFill(Color.WHITE);
        floorL1Button.setStyle("-fx-background-color: #f4ba47");
        drawFloor("L1");
    }
    public void changeFloorL2() {
        resetButtons();
        floorL2Button.setTextFill(Color.WHITE);
        floorL2Button.setStyle("-fx-background-color: #f4ba47");
        drawFloor("L2");
    }


    public void changeFloor(String floor){
        switch(floor){
            case "L2":
                changeFloorL2();
                break;
            case "L1":
                changeFloorL1();
                break;
            case "1":
                changeFloor1();
                break;
            case "2":
                changeFloor2();
                break;
            case "3":
                changeFloor3();
                break;
        }

    }

    public void resetButtons(){

        // #f4ba47
        // F0F0F0 gray
        floor3Button.setStyle("-fx-background-color: #F0F0F0");
        floor3Button.setTextFill(darkGray);
        floor2Button.setStyle("-fx-background-color: #F0F0F0");
        floor2Button.setTextFill(darkGray);
        floor1Button.setStyle("-fx-background-color: #F0F0F0");
        floor1Button.setTextFill(darkGray);
        floorL1Button.setStyle("-fx-background-color: #F0F0F0");
        floorL1Button.setTextFill(darkGray);
        floorL2Button.setStyle("-fx-background-color: #F0F0F0");
        floorL2Button.setTextFill(darkGray);
    }


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
        zoomGroup.setScaleX(tick);
        zoomGroup.setScaleY(tick);
    }

    public void resetZoom(){
        zoomLevel = 1;
        zoomSlider.setValue(1);
        zoomGroup.setScaleX(1);
        zoomGroup.setScaleY(1);
    }


//=== MAP CONTENT FUNCTIONS ===//

    /**
     * Switches to a given floor and draws its image on the screen
     * @param floor the floor that will be drawn
     * @throws SQLException error with database
     */
    public void changeFloorImage(String floor) {
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
        currentNode.toFront();
        currentNode.setFill(color);
        setNodeOnImage(currentNode, nodeID);
    }

    /**
     * Changes the size of the circle representing the nodeID on the map
     * @param nodeID a ID that links to a node in the database
     * @param radius size to change the circle to be
     */
    public void updateNodeSize(String nodeID, int radius){ // WILL BE USED IN NAVIGATION
        Circle currentNode = nodesOnImage.get(nodeID);
        currentNode.toFront();
        currentNode.setRadius(radius);
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

    public void updateEdgeColor(String edgeID, Color color){
        Line edge = linesOnImage.get(edgeID);
        edge.setStroke(color);
    }

    /**
     * Removes a given edgeID from the map
     * @param edgeID a ID that links to a edge in the database
     */
    public void removeEdgeOnImage(String edgeID){
        if (mapView.getChildren().contains(linesOnImage.get(edgeID))){
            int index = getEdgeIndexOnImage(edgeID);
            linesOnImage.remove(edgeID);
            mapView.getChildren().remove(index);
        }
    }

    /**
     * Re-draws all the edges connected to a particular node
     * @param nodeID
     */
    public void updateEdgesConnectedToNode(String nodeID) {
        try {
            List<Map<String, String>> connectedEdges = db.getEdgesConnectedToNode(nodeID);
            for (Map<String, String> edge : connectedEdges) {
                drawSingleEdge(edge.get("STARTNODE"), edge.get("ENDNODE"), Color.BLACK);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Re-draws all the edges connected to a particular node
     */
    public void setArrowsToBeVisible() {
        if (arrowsOnImage.size() >= floorToInt(FLOOR)) {
            for(Polygon arrow : arrowsOnImage.get(floorToInt(FLOOR))){
                arrow.setVisible(true);
                if (!mapView.getChildren().contains(arrow)) mapView.getChildren().add(arrow);
            }
        }
    }



//=== DRAW FUNCTIONS ===//

    //====NODE FUNCTIONS

    public abstract void drawFloor(String floor);
    /**
     * Draws all nodes on the current floor
     * @param colorOfNodes the color the nodes will be displayed as on the map
     * @throws SQLException error with database
     */
    public abstract void drawNodes(Color colorOfNodes);



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

    public abstract Circle setEventHandler(Circle node, String nodeID);

    public abstract void setUpEdgeEventHandler(Line edge, String edgeID);


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

        setEventHandler(node, nodeID);
        setNodeOnImage(node, nodeID);

    }


    //====EDGE FUNCTIONS

    /**
      * Draws a line on the map that represents an edge.
      * @param startNodeID Edge start node
      * @param endNodeID Edge end node
      * @param edgeColor the color the edge will be drawn in
      */
    public void drawSingleEdge(String startNodeID, String endNodeID, Color edgeColor) {
        try {
            Map<String, String> startNode = db.getNode(startNodeID);
            Map<String, String> endNode = db.getNode(endNodeID);

            if ((startNode.get("FLOOR").equals(FLOOR) && endNode.get("FLOOR").equals(FLOOR))){
                double startX = xScale(Integer.parseInt(startNode.get("XCOORD")));
                double startY = yScale(Integer.parseInt(startNode.get("YCOORD")));
                double endX = xScale(Integer.parseInt(endNode.get("XCOORD")));
                double endY = yScale(Integer.parseInt(endNode.get("YCOORD")));
                drawSingleEdge(startX, startY, startNodeID, endX, endY, endNodeID, edgeColor);
            } else if (selectedEdgesList.contains(db.getEdge(startNodeID + "_" + endNodeID))){
                double startX = xScale(Integer.parseInt(startNode.get("XCOORD")));
                double startY = yScale(Integer.parseInt(startNode.get("YCOORD")));
                double endX = xScale(Integer.parseInt(endNode.get("XCOORD")));
                double endY = yScale(Integer.parseInt(endNode.get("YCOORD")));
                drawSingleEdge(startX, startY, startNodeID, endX, endY, endNodeID, yellow);
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

        setUpEdgeEventHandler(edge, edgeID);

        if(linesOnImage.containsKey(edgeID)){
            Line key = linesOnImage.get(edgeID);
            mapView.getChildren().set(mapView.getChildren().indexOf(key), edge);
            linesOnImage.get(edgeID).setStroke(yellow);
        }
        else mapView.getChildren().add(edge);

        linesOnImage.put(edgeID, edge);
    }


    //====ARROWS

    void drawArrows(){
        for(ArrayList<Polygon> floor : arrowsOnImage){
            for(Polygon arrow : floor){
                if(intToFloor(arrowsOnImage.indexOf(floor)).equals(FLOOR)) arrow.setVisible(true);
                else arrow.setVisible(false);
            }
        }
    }


    public int floorToInt(String floor){
        if (floor.equals("L2")) return 0;
        else if (floor.equals("L1")) return 1;
        else return Integer.parseInt(floor) + 1;
    }

    public String intToFloor(int floor){
        if(floor == 0) return "L2";
        if(floor == 1) return "L1";
        else return String.valueOf(floor- 1) ;
    }

    /**
     * Draws up and down arrows to signify floor change for a given edge
     * //TODO: UPDATE THIS JAVADOC
     */
    void drawArrow(Map<String, String> startNode, Map<String, String> endNode) { // TODO : investigate stairs arrows not being drawn
        String startFloor = startNode.get("FLOOR");
        String endFloor = endNode.get("FLOOR");
        int startFloorInt = floorToInt(startFloor);
        int endFloorInt = floorToInt(endFloor);

        double startX = xScale(Integer.parseInt(startNode.get("XCOORD")));
        double startY = yScale(Integer.parseInt(startNode.get("YCOORD")));
        double endX = xScale(Integer.parseInt(endNode.get("XCOORD")));
        double endY = yScale(Integer.parseInt(endNode.get("YCOORD")));

        if(startFloorInt < endFloorInt){
            drawUpArrow(startX, startY, startFloorInt, endFloorInt);
            drawDownArrow(endX, endY, endFloorInt, startFloorInt);
        }
        else{
            drawDownArrow(startX, startY, startFloorInt, endFloorInt);
            drawUpArrow(endX, endY, endFloorInt, startFloorInt);
        }
    }

    // add event handler to draw arrow
    // draw the actual arrow
    // place the arrow to arrows on image
    // draw them on the map view

    private void drawUpArrow(double centerX, double centerY, int startFloor, int endFloor) {
        drawArrow(centerX, centerY, startFloor, endFloor, Color.GREEN, 0);

    }

    private void drawDownArrow(double centerX, double centerY, int startFloor, int endFloor){
        drawArrow(centerX, centerY, startFloor, endFloor, Color.RED, 180);
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
    private void drawArrow(double centerX, double centerY, int startFloor, int endFloor, Color color, double rotationAngle){

        // make the polygon
        Polygon arrow = new Polygon();
        arrow.setFill(color);
        arrow.setStroke(color);
//        if(startFloor == floorToInt(FLOOR)) arrow.setVisible(true);
//        else
        arrow.setVisible(false);
        arrow.toFront();


        Double points[] = new Double[6];
        points[0] = centerX;
        points[2] = centerX + 7 * Math.sqrt(2.0) / 2.0;
        points[4] = centerX - 7 * Math.sqrt(2.0) / 2.0;

        points[1] = centerY - 7;
        points[3] = centerY + 7 * Math.sqrt(2.0) / 2.0;
        points[5] = centerY + 7 * Math.sqrt(2.0) / 2.0;

        arrow.getPoints().addAll(points);
        arrow.setRotate(rotationAngle);

        if(arrowsOnImage.size() - 1 < startFloor){
            for(int i = 0; i < 5; i++) arrowsOnImage.add(new ArrayList<Polygon>());
        }
        arrowsOnImage.get(startFloor).add(arrow);
//        mapView.getChildren().add(arrow);

        arrow.setOnMousePressed((MouseEvent e) ->{
            switch(endFloor){
                case 0:
                    changeFloorL2();
                    break;
                case 1:
                    changeFloorL1();
                    break;
                case 2:
                    changeFloor1();
                    break;
                case 3:
                    changeFloor2();
                    break;
                case 4:
                    changeFloor3();
                    break;
            }
        });

        // Hover over edge to make it thicker
        arrow.setOnMouseEntered((MouseEvent e) ->{
            arrow.setScaleX(2.0);
            arrow.setScaleY(2.0);
            arrow.toFront();
        });

        //Moving mouse off edge will make it stop highlighting
        arrow.setOnMouseExited((MouseEvent e) ->{
            arrow.setScaleX(1.0);
            arrow.setScaleY(1.0);
        });

//        if (startFloor == endFloor) { // TODO : add code for
//            directionArrow.getPoints().removeAll();
//            directionArrow.getPoints().addAll(points);
//            directionArrow.setScaleX(5.0 / 7.0);
//            directionArrow.setScaleY(5.0 / 7.0);
//            directionArrow.setRotate(rotationAngle);
//            directionArrow.setVisible(true);
//            mapView.getChildren().add(directionArrow);
//        }
    }

    public void drawPathArrow(double xCoord, double yCoord, double rotationAngle){
        int index = mapView.getChildren().indexOf(currentPathArrow);
        if(index != -1) mapView.getChildren().remove(index);
        Polygon arrow = new Polygon();
        arrow = new Polygon();
        arrow.setFill(darkBlue);
        arrow.setStroke(darkBlue);
        arrow.setVisible(false);
        arrow.toFront();


        Double points[] = new Double[6];
        points[0] = xCoord;
        points[2] = xCoord + 7 * Math.sqrt(2.0) / 2.0;
        points[4] = xCoord - 7 * Math.sqrt(2.0) / 2.0;

        points[1] = yCoord - 7;
        points[3] = yCoord + 7 * Math.sqrt(2.0) / 2.0;
        points[5] = yCoord + 7 * Math.sqrt(2.0) / 2.0;

        arrow.getPoints().addAll(points);
        arrow.setRotate(rotationAngle);

        currentPathArrow = arrow;
        mapView.getChildren().add(currentPathArrow);

    }


    public void drawPathArrow(Map<String, String> startNode, Map<String, String> endNode){
        int index = mapView.getChildren().indexOf(currentPathArrow);
        if(index != -1) mapView.getChildren().remove(index);
        double X1 = xScale(Integer.parseInt(startNode.get("XCOORD")));
        double Y1 = yScale(Integer.parseInt(startNode.get("YCOORD")));
        double X2 = xScale(Integer.parseInt(endNode.get("XCOORD")));
        double Y2 = yScale(Integer.parseInt(endNode.get("YCOORD")));

        double centerX = (X1 + X2) / 2.0;
        double centerY = (Y1 + Y2) / 2.0;

        double rotationAngle = Math.atan2(Y2-Y1, X2-X1) * 180 / Math.PI + 90.0;

        drawPathArrow(centerX, centerY, rotationAngle);
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

    void removeDirectionArrow(){
        if(mapView.getChildren().contains(directionArrow)) mapView.getChildren().remove(directionArrow);
    }


    Double[] getROSCoordinate(String packet){
        String[] parts = packet.split(",");
        //System.out.println("first: " + parts[0] + ", " + parts[1] + ", " + parts[2]);
        int count = parts[2].length() - parts[2].replaceAll("\\.","").length();
        if(count > 1){
            parts[2] = parts[2].split("\\.")[0] + "." + parts[2].split("\\.")[1];
            //System.out.println("parse once: " + parts[2]);
            int countMinus = parts[2].length() - parts[2].replaceAll("-","").length();
            if(parts[2].indexOf("-") != 0 && countMinus == 1){
                //System.out.println("in one");
                parts[2] = parts[2].split("-")[0];
                //System.out.println("parse twice: " + parts[2]);
            }
            if(countMinus > 1){
                //System.out.println("in two");
                parts[2] = "-" + parts[2].split("-")[1];
                //System.out.println("parse twice: " + parts[2]);
            }
        }
        System.out.println(parts[0] + ", " + parts[1] + ", " + parts[2]);

        Double xCoord = Double.valueOf(parts[0]);
        Double yCoord = Double.valueOf(parts[1]);
        Double yaw = Double.valueOf(parts[2]);

        Double [] coordinate = new Double[3];
        coordinate[0] = xCoord;
        coordinate[1] = yCoord;
        coordinate[2] = yaw;

        //System.out.println(coordinate);
        return coordinate;
    }

    void drawRobotArrow(double centerX, double centerY, String floor, double rotationAngle) {
//        double scaledX = xScale((int)centerX);
//        double scaledY = xScale((int)centerY);
        drawRobotArrow(centerX, centerY, floor, floor, rotationAngle);
    }

    private void drawRobotArrow(double centerX, double centerY, String startFloor, String endFloor, double rotationAngle){

        Polygon floorChangeArrow = new Polygon();

        if(mapView.getChildren().contains(directionArrow)) mapView.getChildren().remove(directionArrow);
        directionArrow = new Polygon();
        directionArrow.setFill(Color.MAGENTA);
        directionArrow.setStroke(Color.MAGENTA);
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

        if (Integer.parseInt(startFloor) < Integer.parseInt(endFloor)) {
            floorChangeArrow.setFill(Color.GREEN);
            floorChangeArrow.setRotate(0);
            for (int i = 0; i < points.length; i++) {
                floorChangeArrow.getPoints().add(points[i]);
            }
            mapView.getChildren().add(floorChangeArrow);
        } else if (Integer.parseInt(startFloor) > Integer.parseInt(endFloor)) {
            floorChangeArrow.setFill(Color.RED);
            floorChangeArrow.setRotate(180);
            for (int i = 0; i < points.length; i++) {
                floorChangeArrow.getPoints().add(points[i]);
            }
            mapView.getChildren().add(floorChangeArrow);
        } else /*if (Integer.parseInt(startFloor) == Integer.parseInt(endFloor))*/{

            directionArrow.getPoints().removeAll();
            directionArrow.getPoints().addAll(points);
            directionArrow.setScaleX(5.0/7.0);
            directionArrow.setScaleY(5.0/7.0);
            directionArrow.setRotate(rotationAngle);
            directionArrow.setVisible(true);
            mapView.getChildren().add(directionArrow);
        }
    }

}
