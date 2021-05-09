package edu.wpi.aquamarine_axolotls.views.mapping;

import com.jfoenix.controls.JFXDrawer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

public class Navigation2 extends GenericMap{

    @FXML
    JFXDrawer drawer;

    VBox treeView;
    VBox listOfDirections;
    VBox stepByStep;

    public void initialize() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/NavigationTreeView.fxml"));
        loader.load();
        treeView = loader.getRoot();
        //treeView = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/NavigationListDirections.fxml"));
        //treeView = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/NavigationStepByStep.fxml"));
        startUp();
        drawFloor("1");
        drawer.setSidePane(treeView);
    }

    public void changeFloor3(){
        if(drawer.isClosed()){
            mapScrollPane.setLayoutX(351);
            mapScrollPane.setPrefWidth(949);
            drawer.open();
        }
        else{
            mapScrollPane.setLayoutX(0);
            mapScrollPane.setPrefWidth(1300);
            drawer.close();
        }
    }

    @Override
    public void nodePopUp() {

    }

    @Override
    public void edgePopUp() {

    }

    @Override
    public void drawFloor(String floor) {
        try {
            changeFloorImage("1");
            drawNodes(darkBlue);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void submitApiKey(ActionEvent actionEvent) {
    }

    public void toggleVoiceDirectionButton(ActionEvent actionEvent) {
    }
}
