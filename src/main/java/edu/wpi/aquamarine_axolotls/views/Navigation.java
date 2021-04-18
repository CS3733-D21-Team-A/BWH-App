package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.pathplanning.SearchAlgorithm;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
//import sun.font.FontConfigManager;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

public class Navigation  extends SPage{

    @FXML
    private AnchorPane anchor;
    @FXML
    private AnchorPane nodeGridAnchor;
    @FXML
    private ImageView mapP;
    @FXML
    private JFXButton homeButton;
    @FXML
    private JFXButton helpB;
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
    private AnchorPane mainAnchor;
    @FXML
    private JFXHamburger hamburger;

    ObservableList<String> options = FXCollections.observableArrayList();
    DatabaseController db;
    private int firstNodeSelect = 0;
    private String firstNode;


    @FXML
    public void initialize() {
        try {
            db = new DatabaseController();
            List<Map<String, String>> nodes = db.getNodes();
            for (Map<String, String> node : nodes) {
                if (node.get("NODETYPE").equals("EXIT") || node.get("NODETYPE").equals("PARK")) {
                    options.add(node.get("LONGNAME"));
                }

            }

            drawNodes();

            startLocation.setItems(options);
            destination.setItems(options);
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
    public Double xScale(int xCoord) {
        Double xCoordDouble = new Double(xCoord);
        Double imgWidth = 438.0;
        Double proportion = imgWidth / 5000;

        Double newXCoord = xCoordDouble * proportion; //may need to add margins depending on how it's placed on

        return newXCoord;
    }

    public Double yScale(int yCoord) {
        Double yCoordDouble = new Double(yCoord);
        Double imgWidth = 298.0;
        Double proportion = imgWidth / 3400;

        Double newYCoord = yCoordDouble * proportion; //may need to add margins depending on how it's placed on

        return newYCoord;
    }


    public void findPath() {
        anchor.getChildren().clear();
        int count = 0;
        try {
            List<Map<String, String>> edges = db.getEdges();
            List<String> nodesList = new ArrayList<String>();
            for (Map<String, String> edge : edges) {
                Circle circ1 = new Circle();
                Circle circ2 = new Circle();

                String startNode = edge.get("STARTNODE");
                String endNode = edge.get("ENDNODE");
                String bothNodes = startNode.concat(endNode);
                if (!nodesList.contains(bothNodes) || (!nodesList.contains(endNode.concat(startNode)))) { //??
                    try {
                        Map<String, String> snode = db.getNode(startNode);
                        Map<String, String> enode = db.getNode(endNode);
                        Double startX = xScale((int) Double.parseDouble(snode.get("XCOORD")));
                        Double startY = yScale((int) Double.parseDouble(snode.get("YCOORD")));
                        Double endX = xScale((int) Double.parseDouble(enode.get("XCOORD")));
                        Double endY = yScale((int) Double.parseDouble(enode.get("YCOORD")));

                        circ1.setCenterX(startX);
                        circ1.setCenterY(startY);
                        circ2.setCenterX(endX);
                        circ2.setCenterY(endY);
                        circ1.setRadius(2);
                        circ2.setRadius(2);
                        circ1.setFill(Color.RED);
                        circ2.setFill(Color.RED);

                        Line line = new Line();
                        line.setStartX(startX);
                        line.setStartY(startY);
                        line.setEndX(endX);
                        line.setEndY(endY);
                        line.setStroke(Color.WHITE);
                        anchor.getChildren().addAll(circ1, circ2, line);
                        nodesList.add(startNode + endNode);
                        count++;
                    } catch (SQLException sq) {
                        sq.printStackTrace();
                    }
                }
            }
        }catch (SQLException sq) {
            sq.printStackTrace();
        } System.out.println(count);
    }


    public void findPaths2() {
        anchor.getChildren().clear();
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


                anchor.getChildren().addAll(circ,line);
                prevX = scaledX;
                prevY = scaledY;
            }
            firstNodeSelect = 0;
    }


    /**
     * Alternate declaration of findPath() that takes a specific start and end, used for clicking nodes on the map directly
     * @param start
     * @param end
     */
    public void findPath(String start, String end) {
        anchor.getChildren().clear();
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


            anchor.getChildren().addAll(circ,line);
            prevX = scaledX;
            prevY = scaledY;
        }
        firstNodeSelect = 0;
    }




