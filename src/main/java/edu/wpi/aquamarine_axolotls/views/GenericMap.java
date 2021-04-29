package edu.wpi.aquamarine_axolotls.views;

import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import org.apache.derby.client.am.Sqlca;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericMap extends SPage{


    // valid nodes list
    //canvas stuff
    @FXML
    Canvas mapCanvas;
    @FXML
    ScrollPane mapScrollPane;
    Group zoomGroup;
    int zoom;
    DatabaseController db;

    // Floor stuff
    Map<String, String> floors;
    String FLOOR = "1";
    @FXML private Menu curFloor;
    
    // Node stuff
    //List<Node> validNodes = new ArrayList<>();
    ObservableList<String> options = FXCollections.observableArrayList();

    // METHODS

    /**
     * Generic startup behavior called in all children. Initializes floors and sets up canvas
     */
    public void startUp(){
        try {
            db = new DatabaseController();

  /*          List<Map<String, String>> nodes = db.getNodesByValue("FLOOR", FLOOR);

            for (Map<String, String> node : nodes) {
                options.add(node.get("LONGNAME"));
            }*/

            floors = new HashMap<>();                   // stores map images
            floors.put("L2", "edu/wpi/aquamarine_axolotls/img/lowerLevel2.png");
            floors.put("L1", "edu/wpi/aquamarine_axolotls/img/lowerLevel1");
            floors.put("1", "edu/wpi/aquamarine_axolotls/img/firstFloor.png");
            floors.put("2", "edu/wpi/aquamarine_axolotls/img/secondFloor.png");
            floors.put("3", "edu/wpi/aquamarine_axolotls/img/thirdFloor.png");

            mapScrollPane.pannableProperty().set(true);
            Group contentGroup = new Group();
            zoomGroup = new Group();
            contentGroup.getChildren().add(zoomGroup);
            zoomGroup.getChildren().add(mapCanvas);
            mapScrollPane.setContent(contentGroup);
            mapCanvas.getGraphicsContext2D().drawImage(new Image(floors.get(FLOOR)), 0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());

            drawFloor(FLOOR);
            zoom = 1;
        } catch (SQLException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
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
    public Double xScale(int xCoord) { return (mapCanvas.getWidth()/5000) * xCoord; }

    /**
     * Scales a y-coordinate to be in proportion to the dimensions of the map images
     * @param yCoord int, the y-coordinate to be scaled
     * @return Double, the coordinate post-scaling
     */
    public Double yScale(int yCoord) { return (mapCanvas.getHeight()/3400) * yCoord; }

    /**
     * Zooms in on the canvas and its scrollpane
     * @param actionEvent
     */
    public void zoomIn(ActionEvent actionEvent) {
        if(zoom < 3){
            zoomGroup.setScaleX(++zoom);
            zoomGroup.setScaleY(zoom);
        }
    }

    /**
     * Resets the zoom level to normal
     */
    public void resetZoom(){
        zoom = 1;
        zoomGroup.setScaleX(1);
        zoomGroup.setScaleY(1);
    }

    /**
     * Zooms out from the canvas and its scrollpane
     * @param actionEvent
     */
    public void zoomOut(ActionEvent actionEvent) {
        if(zoom > 1){
            zoomGroup.setScaleX(--zoom);
            zoomGroup.setScaleY(zoom);
        }
    }


    public void drawNodesAndFloor(String floor, Color colorOfnodes) throws SQLException{
        drawFloor(floor);
        drawNodes(colorOfnodes);
    }


    /**
     * Sets the floor to be rendered, then renders that floor
     * @param floor String, the floor to move to
     */
    public void drawFloor(String floor){
        FLOOR = floor;
        mapCanvas.getGraphicsContext2D().clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        mapCanvas.getGraphicsContext2D().drawImage(new Image(floors.get(FLOOR)), 0,0, mapCanvas.getWidth(), mapCanvas.getHeight());
        curFloor.setText( "Cur Floor : " + FLOOR);
    }

    /**
     * Draws all nodes on the current floor
     */
    public void drawNodes(Color colorOfNodes) throws SQLException{
        for (Map<String, String> node: db.getNodesByValue("FLOOR", FLOOR)) {
            drawSingleNode(node, mapCanvas.getGraphicsContext2D(), colorOfNodes);
        }
    }

    /**
     * Changes the active floor and updates the visible image, nodes, and edges
     * @param floor The new floor
     */
    public void changeFloor(String floor) throws SQLException{
        drawFloor(floor);
        drawNodes(Color.BLUE);
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
     * @param node the node to be drawn
     * @param color the color to fill the node
     */
    public void drawSingleNode(Map<String, String> node, Color color) { drawSingleNode(xScale(Integer.parseInt(node.get("XCOORD"))), yScale(Integer.parseInt(node.get("YCOORD"))), color); }

    /**
     * Draws a single node as a colored dot
     * @param node the node to be drawn
     * @param color the color to fill the node
     */
    public void drawSingleNode(Node node, Color color) { drawSingleNode(xScale(node.getXcoord()), yScale(node.getYcoord()), color); }

    /**
     * Draws a single circle of radius 3 at the given x and y coordinates
     * @param x x coord
     * @param y y coord
     * @param color color to fill the cicle
     */
    private void drawSingleNode(double x, double y, Color color){
        double radius = 3;
        x = x - (radius / 2);
        y = y - (radius / 2);
        mapCanvas.getGraphicsContext2D().setFill(color);
        mapCanvas.getGraphicsContext2D().fillOval(x, y, radius, radius);
    }

    /**
     * Draws two nodes as dots, and connects them with a line
     * @param snode Node to start with
     * @param enode Node to end at
     * @param snodeCol Color of the start node
     * @param enodeCol Color of the end node
     * @param edgeCol Color of the edge
     */
    public void drawTwoNodesWithEdge(Map<String, String> snode, Map<String, String> enode, Color snodeCol, Color enodeCol, Color edgeCol) {
        if (snode.get("FLOOR").equals(FLOOR) && enode.get("FLOOR").equals(FLOOR)){
            GraphicsContext gc = mapCanvas.getGraphicsContext2D();
            gc.setStroke(edgeCol);
            gc.strokeLine(xScale(Integer.parseInt(snode.get("XCOORD"))), yScale(Integer.parseInt(snode.get("YCOORD"))),
                            xScale(Integer.parseInt(enode.get("XCOORD"))), yScale(Integer.parseInt()enode.get("YCOORD")));

            drawSingleNode(snode, snodeCol);
            drawSingleNode(enode, enodeCol);
        }
    }

    /**
     * Draws up and down arrows to signify floor change for a given edge (two nodes)
     * @param start start node
     * @param end end node
     */
    private void drawArrow(Map<String, String> start, Map<String, String> end) { // TODO : investigate stairs arrows not being drawn

        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        int startX = Integer.parseInt(start.get("XCOORD"));
        int startY = Integer.parseInt(start.get("YCOORD"));

        int endX = Integer.parseInt(start.get("XCOORD"));
        int endY = Integer.parseInt(start.get("YCOORD"));

        gc.strokeLine(xScale(startX), yScale(startY), xScale(endX), yScale(endY));

        double xCenter = startX;
        double yCenter = startY;

        double xPoints[] = new double[3];
        xPoints[0] = xCenter;
        xPoints[1] = xCenter + 7 * Math.sqrt(2.0) / 2.0;
        xPoints[2] = xCenter - 7 * Math.sqrt(2.0) / 2.0;
        double yPoints[] = new double[3];

        String startFloor = start.get("FLOOR");
        String endFloor = end.get("FLOOR");

        if (startFloor.equals("G")) startFloor = "0";
        if (startFloor.equals("L1")) startFloor = "-1";
        if (startFloor.equals("L2")) startFloor = "-2";
        if (endFloor.equals("G")) endFloor = "0";
        if (endFloor.equals("L1")) endFloor = "-1";
        if (endFloor.equals("L2")) endFloor = "-2";

        if (startFloor.equals(FLOOR)) {
            if (Integer.parseInt(startFloor) < Integer.parseInt(endFloor)) {

                gc.setFill(Color.GREEN);

                yPoints[0] = yCenter - 7;
                yPoints[1] = yCenter + 7 * Math.sqrt(2.0) / 2.0;
                yPoints[2] = yCenter + 7 * Math.sqrt(2.0) / 2.0;
            } else if (Integer.parseInt(startFloor) > Integer.parseInt(endFloor)) {

                gc.setFill(Color.RED);

                yPoints[0] = yCenter + 7;
                yPoints[1] = yCenter - 7 * Math.sqrt(2.0) / 2.0;
                yPoints[2] = yCenter - 7 * Math.sqrt(2.0) / 2.0;
            }
        }
        gc.fillPolygon(xPoints, yPoints, 3);
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
    private double getDistBetweenNodes(Map<String, String> snode, Map<String, String> enode) {

        double sNodeX = xScale(Integer.parseInt(snode.get("XCOORD")));
        double sNodeY = yScale(Integer.parseInt(snode.get("YCOORD")));
        double eNodeX = xScale(Integer.parseInt(enode.get("XCOORD")));
        double eNodeY = yScale(Integer.parseInt(enode.get("YCOORD")));

        return findDistance(sNodeX, sNodeY, eNodeX, eNodeY);;
    }

    /**
     * Gets Euclidean distance between two pairs of coordinates
     * @param startX The first pairs x value
     * @param startY The first pairs y value
     * @param endX The second pairs x value
     * @param endY The second pairs y value
     * @return The Euclidean distance between the two pairs of coordinates
     */
    private double findDistance(double startX, double startY, double endX, double endY){
        // find differnce between two coordinates
        double xOff = endX - startX;
        double yOff = endY - startY;

        //Give 'em the ol' pythagoras maneuver
        double dist = (Math.pow(xOff, 2) + Math.pow(yOff, 2));
        dist = Math.sqrt(dist);

        return dist;
    }

    //private double get

}
