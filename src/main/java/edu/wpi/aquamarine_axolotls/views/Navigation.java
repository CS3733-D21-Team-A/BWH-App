package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import javax.print.attribute.standard.Destination;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Navigation {

    @FXML private AnchorPane anchor;
    @FXML private JFXButton homeButton;
    @FXML private JFXButton helpButton;
    @FXML private JFXComboBox startLocation;
    @FXML private JFXComboBox destination;
    @FXML private JFXButton findPathButton;


    ObservableList<String> options =
            FXCollections.observableArrayList(
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
        Double imgWidth = 327.0;
        Double proportion = imgWidth/3400;

        Double newYCoord = yCoordDouble*proportion; //may need to add margins depending on how it's placed on

        return newYCoord;
    }

    public void initiateNodes(List<Map<String,String>> nodes){
        for (int i = 0; i < nodes.size(); i++){
            Map<String, String> currentNode = nodes.get(i);
            Double scaledX= xScale(Integer.parseInt(currentNode.get("xcoord")));
            Double scaledY= yScale(Integer.parseInt(currentNode.get("ycoord")));

            Circle node = new Circle();
            node.setCenterX(5000);
            node.setCenterY(3400);
            node.setRadius(4);
            anchor.getChildren().add(node);

        }
        return;
    }
    @FXML
    public void testdrawcircle(){
            Circle node = new Circle();
            node.setCenterX(xScale(1 ));
            node.setCenterY(yScale(1));
            node.setRadius(5);
            anchor.getChildren().add(node);
        }


}
