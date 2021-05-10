package edu.wpi.aquamarine_axolotls.views.mapping;

import com.jfoenix.controls.JFXDrawer;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.pathplanning.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
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

import static sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl.ThreadStateMap.Byte0.newThread;

public class Navigation extends GenericMap {
    List<String> stopList = new ArrayList<>(); //Holds all the stops for when we're doing pathfinding
    List<Map<String, String>> currentPath = new ArrayList<>();

    String covidLikely = "false";
    VBox treeViewSideMenu;
    VBox listOfDirectionsSideMenu;
    VBox stepByStepSideMenu;

    ArrayList<SideMenu> sideControllers = new ArrayList<>();
    SideMenu currentMenu;
    @FXML
    JFXDrawer drawer;
    private int currentStepNumber;
    private List<List<String>> curPathDirections = new ArrayList<>();
    double eta;
    String currentNodeIDContextMenu;

    MenuItem removeStop = new MenuItem("Remove ");
    MenuItem addStart = new MenuItem("Add Starting Point");
    MenuItem addEnd = new MenuItem("Make Ending Point");
    MenuItem changeToStart = new MenuItem("Change to Start");
    MenuItem changeToEnd = new MenuItem("Change to End");
    MenuItem makeIntermediatePoint = new MenuItem("Make Intermediate Point");

    public void initialize() throws java.sql.SQLException, IOException {

        treeViewSideMenu = setUpSideMenu("SideMenuTreeView");
        listOfDirectionsSideMenu = setUpSideMenu("SideMenuListOfDirections");
        stepByStepSideMenu = setUpSideMenu("SideMenuStepByStep");
        drawer.setVisible(false);

        drawer.setSidePane(treeViewSideMenu);
        currentMenu = sideControllers.get(0);


        startUp();
        if(SearchAlgorithmContext.getSearchAlgorithmContext().context == null) {
            SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
        }


        // TODO: CHANGE THIS
        covidLikely = db.getUserByUsername(Aapp.username != null ? Aapp.username : "guest").get("COVIDLIKELY");

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
        TreeItem<String> fav = new TreeItem<>("Favorites");

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
//                        if (covidLikely.equals("true") && node.get("LONGNAME").equals("75 Francis Valet Drop-off")) break;
  //                      if (covidLikely.equals("false") && node.get("LONGNAME").equals("Emergency Department Entrance")) break;
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

        TreeItem<String> root = new TreeItem<>("");
        root.getChildren().addAll(fav, park, rest, stai, dept, labs, info, conf, exit, retl, serv);
        TreeTableColumn<String, String> treeTableColumn1 = new TreeTableColumn<>("Locations");
        treeTableColumn1.setCellValueFactory((TreeTableColumn.CellDataFeatures<String, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue()));
        sideControllers.get(0).treeTable.setRoot(root);
        sideControllers.get(0).treeTable.setShowRoot(false);
        sideControllers.get(0).treeTable.getColumns().add(treeTableColumn1);

        // remove from stop list


        removeStop.setOnAction((ActionEvent e) -> {
            stopList.remove(currentNodeIDContextMenu);
            setStartAndEnd();
            openDrawer();
            if(stopList.size() >= 2) findPath();
        });

        addStart.setOnAction((ActionEvent e) ->{
            stopList.add(currentNodeIDContextMenu);
            setStartAndEnd();
            openDrawer();
            if(stopList.size() >= 2) findPath();
        });

        addEnd.setOnAction((ActionEvent e) ->{
            stopList.add(currentNodeIDContextMenu);
            setStartAndEnd();
            openDrawer();
            if(stopList.size() >= 2) findPath();
        });

        changeToStart.setOnAction((ActionEvent e) -> {
            stopList.set(0, currentNodeIDContextMenu);
            setStartAndEnd();
            openDrawer();
            if(stopList.size() >= 2) findPath();
        });

        changeToEnd.setOnAction((ActionEvent e) -> {
            stopList.set(stopList.size()-1, currentNodeIDContextMenu);
            setStartAndEnd();
            openDrawer();
            if(stopList.size() >= 2) findPath();
        });

        // TODO : figure this out
        makeIntermediatePoint.setOnAction((ActionEvent e) ->{
        });
        contextMenu.getItems().addAll(removeStop, addStart, addEnd, changeToStart, changeToEnd, makeIntermediatePoint);

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

        drawFloor("1");
    }




    @Override
    public void nodePopUp() {

    }