    /**
     * Gets the closest node to the mouse cursor when clicked
     */
    public void getNearestNode(javafx.scene.input.MouseEvent event) {

        System.out.println("Clicked map");
        
        //double x = xScale((int) event.getX());
        //double y = yScale((int) event.getY());
        double x = event.getX();
        double y = event.getY();

        System.out.println(x + " " + y);
        double radius = 30;
        List<Map<String, String>> nodes = new ArrayList<>();

        //Get nodes from database
        try {
            nodes = db.getNodes();
        }
        catch(SQLException se) {
            se.printStackTrace();
        }

        //Establish current closest recorded node and current least distance
        Map<String, String> currClosest = new HashMap<>();
        double currLeastDist = 100000;

        //Loop through nodes
        for (Map<String, String> n : nodes) {
            //Get the x and y of that node
            double currNodeX = xScale(Integer.parseInt(n.get("XCOORD")));
            double currNodeY = yScale(Integer.parseInt(n.get("YCOORD")));

            //Get the difference in x and y between input coords and current node coords
            double xOff = Math.abs(x - currNodeX);
            double yOff = Math.abs(y - currNodeY);

            //Give 'em the ol' pythagoras maneuver
            double dist = (Math.pow(xOff, 2) + Math.pow(yOff, 2));
            dist = Math.sqrt(dist);

            //If the distance is LESS than the given radius...
            if (dist < radius) {
                //...AND the distance is less than the current min, update current closest node
                if (dist < currLeastDist) currClosest = n;
            }
        }

        String currCloseName = currClosest.get("LONGNAME");
        System.out.println(currClosest.get("LONGNAME"));
        
        //Return the long name of this node
        //return currClosest.get("LONGNAME");
        if (this.firstNodeSelect == 0) {
            firstNodeSelect = 1;
            this.firstNode = currCloseName;
        }
        else if (this.firstNodeSelect == 1) {
            if (this.firstNode != null && currCloseName != null) {
                firstNodeSelect = 0;
                findPath(this.firstNode, currCloseName);
            }
            else if (currCloseName != null) this.firstNode = currCloseName;
        }
    }


    public void drawNodes() {
        nodeGridAnchor.getChildren().clear();
        int count = 0;
        try {
            List<Map<String, String>> edges = db.getEdges();
            List<String> nodesList = new ArrayList<>();
            for (Map<String, String> edge : edges) {
                Circle circ1 = new Circle();
                Circle circ2 = new Circle();

                String startNode = edge.get("STARTNODE");
                String endNode = edge.get("ENDNODE");
                String bothNodes = startNode.concat(endNode);
                if (!nodesList.contains(bothNodes) || (!nodesList.contains(endNode.concat(startNode)))) { //??
                    try {
                        Map<String, String> snode = db.getNode(startNode);
                        Map<String, String> enode = db.getNode(endNode);
                        Double startX = xScale((int) Double.parseDouble(snode.get("XCOORD")));
                        Double startY = yScale((int) Double.parseDouble(snode.get("YCOORD")));
                        Double endX = xScale((int) Double.parseDouble(enode.get("XCOORD")));
                        Double endY = yScale((int) Double.parseDouble(enode.get("YCOORD")));

                        circ1.setCenterX(startX);
                        circ1.setCenterY(startY);
                        circ2.setCenterX(endX);
                        circ2.setCenterY(endY);
                        circ1.setRadius(2);
                        circ2.setRadius(2);
                        circ1.setFill(Color.RED);
                        circ2.setFill(Color.RED);

                        Line line = new Line();
                        line.setStartX(startX);
                        line.setStartY(startY);
                        line.setEndX(endX);
                        line.setEndY(endY);
                        line.setStroke(Color.WHITE);
                        nodeGridAnchor.getChildren().addAll(circ1, circ2, line);
                        nodesList.add(startNode + endNode);
                        count++;
                    } catch (SQLException sq) {
                        sq.printStackTrace();
                    }
                }
            }
        }catch (SQLException sq) {
            sq.printStackTrace();
        } System.out.println(count);
    }
}


