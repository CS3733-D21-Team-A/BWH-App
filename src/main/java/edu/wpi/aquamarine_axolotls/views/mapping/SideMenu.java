package edu.wpi.aquamarine_axolotls.views.mapping;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

public class SideMenu extends Navigation{

    @FXML
    public ImageView curArrow;
    @FXML
    public Label curDirection;

    @FXML
    Label startLabel;
    @FXML
    Label endLabel;

    @FXML
    VBox listOfDirections;

    @FXML
    TreeTableView<String> treeTable;

    Navigation navController;

    @Override
    public void initialize() throws SQLException, IOException {

    }

    public void goToStepByStep(ActionEvent actionEvent) {
        navController.goToStepByStep(actionEvent);
    }

    public void toggleVoiceDirectionButton(ActionEvent actionEvent) {
        navController.toggleVoiceDirectionButton(actionEvent);
    }

    public void goToListOfDirections(ActionEvent actionEvent) {
        navController.goToListOfDirections(actionEvent);
    }

    public void startPath(ActionEvent actionEvent) {
        navController.startPath(actionEvent);
    }

    public void clearNav(ActionEvent actionEvent) {
        navController.clearNav(actionEvent);
    }

    public void findPath() {
        navController.findPath();
    }

    public void regress(ActionEvent actionEvent) {
        navController.regress();
    }

    public void progress(ActionEvent actionEvent) {
        navController.progress();
    }


    public void setStartLabel(String startingPoint) {
        this.startLabel.setText(startingPoint);
    }

    public void setEndLabel(String endingPoint) {
        this.startLabel.setText(endingPoint);
    }

    public void setCurArrow(Image arrow) {
        this.curArrow.setImage(arrow);
    }

    public void setCurDirection(String textDirection) {
        this.curDirection.setText(textDirection);
    }

    public void goToTreeView(ActionEvent actionEvent) {
        navController.goToTreeView();
    }
}
