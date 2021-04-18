package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
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
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestHam  extends SPage {

    @FXML
    private AnchorPane anchor1;
    @FXML
    private AnchorPane mainAnchor;
    @FXML
    private JFXToggleButton toggleButton;
    ;

    @FXML
    public void hammy() {
        boolean isSelected = toggleButton.isSelected();

        if(isSelected){
            anchor1.setVisible(false);
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(.5), anchor1);
            TranslateTransition translateTransition1 = new TranslateTransition((Duration.seconds(0.5)), anchor1);
            translateTransition1.play();
        }
        if(!isSelected) {
            anchor1.setVisible(true);
            TranslateTransition translateTransition1 = new TranslateTransition((Duration.seconds(0.5)), anchor1);
            translateTransition1.play();
        }
    }

}