package edu.wpi.cs3733.d21.teamA.views.mapping;
import edu.wpi.cs3733.d21.teamA.extras.SocketClient;
import edu.wpi.cs3733.d21.teamA.pathplanning.*;
import javafx.application.Platform;

import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsStep;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import edu.wpi.cs3733.d21.teamA.extras.VoiceController;
import edu.wpi.cs3733.d21.teamA.Aapp;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static edu.wpi.cs3733.d21.teamA.Settings.*;

public class Navigation extends GenericMap {
    public JFXButton drawerActionButton;
    public ImageView drawerActionImage;


    List<String> stopList = new ArrayList<>(); //Holds all the stops for when we're doing pathfinding
    String[] startAndStop = new String[]{"", ""};
    List<Map<String, String>> currentPath = new ArrayList<>();

    String covidLikely;

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
    private int currentStepNumber = 0;
    private int currentStepNumberGmaps = 0;
    private List<List<String>> curPathDirections = new ArrayList<>();
    double eta;
    String currentNodeIDContextMenu;

    MenuItem removeStop = new MenuItem("Remove ");
    MenuItem addStart = new MenuItem("Set to Start Location");
    MenuItem addEnd = new MenuItem("Set to End Location");
    MenuItem addFav = new MenuItem("Add Favorite");
    MenuItem deleteFav = new MenuItem("Delete Favorite");
    MenuItem changeToStart = new MenuItem("Change to Start");
    MenuItem changeToEnd = new MenuItem("Change to End");
    MenuItem addStop = new MenuItem("Add Stop");

    boolean isVoiceToggled;
    boolean robotConnection;

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
        if (PREFERENCES.get(SEARCH_ALGORITHM, null) == null) {
            PREFERENCES.put(SEARCH_ALGORITHM, "A Star");
        }

