package edu.wpi.aquamarine_axolotls.views.mapping;

import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
     * Searches the valid nodes list for a node with the given ID
     * @param ID String, the ID of the node to find
     * @return The node with the given ID, once it's been found
     */
//    public Node getNodeFromValidID(String ID) {
//        for (Node n : validNodes) {
//            if (n.getNodeID().equals(ID)) return n;
//        }
//        return null;
//    }

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
     * Resets the zoom level to normal
     */
    public void resetZoom(){
        zoom = 1;
        zoomGroup.setScaleX(1);
        zoomGroup.setScaleY(1);
    }


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

    public abstract void nodePopUp();
    public abstract void edgePopUp();

    /**
     * Draws all nodes on the current floor
     */
    public void drawNodes(Color colorOfNodes) throws SQLException{
        for (Map<String, String> node: db.getNodesByValue("FLOOR", FLOOR)) {
            drawSingleNode(node, colorOfNodes);
        }
    }

    public void drawEdges(Color colorOfNodes) throws SQLException {
        for (Map<String, String> edge : db.getEdges())
            drawTwoNodesWithEdge(db.getNode(edge.get("STARTNODE")), db.getNode(edge.get("ENDNODE")), Color.BLUE, Color.BLUE, Color.BLACK);
    }


    /**
     * Change the active floor
     */
    public void changeFloor3() throws SQLException { changeFloor("3"); }
    public void changeFloor2() throws SQLException { changeFloor("2"); }
    public void changeFloor1() throws SQLException { changeFloor("1"); }
    public void changeFloorL1() throws SQLException { changeFloor("L1"); }
    public void changeFloorL2() throws SQLException { changeFloor("L2"); }


    /**
     * Draws a single node as a colored dot
     * This version takes a map of string to string
     * @param node the node to be drawn
     * @param color the color to fill the node
     */
    public void drawSingleNode(Map<String, String> node, Color color) {
        drawSingleNode(Integer.parseInt(node.get("XCOORD")), Integer.parseInt(node.get("YCOORD")), node.get("NODEID"), color);
    }




    public void drawSingleNode(int x, int y, String nodeID, Color color){
        drawSingleNode(xScale(x), yScale(y), nodeID, color);
    }

    /**
     * Draws a single circle of radius 3 at the given x and y coordinates
     * @param x x coord
     * @param y y coord
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
        node.setStroke(color);
        node.setVisible(true);

        //Opening the popup menu
        node.setOnMouseClicked((MouseEvent e) ->{
            if(e.getClickCount() == 2) {
                node.setFill(Color.YELLOW);
                System.out.println("Successfully clicked edge");
                currentID = nodeID;
                state = "Edit";
                nodePopUp();

            }
                 // TODO : make popup here
        });

        // Hover over edge to make it thicker and turn it red
        node.setOnMouseEntered((MouseEvent e) ->{
            node.setFill(Color.RED);
            node.setRadius(5);
            node.toFront();
        });

        //Moving mouse off edge will make it stop highlighting
        node.setOnMouseExited((MouseEvent e) ->{
            node.setFill(color);
            node.setRadius((Math.PI + Math.E)/2);
            node.toBack();
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


    public void zoom(){
        double tick = zoomSlider.getValue();
        System.out.println(tick);
        zoomGroup.setScaleX(tick);
        zoomGroup.setScaleY(tick);
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
                System.out.println("Successfully clicked edge");
                edge.setStroke(Color.YELLOW); // TODO : make popup here
                currentID = startID+"_"+endID;
                edgePopUp();
            }
        });

        // Hover over edge to make it thicker and turn it red
        edge.setOnMouseEntered((MouseEvent e) ->{
            edge.setStroke(Color.YELLOW);
            edge.setStrokeWidth(5);
            edge.toFront();
        });

        //Moving mouse off edge will make it stop highlighting
        edge.setOnMouseExited((MouseEvent e) ->{
            edge.setStroke(edgeCol);
            edge.setStrokeWidth((Math.PI + Math.E)/2);
            edge.toBack();
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
