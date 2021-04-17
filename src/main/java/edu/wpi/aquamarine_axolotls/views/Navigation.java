package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.pathplanning.SearchAlgorithm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

public class Navigation  extends SPage{

    @FXML
    private AnchorPane anchor;
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
    ObservableList<String> options = FXCollections.observableArrayList();
    DatabaseController db;

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
    }

    /**
     * Gets the closest node to the mouse cursor when clicked
     * @param x The x-coord of the mouse, passed as a double so we can do trigonometry with it
     * @param y The y-coord of the mouse
     * @param radius The radius in which to search -- nodes beyond that distance will not be considered
     */
    public String getNearestNode(double x, double y, double radius) {
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
            double currNodeX = Double.parseDouble(n.get("XCOORD"));
            double currNodeY = Double.parseDouble(n.get("YCOORD"));

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

        //Return the long name of this node
        return currClosest.get("LONGNAME");
    }
}
