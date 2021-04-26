package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

public class Navigation extends SPage {

    @FXML private JFXComboBox startLocation;
    @FXML private JFXComboBox destination;
    @FXML private JFXButton findPathButton;
    @FXML private JFXComboBox intermediate;
    @FXML private Label etaLabel;
    @FXML Canvas mapCanvas;
    @FXML ScrollPane mapScrollPane;

    private Group zoomGroup;
    private int zoom;

    ObservableList<String> options = FXCollections.observableArrayList();
    DatabaseController db;
    List<Node> validNodes = new ArrayList<>();
    private int firstNodeSelect = 0;
    private String firstNode;
    private List<String> stopList = new ArrayList<>();
    private List<Node> currPath = new ArrayList<>();
    private int activePath = 0;
    private Map<String, String> floors;

    static String FLOOR = "G";

    @FXML
    public void initialize() {

        if(SearchAlgorithmContext.getSearchAlgorithmContext().context == null){
            SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
        }
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

            drawFloor(FLOOR);
            zoom = 1;

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
        zoom = 1;
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
        stopList.clear();
        currPath.clear();
        activePath = 0;
        drawFloor(FLOOR);
        startLocation.getSelectionModel().clearSelection();
        destination.getSelectionModel().clearSelection();
        intermediate.getSelectionModel().clearSelection();
    }

    public void drawFloor(String floor){
        resetMap(FLOOR);
        for (Node n: validNodes) {
            if (n.getFloor().equals(floor)) drawSingleNode(n, mapCanvas.getGraphicsContext2D());
        }

        if (activePath == 1) {
            for (int i = 0; i < currPath.size() - 1; i++) {
                if (currPath.get(i).getFloor().equals(FLOOR) &&
                    currPath.get(i + 1).getFloor().equals(FLOOR)) {
                    drawNodes(currPath.get(i), currPath.get(i + 1));
                }
            }
        }
    }

    public void resetMap(String floor) {
        resetZoom();
        mapCanvas.getGraphicsContext2D().clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        mapCanvas.getGraphicsContext2D().drawImage(new Image(floors.get(floor)), 0,0, mapCanvas.getWidth(), mapCanvas.getHeight());
        //FLOOR = floor;
    }

    public void changeFloor1() throws FileNotFoundException {
        FLOOR = "1";
        drawFloor(FLOOR);
        //resetMapAndDraw("1");
    }

    public void changeGroundFloor() throws FileNotFoundException {
        FLOOR = "G";
        drawFloor(FLOOR);
        //resetMapAndDraw("G");
    }

