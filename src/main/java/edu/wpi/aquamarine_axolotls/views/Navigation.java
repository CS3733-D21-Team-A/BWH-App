package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.pathplanning.SearchAlgorithm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import javax.print.attribute.standard.Destination;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Navigation {

    @FXML private AnchorPane anchor;
    @FXML private JFXButton homeButton;
    @FXML private JFXButton helpButton;
    @FXML private JFXComboBox startLocation;
    @FXML private JFXComboBox destination;
    @FXML private JFXButton findPathButton;
    ObservableList<String> options = FXCollections.observableArrayList(
                    "Parking Spot 1 45 Francis Street Lobby",
                            "Parking Spot 45 Francis Street Lobby",
                            "Parking Spot 3 45 Francis Street Lobby",
                            "Parking Spot 4 45 Francis Street Lobby",
                            "Parking Spot 5 45 Francis Street Lobby",
                            "Parking Spot 6 45 Francis Street Lobby",
                            "Parking Spot 7 45 Francis Street Lobby",
                            "Parking Spot 8 45 Francis Street Lobby",
                            "Parking Spot 9 45 Francis Street Lobby",
                            "Parking Spot 10 45 Francis Street Lobby",
                            "Parking Spot 11 45 Francis Street Lobby",
                            "Parking Spot 12 45 Francis Street Lobby",
                            "Parking Spot 13 45 Francis Street Lobby",
                            "Parking Spot 14 45 Francis Street Lobby",
                            "Parking Spot 15 45 Francis Street Lobby",
                            "Parking Spot 1 80 Francis Parking",
                            "Parking Spot 2 80 Francis Parking",
                            "Parking Spot 3 80 Francis Parking",
                            "Parking Spot 4 80 Francis Parking",
                            "Parking Spot 5 80 Francis Parking",
                            "Parking Spot 6 80 Francis Parking",
                            "Parking Spot 7 80 Francis Parking",
                            "Parking Spot 8 80 Francis Parking",
                            "Parking Spot 9 80 Francis Parking",
                            "Parking Spot 10 80 Francis Parking",
                            "Entrance 75 Francis St",
                            "Entrance ER 75 Francis Street");
    DatabaseController db = new DatabaseController();

    @FXML
    public void initialize(){
        startLocation.setItems(options);
        destination.setItems(options);
    }

    @FXML //need to navigate to home
    public void return_Home(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/home.fxml"));
            Aapp.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
    * Scales the x coordinate from table for image (5000,3400)
    * @param xCoord x coordinate from table
    * @return scaled X coordinate
     */
    public Double xScale(int xCoord){
        Double xCoordDouble = new Double(xCoord);
        Double imgWidth = 438.0;
        Double proportion = imgWidth/5000;

        Double newXCoord = xCoordDouble*proportion; //may need to add margins depending on how it's placed on

        return newXCoord;
    }

    public Double yScale(int yCoord){
        Double yCoordDouble = new Double(yCoord);
        Double imgWidth = 298.0;
        Double proportion = imgWidth/3400;

        Double newYCoord = yCoordDouble*proportion; //may need to add margins depending on how it's placed on

        return newYCoord;
    }

    public void findPath(){
        anchor.getChildren().removeAll();

        String start = startLocation.getSelectionModel().getSelectedItem().toString();
        String end = destination.getSelectionModel().getSelectedItem().toString();
        SearchAlgorithm searchAlgorithm = new SearchAlgorithm();
        List<Node> pathNodes = searchAlgorithm.getPath(start, end);

        Double prevX = 1000.0;
        Double prevY = 1000.0;
        for (int i = 0; i < pathNodes.size(); i++){
            Circle circ = new Circle();
            Line line = new Line();
            Double scaledX = xScale(pathNodes.get(i).getXcoord());
            Double scaledY = yScale(pathNodes.get(i).getYcoord());

            circ.setCenterX(scaledX);
            circ.setCenterY(scaledY);
            circ.setRadius(1);

            line.setStartX(scaledX);
            line.setStartY(scaledY);
            line.setEndX(prevX);
            line.setEndY(prevY);

            anchor.getChildren().addAll(circ, line);
            prevX = scaledX;
            prevY = scaledY;
        }

        /*
        try{
            Map<String, String> node = db.getNode("aPARK001GG"); // only displays a node and top left and bottom right
            Circle circ = new Circle();
            circ.setCenterX(xScale(Integer.parseInt(node.get("XCOORD")) ));
            circ.setCenterY(yScale(Integer.parseInt(node.get("YCOORD")) ));
            circ.setRadius(1);
            anchor.getChildren().add(circ);

            circ = new Circle();
            circ.setCenterX(0);
            circ.setCenterY(0);
            circ.setRadius(4);
            anchor.getChildren().add(circ);

            circ = new Circle();
            circ.setCenterX(xScale(5000));
            circ.setCenterY(yScale(3400));
            circ.setRadius(1);
            anchor.getChildren().add(circ);
        } catch(SQLException e){
            e.printStackTrace();
        }*/
        return;
    }


}