        switch (PREFERENCES.get(SEARCH_ALGORITHM,null)) {
            case "A Star":
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
                break;
            case "Dijkstra":
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new Dijkstra());
                break;
            case "Breadth First":
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new BreadthFirstSearch());
                break;
            case "Depth First":
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new DepthFirstSearch());
                break;
            case "Best First":
                SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new BestFirstSearch());
                break;
        }

        drawer.setOnDrawerClosed(event ->{
            drawerActionButton.setLayoutX(-5);
            drawerActionButton.setVisible(true);
            drawer.setVisible(false);
        });



        currentMenu.setUpTree();

        mapView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() != MouseButton.SECONDARY) contextMenu.hide();
        });


        removeStop.setOnAction((ActionEvent e) -> {
            if(startAndStop[0].equals(currentNodeIDContextMenu)){
                if(stopList.isEmpty()){
                    startAndStop[0] = "";
                    currentPath.clear();
                    sideControllers.get(1).clearAll();
                    sideControllers.get(2).clearAll();
                    drawFloor(FLOOR);
                }
                else{
                    startAndStop[0] = stopList.get(0);
                    changeNodeColorOnImage(startAndStop[0], Color.GREEN);
                    stopList.remove(0);
                }
            }
            else if(startAndStop[1].equals(currentNodeIDContextMenu)){
                if(stopList.isEmpty()){
                    startAndStop[1] = "";
                    currentPath.clear();
                    sideControllers.get(1).clearAll();
                    sideControllers.get(2).clearAll();
                    drawFloor(FLOOR);
                }
                else{
                    startAndStop[1] = stopList.get(stopList.size()-1);
                    changeNodeColorOnImage(startAndStop[stopList.size()-1], Color.RED);
                    stopList.remove(stopList.size()-1);
                }
            }
            else stopList.remove(currentNodeIDContextMenu);
            double opacity = 1;
            if(currentPath.size() > 0) opacity = .4;
            changeNodeColorOnImage(currentNodeIDContextMenu, Color.web("#003da6", opacity));
            updateNodeSize(currentNodeIDContextMenu, 3);
            setStartAndEnd();
            openDrawer();
            if(stopList.size() >= 2) findPath();
        });

        addStart.setOnAction((ActionEvent e) ->{
            addStart(currentNodeIDContextMenu);
        });

        addEnd.setOnAction((ActionEvent e) ->{
            addEnd(currentNodeIDContextMenu);
        });

        changeToStart.setOnAction((ActionEvent e) -> {
            if(stopList.contains(currentNodeIDContextMenu)) stopList.remove(currentNodeIDContextMenu);
            double opacity = 1;
            if(currentPath.size() > 0) opacity = .4;
            changeNodeColorOnImage(startAndStop[0], Color.web("#003da6", opacity));
            updateNodeSize(startAndStop[0], 3);
            startAndStop[0] = currentNodeIDContextMenu;
            changeNodeColorOnImage(currentNodeIDContextMenu, Color.GREEN);
            updateNodeSize(currentNodeIDContextMenu, 5);
            setStartAndEnd();
            openDrawer();
            if(!startAndStop[0].equals("") && !startAndStop[1].equals("")) findPath();
        });

        changeToEnd.setOnAction((ActionEvent e) -> {
            if(stopList.contains(currentNodeIDContextMenu)) stopList.remove(currentNodeIDContextMenu);
            double opacity = 1;
            if(currentPath.size() > 0) opacity = .4;
            changeNodeColorOnImage(startAndStop[1], Color.web("#003da6", opacity));
            updateNodeSize(startAndStop[1], 3);
            startAndStop[1] = currentNodeIDContextMenu;
            changeNodeColorOnImage(currentNodeIDContextMenu, Color.RED);
            updateNodeSize(currentNodeIDContextMenu, 5);
            setStartAndEnd();
            openDrawer();
            if(!startAndStop[0].equals("") && !startAndStop[1].equals("")) findPath();
        });

        addFav.setOnAction((ActionEvent e) ->{
            sideControllers.get(0).addToFavorites(currentNodeIDContextMenu);
        });

        deleteFav.setOnAction((ActionEvent e)->{
            sideControllers.get(0).deleteFromFavorites(currentNodeIDContextMenu);
        });

        addStop.setOnAction((ActionEvent e) ->{
            addStop(currentNodeIDContextMenu);
        });
        contextMenu.getItems().addAll(removeStop, addStart, addEnd, changeToStart, changeToEnd, addStop, addFav, deleteFav);

        sideControllers.get(0).treeTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TreeItem<String> selectedFromTreeView = sideControllers.get(0).treeTable.getSelectionModel().getSelectedItem();
                if (selectedFromTreeView.getChildren().isEmpty()) {
                    try {
                        String nodeID = db.getNodesByValue("LONGNAME", selectedFromTreeView.getValue()).get(0).get("NODEID");
                        if(startAndStop[0].equals("") && !startAndStop[1].equals(nodeID)){
                            changeFloor(db.getNode(nodeID).get("FLOOR"));
                            addStart(nodeID);
                        }
                        else if(startAndStop[1].equals("") && !startAndStop[0].equals(nodeID)){
                            changeFloor(db.getNode(nodeID).get("FLOOR"));
                            addEnd(nodeID);
                        }
                        else if(!startAndStop[0].equals("") && !startAndStop[1].equals("")
                                && !startAndStop[1].equals(nodeID) && !startAndStop[0].equals(nodeID)
                                && !stopList.contains(nodeID) && !currentPath.contains(db.getNode(nodeID))){
                            changeFloor(db.getNode(nodeID).get("FLOOR"));
                            addStop(nodeID);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    public void addStart(String nodeID){
        changeNodeColorOnImage(nodeID, Color.GREEN);
        updateNodeSize(nodeID, 5);
        startAndStop[0] = nodeID;
        setStartAndEnd();
        openDrawer();
        if(!startAndStop[0].equals("") && !startAndStop[1].equals("")) findPath();
    }

    public void addEnd(String nodeID){
        changeNodeColorOnImage(nodeID, Color.RED);
        updateNodeSize(nodeID, 5);
        startAndStop[1] = nodeID;
        setStartAndEnd();
        openDrawer();
        if(!startAndStop[0].equals("") && !startAndStop[1].equals("")) findPath();
    }

    public void addStop(String nodeID){
        stopList.add(nodeID);
        findPath();
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
            try {
                if(!startAndStop[0].equals("") && db.getNode(startAndStop[0]).get("FLOOR").equals(FLOOR)){
                    changeNodeColorOnImage(startAndStop[0], Color.GREEN);
                    updateNodeSize(startAndStop[0], 5);
                }
                if(!startAndStop[1].equals("") && db.getNode(startAndStop[0]).get("FLOOR").equals(FLOOR)){
                    changeNodeColorOnImage(startAndStop[1], Color.RED);
                    updateNodeSize(startAndStop[1], 5);
                }
                for(String node : stopList){
                    if(db.getNode(node).get("FLOOR").equals(FLOOR)){
                        changeNodeColorOnImage(node, Color.ORANGE);
                        updateNodeSize(node, 5);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

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
        covidLikely = db.getUserByUsername(PREFERENCES.get(USER_NAME,null)).get("COVIDLIKELY");
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
        addStop.setVisible(false);
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
                if(currentPath.size() == 0){
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

                if(currentPath.size() == 0){
                    if(startAndStop[0].equals("") && !startAndStop[1].equals(currentNodeIDContextMenu)) addStart.setVisible(true);
                    else if(!startAndStop[0].equals(currentNodeIDContextMenu) && !startAndStop[1].equals(currentNodeIDContextMenu)) changeToStart.setVisible(true);
                    if(startAndStop[1].equals("") && !startAndStop[0].equals(currentNodeIDContextMenu)) addEnd.setVisible(true);
                    else if(!startAndStop[1].equals(currentNodeIDContextMenu) && !startAndStop[0].equals(currentNodeIDContextMenu)) changeToEnd.setVisible(true);
                    if(startAndStop[1].equals(currentNodeIDContextMenu) || startAndStop[0].equals(currentNodeIDContextMenu) || stopList.contains(currentNodeIDContextMenu)){
                        removeStop.setVisible(true);
                    }
                    contextMenu.show(mapView, e.getScreenX(), e.getScreenY());
                }
                else{
                    try {
                        if(!startAndStop[0].equals("") && !startAndStop[1].equals("")
                                && !startAndStop[1].equals(currentNodeIDContextMenu) && !startAndStop[0].equals(currentNodeIDContextMenu)
                                && !stopList.contains(currentNodeIDContextMenu) && !currentPath.contains(db.getNode(currentNodeIDContextMenu))){
                            addStop.setVisible(true);
                            contextMenu.show(mapView, e.getScreenX(), e.getScreenY());
                        }
                        if(!startAndStop[0].equals(currentNodeIDContextMenu) && !startAndStop[1].equals(currentNodeIDContextMenu)) changeToStart.setVisible(true);
                        if(!startAndStop[1].equals(currentNodeIDContextMenu) && !startAndStop[0].equals(currentNodeIDContextMenu)) changeToEnd.setVisible(true);

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }

            }
            else{
                try {
                    if(startAndStop[0].equals("") && !startAndStop[1].equals(nodeID)){
                        changeFloor(db.getNode(nodeID).get("FLOOR"));
                        addStart(nodeID);
                    }
                    else if(startAndStop[1].equals("") && !startAndStop[0].equals(nodeID)){
                        changeFloor(db.getNode(nodeID).get("FLOOR"));
                        addEnd(nodeID);
                    }
                    else if(!startAndStop[0].equals("") && !startAndStop[1].equals("")
                            && !startAndStop[1].equals(nodeID) && !startAndStop[0].equals(nodeID)
                            && !stopList.contains(nodeID) && !currentPath.contains(db.getNode(nodeID))){
                        changeFloor(db.getNode(nodeID).get("FLOOR"));
                        addStop(nodeID);
                    }
                } catch(SQLException ex){
                    ex.printStackTrace();
                }
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

    @Override
    public void setUpEdgeEventHandler(Line edge, String edgeID) {

    }


    //=== PATHFINDING

    /**
     * Uses the current search algorithm to find a path between all the current stops, then display that path on screen
     */
    @FXML
    void findPath() {
        goToListOfDirections();
        currentPath.clear();
        List<String> allStops = new ArrayList<>();
        allStops.add(startAndStop[0]);
        allStops.addAll(stopList);
        allStops.add(startAndStop[1]);
        for (int i = 0; i < allStops.size() - 1; i++) {
            String currentStart = allStops.get(i);
            String currentEnd = allStops.get(i + 1);
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
    void drawPath() {
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
                drawAnimatedPath(node.get("NODEID"),  nextNode.get("NODEID"), Color.BLACK);
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


    public void centerPath() {
        double furthestLeft = 10000;
        double furthestRight = 0;
        double furthestDown = 10000;
        double furthestUp = 0;

        if (!currentPath.isEmpty()) {
            for (Map<String, String> currNode: currentPath) {
                if (currNode.get("FLOOR").equals(FLOOR)) {
                    double currX = xScale(Integer.parseInt(currNode.get("XCOORD")));
                    double currY = yScale(Integer.parseInt(currNode.get("YCOORD")));

                    if (currX < furthestLeft) furthestLeft = currX;
                    if (currX > furthestRight) furthestRight = currX;

                    if (currY < furthestDown) furthestDown = currY;
                    if (currY > furthestUp) furthestUp = currY;
                }
            }

            double centerX = (Math.abs(furthestRight - furthestLeft) / 2) + furthestLeft;
            double centerY = (Math.abs(furthestUp - furthestDown) / 2) + furthestDown;

            double horizontalShift = (centerX / mapScrollPane.getViewportBounds().getWidth());
            double verticalShift = (centerY / mapScrollPane.getViewportBounds().getHeight());

            mapScrollPane.setHvalue(horizontalShift);
            mapScrollPane.setVvalue(verticalShift);
        }
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



    /**
     * Draws animated path
     * @param startID ID of starting node
     * @param endID ID of ending node
     * @param edgeCol Color of the edge
     */
    public void drawAnimatedPath(String startID, String endID, Color edgeCol) {
        Map<String, String> snode;
        Map<String, String> enode;

        try{
            snode = db.getNode(startID);
            enode = db.getNode(endID);
            String edgeID = startID + "_" + endID;

            double startX = xScale(Integer.parseInt(snode.get("XCOORD")));
            double startY = yScale(Integer.parseInt(snode.get("YCOORD")));
            double endX = xScale(Integer.parseInt(enode.get("XCOORD")));
            double endY = yScale(Integer.parseInt(enode.get("YCOORD")));

            Line l = new Line();
            l.getStrokeDashArray().addAll(25d, 10d);
            l.setStartX(startX);
            l.setStartY(startY);
            l.setEndX(endX);
            l.setEndY(endY);
            l.setStroke(edgeCol);
            l.setStrokeWidth(magicNumber);
            l.setId(edgeID);

            if(linesOnImage.containsKey(edgeID)){
                Line key = linesOnImage.get(edgeID);
                mapView.getChildren().set(mapView.getChildren().indexOf(key), l);
                linesOnImage.get(edgeID).setStroke(yellow);
            }
            else mapView.getChildren().add(l);

            linesOnImage.put(edgeID, l);

            final double maxOffset =
                    l.getStrokeDashArray().stream()
                            .reduce(
                                    0d,
                                    (a, b) -> a + b
                            );

            Timeline timeline = new Timeline(
                    new KeyFrame(

                            javafx.util.Duration.ZERO,
                            new KeyValue(
                                    l.strokeDashOffsetProperty(),
                                    0,
                                    Interpolator.LINEAR
                            )
                    ),
                    new KeyFrame(
                            javafx.util.Duration.seconds(2),
                            new KeyValue(
                                    l.strokeDashOffsetProperty(),
                                    maxOffset,
                                    Interpolator.LINEAR
                            )
                    )
            );
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.setRate(-1.0);
            timeline.play();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

                                            //=== SIDE BAR METHODS ===//

    public void startPath() throws IOException {
        goToStepByStep();
    }

    public void openDrawer(){
        mapScrollPane.setLayoutX(351);
        mapScrollPane.setPrefWidth(949);
        drawerActionImage.setImage(new Image("/edu/wpi/cs3733/d21/teamA/img/hideDrawer.png"));
        drawerActionButton.setLayoutX(349);
        drawer.open();
        drawer.setVisible(true);
    }

    public void closeDrawer(){
        mapScrollPane.setLayoutX(0);
        mapScrollPane.setPrefWidth(1300);
        drawerActionImage.setImage(new Image("/edu/wpi/cs3733/d21/teamA/img/openDrawer.png"));
        drawerActionButton.setVisible(false);
        drawer.close();
    }


    protected void goToTreeView() {
        drawer.setSidePane(treeViewSideMenu);
        currentMenu = sideControllers.get(0);
        setStartAndEnd();
    }

    public void goToListOfDirections() {
        if(currentMenu.equals(sideControllers.get(2))) {
            try {
                unHighlightDirection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        drawer.setSidePane(listOfDirectionsSideMenu);
        currentMenu = sideControllers.get(1);
        setStartAndEnd();
    }

    public void goToStepByStep() throws IOException {
        currentStepNumber = 0;
        drawer.setSidePane(stepByStepSideMenu);
        currentMenu = sideControllers.get(2);
        setStartAndEnd();
        startDir();
    }

    public void goToGmapsListOfDirections() {
        voice.stop();
        drawer.setSidePane(gmapsListOfDirections);
        sideControllers.get(3).setUpGmaps();
        currentMenu = sideControllers.get(3);
    }

    public void goToGmapsStepByStep() {
        currentStepNumberGmaps = 0;
        drawer.setSidePane(gmapsStepByStep);
        currentMenu = sideControllers.get(4);
        if(isVoiceToggled) {
            voice.say(voice.getTextOptimizationGmaps(gmapsDir.get(0)), newThread);
        }
        currentMenu.setCurDirection(gmapsDir.get(0));
        currentMenu.setCurArrow(textDirectionToImage(gmapsDir.get(0)));
    }


    public VBox setUpSideMenu(String name) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/cs3733/d21/teamA/fxml/" + name +  ".fxml"));
        loader.load();
        SideMenu controller = loader.getController();
        controller.navController = this;
        sideControllers.add(controller);
        return loader.getRoot();
    }

    public void setStartAndEnd(){
        int size = stopList.size();
        try{
            if(startAndStop[0].equals("")) currentMenu.setStartLabel("Start Location");
            else currentMenu.setStartLabel(db.getNode(startAndStop[0]).get("LONGNAME"));
            if(startAndStop[1].equals("")) currentMenu.setEndLabel("End Location");
            else currentMenu.setEndLabel(db.getNode(startAndStop[1]).get("LONGNAME"));
        } catch (SQLException e){
            e.printStackTrace();
        }

    }

                                        //==== LIST OF DIRECTIONS ====//
    public void startDir() throws IOException {
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
        String sendPacket = getROSDirection();
        if(robotConnection){
            Aapp.clientSender.send(sendPacket);
            popUp("Turtlebot Info", "\n\n\n\n\n Robot is coming ...\n Please wait... ");
        }
    }

    /**
     * Progresses to the next step in the text directions
     */
    public void progress() throws SQLException {
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
    public void regress() throws SQLException {
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
        if(isVoiceToggled && currentPath.size() > 0 && sideControllers.indexOf(currentMenu) == 2) voice.say(voice.getTextOptimization(curPathDirections.get(0).get(currentStepNumber)), newThread);
        else if(isVoiceToggled && sideControllers.indexOf(currentMenu) == 4 && gmapsDir.size() > 0) voice.say(voice.getTextOptimizationGmaps(gmapsDir.get(currentStepNumberGmaps)), newThread);
    }

    public void highlightDirection() throws SQLException{
        String curID = curPathDirections.get(1).get(currentStepNumber);

        if (curID.contains("_")) {
            String floor = db.getNode(curID.substring(0, curID.indexOf("_"))).get("FLOOR");
            if(!floor.equals(FLOOR)) changeFloor(floor);
            updateEdgeColor(curID, yellow);
            int index = curID.indexOf("_");
            Map<String, String> start = db.getNode(curID.substring(0,index));
            Map<String, String> end = db.getNode(curID.substring(index+1));
            drawPathArrow(start, end);

        }
        else {
            String floor = db.getNode(curID).get("FLOOR");
            if(!floor.equals(FLOOR)) changeFloor(floor);
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
        if (curDirectionID.contains("_")){
            String floor = db.getNode(curDirectionID.substring(0, curDirectionID.indexOf("_"))).get("FLOOR");
            if(!floor.equals(FLOOR)) changeFloor(floor);
            updateEdgeColor(curDirectionID, Color.BLACK);
        }
        else {
            String floor = db.getNode(curDirectionID).get("FLOOR");
            if(!floor.equals(FLOOR)) changeFloor(floor);
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
        startAndStop[0] = "";
        startAndStop[1] = "";
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
        if (direction.contains("left")) return new Image("/edu/wpi/cs3733/d21/teamA/img/leftArrow.png");
        else if (direction.contains("right")) return new Image("/edu/wpi/cs3733/d21/teamA/img/rightArrow.png");
        else if (direction.contains("elevator")) return new Image("/edu/wpi/cs3733/d21/teamA/img/elevator.png");
        else if (direction.contains("stairs")) return new Image("/edu/wpi/cs3733/d21/teamA/img/stairs.png");
        else if (direction.contains("Turn around")) return new Image("/edu/wpi/cs3733/d21/teamA/img/turn around.png");
        else if (direction.contains("You have arrived at your destination.")) return new Image("/edu/wpi/cs3733/d21/teamA/img/arrived.png");
        else return new Image("/edu/wpi/cs3733/d21/teamA/img/straight.png");

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


    private String getROSDirection(){
        String coordinateList = "";
        for (Map<String,String> n: currentPath) {
            coordinateList += n.get("XCOORD") + "," + n.get("YCOORD") + ";";
        }
        System.out.println(coordinateList);
        return coordinateList;
    }

    public void toggleRobot() throws IOException, InterruptedException {
        robotConnection = !robotConnection;
        if (robotConnection && !Aapp.serverRunning){
            String host = "192.168.1.118";
            Aapp.clientSender = new SocketClient(host,7777);
            Aapp.clientReceiver = new SocketClient(host,5555);
            Aapp.clientInfoReceiver = new SocketClient(host,5556);
            Aapp.clientThreadSender = new Thread(() -> {
                try {
                    String massage;
                    massage = Aapp.clientSender.getMassage();
                    //System.out.println("get massage");
                    if(!massage.equals("Verifying Server!")){
                        System.out.println("Server Wrong!");
                        System.exit(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Aapp.clientThreadSender.start();

            Aapp.clientInfoThreadReceiver = new Thread(() -> {
                try{
                    while (!Thread.currentThread().isInterrupted()){
                        String message = Aapp.clientInfoReceiver.getMassage();
                        System.out.println("in first info thread");
                        if(message.contains("progress to the")){
                                System.out.println(message);
                                Platform.runLater(() -> {
                                    try {
                                        System.out.println("progress 1");
                                        progress();
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                });
                                Thread.sleep(2500);
                                Platform.runLater(() -> {
                                    try {
                                        System.out.println("progress 2");
                                        progress();
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                });
                            }
//                            else if(robotDirectionNum == 0){
//                                robotDirectionNum ++;
//                        }
                        else if(message.contains("faraway")){
                            System.out.println(message);
                            Platform.runLater(() ->popUp("Turtlebot Info", "\n\n\n\n\n Looks like you are far behind ...\n Robot is waiting for you "));
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                });
                Aapp.clientInfoThreadReceiver.start();

            Aapp.clientThreadReceiver = new Thread(() -> {
                try{
                    while (!Thread.currentThread().isInterrupted()){
                        String message = Aapp.clientReceiver.getMassage();
                        Double[] robotCoordinate = getROSCoordinate(message);
                        if (robotCoordinate != null  && FLOOR.equals("L1")){
                            Platform.runLater(() -> drawRobotArrow(xScale((int)(robotCoordinate[0]*10+2130)),yScale((int)(-robotCoordinate[1]*10+1050)),FLOOR,-robotCoordinate[2]*180/Math.PI+90));
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
            Aapp.clientThreadReceiver.start();
            Aapp.serverRunning = true;
            System.out.println("server running");
        }
        else if(robotConnection && Aapp.serverRunning){
            Aapp.clientSender.send("resume nav");
            Aapp.clientThreadReceiver = new Thread(() -> {
                try{
                    while (!Thread.currentThread().isInterrupted()){
                        String message = Aapp.clientReceiver.getMassage();
                        System.out.println(message);
                        Double[] robotCoordinate = getROSCoordinate(message);
                        if (robotCoordinate != null && FLOOR.equals("L1")){
                            Platform.runLater(() -> drawRobotArrow(xScale((int)(robotCoordinate[0]*10+2130)),yScale((int)(-robotCoordinate[1]*10+1050)),FLOOR,-robotCoordinate[2]*180/Math.PI+90));
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
            Aapp.clientThreadReceiver.start();
            Aapp.clientInfoThreadReceiver.stop();
            Aapp.clientInfoThreadReceiver = new Thread(() -> {
                try{
                    while (!Thread.currentThread().isInterrupted()){
                        String message = Aapp.clientInfoReceiver.getMassage();
                        System.out.println("in restarted info thread");
                        if(message.contains("progress to the")){
                                System.out.println(message);
                                Platform.runLater(() -> {
                                    try {
                                        System.out.println("progress 1");
                                        progress();
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                });
                                Thread.sleep(2500);
                                Platform.runLater(() -> {
                                    try {
                                        System.out.println("progress 2");
                                        progress();
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                });
                        }
                        else if(message.contains("faraway")){
                            System.out.println(message);
                            Platform.runLater(() ->popUp("Turtlebot Info", "\n\n\n\n\n Looks like you are far behind ...\n Robot is waiting for you "));
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
            Aapp.clientInfoThreadReceiver.start();
        }
        else if(!robotConnection && Aapp.serverRunning){
            Aapp.clientThreadReceiver.suspend();
            //Aapp.clientInfoThreadReceiver.stop();
            removeDirectionArrow();
            Aapp.clientSender.send("cancel nav");
        }
    }
}


