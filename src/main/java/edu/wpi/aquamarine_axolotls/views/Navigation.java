package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.pathplanning.SearchAlgorithm;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.*;

public class Navigation extends SPage {

    @FXML
    private ImageView groundFloor;
    @FXML
    private JFXComboBox startLocation;
    @FXML
    private JFXComboBox destination;
    @FXML
    private JFXButton findPathButton;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private AnchorPane anchor1;
    @FXML
    private JFXButton addStopbtn;
    @FXML
    private JFXComboBox intermediate;
    @FXML
    private Label etaLabel;
    @FXML
    private AnchorPane mainAnchor;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private Text time;
    @FXML
    private JFXHamburger burger;
    @FXML
    private JFXDrawer menuDrawer;
    @FXML
    private VBox box;

    @FXML Canvas mapCanvas;
    @FXML ScrollPane mapScrollPane;

    private Group zoomGroup;
    private int zoom;

    ObservableList<String> options = FXCollections.observableArrayList();
    DatabaseController db;
    List<Node> validNodes = new ArrayList<>();
    private int firstNodeSelect = 0;
    private String firstNode;
    private List<String> pathList = new ArrayList<>();
    private int activePath = 0;


    private HamburgerBasicCloseTransition transition;
    static double SCALE_DELTA = 1.25;
    static double SCALE_TOTAL = 2.0;
    static double SCALE = 1.0;
    static String FLOOR = "G";
    static Double imgWidth = 991.0;
    static Double imgHeight = 669.0;
    
    Map<String, String> floors;

