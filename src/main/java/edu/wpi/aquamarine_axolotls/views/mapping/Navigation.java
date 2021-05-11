package edu.wpi.aquamarine_axolotls.views.mapping;

import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.Duration;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.events.JFXDrawerEvent;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.extras.VoiceController;
import edu.wpi.aquamarine_axolotls.pathplanning.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import oracle.jrockit.jfr.JFR;
import org.checkerframework.checker.units.qual.C;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static edu.wpi.aquamarine_axolotls.Settings.*;

public class Navigation extends GenericMap {
    public JFXButton drawerActionButton;
    public ImageView drawerActionImage;


    List<String> stopList = new ArrayList<>(); //Holds all the stops for when we're doing pathfinding
    List<Map<String, String>> currentPath = new ArrayList<>();

    String covidLikely = "false";
    VBox treeViewSideMenu;
    VBox listOfDirectionsSideMenu;
    VBox stepByStepSideMenu;
    VBox gmapsListOfDirections;
    VBox gmapsStepByStep;
    List<String> gmapsDir = new ArrayList<String>();
    private Thread newThread = new Thread();
    private VoiceController voice = new VoiceController("kevin16");

    ArrayList<SideMenu> sideControllers = new ArrayList<>();
    SideMenu currentMenu;
    @FXML
    JFXDrawer drawer;
    private int currentStepNumber;
    private int currentStepNumberGmaps;
    private List<List<String>> curPathDirections = new ArrayList<>();
    double eta;
    String currentNodeIDContextMenu;

    MenuItem removeStop = new MenuItem("Remove ");
    MenuItem addStart = new MenuItem("Add Starting Point");
    MenuItem addEnd = new MenuItem("Make Ending Point");
    MenuItem addFav = new MenuItem("Add Favorite");
    MenuItem deleteFav = new MenuItem("Delete Favorite");
    MenuItem changeToStart = new MenuItem("Change to Start");
    MenuItem changeToEnd = new MenuItem("Change to End");
    MenuItem makeIntermediatePoint = new MenuItem("Make Intermediate Point");

    boolean isVoiceToggled;

