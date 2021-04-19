package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXToggleButton;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;


public abstract class SEditing extends SPage{
    @FXML ImageView groundFloor;
    @FXML ImageView floor1;
    @FXML MenuItem floor1View;
    @FXML MenuItem groundFloorView;

    @FXML AnchorPane anchor;
    @FXML JFXToggleButton toggleButton;

    @FXML AnchorPane nodeGridAnchor;

    @FXML
    public void adminHome(ActionEvent actionEvent) {
        sceneSwitch("AdminMainPage");
    }

    public void changeGroundFloor(){
        groundFloor.setVisible(true);
        floor1.setVisible(false);
    }

    public void changeFloor1(){
        groundFloor.setVisible(false);
        floor1.setVisible(true);
        System.out.println("got floor 1");
    }

    @FXML
    public void chartAnchor() {
        boolean isSelected = toggleButton.isSelected();

        if(isSelected) {
            anchor.setVisible(true);
            TranslateTransition translateTransition = new TranslateTransition((Duration.seconds(.5)), anchor);
            translateTransition.setFromY(450);
            translateTransition.setToY(0);

            translateTransition.play();
        }
        if(!isSelected) {
            TranslateTransition translateTransition1 = new TranslateTransition((Duration.seconds(.5)), anchor);
            translateTransition1.setFromY(0);
            translateTransition1.setToY(450);
            translateTransition1.play();

        }
    }

    public Double xScale(int xCoord) {
        Double xCoordDouble = new Double(xCoord);
        Double imgWidth = 425.0;
        Double proportion = imgWidth / 5000;

        Double newXCoord = xCoordDouble * proportion; //may need to add margins depending on how it's placed on

        return newXCoord;
    }

    public Double yScale(int yCoord) {
        Double yCoordDouble = new Double(yCoord);
        Double imgWidth = 293.0;
        Double proportion = imgWidth / 3400;

        Double newYCoord = yCoordDouble * proportion; //may need to add margins depending on how it's placed on

        return newYCoord;
    }


}