    @FXML
    public void initialize() {
        try {
            db = new DatabaseController();
            List<Map<String, String>> nodes = db.getNodes();
            for (Map<String, String> node : nodes) {
                if ((node.get("NODETYPE").equals("WALK"))
                        || node.get("NODETYPE").equals("PARK")
                        || (node.get("BUILDING").equals("45 Francis") && node.get("FLOOR").equals("1"))
                        || (node.get("BUILDING").equals("Tower") && node.get("FLOOR").equals("1"))) {
                    options.add(node.get("LONGNAME"));
                    validNodes.add(new Node(node.get("NODEID"),
                            Integer.parseInt(node.get("XCOORD")),
                            Integer.parseInt(node.get("YCOORD")),
                            node.get("FLOOR"),
                            node.get("BUILDING"),
                            node.get("NODETYPE"),
                            node.get("LONGNAME"),
                            node.get("SHORTNAME")));
                }

            }
            floors = new HashMap<>();
            floors.put("G", "edu/wpi/aquamarine_axolotls/img/groundFloor.png");
            floors.put("1", "edu/wpi/aquamarine_axolotls/img/firstFloor.png");
            mapScrollPane.pannableProperty().set(true);
            Group contentGroup = new Group();
            zoomGroup = new Group();
            contentGroup.getChildren().add(zoomGroup);
            zoomGroup.getChildren().add(mapCanvas);
            mapScrollPane.setContent(contentGroup);

            mapCanvas.getGraphicsContext2D().drawImage(new Image(floors.get(FLOOR)), 0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
            zoom = 1;

            changeFloorNodes();

            startLocation.setItems(options);
            destination.setItems(options);
            intermediate.setItems(options);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    /**
     * Scales the x coordinate from table for image (5000,3400)
     *
     * @param xCoord x coordinate from table
     * @return scaled X coordinate
     */
    public Double xScale(int xCoord) { return (mapCanvas.getWidth()/5000) * xCoord; }
    public Double yScale(int yCoord) { return (mapCanvas.getHeight()/3400) * yCoord; }


    public void zoomIn(ActionEvent actionEvent) {
        if(zoom < 3){
            zoomGroup.setScaleX(++zoom);
            zoomGroup.setScaleY(zoom);
        }
    }

    public void resetZoom(){
        zoomGroup.setScaleX(1);
        zoomGroup.setScaleY(1);
    }

    public void zoomOut(ActionEvent actionEvent) {
        if(zoom > 1){
            zoomGroup.setScaleX(--zoom);
            zoomGroup.setScaleY(zoom);
        }
    }

    public void clearNodes() {
        resetMap(FLOOR);
        stopList.clear();
        startLocation.getSelectionModel().clearSelection();
        destination.getSelectionModel().clearSelection();
        intermediate.getSelectionModel().clearSelection();
        activePath = 0;
    }

    public void resetMap(String floor) {
        resetZoom();
        mapCanvas.getGraphicsContext2D().clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        mapCanvas.getGraphicsContext2D().drawImage(new Image(floors.get(floor)), 0,0, mapCanvas.getWidth(), mapCanvas.getHeight());
        FLOOR = floor;
    }

    public void resetMapAndDraw(String floor) {
        resetMap(floor);
        drawFloor(floor);
    }

    public void drawFloor(String floor){
        resetMap(FLOOR);
        for (Node n: validNodes) if(n.getFloor().equals(floor)) drawSingleNode(n);

        if (activePath == 1) {
            for (int i = 0; i < currPath.size() - 1; i++) {
                drawNodes(currPath.get(i), currPath.get(i + 1));
            }
        }
    }


    public void changeFloor1() throws FileNotFoundException {
        resetMapAndDraw("1");
    }

    public void changeGroundFloor() throws FileNotFoundException {
        resetMapAndDraw("G");
    }



    public void findPath() {
        if (startLocation.getSelectionModel().getSelectedItem() == null || destination.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        String start = startLocation.getSelectionModel().getSelectedItem().toString();
        String end = destination.getSelectionModel().getSelectedItem().toString();
        resetMap(FLOOR);
        findPath(start, end);
    }


    public void startEndlocation() {
/*        if (activePath == 0) anchor.getChildren().clear();
        String start = startLocation.getSelectionModel().getSelectedItem().toString();
        String end = destination.getSelectionModel().getSelectedItem().toString();
        SearchAlgorithm searchAlgorithm;
        List<Node> pathNodes = new ArrayList<>();
        try {
            searchAlgorithm = new SearchAlgorithm();
            pathNodes = searchAlgorithm.getPath(start, end);

        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (URISyntaxException ue) {
            ue.printStackTrace();
        } catch (SQLException sq) {
            sq.printStackTrace();
        }


        Double prevX = xScale(pathNodes.get(0).getXcoord()); // TODO : fix this jank code
        Double prevY = yScale(pathNodes.get(0).getYcoord());

        for (Node node : pathNodes) {
            Circle circ = new Circle();
            Line line = new Line();
            Double scaledX = xScale(node.getXcoord());
            Double scaledY = yScale(node.getYcoord());

            circ.setCenterX(scaledX);
            circ.setCenterY(scaledY);
            circ.setRadius(2);
            circ.setFill(Color.RED);

            line.setStartX(scaledX);
            line.setStartY(scaledY);
            line.setEndX(prevX);
            line.setEndY(prevY);
            line.setStroke(Color.RED);


            anchor.getChildren().addAll(circ, line);
            prevX = scaledX;
            prevY = scaledY;
        }
        firstNodeSelect = 0;*/
    }


    /**
     * Alternate declaration of findPath() that takes a specific start and end, used for clicking nodes on the map directly
     *
     * @param start String, long name of start node
     * @param end   String, long name of end node
     */
    public void findPath(String start, String end) {
        stopList.add(start);
        stopList.add(end);
        SearchAlgorithm searchAlgorithm;
        double etaTotal, minutes, seconds;
        currPath.clear();
        try {
            searchAlgorithm = new SearchAlgorithm();
            for (int i = 0; i < pathList.size() - 1; i++) {
                pathNodes.addAll(searchAlgorithm.getPath(pathList.get(i), pathList.get(i + 1)));
            }

            etaTotal = searchAlgorithm.getETA(currPath);
            minutes = Math.floor(etaTotal);
            seconds = Math.floor((etaTotal - minutes) * 60);
            etaLabel.setText((int) minutes + ":" + (int) seconds);


        } catch (IOException | URISyntaxException | SQLException e) {
            e.printStackTrace();
        }

        if(currPath.isEmpty()) return;

        firstNodeSelect = 0;
        activePath = 1;
    }

    /**
     * Gets the closest node to the mouse cursor when clicked
     */
    public void getNearestNode(javafx.scene.input.MouseEvent event) {
        //System.out.println("Clicked map");

        //double x = xScale(event.getX());
        //double y = yScale(event.getY());
        double x = event.getX();
        double y = event.getY();

        //System.out.println(x + " " + y);
        double radius = 20;

        //Establish current closest recorded node and current least distance
        Map<String, String> currClosest = new HashMap<>();
        double currLeastDist = 100000;

        //Loop through nodes
        for (Map<String, String> n : validNodes) {
            if (
                    (FLOOR == 0 && n.get("FLOOR").equals("G"))
                            || (FLOOR == 1 && n.get("FLOOR").equals("1"))) {
                //Get the x and y of that node
                double currNodeX = xScale(Double.parseDouble(n.get("XCOORD")));
                double currNodeY = yScale(Double.parseDouble(n.get("YCOORD")));

                //Get the difference in x and y between input coords and current node coords
                double xOff = x - currNodeX;
                double yOff = y - currNodeY;

                //Give 'em the ol' pythagoras maneuver
                double dist = (Math.pow(xOff, 2) + Math.pow(yOff, 2));
                dist = Math.sqrt(dist);

                //If the distance is LESS than the given radius...
                if (dist < radius) {
                    //...AND the distance is less than the current min, update current closest node
                    if (dist < currLeastDist) {
                        currClosest = n;
                        currLeastDist = dist;
                    }
                }
            }
        }

        if (currClosest.isEmpty()) return;

        String currCloseName = currClosest.get("LONGNAME");
        //System.out.println(currClosest.get("LONGNAME"));

        if (activePath == 0) {
            if (this.firstNodeSelect == 0) {
                firstNodeSelect = 1;
                this.firstNode = currCloseName;
            } else if (this.firstNodeSelect == 1) {
                if (this.firstNode != null && currCloseName != null) {
                    firstNodeSelect = 0;
                    pathList.add(this.firstNode);
                    pathList.add(currCloseName);
                    findPath(pathList.get(0), pathList.get(1));
                    activePath = 1;
                }
            }
        } else if (activePath == 1) {
            anchor.getChildren().clear();
            pathList.add(pathList.size() - 1, currCloseName);
            for (int i = 0; i < pathList.size() - 1; i++) {
                findPath(pathList.get(i), pathList.get(i + 1));
            }
        }
    }

    public void changeFloorNodes() {
/*        //nodeGridAnchor.getChildren().clear();
        GraphicsContext gc = nodeCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, nodeCanvas.getWidth(), nodeCanvas.getHeight());
        int count = 0;
        try {
            List<Map<String, String>> edges = db.getEdges();
            List<String> nodesList = new ArrayList<>();
            for (Map<String, String> edge : edges) {
                String startNode = edge.get("STARTNODE");
                String endNode = edge.get("ENDNODE");
                String bothNodes = startNode.concat(endNode);
                if (!nodesList.contains(bothNodes) || (!nodesList.contains(endNode.concat(startNode)))) { //??
                    try {
                        Map<String, String> snode = db.getNode(startNode);
                        Map<String, String> enode = db.getNode(endNode);
                        if (FLOOR == 1 &&
                                ((snode.get("FLOOR").equals("1")) && (snode.get("BUILDING").equals("Tower") || snode.get("BUILDING").equals("45 Francis"))) &&
                                ((enode.get("FLOOR").equals("1")) && (enode.get("BUILDING").equals("Tower") || enode.get("BUILDING").equals("45 Francis")))) {
                            drawNodes(snode, enode);
                            nodesList.add(startNode + endNode);
                            count++;
                        } else if (groundFloor.isVisible() &&
                                (snode.get("FLOOR").equals("G") && enode.get("FLOOR").equals("G"))) {
                            drawNodes(snode, enode);
                            nodesList.add(startNode + endNode);
                            count++;
                        }
                    } catch (SQLException sq) {
                        sq.printStackTrace();
                    }
                }
            }
        } catch (SQLException sq) {
            sq.printStackTrace();
        }
        System.out.println(count);*/
    }

    public void drawSingleNode(Node node) {
        double x = xScale(node.getXcoord());
        double y = yScale(node.getYcoord());
        double radius = 4;
        x = x - (radius / 2);
        y = y - (radius / 2);
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.setFill(Color.RED);
        gc.fillOval(x, y, radius, radius);
    }

    public void drawNodes(Node snode, Node enode) {
        if (snode.getFloor().equals(FLOOR) || enode.getFloor().equals(FLOOR)){
            GraphicsContext gc = mapCanvas.getGraphicsContext2D();
            gc.moveTo(xScale(snode.getXcoord()), yScale(snode.getYcoord()));
            gc.lineTo(xScale(enode.getXcoord()), yScale(enode.getYcoord()));
            gc.stroke();

            drawSingleNode(snode);
            drawSingleNode(enode);
        }
    }


    public void addStop() {
/*        if (!(startLocation.getSelectionModel().getSelectedItem() == null) && (!(destination.getSelectionModel().getSelectedItem() == null))) {
            intermediate.setVisible(true);
        }*/
    }



}