    public void findPath() {
        currPath.clear();
        stopList.clear();
        activePath = 0;
        if (startLocation.getSelectionModel().getSelectedItem() == null || destination.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        String start = startLocation.getSelectionModel().getSelectedItem().toString();
        String end = destination.getSelectionModel().getSelectedItem().toString();
        stopList.add(start);
        stopList.add(end);
        findPathSingleSegment(start, end);
        drawFloor(FLOOR);
    }


    public void startEndlocation() {
/*        if (activePath == 0) anchor.getChildren().clear();
        String start = startLocation.getSelectionModel().getSelectedItem().toString();
        String end = destination.getSelectionModel().getSelectedItem().toString();
        SearchAlgorithmContext searchAlgorithmContext;
        List<Node> pathNodes = new ArrayList<>();
        try {
            searchAlgorithmContext = new SearchAlgorithmContext(new AStar());
            pathNodes = searchAlgorithmContext.getPath(start, end);

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
     * @param start String, long name of start node
     * @param end   String, long name of end node
     */
    public void findPathSingleSegment(String start, String end) {
        double etaTotal, minutes, seconds;
        //currPath.clear();
        //for (int i = 0; i < stopList.size() - 1; i++) {
            //currPath.addAll(searchAlgorithmContext.getPath(stopList.get(i), stopList.get(i + 1)));
        //}

        currPath.addAll(SearchAlgorithmContext.getSearchAlgorithmContext().getPath(start, end));
        System.out.println(SearchAlgorithmContext.getSearchAlgorithmContext().context);

        /*
        etaTotal = searchAlgorithmContext.getETA(currPath);
        minutes = Math.floor(etaTotal);
        seconds = Math.floor((etaTotal - minutes) * 60);
        etaLabel.setText((int) minutes + ":" + (int) seconds);*/

        if(currPath.isEmpty()) return;

        firstNodeSelect = 0;
        activePath = 1;
        //drawFloor(FLOOR);
        //drawSingleNode(getNodeFromValid(stopList.get(stopList.size() - 1)));
    }

    /**
     * Gets the closest node to the mouse cursor when clicked
     */
    public void getNearestNode(javafx.scene.input.MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            //System.out.println("Clicked map");

            //double x = xScale(event.getX());
            //double y = yScale(event.getY());
            double x = event.getX();
            double y = event.getY();

            //System.out.println(x + " " + y);
            double radius = 20;

            //Establish current closest recorded node and current least distance
            Node currClosest = null;
            double currLeastDist = 100000;

            //Loop through nodes
            for (Node n : validNodes) {
                if ((FLOOR == "G" && n.getFloor().equals("G"))
                        || (FLOOR == "1" && n.getFloor().equals("1"))) {
                    //Get the x and y of that node
                    double currNodeX = xScale(n.getXcoord());
                    double currNodeY = yScale(n.getYcoord());

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

            if (currClosest == null) return;

            else {
                String currCloseName = currClosest.getLongName();

                if (activePath == 0) { //if there's no active path, we'll handle that
                    if (firstNodeSelect == 0) {
                        firstNode = currCloseName;
                        firstNodeSelect = 1;
                    } else if (firstNodeSelect == 1) {
                        stopList.clear();
                        stopList.add(firstNode);
                        stopList.add(currCloseName);
                        currPath.clear();
                        findPathSingleSegment(stopList.get(0), stopList.get(1));
                        drawFloor(FLOOR);
                    }
                }
                else if (activePath == 1) {
                    stopList.add(stopList.size() - 1, currCloseName);
                    currPath.clear();
                    for (int i = 0; i < stopList.size() - 1; i++) {
                        findPathSingleSegment(stopList.get(i), stopList.get(i + 1));
                    }
                    drawFloor(FLOOR);
                }
            }

            //System.out.println(currClosest.get("LONGNAME"));

//        if (activePath == 0) {
//            if (this.firstNodeSelect == 0) {
//                firstNodeSelect = 1;
//                this.firstNode = currCloseName;
//            } else if (this.firstNodeSelect == 1) {
//                if (this.firstNode != null && currCloseName != null) {
//                    firstNodeSelect = 0;
//                    pathList.add(this.firstNode);
//                    pathList.add(currCloseName);
//                    findPath(pathList.get(0), pathList.get(1));
//                    activePath = 1;
//                }
//            }
//        } else if (activePath == 1) {
//            anchor.getChildren().clear();
//            pathList.add(pathList.size() - 1, currCloseName);
//            for (int i = 0; i < pathList.size() - 1; i++) {
//                findPath(pathList.get(i), pathList.get(i + 1));
//            }
//        }
        }
    }

    private double getDistBetweenNodes(Node snode, Node enode) {

        double sNodeX = xScale(snode.getXcoord());
        double sNodeY = yScale(snode.getYcoord());

        double eNodeX = xScale(enode.getXcoord());
        double eNodeY = yScale(enode.getYcoord());

        //Get the difference in x and y between input coords and current node coords
        double xOff = eNodeX - sNodeX;
        double yOff = eNodeY - sNodeY;

        //Give 'em the ol' pythagoras maneuver
        double dist = (Math.pow(xOff, 2) + Math.pow(yOff, 2));
        dist = Math.sqrt(dist);

        return dist;
    }

    public void drawSingleNode(Node node, GraphicsContext gc) {
        double x = xScale(node.getXcoord());
        double y = yScale(node.getYcoord());
        double radius = 3;
        x = x - (radius / 2);
        y = y - (radius / 2);
        gc.setFill(Color.BLUE);
        gc.fillOval(x, y, radius, radius);
    }

    public void drawNodes(Node snode, Node enode) {
        if (snode.getFloor().equals(FLOOR) && enode.getFloor().equals(FLOOR)){
            GraphicsContext gc = mapCanvas.getGraphicsContext2D();
            /*gc.moveTo(xScale(snode.getXcoord()), yScale(snode.getYcoord()));
            gc.lineTo(xScale(enode.getXcoord()), yScale(enode.getYcoord()));
            gc.stroke();*/
            gc.strokeLine(xScale(snode.getXcoord()), yScale(snode.getYcoord()), xScale(enode.getXcoord()), yScale(enode.getYcoord()));

            drawSingleNode(snode, gc);
            drawSingleNode(enode, gc);
        }
    }


    public void addStop() {
/*        if (!(startLocation.getSelectionModel().getSelectedItem() == null) && (!(destination.getSelectionModel().getSelectedItem() == null))) {
            intermediate.setVisible(true);
        }*/
    }



}


