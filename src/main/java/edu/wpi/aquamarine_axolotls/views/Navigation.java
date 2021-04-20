package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.pathplanning.SearchAlgorithm;
import javafx.animation.*;
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
import javafx.scene.text.Text;
import javafx.util.Duration;
//import sun.font.FontConfigManager;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalTime;
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
    private JFXButton addStopbtn;

    @FXML
    private JFXComboBox intermediate;

    @FXML
    private AnchorPane mainAnchor;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private Text time;
    ObservableList<String> options = FXCollections.observableArrayList();
    DatabaseController db;
    private int firstNodeSelect = 0;
    private String firstNode;
    private List<String> pathList = new ArrayList<>();
    private int activePath = 0;


    @FXML
    public void initialize() {
        intermediate.setVisible(false);
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime currentTime = LocalTime.now();
            int hour = currentTime.getHour();
            if(hour>12){
                hour = currentTime.getHour()-12;
            }
            time.setText(hour + ":" + currentTime.getMinute() + ":" + currentTime.getSecond());
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

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


    public Double xScaleDouble(double xCoord) {
        Double imgWidth = 438.0;
        Double proportion = imgWidth / 5000;

        Double newXCoord = xCoord * proportion; //may need to add margins depending on how it's placed on

        return newXCoord;
    }


    public Double yScaleDouble(double yCoord) {
        Double imgWidth = 298.0;
        Double proportion = imgWidth / 3400;

        Double newYCoord = yCoord * proportion; //may need to add margins depending on how it's placed on

        return newYCoord;
    }


    public void findPath() {
        if (startLocation.getSelectionModel().getSelectedItem() == null || destination.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        String start = startLocation.getSelectionModel().getSelectedItem().toString();
        String end = destination.getSelectionModel().getSelectedItem().toString();
        anchor.getChildren().clear();
        pathList.clear();
        pathList.add(start);
        pathList.add(end);
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


        Double prevX = xScaleDouble(pathNodes.get(0).getXcoord()); // TODO : fix this jank code
        Double prevY = yScaleDouble(pathNodes.get(0).getYcoord());


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


    public void findPaths2() {
        if (activePath == 0) anchor.getChildren().clear();
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
     * @param start String, long name of start node
     * @param end String, long name of end node
     */
    public void findPath(String start, String end) {
        if(activePath == 0) anchor.getChildren().clear();
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


        Double prevX = xScaleDouble(pathNodes.get(0).getXcoord()); // TODO : fix this jank code
        Double prevY = yScaleDouble(pathNodes.get(0).getYcoord());


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

        //double x = xScaleDouble(event.getX());
        //double y = yScaleDouble(event.getY());
        double x = event.getX();
        double y = event.getY();

        System.out.println(x + " " + y);
        double radius = 20;
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
            double currNodeX = xScaleDouble(Double.parseDouble(n.get("XCOORD")));
            double currNodeY = yScaleDouble(Double.parseDouble(n.get("YCOORD")));

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

        String currCloseName = currClosest.get("LONGNAME");
        System.out.println(currClosest.get("LONGNAME"));

        if (activePath == 0) {
            //Return the long name of this node
            //return currClosest.get("LONGNAME");
            if (this.firstNodeSelect == 0) {
                firstNodeSelect = 1;
                this.firstNode = currCloseName;
            }
            else if (this.firstNodeSelect == 1) {
                if (this.firstNode != null && currCloseName != null) {
                    firstNodeSelect = 0;
                    pathList.add(this.firstNode);
                    pathList.add(currCloseName);
                    findPath(pathList.get(0), pathList.get(1));
                    activePath = 1;
                }
            }
        }
        else if (activePath == 1) {
            anchor.getChildren().clear();
            pathList.add(pathList.size() - 1, currCloseName);
            for (int i = 0; i < pathList.size() - 1; i++) {
                findPath(pathList.get(i), pathList.get(i + 1));
            }
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


    public void clearNodes() {
        anchor.getChildren().clear();
        activePath = 0;
    }

    public void addStop(){
        if (!(startLocation.getSelectionModel().getSelectedItem() == null)&& (!(destination.getSelectionModel().getSelectedItem() == null))){
            intermediate.setVisible(true);
        }
    }
}


