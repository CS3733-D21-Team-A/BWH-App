package edu.wpi.aquamarine_axolotls.views.mapping;

import com.google.maps.model.DirectionsLeg;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static edu.wpi.aquamarine_axolotls.Settings.PREFERENCES;
import static edu.wpi.aquamarine_axolotls.Settings.USER_NAME;
import static edu.wpi.aquamarine_axolotls.extras.Directions.*;

public class SideMenu extends GenericPage {

    @FXML
    public ImageView curArrow;
    @FXML
    public Label curDirection;

    @FXML
    public JFXButton gmapsStartButton;

    @FXML
    Label startLabel;
    @FXML
    Label endLabel;

    @FXML
    VBox listOfDirections;

    @FXML
    Label etaLabel;

    @FXML
    JFXComboBox<String> endLocationGmaps;

    @FXML
    JFXTextField startLocationGmaps;


    @FXML
    TreeTableView<String> treeTable;

    Navigation navController;

    DatabaseController db = DatabaseController.getInstance();

    @FXML
    JFXToggleButton voiceDirection;

    @FXML
    JFXToggleButton robotConnection;

    TreeItem<String> fav = new TreeItem<>("Favorites");

    double eta;

    public void goToStepByStep() throws IOException {
        navController.goToStepByStep();
    }

    public void toggleVoiceDirectionButton() {
        navController.toggleVoice();
    }

    public void toggleVoiceSlider() {
        voiceDirection.setSelected(!voiceDirection.isSelected());
    }

    public void toggleRobotConnectionButton() throws IOException, InterruptedException {
        navController.toggleRobot();
    }

    public void goToListOfDirections() {
        navController.goToListOfDirections();
    }

    public void goToGmapsListOfDirections() {
        navController.goToGmapsListOfDirections();
    }

    public void startPath() {
        navController.startPath();
    }

    public void clearNav() {
        startLabel.setText("Start Location");
        endLabel.setText("End Location");
        navController.clearNav();
    }

