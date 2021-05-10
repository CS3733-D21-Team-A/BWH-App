package edu.wpi.aquamarine_axolotls.views.mapping;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
    Label etaLabel;

    @FXML
    TreeTableView<String> treeTable;

    Navigation navController;

    double eta;

    @Override
    public void initialize() throws SQLException, IOException {

    }

    public void goToStepByStep() {
        navController.goToStepByStep();
    }

    public void toggleVoiceDirectionButton() {
        navController.toggleVoiceDirectionButton();
    }

    public void goToListOfDirections() {
        navController.goToListOfDirections();
    }

    public void startPath() {
        navController.startPath();
    }

    public void clearNav() {
        startLabel.setText("Start Location");
        endLabel.setText("End Location");
        navController.clearNav();
    }

    public void findPath() {
        navController.findPath();
    }

    public void regress() throws SQLException, InterruptedException {
        navController.regress();
    }

    public void progress() throws SQLException, InterruptedException {
        navController.progress();
    }

    public void setEtaLabel(double eta){
        this.eta = eta;
        etaLabel.setText(etaToString(eta));
    }

    private String etaToString(Double eta){
        int minutes = eta.intValue();
        int seconds = (int) (60 * (eta - minutes));
        String minString = String.valueOf(minutes);
        String secString = String.valueOf(seconds);
        return minString + " minutes " + secString + " seconds";
    }

    public void updateETA(double number){
        setEtaLabel(eta + number);
    }

    public void addToListOfDirections(String directionText){
        ImageView arrow = new ImageView(navController.textDirectionToImage(directionText));
        arrow.setFitHeight(50.0);
        arrow.setFitWidth(50.0);
        Label directionLabel = new Label(directionText);
        directionLabel.setWrapText(true);
        HBox directionContainer = new HBox(10, arrow, directionLabel);
        directionContainer.setAlignment(Pos.CENTER_LEFT);
        listOfDirections.getChildren().add(directionContainer);
    }

    public void clearListOfDirections(){
        listOfDirections.getChildren().clear();
    }

    public void setStartLabel(String startingPoint) {
        this.startLabel.setText(startingPoint);
    }

    public void setEndLabel(String endingPoint) {
        this.endLabel.setText(endingPoint);
    }

    public void setCurArrow(Image arrow) {
        this.curArrow.setImage(arrow);
    }

    public void setCurDirection(String textDirection) {
        this.curDirection.setText(textDirection);
    }

    public void goToTreeView() {
        navController.goToTreeView();
    }
}