    public void initialize() throws java.sql.SQLException, IOException {

        treeViewSideMenu = setUpSideMenu("SideMenuTreeView");
        listOfDirectionsSideMenu = setUpSideMenu("SideMenuListOfDirections");
        stepByStepSideMenu = setUpSideMenu("SideMenuStepByStep");
        gmapsListOfDirections = setUpSideMenu("SideMenuListOfDirectionsGMAPS");
        gmapsStepByStep = setUpSideMenu("SideMenuStepByStepGMAPS");
        drawer.setVisible(false);

        drawer.setSidePane(treeViewSideMenu);
        currentMenu = sideControllers.get(0);
        openDrawer();


        startUp();
        if(SearchAlgorithmContext.getSearchAlgorithmContext().context == null) {
            SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
        }

        drawer.setOnDrawerClosed(event ->{
            drawerActionButton.setLayoutX(-5);
            drawerActionButton.setVisible(true);
            drawer.setVisible(false);
        });

        // TODO: CHANGE THIS
        covidLikely = db.getUserByUsername(PREFERENCES.get(USER_NAME,null)).get("COVIDLIKELY");


        currentMenu.setUpTree();


        removeStop.setOnAction((ActionEvent e) -> {
            stopList.remove(currentNodeIDContextMenu);
            changeNodeColorOnImage(currentNodeIDContextMenu, Color.web("#003da6", .4));
            updateNodeSize(currentNodeIDContextMenu, 3);
            setStartAndEnd();
            openDrawer();
            if(stopList.size() >= 2) findPath();
        });

        addStart.setOnAction((ActionEvent e) ->{
            changeNodeColorOnImage(currentNodeIDContextMenu, Color.GREEN);
            updateNodeSize(currentNodeIDContextMenu, 5);
            stopList.add(currentNodeIDContextMenu);
            setStartAndEnd();
            openDrawer();
            if(stopList.size() >= 2) findPath();
        });

        addEnd.setOnAction((ActionEvent e) ->{
            changeNodeColorOnImage(currentNodeIDContextMenu, Color.RED);
            updateNodeSize(currentNodeIDContextMenu, 5);
            stopList.add(currentNodeIDContextMenu);
            setStartAndEnd();
            openDrawer();
            if(stopList.size() >= 2) findPath();
        });

        changeToStart.setOnAction((ActionEvent e) -> {
            String prevID = stopList.get(0);
            changeNodeColorOnImage(prevID, Color.web("#003da6", .4));
            updateNodeSize(prevID, 3);
            stopList.set(0, currentNodeIDContextMenu);
            changeNodeColorOnImage(currentNodeIDContextMenu, Color.GREEN);
            updateNodeSize(currentNodeIDContextMenu, 5);
            setStartAndEnd();
            openDrawer();
            if(stopList.size() >= 2) findPath();
        });

        changeToEnd.setOnAction((ActionEvent e) -> {
            String prevID = stopList.get(stopList.size()-1);
            changeNodeColorOnImage(prevID, Color.web("#003da6", .4));
            updateNodeSize(prevID, 3);
            stopList.set(stopList.size()-1, currentNodeIDContextMenu);
            changeNodeColorOnImage(currentNodeIDContextMenu, Color.RED);
            updateNodeSize(currentNodeIDContextMenu, 5);
            setStartAndEnd();
            openDrawer();
            if(stopList.size() >= 2) findPath();
        });

        addFav.setOnAction((ActionEvent e) ->{
            sideControllers.get(0).addToFavorites(currentNodeIDContextMenu);
        });

        deleteFav.setOnAction((ActionEvent e)->{
            sideControllers.get(0).deleteFromFavorites(currentNodeIDContextMenu);
        });

        // TODO : figure this out
        makeIntermediatePoint.setOnAction((ActionEvent e) ->{
        });
        contextMenu.getItems().addAll(removeStop, addStart, addEnd, changeToStart, changeToEnd, makeIntermediatePoint, addFav, deleteFav);

        sideControllers.get(0).treeTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TreeItem<String> selectedFromTreeView = sideControllers.get(0).treeTable.getSelectionModel().getSelectedItem();
                if (selectedFromTreeView.getChildren().isEmpty()) {
                    try {
                        stopList.add(db.getNodesByValue("LONGNAME", selectedFromTreeView.getValue()).get(0).get("NODEID"));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    if (stopList.size() == 1) currentMenu.setStartLabel(selectedFromTreeView.getValue());
                    else if (stopList.size() >= 2) findPath();
                }
            }
        });
    }




    //====DRAWING
    @Override
    public void drawFloor(String floor) {
        changeFloorImage(floor);
        if(currentPath.size() > 0){
            drawNodes(Color.web("#003da6", .4));
            drawPath();
            setArrowsToBeVisible();
        }
        else{
            drawNodes(darkBlue);
        }
    }

    @Override
    public void drawNodes(Color colorOfNodes) {
        try {
            drawNodesNoHallWalk(darkBlue);
            drawFavorites();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void drawNodesNoHallWalk(Color colorOfNodes) throws SQLException{
        for (Map<String, String> node: db.getNodesByValue("FLOOR", FLOOR)) {
            if(
                   !(node.get("NODETYPE").equals("HALL") || node.get("NODETYPE").equals("WALK"))
                && !(covidLikely.equals("true") && node.get("LONGNAME").equals("75 Francis Valet Drop-off"))
                && !(covidLikely.equals("false") && node.get("LONGNAME").equals("Emergency Department Entrance"))
            ) drawSingleNode(node.get("NODEID"), colorOfNodes);
        }
    }

    public void drawFavorites(){
        try {
            for(Map<String, String> fav : db.getFavoriteNodesForUser(PREFERENCES.get(USER_NAME,null))){
                Map<String, String> node = db.getNode(fav.get("LOCATIONID"));
                if(node.get("FLOOR").equals(FLOOR)){
                    String color;
                    double opacity;
                    if(node.get("NODETYPE").equals("PARK")) color = "#FFFF00";
                    else color = "#FF69B4";

                    if(currentPath.size() > 0) opacity = .4;
                    else opacity = 1;
                    changeNodeColorOnImage(node.get("NODEID"), Color.web(color, opacity));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetContextMenu(){
        // TODO : set context menu to be cisible
        removeStop.setVisible(false);
        addStart.setVisible(false);
        addEnd.setVisible(false);
        changeToStart.setVisible(false);
        changeToEnd.setVisible(false);
        makeIntermediatePoint.setVisible(false);
        deleteFav.setVisible(false);
        addFav.setVisible(false);
    }

    @Override
    public Circle setEventHandler(Circle node, String nodeID) {
        //When you click a node in navigation, it gets selected/de-selected
        node.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton().equals(MouseButton.SECONDARY)) {
                currentNodeIDContextMenu = nodeID;
                resetContextMenu();
                 // maybe remove?
                if(stopList.size() == 0){
                    addStart.setVisible(true);
                    try {
                        if(!PREFERENCES.get(USER_TYPE, null).equals("Guest")) {
                            if(db.getFavoriteNodeByUserAndName(PREFERENCES.get(USER_NAME, null), db.getNode(currentNodeIDContextMenu).get("LONGNAME")) != null){
                                deleteFav.setVisible(true);
                            }else{
                                addFav.setVisible(true);
                            }
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                if(stopList.size() >= 1){
                    changeToStart.setVisible(true);
                    addEnd.setVisible(true);
                }
                if(stopList.size() >= 2){
                    changeToEnd.setVisible(true);
                }
                if(stopList.contains(currentNodeIDContextMenu)){
                    removeStop.setVisible(true);
                }

                contextMenu.show(mapView, e.getScreenX(), e.getScreenY());

            }
            else{
                if (stopList.contains(nodeID)) { //If the node you click is already in the stopList, it gets removed
                    stopList.remove(nodeID); //So you can toggle destinations
                    changeNodeColorOnImage(nodeID, Color.web("#003da6", .4));
                    updateNodeSize(nodeID, 3);
                }
                else{

                    if(stopList.size() == 0){
                        stopList.add(nodeID);
                        changeNodeColorOnImage(nodeID, Color.GREEN);
                        updateNodeSize(nodeID, 5);
                    }
                    else{
                        stopList.add(nodeID);
                    }
                    goToTreeView();
                    openDrawer();
                }
                setStartAndEnd();
                if(stopList.size() >= 2) findPath();
            }
        });

        node.setOnMouseEntered((MouseEvent e) -> {
            node.setRadius(5);
        });

        node.setOnMouseExited((MouseEvent e) -> {
            if (!(node.getFill().equals(Color.RED) || node.getFill().equals(Color.ORANGE) || node.getFill().equals(Color.GREEN))) {
                node.setRadius(magicNumber);
            }
        });

        return node;
    }


    //=== PATHFINDING

    /**
     * Uses the current search algorithm to find a path between all the current stops, then display that path on screen
     */
    @FXML
    void findPath() {
        goToListOfDirections();
        currentPath.clear();
        for (int i = 0; i < stopList.size() - 1; i++) {
            String currentStart = stopList.get(i);
            String currentEnd = stopList.get(i + 1);
            List<Map<String, String>> path = SearchAlgorithmContext.getSearchAlgorithmContext().getPath(currentStart, currentEnd);
            for(int j = 0; j < 3; j++){
                List<Map<String, String>> toRemove = new ArrayList<>();
                for (Map<String, String> node : path) {
                    if (SearchAlgorithmContext.getSearchAlgorithmContext().nodeIsUnimportant(path, node))
                        toRemove.add(node);
                }
                path.removeAll(toRemove);
            }
            currentPath.addAll(path);
        }
        curPathDirections.clear();
        curPathDirections = SearchAlgorithmContext.getSearchAlgorithmContext().getTextDirections(currentPath);
        sideControllers.get(1).clearListOfDirections();
        sideControllers.get(1).addToListOfDirections("0. You are now on Floor " + currentPath.get(0).get("FLOOR") + ".");
        for(String direction : curPathDirections.get(0)){
            sideControllers.get(1).addToListOfDirections(direction);
        }
        eta = SearchAlgorithmContext.getSearchAlgorithmContext().getETA(currentPath);
        sideControllers.get(1).setEtaLabel(eta); // add big eta to lod
        sideControllers.get(2).setEtaLabel(eta); // add big eta to step by step

        drawPath();
    }

    /**
     * Draws the current path onto the map
     */
    void drawPath(){
        linesOnImage.clear();
        nodesOnImage.clear();
        arrowsOnImage.clear();
        mapView.getChildren().clear();
        try {
            drawNodesNoHallWalk(Color.web("#003da6", .4));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
//        currentPath.get(0).get("FLOOR");
        for(int i = 0; i < currentPath.size() - 1; i++){
            Map<String, String> node = currentPath.get(i);
            Map<String, String> nextNode = currentPath.get(i+1);
            if(!node.get("FLOOR").equals(nextNode.get("FLOOR"))){
                drawArrow(node, nextNode);
            }
            else if (node.get("FLOOR").equals(FLOOR)) {
                Color color = darkBlue;
                int radius = 3;
                if(i == 0){
                    color = Color.GREEN;
                    radius = 5;
                }
                else if(i == currentPath.size() - 1){
                    color = Color.RED;
                    radius = 5;
                }
                else if(stopList.contains(node.get("NODEID"))){
                    color = Color.ORANGE;
                    radius = 5;
                }
                drawSingleEdge(node.get("NODEID"), nextNode.get("NODEID"), Color.BLACK);
                if ((node.get("NODETYPE").equals("HALL") || node.get("NODETYPE").equals("WALK"))) {
                    drawSingleNode(node.get("NODEID"), color);
                }
                else changeNodeColorOnImage(node.get("NODEID"), color);
                updateNodeSize(node.get("NODEID"), radius);

            }
        }

        if (currentPath.get(currentPath.size() - 1).get("FLOOR").equals(FLOOR)) {
            changeNodeColorOnImage(currentPath.get(currentPath.size() - 1).get("NODEID"), Color.RED);
            updateNodeSize(currentPath.get(currentPath.size() - 1).get("NODEID"), 5);
        }
        setArrowsToBeVisible();

    }


    //==== GMAPS ====//

    public void clearGmaps(){
        sideControllers.get(3).clearAll();
        sideControllers.get(4).clearAll();
    }

    public void setGmapsListOfDirections(DirectionsLeg directionsLeg){
        sideControllers.get(3).updateETA(directionsLeg.duration.inSeconds/60);
        sideControllers.get(4).updateETA(directionsLeg.duration.inSeconds/60);
        sideControllers.get(4).setStartLabel(sideControllers.get(3).getStartLocationGmap());
        sideControllers.get(4).setEndLabel(sideControllers.get(3).getEndLocationGmap());
        DirectionsStep[] steps = directionsLeg.steps;
        for(int i = 0; i < steps.length; i++){
            String s = steps[i].htmlInstructions;
            String newString = String.valueOf(i+1) + ") " + parseHtmlDir(s);

            if (newString.contains("Destination")){
                String[] str = newString.split("Destination", 2);
                str[1] = String.valueOf(i+1) + ") Destination" + str[1];

                for (int j = 0; j < 2; j++){
                    gmapsDir.add(str[j]);
                    sideControllers.get(3).addToListOfDirections(str[j]);
                }
                return;
            }

            gmapsDir.add(newString);
            sideControllers.get(3).addToListOfDirections(newString);
        }

    }

    public String parseHtmlDir(String direction){
        String parsed = direction.replaceAll("\\<.*?\\>", "");
        return parsed;
    }

    public void regressGmaps() {
        voice.stop();
        if (currentStepNumberGmaps != 0){
            currentStepNumberGmaps -= 1;
            currentMenu.setCurDirection(gmapsDir.get(currentStepNumberGmaps));
            currentMenu.setCurArrow(textDirectionToImage(gmapsDir.get(currentStepNumberGmaps)));
            if(isVoiceToggled) {
                voice.say(voice.getTextOptimizationGmaps(gmapsDir.get(currentStepNumberGmaps)), newThread);
            }
        }
    }

    public void progressGmaps() {
        voice.stop();
        if (currentStepNumberGmaps < gmapsDir.size()-1){
            currentStepNumberGmaps += 1;
            currentMenu.setCurDirection(gmapsDir.get(currentStepNumberGmaps));
            currentMenu.setCurArrow(textDirectionToImage(gmapsDir.get(currentStepNumberGmaps)));
            if(isVoiceToggled) {
                voice.say(voice.getTextOptimizationGmaps(gmapsDir.get(currentStepNumberGmaps)), newThread);
            }
        }
    }


                                            //=== SIDE BAR METHODS ===//

    public void startPath() {
        goToStepByStep();
    }

    public void openDrawer(){
        mapScrollPane.setLayoutX(351);
        mapScrollPane.setPrefWidth(949);
        drawerActionImage.setImage(new Image("/edu/wpi/aquamarine_axolotls/img/hideDrawer.png"));
        drawerActionButton.setLayoutX(349);
        drawer.open();
        drawer.setVisible(true);
    }

    public void closeDrawer(){
        mapScrollPane.setLayoutX(0);
        mapScrollPane.setPrefWidth(1300);
        drawerActionImage.setImage(new Image("/edu/wpi/aquamarine_axolotls/img/openDrawer.png"));
        drawerActionButton.setVisible(false);
        drawer.close();
    }


    protected void goToTreeView() {
        drawer.setSidePane(treeViewSideMenu);
        currentMenu = sideControllers.get(0);
        setStartAndEnd();
    }

    public void goToListOfDirections() {
        drawer.setSidePane(listOfDirectionsSideMenu);
        currentMenu = sideControllers.get(1);
        setStartAndEnd();
    }

    public void goToStepByStep() {
        drawer.setSidePane(stepByStepSideMenu);
        currentMenu = sideControllers.get(2);
        setStartAndEnd();
        startDir();
    }

    public void goToGmapsListOfDirections() {
        drawer.setSidePane(gmapsListOfDirections);
        sideControllers.get(3).setUpGmaps();
        currentMenu = sideControllers.get(3);
    }

    public void goToGmapsStepByStep() {
        drawer.setSidePane(gmapsStepByStep);
        currentMenu = sideControllers.get(4);
        currentMenu.setCurDirection(gmapsDir.get(0));
        currentMenu.setCurArrow(textDirectionToImage(gmapsDir.get(0)));
    }


    public VBox setUpSideMenu(String name) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/" + name +  ".fxml"));
        loader.load();
        SideMenu controller = loader.getController();
        controller.navController = this;
        sideControllers.add(controller);
        return loader.getRoot();
    }

    public void setStartAndEnd(){
        int size = stopList.size();
        try{
            if(size == 1){
                currentMenu.setStartLabel(db.getNode(stopList.get(stopList.size() - 1)).get("LONGNAME"));
            }
            else if(size >= 2) {
                currentMenu.setStartLabel(db.getNode(stopList.get(0)).get("LONGNAME"));
                currentMenu.setEndLabel(db.getNode(stopList.get(stopList.size() - 1)).get("LONGNAME"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

    }

                                        //==== LIST OF DIRECTIONS ====//
    public void startDir() {
        currentStepNumber = 0; // was dirIndex
        String curDirection = curPathDirections.get(0).get(currentStepNumber);
        setArrowsToBeVisible();
        currentMenu.setCurArrow(textDirectionToImage(curDirection));
        currentMenu.setCurDirection(curDirection);
        String nodeID = curPathDirections.get(1).get(0);
        if(nodeID.contains("_")) nodeID = nodeID.substring(0, nodeID.indexOf("_"));
        try {
            drawFloor(db.getNode(nodeID).get("FLOOR"));
            highlightDirection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isVoiceToggled) {
            voice.say(voice.getTextOptimization(curPathDirections.get(0).get(currentStepNumber)), newThread);
        }
    }

    /**
     * Progresses to the next step in the text directions
     */
    public void progress() throws SQLException,InterruptedException {
        voice.stop();
        if (currentStepNumber < curPathDirections.get(0).size() - 1){
            unHighlightDirection();
            currentStepNumber += 1;
            String curNode = curPathDirections.get(1).get(currentStepNumber);
            String curFloor = getInstructionsFloor(curNode);

            if(!curFloor.equals(FLOOR)) drawFloor(curFloor);
            updateETAProgress();
            String curDirection = curPathDirections.get(0).get(currentStepNumber);
            currentMenu.setCurArrow(textDirectionToImage(curDirection));
            currentMenu.setCurDirection(curPathDirections.get(0).get(currentStepNumber));
            highlightDirection();
            if(isVoiceToggled) {
                voice.say(voice.getTextOptimization(curPathDirections.get(0).get(currentStepNumber)), newThread);
            }
        }
    }

    /**
     * Moves back to the previous step in the text directions
     */
    public void regress() throws SQLException,InterruptedException{
        voice.stop();
        if (currentStepNumber != 0) {
            unHighlightDirection();
            currentStepNumber -= 1;
            String curNode = curPathDirections.get(1).get(currentStepNumber);
            String curFloor = getInstructionsFloor(curNode);
            if(!curFloor.equals(FLOOR)) drawFloor(curFloor);
            String curDirection = curPathDirections.get(0).get(currentStepNumber);
            currentMenu.setCurArrow(textDirectionToImage(curDirection));
            currentMenu.setCurDirection(curPathDirections.get(0).get(currentStepNumber));
            updateETARegress();
            highlightDirection();
            if(isVoiceToggled) {
                voice.say(voice.getTextOptimization(curPathDirections.get(0).get(currentStepNumber)), newThread);
            }
        }
    }

    public void toggleVoice() {
        voice.stop();
        isVoiceToggled =  !isVoiceToggled;
        sideControllers.forEach((a) -> {
            if (a != currentMenu) a.toggleVoiceSlider();
        });
        if(isVoiceToggled && currentPath.size() > 0) voice.say(voice.getTextOptimization(curPathDirections.get(0).get(currentStepNumber)), newThread);
    }

    public void highlightDirection() throws SQLException{
        String curID = curPathDirections.get(1).get(currentStepNumber);

        if (curID.contains("_")) {
            updateEdgeColor(curID, yellow);
            int index = curID.indexOf("_");
            Map<String, String> start = db.getNode(curID.substring(0,index));
            Map<String, String> end = db.getNode(curID.substring(index+1));
            drawPathArrow(start, end);

        }
        else {
            Map<String, String> node = db.getNode(curID);
            changeNodeColorOnImage(curID, darkBlue);
            if(currentStepNumber != 0) changeNodeColorOnImage(curID, yellow);
            else changeNodeColorOnImage(curID, Color.GREEN);
            if (currentStepNumber == curPathDirections.get(1).size() - 1) drawPathArrow(node, node); // TODO : could cause issues
            else {
                String nextNodes = curPathDirections.get(1).get(currentStepNumber + 1);
                String nextNodeID = nextNodes.substring(nextNodes.indexOf("_")+1);
                Map<String, String> nextNode = db.getNode(nextNodeID);
                if(node.get("FLOOR").equals(nextNode.get("FLOOR"))){
                    drawPathArrow(node, nextNode);
                }

            }
        }
    }

    public void unHighlightDirection() throws SQLException{
        String curDirectionID = curPathDirections.get(1).get(currentStepNumber);
        if (curDirectionID.contains("_")) updateEdgeColor(curDirectionID, Color.BLACK);
        else {
            if(currentStepNumber == curPathDirections.get(1).size() - 1) changeNodeColorOnImage(curDirectionID, Color.RED);
            else if (stopList.contains(curDirectionID)) changeNodeColorOnImage(curDirectionID, Color.ORANGE);
            else if(currentStepNumber != 0) changeNodeColorOnImage(curDirectionID, darkBlue);
        }
    }


    public void submitApiKey() {
    }

    public void clearNav() {
        sideControllers.get(0).clearAll();
        sideControllers.get(1).clearAll();
        sideControllers.get(2).clearAll();
        gmapsDir.clear();
        stopList.clear();
        currentPath.clear();
        linesOnImage.clear();
        nodesOnImage.clear();
        arrowsOnImage.clear();
        mapView.getChildren().clear();
        drawFloor(FLOOR);
    }


    /**
     * Extracts the floor from the provided node or nodes corresponding to a text direction.
     * @param nodeInstruction The node or nodes passed in are of the form NODEID or NODEID,NODEID where the second represents the
     * action of traveling between two nodes.
     * @return A floor that corresponds to the map
     */
    private String getInstructionsFloor(String nodeInstruction){
        String curFloor = null;
        if (nodeInstruction.contains("_")) {
            int index = nodeInstruction.indexOf("_");
            try {
                curFloor = db.getNode(nodeInstruction.substring(0, index)).get("FLOOR");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                curFloor = db.getNode(nodeInstruction).get("FLOOR");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return curFloor;
    }

    public Image textDirectionToImage(String direction){ //update arrow
        if (direction.contains("left")) return new Image("/edu/wpi/aquamarine_axolotls/img/leftArrow.png");
        else if (direction.contains("right")) return new Image("/edu/wpi/aquamarine_axolotls/img/rightArrow.png");
        else if (direction.contains("elevator")) return new Image("/edu/wpi/aquamarine_axolotls/img/elevator.png");
        else if (direction.contains("stairs")) return new Image("/edu/wpi/aquamarine_axolotls/img/stairs.png");
        else if (direction.contains("Turn around")) return new Image("/edu/wpi/aquamarine_axolotls/img/turn around.png");
        else if (direction.contains("You have arrived at your destination.")) return new Image("/edu/wpi/aquamarine_axolotls/img/arrived.png");
        else return new Image("/edu/wpi/aquamarine_axolotls/img/straight.png");

    }


    public void updateETA(int number, String edgeID) {
        try {
            if (edgeID.contains("_")) {
                String startID = edgeID.substring(0, edgeID.indexOf('_'));
                String endID = edgeID.substring(edgeID.indexOf('_') + 1);
                try {
                    currentMenu.updateETA(number * SearchAlgorithmContext.getSearchAlgorithmContext().getETASingleEdge(db.getNode(startID), db.getNode(endID)));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (currentStepNumber < curPathDirections.get(1).size()){
                String nodeID = edgeID;
                String otherID;
                if (edgeID.equals(curPathDirections.get(1).get(currentStepNumber))) otherID = curPathDirections.get(1).get(currentStepNumber + 1);
                else otherID = curPathDirections.get(1).get(currentStepNumber);
                if (db.nodeExists(nodeID) && db.nodeExists(otherID)){
                    Map<String, String> node = db.getNode(nodeID);
                    Map<String, String> otherNode = db.getNode(otherID);
                    if (!node.get("FLOOR").equals(otherNode.get("FLOOR"))) {
                        currentMenu.updateETA(number * SearchAlgorithmContext.getSearchAlgorithmContext().getETASingleEdge(node, otherNode));
                    }
                }
            } else{
                currentMenu.updateETA(-eta);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateETAProgress(){
        if(currentStepNumber > 0) {
            String lastID = curPathDirections.get(1).get(currentStepNumber -1);
            updateETA(-1, lastID);
        }

    }

    public void updateETARegress(){
        String currentID = curPathDirections.get(1).get(currentStepNumber);
        updateETA(1, currentID);
    }

    public void drawerAction() {
        if(drawer.isClosed()) openDrawer();
        else closeDrawer();
    }


}


