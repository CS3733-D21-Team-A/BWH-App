package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.lang.Math.floor;

public class Navigation {

    @FXML private AnchorPane anchor;
    @FXML private JFXButton homeButton;
    @FXML private JFXButton helpButton;
    @FXML private JFXComboBox startLocation;
    @FXML private JFXComboBox destination;
    @FXML private JFXButton findPathButton;

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
            node.setCenterX(xScale(5000));
            node.setCenterY(yScale(3400));
            node.setRadius(5);
            anchor.getChildren().add(node);
        }


}
