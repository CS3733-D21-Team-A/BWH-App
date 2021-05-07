package edu.wpi.aquamarine_axolotls.views.mapping;

import com.sun.prism.shader.DrawCircle_LinearGradient_PAD_AlphaTest_Loader;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import org.apache.derby.client.am.Sqlca;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GenericMap extends GenericPage {

    // valid nodes list
    //canvas stuff
    @FXML
    ImageView mapImage;
    @FXML
    Pane mapView;
    @FXML
    ScrollPane mapScrollPane;
    @FXML
    public Slider zoomSlider;

    Group zoomGroup;
    int zoom;
    DatabaseController db;
    ContextMenu contextMenu = new ContextMenu();
    double contextMenuX = 0;
    double contextMenuY = 0;
    Map<String, Circle> nodesOnImage = new HashMap<>();
    Map<String, Line> linesOnImage = new HashMap<>();

    String state = "";
    String currentID;
    List<Map<String, String>> selectedNodesList = new ArrayList<>();

    boolean isDoubleClicked = false;
    boolean isPrimaryDown = false;

    // Floor stuff
    static Map<String, String> floors = new HashMap<String,String>() {{
        put("L2", "edu/wpi/aquamarine_axolotls/img/lowerLevel2.png");
        put("L1", "edu/wpi/aquamarine_axolotls/img/lowerLevel1.png");
        put("1", "edu/wpi/aquamarine_axolotls/img/firstFloor.png");
        put("2", "edu/wpi/aquamarine_axolotls/img/secondFloor.png");
        put("3", "edu/wpi/aquamarine_axolotls/img/thirdFloor.png");
    }};
    String FLOOR = "1";
    @FXML private Menu curFloor;

    @FXML private Polygon directionArrow;
    
    // Node stuff
    //List<Node> validNodes = new ArrayList<>();
    ObservableList<String> options = FXCollections.observableArrayList();

    // METHODS

    /**
     * Generic startup behavior called in all children. Initializes floors and sets up canvas
     */
    public void startUp(){
        try {
            db = DatabaseController.getInstance();

            mapScrollPane.pannableProperty().set(true);
            mapView.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
                if(event.getButton() == MouseButton.PRIMARY) mapScrollPane.pannableProperty().set(true);
                else mapScrollPane.pannableProperty().set(false);
            });

            // TODO : add zoom in and out with key shortcut
/*            zoomSlider.setOnMouseDragged((MouseEvent mouse) ->{
                double tick = zoomSlider.getValue();
                System.out.println(tick);
                zoomGroup.setScaleX(tick);
                zoomGroup.setScaleY(tick);
            });*/


            Group contentGroup = new Group();
            zoomGroup = new Group();
            contentGroup.getChildren().add(zoomGroup);
            zoomGroup.getChildren().add(mapImage);
            zoomGroup.getChildren().add(mapView);
            mapScrollPane.setContent(contentGroup);
            mapImage.setPreserveRatio(false);
            mapImage.setImage(new Image(floors.get(FLOOR)));
            curFloor = new Menu();

            drawFloor(FLOOR);
            zoom = 1;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hides context menu when left clicked is clicked
     * @param event the mouse click event
     */
    @FXML
    public void hideContextMenu(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) contextMenu.hide();
    }

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
    public Double inverseXScale(int xCoord) { return (5000/mapImage.getFitWidth()) * xCoord; }

    /**
     * Scales a y-coordinate to be in proportion to the dimensions of the map images
     * @param yCoord int, the y-coordinate to be scaled
     * @return Double, the coordinate post-scaling
     */
    public Double inverseYScale(int yCoord) { return (3400/mapImage.getFitHeight()) * yCoord; }

    /**
     * Resets the zoom level to normal
     */
    public void resetZoom(){
        zoom = 1;
        zoomGroup.setScaleX(1);
        zoomGroup.setScaleY(1);
    }

    /**
     * Zooms in the map based on the position of the slider
     */
    public void zoom(){
        double tick = zoomSlider.getValue();
        System.out.println(tick);
        zoomGroup.setScaleX(tick);
        zoomGroup.setScaleY(tick);
    }

    /**
     * Draws a floor image, and then draws the nodes on top
     * @param floor The name of the floor to draw
     * @param colorOfnodes The color of the nodes
     * @throws SQLException
     */
    public void drawNodesAndFloor(String floor, Color colorOfnodes) throws SQLException{
        drawFloor(floor);
        drawNodes(colorOfnodes);
    }

    /**
     * Sets the floor to be rendered, then renders that floor
     * @param floor String, the floor to move to
     */
    public void drawFloor(String floor) throws SQLException{
        FLOOR = floor;
        mapImage.setImage(new Image(floors.get(FLOOR)));
        mapView.getChildren().clear();
        nodesOnImage.clear();
        linesOnImage.clear();
    }

    /**
     * Draws all nodes on the current floor
     */
    public void drawNodes(Color colorOfNodes) throws SQLException{
        for (Map<String, String> node: db.getNodesByValue("FLOOR", FLOOR)) {
            drawSingleNode(node, colorOfNodes);
        }
    }

    /**
     * Draws all edges and nodes on the current floor
     * @param colorOfNodes Color of the nodes to draw
     * @throws SQLException
     */
    public void drawEdges(Color colorOfNodes) throws SQLException {
        for (Map<String, String> edge : db.getEdges())
            drawTwoNodesWithEdge(db.getNode(edge.get("STARTNODE")), db.getNode(edge.get("ENDNODE")), Color.web("#003DA6"), Color.web("#003DA6"), Color.BLACK);
    }
    
    /**
     * Change the active floor
     */
    public void changeFloor3() throws SQLException { drawFloor("3"); }
    public void changeFloor2() throws SQLException { drawFloor("2"); }
    public void changeFloor1() throws SQLException { drawFloor("1"); }
    public void changeFloorL1() throws SQLException { drawFloor("L1"); }
    public void changeFloorL2() throws SQLException { drawFloor("L2"); }

    /**
     * Given a new circle and node ID, replaces the circle associated with that ID in the nodesOnImage list with the new circle
     * @param newCircle The new circle to put in
     * @param nodeID The ID to give that circle
     */
    public void setNodeOnImage(Circle newCircle, String nodeID){
        Circle previousCircle = nodesOnImage.get(nodeID);
        nodesOnImage.put(nodeID, newCircle);
        mapView.getChildren().set(getNodeIndexOnImage(previousCircle), newCircle);
    }

    /**
     * Given a node ID, gets the index of the circle associated with that ID in the nodesOnImage list
     * @param nodeID The nodeID to look up
     * @return Index of the circle associated with that ID
     */
    public int getNodeIndexOnImage(String nodeID){ return getNodeIndexOnImage(nodesOnImage.get(nodeID)); }

    /**
     * Overload of getNodeIndexOnImage, takes a circle and returns the index of that circle
     * @param previousCircle The circle to look up
     * @return The index of that circle in the nodesOnImage list
     */
    public int getNodeIndexOnImage(Circle previousCircle){
        return mapView.getChildren().indexOf(previousCircle);
    }

    public abstract void nodePopUp();
    public abstract void edgePopUp();

    /**
     * Draws a single node as a colored dot
     * This version takes a map of string to string
     * @param node the node to be drawn
     * @param color the color to fill the node
     */
    public void drawSingleNode(Map<String, String> node, Color color) {
        drawSingleNode(Integer.parseInt(node.get("XCOORD")), Integer.parseInt(node.get("YCOORD")), node.get("NODEID"), color);
    }

    /**
     * Overload of drawSingleNode that takes an x and y coordinate in integers
     * @param x Int, x coordinate
     * @param y Int, y coordinate
     * @param nodeID ID of the node to draw
     * @param color Color of the node
     */
    public void drawSingleNode(int x, int y, String nodeID, Color color){
        drawSingleNode(xScale(x), yScale(y), nodeID, color);
    }

    /**
     * Draws a single circle of radius 3 at the given x and y coordinates
     * @param x x coord
     * @param y y coord
     * @param nodeID ID to give the new node
     * @param color color to fill the cicle
     */
    public void drawSingleNode(double x, double y, String nodeID, Color color){
        double radius = (Math.PI + Math.E)/2;

        Circle node = new Circle();
        node.setCenterX(x);
        node.setCenterY(y);
        node.setRadius(radius);
        node.setFill(color);
        node.toFront();
        node.setStroke(Color.web("#003DA6"));
        node.setVisible(true);

        node.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(!event.isShiftDown()) return;
            try {
                Circle newNode = new Circle(e.getX(), e.getY(), 10, Color.web("#F4BA47")); // #7D99C9
                Map<String, String> currentNode = db.getNode(nodeID);
                setNodeOnImage(newNode, nodeID);
                currentNode.put("XCOORD", String.valueOf((inverseXScale((int) event.getX())).intValue()));
                currentNode.put("YCOORD", String.valueOf((inverseYScale((int) e.getY())).intValue()));
                System.out.println(currentNode);
                db.editNode(nodeID,currentNode);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

//        node.setOnMousePressed((MouseEvent e) -> {
//
//            System.out.println("FROM drag double click" + isDoubleClicked);
//            System.out.println("FROM drag pbutt" + e.isPrimaryButtonDown());
//            if((/*!e.isControlDown() && */!e.isShiftDown())) return;
//
//            try {
//                Circle newNode = new Circle(e.getX(), e.getY(), 10, Color.web("#F4BA47")); // #7D99C9
//                Map<String, String> currentNode = db.getNode(nodeID);
//                setNodeOnImage(newNode, nodeID);
//                currentNode.put("XCOORD", String.valueOf((inverseXScale((int) e.getX())).intValue()));
//                currentNode.put("YCOORD", String.valueOf((inverseYScale((int) e.getY())).intValue()));
//                System.out.println(currentNode);
//                db.editNode(nodeID,currentNode);
//            } catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//        });
//
//        node.setOnMouseDragExited((MouseEvent e) ->{
//            System.out.print("ERROR");
//        });


//        node.setOnDragExited((Event e) ->{
//            dragDetected = false;
//        });
//
//       node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
//
//       });

        node.setOnMousePressed((MouseEvent e) -> {
            if (e.getClickCount() == 2) isDoubleClicked = true;
        });

        node.setOnMouseClicked((MouseEvent e) ->{
            //If you double click, opens up the editing menu
//            if(e.isPrimaryButtonDown()) isPrimaryDown = true;
            if (e.getClickCount() == 2) {
                node.setFill(Color.web("#F4BA47"));
                System.out.println("Successfully clicked node");
                currentID = nodeID;
                state = "Edit";
                nodePopUp();

            }
            //Otherwise, single clicks will select/deselect nodes
            else {
                Circle currentCircle = nodesOnImage.get(nodeID);
                //If node is already yellow (meaning it's selected) de-select it
                if (currentCircle.getFill().equals(Color.web("#F4BA47"))) {
                    try {
                        selectedNodesList.remove(db.getNode(nodeID));
                        System.out.println(selectedNodesList);
                        node.setFill(Color.BLUE);
                        setNodeOnImage(currentCircle, nodeID);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                //Otherwise select it
                else{
                    try {
                        selectedNodesList.add(db.getNode(nodeID));
                        System.out.println(selectedNodesList);
                        currentCircle.setFill(Color.web("#F4BA47"));
                        setNodeOnImage(currentCircle, nodeID);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });

        // Hover over edge to make it thicker and turn it red
        node.setOnMouseEntered((MouseEvent e) ->{
                node.setRadius(5);
        });

        //Moving mouse off edge will make it stop highlighting
        node.setOnMouseExited((MouseEvent e) ->{
            if (!node.getFill().equals(Color.web("#F4BA47"))) {
                node.setRadius((Math.PI + Math.E)/2);
            }
        });

        if(nodesOnImage.containsKey(nodeID)){
            Circle key = nodesOnImage.get(nodeID);
            mapView.getChildren().set(mapView.getChildren().indexOf(key), node);
        }
        else mapView.getChildren().add(node);

        nodesOnImage.put(nodeID, node);

    }

    /**
     * Draws a single node as a colored dot
     * This version takes a map of string to string
     * @param node the node to be drawn
     * @param color the color to fill the node
     */
    public void drawSingleNodeHighLight(Map<String, String> node, Color color) { drawSingleNodeHighLight(xScale(Integer.parseInt(node.get("XCOORD"))), yScale(Integer.parseInt(node.get("YCOORD"))), color); }

    /**
     * Draws a single node as a colored dot
     * This version takes a node
     * @param node the node to be drawn
     * @param color the color to fill the node
     */
    public void drawSingleNodeHighLight(Node node, Color color) { drawSingleNodeHighLight(xScale(node.getXcoord()), yScale(node.getYcoord()), color); }

    /**
     * Draws a single circle of radius 3 at the given x and y coordinates
     * @param x x coord
     * @param y y coord
     * @param color color to fill the cicle
     */
    private void drawSingleNodeHighLight(double x, double y, Color color){
        double radius = 6;
        //x = x - (radius / 2);
        //y = y - (radius / 2);

        Circle node = new Circle();
        node.setCenterX(x);
        node.setCenterY(y);
        node.setRadius(radius);
        node.setFill(color);
        mapView.getChildren().add(node);
    }



    /**
     * Draws two nodes as dots, and connects them with a line (ONLY DRAWS ON THE CURRENT FLOOR)
     * This version takes two maps of string to string
     * @param snode Node to start with
     * @param enode Node to end at
     * @param snodeCol Color of the start node
     * @param enodeCol Color of the end node
     * @param edgeCol Color of the edge
     */
    void drawTwoNodesWithEdge(Map<String, String> snode, Map<String, String> enode, Color snodeCol, Color enodeCol, Color edgeCol) {
        if (snode.get("FLOOR").equals(FLOOR) && enode.get("FLOOR").equals(FLOOR)){
            double startX = xScale(Integer.parseInt(snode.get("XCOORD")));
            double startY = yScale(Integer.parseInt(snode.get("YCOORD")));
            String startID = snode.get("NODEID");
            double endX = xScale(Integer.parseInt(enode.get("XCOORD")));
            double endY = yScale(Integer.parseInt(enode.get("YCOORD")));
            String endID = enode.get("NODEID");
            drawTwoNodesWithEdge(startX, startY, startID, endX, endY, endID, snodeCol, enodeCol, edgeCol);
        }
    }

    /**
     * Draws two nodes as dots, and connects them with a line
     * This version takes two node objects
     * @param snode Node to start with
     * @param enode Node to end at
     * @param snodeCol Color of the start node
     * @param enodeCol Color of the end node
     * @param edgeCol Color of the edge
     */
    void drawTwoNodesWithEdge(Node snode, Node enode, Color snodeCol, Color enodeCol, Color edgeCol) {
        if (snode.getFloor().equals(FLOOR) && enode.getFloor().equals(FLOOR)){
            double startX = xScale(snode.getXcoord());
            double startY = yScale(snode.getYcoord());
            double endX = xScale(enode.getXcoord());
            double endY = yScale(enode.getYcoord());
            String startID = snode.getNodeID();
            String endID = enode.getNodeID();
            drawTwoNodesWithEdge(startX, startY, startID, endX, endY, endID, snodeCol, enodeCol, edgeCol);
        }
    }

    /**
     * Draws two nodes as dots, and connects them with a line
     * @param snodeCol Color of the start node
     * @param enodeCol Color of the end node
     * @param edgeCol Color of the edge
     */
    private void drawTwoNodesWithEdge (double startX, double startY, String startID, double endX, double endY, String endID, Color snodeCol, Color enodeCol, Color edgeCol) {

        Line edge = new Line();
        edge.setStartX(startX);
        edge.setStartY(startY);
        edge.setEndX(endX);
        edge.setEndY(endY);
        edge.toBack();
        edge.setStroke(edgeCol);
        edge.setStrokeWidth((Math.PI + Math.E)/2);
        edge.setFill(edgeCol);

        //Opening the popup menu
        edge.setOnMouseClicked((MouseEvent e) ->{
            if(e.getClickCount() == 2){
                state = "Edit";
                currentID = startID+"_"+endID;
                edgePopUp();
            }
        });

        // Hover over edge to make it thicker and turn it red
        edge.setOnMouseEntered((MouseEvent e) ->{
            edge.setStrokeWidth(5);
            edge.toBack();
        });

        //Moving mouse off edge will make it stop hig
        // hlighting
        edge.setOnMouseExited((MouseEvent e) ->{
            edge.setStrokeWidth((Math.PI + Math.E)/2);

        });

        String edgeID = startID + "_" + endID;
        if(linesOnImage.containsKey(edgeID)){
            Line key = linesOnImage.get(edgeID);
            mapView.getChildren().set(mapView.getChildren().indexOf(key), edge);
            linesOnImage.get(edgeID).setStroke(Color.GREEN);
        }
        else mapView.getChildren().add(edge);

        linesOnImage.put(edgeID, edge);

        drawSingleNode(startX, startY, startID, snodeCol);
        drawSingleNode(endX, endY, endID, enodeCol);
    }

    void drawArrow(double centerX, double centerY, String floor, double rotationAngle) {
//        double scaledX = xScale((int)centerX);
//        double scaledY = xScale((int)centerY);
        drawArrow(centerX, centerY, floor, floor, rotationAngle);
    }

    /**
     * Draws up and down arrows to signify floor change for a given edge (two nodes)
     * @param start start node
     * @param end end node
     */
    void drawArrow(Map<String, String> start, Map<String, String> end) { // TODO : investigate stairs arrows not being drawn

        double startX = xScale(Integer.parseInt(start.get("XCOORD")));
        double startY = yScale(Integer.parseInt(start.get("YCOORD")));

//        double endX = xScale(Integer.parseInt(start.get("XCOORD")));
//        double endY = yScale(Integer.parseInt(start.get("YCOORD")));

        String startFloor = start.get("FLOOR");
        String endFloor = end.get("FLOOR");

        if(startFloor.equals(FLOOR) && !endFloor.equals(FLOOR)){
            drawArrow(startX, startY, startFloor, endFloor, 0.0);
        }
    }

    /**
     * Draws up and down arrows to signify floor change for a given edge (two nodes)
     * @param start start node
     * @param end end node
     */
    void drawArrow(Node start, Node end) { // TODO : investigate stairs arrows not being drawn

        double startX = xScale(start.getXcoord());
        double startY = yScale(start.getYcoord());

//        double endX = xScale(end.getXcoord());
//        double endY = yScale(end.getYcoord());

        String startFloor = start.getFloor();
        String endFloor = end.getFloor();

        if(startFloor.equals(FLOOR) && !endFloor.equals(FLOOR)){
            drawArrow(startX, startY, startFloor, endFloor, 0.0);
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

        Polygon floorChangeArrow = new Polygon();

        if(mapView.getChildren().contains(directionArrow)) mapView.getChildren().remove(directionArrow);
        directionArrow = new Polygon();
        directionArrow.setFill(Color.BLUE);
        directionArrow.setStroke(Color.BLUE);
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
     * Gets the Euclidean distance between 2 nodes
     * @param snode The starting node
     * @param enode The ending node
     * @return The Euclidean distance between the given nodes
     */
    double getDistBetweenNodes(Map<String, String> snode, Map<String, String> enode) {

        double sNodeX = xScale(Integer.parseInt(snode.get("XCOORD")));
        double sNodeY = yScale(Integer.parseInt(snode.get("YCOORD")));
        double eNodeX = xScale(Integer.parseInt(enode.get("XCOORD")));
        double eNodeY = yScale(Integer.parseInt(enode.get("YCOORD")));

        return findDistance(sNodeX, sNodeY, eNodeX, eNodeY);
    }

    /**
     * Gets Euclidean distance between two pairs of coordinates
     * @param startX The first pairs x value
     * @param startY The first pairs y value
     * @param endX The second pairs x value
     * @param endY The second pairs y value
     * @return The Euclidean distance between the two pairs of coordinates
     */
    double findDistance(double startX, double startY, double endX, double endY){
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

    //private double get

}
