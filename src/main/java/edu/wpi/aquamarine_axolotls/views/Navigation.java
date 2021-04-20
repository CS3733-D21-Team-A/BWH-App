package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.pathplanning.SearchAlgorithm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

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
    ObservableList<String> options = FXCollections.observableArrayList();
    DatabaseController db;

    @FXML
    public void initialize() {
        try {
            db = new DatabaseController();
            List<Map<String, String>> nodes = db.getNodes();
            for (Map<String, String> node : nodes) {
                if (node.get("NODETYPE").equals("PARK")
                        || ( node.get("BUILDING").equals("45 Francis") && node.get("FLOOR").equals("1"))
                        || ( node.get("BUILDING").equals("Tower") && node.get("FLOOR").equals("1")) ) {
                    options.add(node.get("NODEID"));
                }

            }

            startLocation.setItems(options);
            destination.setItems(options);

            db.close();
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

    public void clearNodes(ActionEvent actionEvent) {
    }

    public void getNearestNode(MouseEvent mouseEvent) {
    }

    public void addStop(){
        if (!(startLocation.getSelectionModel().getSelectedItem() == null)&& (!(destination.getSelectionModel().getSelectedItem() == null))){
            intermediate.setVisible(true);
        }
    }
}