    public void addToFavorites(String nodeID){
        try {
            Map<String, String > node = db.getNode(nodeID);
            if(fav.getChildren().size() == 0) treeTable.getRoot().getChildren().add(0, fav);
            db.addFavoriteNodeToUser(PREFERENCES.get(USER_NAME,null), nodeID, node.get("LONGNAME"));
            fav.getChildren().add(new TreeItem<String>(node.get("LONGNAME")));
            if(node.get("NODETYPE").equals("PARK")) navController.changeNodeColorOnImage(nodeID, javafx.scene.paint.Color.YELLOW);
            else navController.changeNodeColorOnImage(nodeID, Color.HOTPINK);
            treeTable.refresh();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void deleteFromFavorites(String nodeID){
        try {
            Map<String, String> node = db.getNode(nodeID);
            if(node != null) {
                if(db.getFavoriteNodeByUserAndName(PREFERENCES.get(USER_NAME,null), node.get("LONGNAME")) != null){
                    for (int i = 0; i < fav.getChildren().size(); i++) {
                        String nodeName = fav.getChildren().get(i).getValue();
                        if (nodeName.equals(node.get("LONGNAME"))) {
                            fav.getChildren().remove(i);
                            db.deleteFavoriteNodeFromUser(PREFERENCES.get(USER_NAME,null), nodeName);
                            navController.changeNodeColorOnImage(nodeID, Color.BLUE);
                            break;
                        }
                    }
                    if(fav.getChildren().isEmpty()) treeTable.getRoot().getChildren().remove(0);
                    treeTable.refresh();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void setUpGmaps(){
        endLocationGmaps.setItems(FXCollections
                .observableArrayList("Emergency Room", "Valet", "Parking Spot"));
    }

    /**
     *  starting the path for external
     */
    public void findPathExt() {
        String startLocation = startLocationGmaps.getText();
        String destination = endLocationGmaps.getSelectionModel().getSelectedItem();
        if(startLocation.equals("") || destination.equals("")) return;

        try{
            switch(destination){
                case "Emergency Room":
                    navController.setGmapsListOfDirections(navigateToER(startLocation));
                    break;
                case "Parking Spot":
                    navController.setGmapsListOfDirections(navigateToClosestParking(startLocation));
                    break;
                case "Valet":
                    navController.setGmapsListOfDirections(navigateToClosestValet(startLocation));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  starting the path for external
     */
    public void startPathExt() {
        if(!navController.gmapsDir.isEmpty()) navController.goToGmapsStepByStep();
    }

    public void setGmapsStepByStep(DirectionsLeg directionsLeg){

    }


    public void setUpTree(){
        TreeItem<String> park = new TreeItem<>("Parking Spots");
        TreeItem<String> rest = new TreeItem<>("Restrooms");
        TreeItem<String> stai = new TreeItem<>("Stairs");
        TreeItem<String> dept = new TreeItem<>("Departments");
        TreeItem<String> labs = new TreeItem<>("Laboratories");
        TreeItem<String> info = new TreeItem<>("Help Desks");
        TreeItem<String> conf = new TreeItem<>("Conference Rooms");
        TreeItem<String> exit = new TreeItem<>("Entrances");
        TreeItem<String> retl = new TreeItem<>("Non Medical Commercial Areas");
        TreeItem<String> serv = new TreeItem<>("Non Medical Services");
        fav = new TreeItem<>("Favorites");


        try {
            for (Map<String, String> node: db.getNodes()) { // TODO : make db method to get nodes that arent hall/walk
                if(!(node.get("NODETYPE").equals("HALL") || node.get("NODETYPE").equals("WALK"))){
                    String nodeType = node.get("NODETYPE");
                    String longName = node.get("LONGNAME");
                    switch (nodeType){
                        case "PARK":
                            park.getChildren().add(new TreeItem<>(longName));
                            break;
                        case "REST":
                            rest.getChildren().add(new TreeItem<>(longName));
                            break;
                        case "STAI":
                            stai.getChildren().add(new TreeItem<>(longName));
                            break;
                        case "DEPT":
                            dept.getChildren().add(new TreeItem<>(longName));
                            break;
                        case "LABS":
                            labs.getChildren().add(new TreeItem<>(longName));
                            break;
                        case "INFO":
                            info.getChildren().add(new TreeItem<>(longName));
                            break;
                        case "CONF":
                            conf.getChildren().add(new TreeItem<>(longName));
                            break;
                        case "EXIT":
                            if (navController.covidLikely.equals("true") && node.get("LONGNAME").equals("75 Francis Valet Drop-off")) break;
                            if (navController.covidLikely.equals("false") && node.get("LONGNAME").equals("Emergency Department Entrance")) break;
                            exit.getChildren().add(new TreeItem<>(longName));
                            break;
                        case "RETL":
                            retl.getChildren().add(new TreeItem<>(longName));
                            break;
                        case "SERV":
                            serv.getChildren().add(new TreeItem<>(longName));
                            break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        TreeItem<String> root = new TreeItem<>("");
        root.getChildren().addAll(fav, park, rest, stai, dept, labs, info, conf, exit, retl, serv);
        TreeTableColumn<String, String> treeTableColumn1 = new TreeTableColumn<>("Locations");
        treeTableColumn1.setPrefWidth(348);
        treeTableColumn1.setResizable(false);
        treeTableColumn1.setCellValueFactory((TreeTableColumn.CellDataFeatures<String, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue()));
        treeTableColumn1.sortableProperty().set(false);
        treeTable.setRoot(root);
        treeTable.setShowRoot(false);
        treeTable.getColumns().add(treeTableColumn1);
        try {
            List<Map<String, String>> favorites = db.getFavoriteNodesForUser(PREFERENCES.get(USER_NAME,null));
            for(Map<String, String> node: favorites) fav.getChildren().add(new TreeItem<>(node.get("NODENAME")));
            if(favorites.isEmpty()) treeTable.getRoot().getChildren().remove(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }


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

    public void regressGmaps() throws SQLException, InterruptedException {
        navController.regressGmaps();
    }

    public void progressGmaps() throws SQLException, InterruptedException {
        navController.progressGmaps();
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

        String textToDisplay = directionText;
        textToDisplay = textToDisplay.substring(textToDisplay.indexOf(' ') + 1);

        ImageView arrow = new ImageView(navController.textDirectionToImage(directionText));
        arrow.setFitHeight(50.0);
        arrow.setFitWidth(50.0);

        HBox directionContainer;

        if(textToDisplay.contains("You are now on Floor")){
            textToDisplay = textToDisplay.substring(textToDisplay.indexOf('F'), textToDisplay.indexOf('.'));
            Label directionLabel = new Label(textToDisplay);
            directionLabel.setMaxWidth(240);
            directionLabel.setMinWidth(240);
            directionLabel.setWrapText(true);
            directionContainer = new HBox(0, arrow, directionLabel);
            directionContainer.setAlignment(Pos.CENTER_LEFT);
        } else {
            Label directionLabel = new Label(textToDisplay);
            directionLabel.setMaxWidth(240);
            directionLabel.setMinWidth(240);
            directionLabel.setWrapText(true);
            directionContainer = new HBox(0, arrow, directionLabel);
            directionContainer.setAlignment(Pos.CENTER_RIGHT);
        }

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


    public String getEndLocationGmap(){
        return endLocationGmaps.getSelectionModel().getSelectedItem();
    }

    public String getStartLocationGmap(){
        return startLocationGmaps.getText();
    }

    public void cancelGmaps(){
        navController.clearGmaps();
        navController.goToTreeView();
    }

    public void clearAll(){
        if(eta != 0) eta = 0;
        if(curArrow != null) curArrow.setImage(navController.textDirectionToImage(""));
        if(curDirection != null) curDirection.setText("");
        if(startLabel != null) startLabel.setText("Start Location");
        if(endLabel != null) endLabel.setText("End Location");
        if(listOfDirections != null) listOfDirections.getChildren().clear();
        if(etaLabel != null) etaLabel.setText("");
        if(endLocationGmaps != null) endLocationGmaps.getSelectionModel().clearSelection();
        if(startLocationGmaps != null) startLocationGmaps.clear();
        if(!navController.gmapsDir.isEmpty()) navController.gmapsDir.clear();
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
