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
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import sun.font.FontConfigManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

//adds all nodes
/*
    public void findPath() {
        anchor.getChildren().clear();
        try {
            List<Map<String, String>> nodes = db.getNodes();

            for (Map<String, String> node : nodes) {
                Double scaledX = xScale((int) Double.parseDouble(node.get("XCOORD")));
                Double scaledY = yScale((int) Double.parseDouble(node.get("YCOORD")));
                String nodeID = node.get("NODEID");
                Circle circ = new Circle();
                circ.setCenterX(scaledX);
                circ.setCenterY(scaledY);
                circ.setRadius(2);
                circ.setFill(Color.RED);


                try{
                    List<Map<String, String>> edges = db.getEdgesConnectedToNode(nodeID);
                    for(Map<String,String> edge: edges) {

                        Line line = new Line();
                        line.setStartY(scaledY);
                        line.setStartX(scaledX);
                        line.setEndX(db.getNode(edge));
                        line.setEndY(scaledY);

                        prevScaledX = scaledX;
                        prevScaledY = scaledY;
                    }
                }catch(SQLException sq){
                    sq.printStackTrace();
                ;}

                anchor.getChildren().addAll(circ,line);


                prevnode = node;
            }
        } catch (SQLException sq) {
            sq.printStackTrace();
        }
    }

*/

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
    }

}