    @Override
    public void edgePopUp() {

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
            if (covidLikely != null) {
                if (covidLikely.equals("true") && node.get("LONGNAME").equals("75 Francis Valet Drop-off")) {}
                else if (covidLikely.equals("false") && node.get("LONGNAME").equals("Emergency Department Entrance")) {}
            }
            else if(!(node.get("NODETYPE").equals("HALL") || node.get("NODETYPE").equals("WALK"))) drawSingleNode(node.get("NODEID"), colorOfNodes);
        }
    }

    public void drawFavorites(){
        try {
            for(Map<String, String> fav : db.getFavoriteNodesForUser(Aapp.username)){
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
    }

    @Override
    public Circle setEventHandler(Circle node, String nodeID) {
        //When you click a node in navigation, it gets selected/de-selected
        node.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                currentNodeIDContextMenu = nodeID;
                resetContextMenu();
                 // maybe remove?
                if(stopList.size() == 0){
                    addStart.setVisible(true);
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
                //contextMenu.show();
                // change to context menu stuff
//                if (stopList.contains(nodeID)) { //If the node you click is already in the stopList, it gets removed
//                    stopList.remove(nodeID); //So you can toggle destinations
//                }
//                else{
//                    stopList.add(nodeID); //Otherwise, add the node's ID to the stopList
//                    goToTreeView();
//                    openDrawer();
//                }
//                setStartAndEnd();
//                if(stopList.size() >= 2) findPath();
//            }
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
        curPathDirections = SearchAlgorithmContext.getSearchAlgorithmContext().getTextDirections(currentPath);
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

        mapView.getChildren().clear();
        try {
            drawNodesNoHallWalk(Color.web("#003da6", .4));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        currentPath.get(0).get("FLOOR");
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
                drawSingleEdge(node.get("NODEID") + "_" + nextNode.get("NODEID"), Color.BLACK);
//                drawSingleEdge(nextNode.get("NODEID") + "_" + node.get("NODEID"), Color.BLACK);
                if ((node.get("NODETYPE").equals("HALL") || node.get("NODETYPE").equals("WALK"))) {
                    drawSingleNode(node.get("NODEID"), color);
                }
                else changeNodeColorOnImage(node.get("NODEID"), color);
                updateNodeSize(node.get("NODEID"), radius);

            }
        }


        // update with each step (substract eta of singel edge

        if (currentPath.get(currentPath.size() - 1).get("FLOOR").equals(FLOOR)) {
            changeNodeColorOnImage(currentPath.get(currentPath.size() - 1).get("NODEID"), Color.RED);
            updateNodeSize(currentPath.get(currentPath.size() - 1).get("NODEID"), 5);
        }
        setArrowsToBeVisible();

    }


//    public void changeFloor3(){
//        if(drawer.isClosed()) openDrawer();
//        else{
//            closeDrawer();
//        }
//    }


                                            //=== SIDE BAR METHODS ===//

    public void startPath() {
        goToStepByStep();
    }

    public void openDrawer(){
        mapScrollPane.setLayoutX(351);
        mapScrollPane.setPrefWidth(949);
        drawer.open();
        drawer.setVisible(true);
    }

    public void closeDrawer(){
        mapScrollPane.setLayoutX(0);
        mapScrollPane.setPrefWidth(1300);
        drawer.close();
        drawer.setVisible(false);
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

//        if(voiceDirection.isSelected()) {
//            voice.say(voice.getTextOptimization(curDirection.getText()), newThread);
//        }
    }

    /**
     * Progresses to the next step in the text directions
     */
    public void progress() throws SQLException,InterruptedException {
//        voice.stop();
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
//            if(voiceDirection.isSelected()) {
//                voice.say(voice.getTextOptimization(curDirection.getText()), newThread);
//            }
        }
    }

    /**
     * Moves back to the previous step in the text directions
     */
    public void regress() throws SQLException,InterruptedException{
//        voice.stop();
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
//            if(voiceDirection.isSelected()) {
//                voice.say(voice.getTextOptimization(curDirection.getText()), newThread);
//            }
        }
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
            if(currentStepNumber + 1 < curPathDirections.get(1).size()){
                String nextID = curPathDirections.get(1).get(currentStepNumber+1);
                nextID = nextID.substring(0, nextID.indexOf("_"));
                changeNodeColorOnImage(nextID, yellow);
            }
            else changeNodeColorOnImage(curID, yellow);
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
        else { if(currentStepNumber != 0) changeNodeColorOnImage(curDirectionID, darkBlue); }
    }


    public void submitApiKey() {
    }

    public void toggleVoiceDirectionButton() {
    }

    public void clearNav() {
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


    public void updateETA(int number, String edgeID){
        if (edgeID.contains("_")) {
            String startID = edgeID.substring(0, edgeID.indexOf('_'));
            String endID = edgeID.substring(edgeID.indexOf('_') + 1);
            try {
                currentMenu.updateETA(number * SearchAlgorithmContext.getSearchAlgorithmContext().getETASingleEdge(db.getNode(startID), db.getNode(endID)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
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


    //
//    @FXML private Label startLabel;
//    @FXML private Label endLabel;
//    @FXML private JFXButton cancelPath;
//    @FXML private Label etaLabel;
//    @FXML private Label curDirection;
//    @FXML private ImageView arrow;
//    @FXML private VBox stepByStep;
//    @FXML private VBox listDirVBox;
//    @FXML private VBox listOfDirections;
//    @FXML private Group textDirectionsGroup;
//    @FXML private TreeTableView<String> treeTable;
//    @FXML private JFXToggleButton voiceDirection;
//
//    @FXML private JFXComboBox destinationDropdown;
//    @FXML private JFXTextField startTextfield;
//    @FXML private VBox listDirVBoxExt;
//    @FXML private VBox listOfDirectionsExt;
//    @FXML private VBox stepByStepExt;
//    @FXML private JFXTextField apiKey;
//    @FXML private Pane apiKeyPane;
//    @FXML private Label curDirectionExt;
//    @FXML private Label invalidKey;
//    @FXML private Label etaLabelExt;
//    @FXML private Tab internalNav;
//    @FXML private Tab externalNav;
//
//
//    ObservableList<String> options = FXCollections.observableArrayList();
//    private int firstNodeSelect = 0;
//    private String firstNode;
//    private List<String> stopList = new ArrayList<>();
//    private List<Node> currPath = new ArrayList<>();
//    private boolean activePath = false;
//    private List<List<String>> currPathDir = new ArrayList<>();
//    static int dirIndex = 0; //why is this static?
//    private List<Map<String,String>> intermediatePoints = new ArrayList<>();
//    private Map<String,String> endPoint;
//    private VoiceController voice = new VoiceController("kevin16");
//    private Thread newThread = new Thread();
//
//    private static DirectionsLeg dirLeg;
//    private List<String> extDir = new ArrayList<String>();
//    int dirIndexExt = 0;
//    String covidLikely;
//
//    @FXML
//    public void initialize() throws SQLException {
//
//
//        destinationDropdown.setItems(FXCollections
//                .observableArrayList("Emergency Room", "Valet", "Parking Spot")
//        );
//
//        drawNodesAndFloor("1", Color.BLUE);
//        List<Map<String, String>> favorites = db.getFavoriteNodesForUser(Aapp.username);
//        for(Map<String, String> node: favorites) fav.getChildren().add(new TreeItem<>(node.get("NODENAME")));
//        if(favorites.isEmpty()) treeTable.getRoot().getChildren().remove(0);
//
//        drawFloor("1");
//        drawNodesAndFavorites();
//
//        stepByStep.setVisible(false);
//        listDirVBox.setVisible(false);
//        listDirVBox.toFront();
//        treeTable.setVisible(true);
//        treeTable.toFront();
//
//        stepByStepExt.setVisible(false);
//        listDirVBoxExt.setVisible(false);
//
//        MenuItem item1 = new MenuItem(("Add Stop"));
//        MenuItem item2 = new MenuItem(("Add to Favorites"));
//        MenuItem addStop = new MenuItem(("Add Stop"));
//        MenuItem addFav = new MenuItem(("Add to Favorites"));
//        MenuItem deleteFav = new MenuItem(("Remove from favorites")); //TODO: this should only show up when clicking on a favorite node
//
//        treeTable.setOnMouseClicked(event -> {
//            if (event.getClickCount() == 2) {
//                TreeItem<String> selectedFromTreeView = treeTable.getSelectionModel().getSelectedItem();
//                if (selectedFromTreeView.getChildren().isEmpty()) {
//                    System.out.println(selectedFromTreeView.getValue());
//
//                    if (selectedFromTreeView != null) {
//                        if (firstNodeSelect == 0) {
//                            stopList.add(selectedFromTreeView.getValue());
//                            startLabel.setText(stopList.get(0));
//                            firstNodeSelect = 1;
//                        }
//                        else if (firstNodeSelect == 1) {
//                            stopList.add(selectedFromTreeView.getValue());
//                            endLabel.setText(stopList.get(1));
//                            firstNodeSelect = 0;
//                            try {
//                                findPathSingleSegment(stopList.get(0), stopList.get(1));
//                                drawPath(FLOOR);
//                            }
//                            catch(SQLException se) { //oh FINE we'll handle the damn exception
//                                se.printStackTrace(); //guess we can't keep throwing it forever
//                            }
//                        }
//                    }
//                }
//            }
//        });
//
//        addStop.setOnAction((ActionEvent e)->{
//            try {
//                addDestination(contextMenuX, contextMenuY);
//
//            }
//            catch(SQLException se) {
//                se.printStackTrace();
//            }
//        });
//        addFav.setOnAction((ActionEvent e) -> {
//            try {
//                Map<String, String> node = getNearestNode(contextMenuX, contextMenuY);
//                if(node != null) {
//                    if(fav.getChildren().size() == 0) treeTable.getRoot().getChildren().add(0, fav);
//                    db.addFavoriteNodeToUser(Aapp.username, node.get("NODEID"), node.get("LONGNAME"));
//                    fav.getChildren().add(new TreeItem<String>(node.get("LONGNAME")));
//                    if(node.get("NODETYPE").equals("PARK")) drawSingleNode(node, Color.YELLOW);
//                    else drawSingleNode(node, Color.HOTPINK);
//                    treeTable.refresh();
//                }
//            } catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//        });
//
//        deleteFav.setOnAction((ActionEvent e)->{
//            try {
//                Map<String, String> node = getNearestNode(contextMenuX, contextMenuY);
//                if(node != null) {
//                    if(db.getFavoriteNodeByUserAndName(Aapp.username, node.get("LONGNAME")) != null){
//                        for (int i = 0; i < fav.getChildren().size(); i++) {
//                            String nodeName = fav.getChildren().get(i).getValue();
//                            if (nodeName.equals(node.get("LONGNAME"))) {
//                                fav.getChildren().remove(i);
//                                db.deleteFavoriteNodeFromUser(Aapp.username, nodeName);
//                                drawSingleNode(node, Color.BLUE);
//                                break;
//                            }
//                        }
//                        if(fav.getChildren().isEmpty()) treeTable.getRoot().getChildren().remove(0);
//                        treeTable.refresh();
//                    }
//                }
//            } catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//        });
//        contextMenu.getItems().clear();
//        if (Aapp.userType.equals("Guest")) contextMenu.getItems().addAll(addStop);
//        else contextMenu.getItems().addAll(addStop, addFav, deleteFav);
//
//        mapView.setOnContextMenuRequested(event -> {
//            contextMenu.show(mapView, event.getScreenX(), event.getScreenY());
//            contextMenuX = event.getX();
//            contextMenuY = event.getY();
//        });
//
//    }
//
//    @Override
//    public void goHome() {
//        voice.stop();
//        super.goHome();
//    }
//
//    public void drawNodesAndFavorites() throws SQLException{
//        for (Map<String, String> node: db.getNodesByValue("FLOOR", FLOOR)) {
//            if (covidLikely.equals("true") && node.get("LONGNAME").equals("75 Francis Valet Drop-off")) {}
//            else if (covidLikely.equals("false") && node.get("LONGNAME").equals("Emergency Department Entrance")){}
//            else if(!(node.get("NODETYPE").equals("HALL") || node.get("NODETYPE").equals("WALK"))) drawSingleNode(node, Color.BLUE);
//        }
//        for(Map<String, String> fav : db.getFavoriteNodesForUser(Aapp.username)){
//            Map<String, String> node = db.getNode(fav.get("LOCATIONID"));
//            if(node.get("FLOOR").equals(FLOOR)){
//                if(node.get("NODETYPE").equals("PARK")) drawSingleNode(node, Color.YELLOW);
//                else drawSingleNode(node, Color.HOTPINK);
//            }
//        }
//    }
//
//    @Override
//    public void changeFloor(String floor) throws SQLException{
//        drawFloor(floor);
//        if(activePath) {
//            drawPath(floor);
//            for (Map<String, String> intermediatePointToDraw : intermediatePoints) {
//                if (intermediatePointToDraw.get("FLOOR").equals(FLOOR)) {
//                    if (intermediatePoints.indexOf(intermediatePointToDraw) == intermediatePoints.size() - 1) {
//                        drawSingleNodeHighLight(intermediatePointToDraw, Color.MAGENTA);
//                    } else drawSingleNodeHighLight(intermediatePointToDraw, Color.ORANGE);
//                }
//            }
//            if (intermediatePoints.size() > 0 && intermediatePoints.get(intermediatePoints.size() - 1).get("FLOOR").equals(FLOOR))
//                drawSingleNode(intermediatePoints.get(intermediatePoints.size()-1),Color.MAGENTA);
//        }
//        else drawNodesAndFavorites();
//    }
//
//    public void drawNodesNoHallWalk(Color colorOfNodes) throws SQLException{
//        for (Map<String, String> node: db.getNodesByValue("FLOOR", FLOOR)) {
//            if (covidLikely.equals("true") && node.get("LONGNAME").equals("75 Francis Valet Drop-off")) {}
//            else if (covidLikely.equals("false") && node.get("LONGNAME").equals("Emergency Department Entrance")) {}
//            else if(!(node.get("NODETYPE").equals("HALL") || node.get("NODETYPE").equals("WALK"))) drawSingleNode(node, colorOfNodes);
//        }
//    }
//
//    /**
//     * Clears out everything relating to navigation, including the stop list and path, and redraws the current floor
//     */
//    public void clearNav() throws SQLException{
//        stopList.clear();
//        currPath.clear();
//        currPathDir.clear();
//        intermediatePoints.clear();
//        activePath = false;
//        etaLabel.setText("");
//        startLabel.setText("");
//        endLabel.setText("");
//        drawFloor(FLOOR);
//        drawNodesAndFavorites();
//        startLabel.setText("");
//        endLabel.setText("");
//
//        listDirVBox.setVisible(false);
//        treeTable.setVisible(true);
//        treeTable.toFront();
//    }
//
//
//
//
//    /**
//     * Cancels the current set of text directions
//     */
//    public void cancelDir() {
//        stepByStep.setVisible(false);
//        listDirVBox.setVisible(true);
//        treeTable.setVisible(false);
//        listDirVBox.toFront();
//
//        cancelPath.setDisable(false);
//    }
//
//    /**
//     * Starts the text directions once they're initialized
//     */
//    public void startDir() throws SQLException,InterruptedException{
//
//        stepByStep.setVisible(true);
//        listDirVBox.setVisible(false);
//        treeTable.setVisible(false);
//        stepByStep.toFront();
//
//        cancelPath.setDisable(true);
//
//        dirIndex = 0;
//        changeArrow(currPathDir.get(0).get(dirIndex));
//        String nodeID = currPathDir.get(1).get(0);
//        if(nodeID.contains(",")) nodeID = nodeID.substring(0, currPathDir.get(1).get(0).indexOf(","));
//        changeFloor(db.getNode(nodeID).get("FLOOR"));
//        drawPath(FLOOR);
//        highlightDirection();
//        curDirection.setText(currPathDir.get(0).get(dirIndex));
//        if(voiceDirection.isSelected()) {
//            voice.say(voice.getTextOptimization(curDirection.getText()), newThread);
//        }
//    }
//
//    /**
//     * Progresses to the next step in the text directions
//     */
//    public void progress() throws SQLException,InterruptedException {
//        voice.stop();
//        if (dirIndex < currPathDir.get(0).size() - 1){
//            unHighlightDirection();
//            dirIndex += 1;
//            String curNode = currPathDir.get(1).get(dirIndex);
//            String curFloor = getInstructionsFloor(curNode);
//
//            if(!curFloor.equals(FLOOR)) changeFloor(curFloor);
//
//            changeArrow(currPathDir.get(0).get(dirIndex));
//            curDirection.setText(currPathDir.get(0).get(dirIndex)); //get next direction
//            highlightDirection();
//            if(voiceDirection.isSelected()) {
//                voice.say(voice.getTextOptimization(curDirection.getText()), newThread);
//            }
//        }
//    }
//
//    /**
//     * Moves back to the previous step in the text directions
//     */
//    public void regress() throws SQLException,InterruptedException{
//        voice.stop();
//        if (dirIndex != 0) {
//            unHighlightDirection();
//            dirIndex -= 1;
//            String curNode = currPathDir.get(1).get(dirIndex);
//            String curFloor = getInstructionsFloor(curNode);
//            if(!curFloor.equals(FLOOR)) changeFloor(curFloor);
//            changeArrow(currPathDir.get(0).get(dirIndex));
//            curDirection.setText(currPathDir.get(0).get(dirIndex));
//            highlightDirection();
//            if(voiceDirection.isSelected()) {
//                voice.say(voice.getTextOptimization(curDirection.getText()), newThread);
//            }
//        }
//    }
//
//    /**
//     * Extracts the floor from the provided node or nodes corresponding to a text direction.
//     * @param nodeInstruction The node or nodes passed in are of the form NODEID or NODEID,NODEID where the second represents the
//     * action of traveling between two nodes.
//     * @return A floor that corresponds to the map
//     */
//    private String getInstructionsFloor(String nodeInstruction){
//        String curFloor = null;
//        if (nodeInstruction.contains(",")) {
//            int index = nodeInstruction.indexOf(",");
//            try {
//                curFloor = db.getNode(nodeInstruction.substring(0, index)).get("FLOOR");
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        else {
//            try {
//                curFloor = db.getNode(nodeInstruction).get("FLOOR");
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        return curFloor;
//    }
//
//    /**
//     * Changes the icon for the current text direction
//     * @param direction The current text direction, used to determine what icon is needed
//     */
//    public void changeArrow(String direction){ //update arrow
//        Image arrowImg; //TODO: Switch case?
//        if (direction.contains("left")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/leftArrow.png");
//        else if (direction.contains("right")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/rightArrow.png");
//        else if (direction.contains("elevator")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/elevator.png");
//        else if (direction.contains("stairs")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/stairs.png");
//        else if (direction.contains("Turn around")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/turn around.png");
//        else if (direction.contains("You have arrived at your destination.")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/arrived.png");
//        else arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/straight.png");
//
//        arrow.setImage(arrowImg);
//    }
//
//    /**
//     * Adds all of the text directions to the visual list of directions on the screen
//     */
//    public void initializeDirections() { // adds each step to the list of direction
//        cancelDir();
//        listOfDirections.getChildren().clear();
//        for (int i = 0; i < currPathDir.get(0).size(); i++) {
//
//            ImageView arrowImage = new ImageView();     //fitHeight="69.0" fitWidth="73.0"
//            Image img;
//            String direction = currPathDir.get(0).get(i);
//
//            arrowImage.setFitWidth(50.0);
//            arrowImage.setFitHeight(50.0);
//
//            if (direction.contains("left")) img = new Image("/edu/wpi/aquamarine_axolotls/img/leftArrow.png");
//            else if (direction.contains("right")) img = new Image("/edu/wpi/aquamarine_axolotls/img/rightArrow.png");
//            else if (direction.contains("elevator")) img = new Image("/edu/wpi/aquamarine_axolotls/img/elevator.png");
//            else if (direction.contains("stairs")) img = new Image("/edu/wpi/aquamarine_axolotls/img/stairs.png");
//            else if (direction.contains("Turn around")) img = new Image("/edu/wpi/aquamarine_axolotls/img/turn around.png");
//            else if (direction.contains("You have arrived at your destination.")) img = new Image("/edu/wpi/aquamarine_axolotls/img/arrived.png");
//            else img = new Image("/edu/wpi/aquamarine_axolotls/img/straight.png");
//
//            arrowImage.setImage(img);
//
//            Label l = new Label(direction);
//            l.setWrapText(true);
//
//            HBox hbox = new HBox(10, arrowImage, l);
//            hbox.setAlignment(Pos.CENTER_LEFT);
//
//            listOfDirections.getChildren().add(hbox);
//        }
//    }
//
///*    public void extractValue(){
//
//    }*/
//
//    /**
//     * Highlights the current portion of the map that the current direction is on
//     */
//    public void highlightDirection() throws SQLException{
//        String curID = currPathDir.get(1).get(dirIndex);
//
//        // draws an edge on map
//        if (curID.contains(",")) {
//            int index = curID.indexOf(",");
//            Map<String, String> start = db.getNode(curID.substring(0,index));
//            Map<String, String> end = db.getNode(curID.substring(index+1));
//            drawTwoNodesWithEdge(start, end, Color.BLUE, Color.BLUE, Color.RED );
//
//            double X1 = xScale(Integer.parseInt(start.get("XCOORD")));
//            double Y1 = yScale(Integer.parseInt(start.get("YCOORD")));
//            double X2 = xScale(Integer.parseInt(end.get("XCOORD")));
//            double Y2 = yScale(Integer.parseInt(end.get("YCOORD")));
//
//            double centerX = (X1 + X2) / 2.0;
//            double centerY = (Y1 + Y2) / 2.0;
//
//            double rotationAngle = Math.atan2(Y2-Y1, X2-X1) * 180 / Math.PI + 90.0;
//
//            if(start.get("FLOOR").equals(end.get("FLOOR"))){
//                drawArrow(centerX, centerY + 1, start.get("FLOOR"), rotationAngle);
//            } else {
//                removeDirectionArrow();
//            }
//
//        } else {
//            Map<String, String> node = db.getNode(curID);
//            drawSingleNode(node, Color.RED);
////            if(dirIndex + 1 < currPathDir.get(1).size()) {
////                String nextID = currPathDir.get(1).get(dirIndex+1);
////                nextID = nextID.substring(0, nextID.indexOf(","));
////                node = db.getNode(nextID);
////                drawSingleNode(node, Color.RED);
////            }else {
////                node = db.getNode(curID);
////                drawSingleNode(node, Color.RED);
////            }
//
//
//
//            if (dirIndex == currPathDir.get(1).size() - 1){
//                double X1 = xScale(Integer.parseInt(node.get("XCOORD")));
//                double Y1 = yScale(Integer.parseInt(node.get("YCOORD")));
//
//                drawArrow(X1, Y1, node.get("FLOOR"), 0);
//            }
//            else {
//                String nextNodes = currPathDir.get(1).get(dirIndex + 1);
//                String nextNodeID = nextNodes.substring(nextNodes.indexOf(",")+1);
//                Map<String, String> nextNode = db.getNode(nextNodeID);
//
//                double X1 = xScale(Integer.parseInt(node.get("XCOORD")));
//                double Y1 = yScale(Integer.parseInt(node.get("YCOORD")));
//                double X2 = xScale(Integer.parseInt(nextNode.get("XCOORD")));
//                double Y2 = yScale(Integer.parseInt(nextNode.get("YCOORD")));
//                double rotationAngle = Math.atan2(Y2-Y1, X2-X1) * 180 / Math.PI + 90.0;
//
//                if(node.get("FLOOR").equals(nextNode.get("FLOOR"))){
//                    drawArrow(X1, Y1, node.get("FLOOR"), rotationAngle);
//                }else {
//                    removeDirectionArrow();
//                }
//            }
//        }
//    }
//
//    /**
//     * Removes the highlight from the current part of the map
//     */
//    public void unHighlightDirection() throws SQLException{
//        String curNode = currPathDir.get(1).get(dirIndex);
//        if (curNode.contains(",")){
//            int index = curNode.indexOf(",");
//            Map<String, String> start = db.getNode(curNode.substring(0,index));
//            Map<String, String> end = db.getNode(curNode.substring(index+1));
//            drawTwoNodesWithEdge(start, end, Color.BLUE, Color.BLUE, Color.BLACK );
//        }
//        else{
//            if(dirIndex != 0) drawSingleNode(db.getNode(curNode), Color.BLUE);
//        }
//
//    }
//
//    /**
//     * Gets the current closest node to the mouse and uses it to navigate
//     * If there's no active path, this function will define a new one -- otherwise, it will add more stops
//     */
//    public void addDestination(double x, double y) throws SQLException{
//
//        //if(event.getButton().equals(MouseButton.PRIMARY)) {
//        Map<String, String> newDestination = getNearestNode(x, y);
//
//        if (newDestination.get("NODETYPE").equals("HALL") || newDestination.get("NODETYPE").equals("WALK")) return;
//
//        if (newDestination != null) {
//            String currCloseName = newDestination.get("LONGNAME");
//
//            if (!activePath) { //if there's no active path, we'll handle that
//                if ( firstNodeSelect == 0 ) {
//                    firstNode = currCloseName;
//                    firstNodeSelect = 1;
//                    drawSingleNodeHighLight(newDestination,Color.GREEN);
//                    startLabel.setText(currCloseName);
//                }
//                else if ( firstNodeSelect == 1 ) {
//                    stopList.clear ( );
//                    stopList.add ( firstNode );
//                    stopList.add ( currCloseName );
//                    currPath.clear ( );
//                    findPathSingleSegment ( stopList.get ( 0 ) ,stopList.get ( 1 ) );
//                    drawPath ( FLOOR );
//                    intermediatePoints.add(newDestination);
//                    drawSingleNodeHighLight(newDestination,Color.MAGENTA);
//                    endLabel.setText(currCloseName);
//                }
//            }
//            else {
//                stopList.add(stopList.size(), currCloseName);
//                currPath.clear();
//                for (int i = 0; i < stopList.size() - 1; i++) {
//                    findPathSingleSegment(stopList.get(i), stopList.get(i + 1));
//                }
//                drawPath(FLOOR);
//                intermediatePoints.add(newDestination); // store the intermediate points, not erased when drawing new intermediate path
//                for (Map<String,String> intermediatePointToDraw : intermediatePoints){
//                    if ((intermediatePointToDraw.get("FLOOR").equals(FLOOR)) &&
//                            (intermediatePoints.indexOf(intermediatePointToDraw) != intermediatePoints.size() - 1))
//                                drawSingleNodeHighLight(intermediatePointToDraw, Color.ORANGE);
//                }
//                drawSingleNodeHighLight(newDestination,Color.MAGENTA);
//            }
//        }
//
//    }
//
//    /**
//     *  starting the path for external
//     */
//    public void findPathExt() {
//        listOfDirectionsExt.getChildren().clear();
//
//        String destination = destinationDropdown.getSelectionModel().getSelectedItem().toString();
//        String startLocation = startTextfield.getText();
//
//        try{
//            switch(destination){
//                case "Emergency Room":
//                    dirLeg = navigateToER(startLocation);
//                    break;
//                case "Parking Spot":
//                    dirLeg = navigateToClosestParking(startLocation);
//                    break;
//                case "Valet":
//                    dirLeg = navigateToClosestValet(startLocation);
//                    break;
//            }
//            Duration eta = dirLeg.duration;
//            etaLabelExt.setText(String.valueOf(eta));
//
//            stepByStepExt.setVisible(false);
//            listDirVBoxExt.setVisible(true);
//            listDirVBoxExt.toFront();
//
//            DirectionsStep[] steps = dirLeg.steps;
//            System.out.println(steps);
//
//            for (int i = 0; i < steps.length; i++){
//                String s = steps[i].htmlInstructions;
//                String newString = String.valueOf(i+1) + ") " + parseHtmlDir(s);
//
//                if (newString.contains("Destination")){
//                    String[] str = newString.split("Destination", 2);
//                    str[1] = String.valueOf(i+1) + ") Destination" + str[1];
//
//                    for (int j = 0; j < 2; j++){
//                        extDir.add(str[j]);
//                        Label l = new Label(str[j]);
//                        l.setWrapText(true);
//                        listOfDirectionsExt.getChildren().add(l);
//                    }
//                    return;
//                }
//
//                extDir.add(newString);
//                Label l = new Label(newString);
//                l.setWrapText(true);
//                listOfDirectionsExt.getChildren().add(l);
//            }
//        } catch (Exception e) {
//            apiKeyPane.setVisible(true);
//
//        }
//
//    }
//
//    /**
//     * initializing step-by-step for external directions
//     */
//    public void startDirExt() {
//        stepByStepExt.setVisible(true);
//        listDirVBoxExt.setVisible(false);
//        stepByStepExt.toFront();
//
//        dirIndexExt = 0;
//
//        curDirectionExt.setText(extDir.get(dirIndexExt));
//        if (voiceDirection.isSelected()) {
//            String dirText = curDirectionExt.getText();
//            voice.say(dirText.substring(dirText.indexOf(") ")), newThread);
//        }
//    }
//
//    public void regressExt() {
//        voice.stop();
//        if (dirIndexExt != 0){
//            dirIndexExt -= 1;
//            curDirectionExt.setText(extDir.get(dirIndexExt));
//            if(voiceDirection.isSelected()) {
//                String dirText = curDirectionExt.getText();
//                voice.say(dirText.substring(dirText.indexOf(") ")), newThread);
//            }
//        }
//    }
//
//    public void progressExt() {
//        voice.stop();
//        if (dirIndexExt < extDir.size()-1){
//            dirIndexExt += 1;
//            curDirectionExt.setText(extDir.get(dirIndexExt));
//            if(voiceDirection.isSelected()) {
//                String dirText = curDirectionExt.getText();
//                voice.say(dirText.substring(dirText.indexOf(") ")), newThread);
//            }
//        }
//    }
//
//    /**
//     * going back to list directions from step-by-step
//     */
//    public void cancelDirExt() {
//        stepByStepExt.setVisible(false);
//        listDirVBoxExt.setVisible(true);
//        listDirVBoxExt.toFront();
//    }
//
//    public void clearNavExt() {
//        stepByStepExt.setVisible(false);
//        listDirVBoxExt.setVisible(false);
//    }
//
//    public String parseHtmlDir(String direction){
//        String parsed = direction.replaceAll("\\<.*?\\>", "");
//        return parsed;
//    }
//
//    public void submitApiKey(){
//        prefs.put(Settings.API_KEY, apiKey.getText());
//        apiKeyPane.setVisible(false);
//
//        if (!apiKey.getText().equals(null)){
//            invalidKey.setVisible(true);
//        }
//        findPathExt();
//    }
//    @FXML
//    public void toggleVoiceDirectionButton() {
//        if (!voiceDirection.isSelected()){
//            voice.stop();
//        } else if (internalNav.isSelected() && stepByStep.isVisible()) {
//            voice.say(voice.getTextOptimization(curDirection.getText()), newThread);
//        } else if (externalNav.isSelected() && stepByStepExt.isVisible()) {
//            String dirText = curDirectionExt.getText();
//            voice.say(dirText.substring(dirText.indexOf(") ")), newThread);
//        }
//    }
//
//    @FXML
//    public void stopVoice() {
//        voice.stop();
//    }
}


