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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class Navigation extends GenericMap {
    List<String> stopList = new ArrayList<>(); //Holds all the stops for when we're doing pathfinding
    List<Map<String, String>> currentPath = new ArrayList<>();
    @FXML
    private TreeTableView<String> treeTable;
    String covidLikely;
    VBox treeViewSideMenu;
    VBox listOfDirectionsSideMenu;
    VBox stepByStepSideMenu;
    @FXML
    JFXDrawer drawer;



    public void initialize() throws java.sql.SQLException, IOException {

        treeViewSideMenu = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/TreeViewSideMenu.fxml"));

        startUp();
        if(SearchAlgorithmContext.getSearchAlgorithmContext().context == null) {
            SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
        }

        drawer.setSidePane(treeViewSideMenu);

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
        treeTable.setRoot(root);
        treeTable.setShowRoot(false);
        treeTable.getColumns().add(treeTableColumn1);
    }

    @Override
    public void nodePopUp() {

    }

    @Override
    public void edgePopUp() {

    }

    @Override
    public void drawFloor(String floor) {
        changeFloorImage(floor);
//        if(currentPath.size() > 0){
//            drawNodes(Color.web("#003da6", .4));
//            drawPath();
//        }
//        else{
//            drawNodes(darkBlue);
//        }
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

    public void drawNodesNoHallWalk(Color colorOfNodes) throws SQLException{
        for (Map<String, String> node: db.getNodesByValue("FLOOR", FLOOR)) {
            if (covidLikely.equals("true") && node.get("LONGNAME").equals("75 Francis Valet Drop-off")) {}
            else if (covidLikely.equals("false") && node.get("LONGNAME").equals("Emergency Department Entrance")) {}
            else if(!(node.get("NODETYPE").equals("HALL") || node.get("NODETYPE").equals("WALK"))) drawSingleNode(node.get("NODEID"), colorOfNodes);
        }
    }

    @Override
    public Circle setEventHandler(Circle node, String nodeID) {
        //When you click a node in navigation, it gets selected/de-selected
        node.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                if (stopList.contains(nodeID)) { //If the node you click is already in the stopList, it gets removed
                    stopList.remove(nodeID); //So you can toggle destinations
                }
                else stopList.add(nodeID); //Otherwise, add the node's ID to the stopList

                if (stopList.size() >= 2) findPath(); //After adding the node, if the stopList is at least 2 items, we find the path

            }
        });

        node.setOnMouseEntered((MouseEvent e) -> {
            node.setRadius(5);
        });

        node.setOnMouseExited((MouseEvent e) -> {
            node.setRadius(magicNumber);
        });

        return node;
    }


    /**
     * Uses the current search algorithm to find a path between all the current stops, then display that path on screen
     */
    @FXML
    void findPath() {
        stepByStepSideMenu.setVisible(false);
        listOfDirectionsSideMenu.setVisible(true);
        treeViewSideMenu.setVisible(false);
        drawer.setSidePane(listOfDirectionsSideMenu);
//        for (int i = 0; i < stopList.size() - 1; i++) {
//            String currentStart = stopList.get(i);
//            String currentEnd = stopList.get(i + 1);
//            List<Map<String, String>> path = SearchAlgorithmContext.getSearchAlgorithmContext().getPath(currentStart, currentEnd);
//            currentPath.addAll(path);
//        }
//        drawPath();
    }

    /**
     * Draws the current path onto the map
     */
    void drawPath(){
        linesOnImage.clear();
        for(int i = 0; i < currentPath.size() - 1; i++){
            Map<String, String> node = currentPath.get(i);
            Map<String, String> nextNode = currentPath.get(i+1);
            if(!node.get("FLOOR").equals(nextNode.get("FLOOR"))){
                drawArrow(node.get("NODEID") + "_" + node.get("EDGEID"));
            }
            else {
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
                else if(stopList.contains(node)){
                    color = Color.ORANGE;
                    radius = 5;
                }
                drawSingleEdge(node.get("NODEID") + "_" + node.get("EDGEID"), Color.BLACK);
                changeNodeColorOnImage(node.get("NODEID"), color);
                updateNodeSize(node.get("NODEID"), radius);

            }
        }
    }

    public void submitApiKey(ActionEvent actionEvent) {
    }

    public void toggleVoiceDirectionButton(ActionEvent actionEvent) {
    }


    public void startPath(ActionEvent actionEvent) {
        stepByStepSideMenu.setVisible(true);
        listOfDirectionsSideMenu.setVisible(false);
        treeViewSideMenu.setVisible(false);
        drawer.setSidePane(stepByStepSideMenu);
    }

    public void goToStepByStep(ActionEvent actionEvent) {
        stepByStepSideMenu.setVisible(true);
        listOfDirectionsSideMenu.setVisible(false);
        treeViewSideMenu.setVisible(false);
        drawer.setSidePane(stepByStepSideMenu);
    }

    public void clearNav(ActionEvent actionEvent) {
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

    public void goToListOfDirections(ActionEvent actionEvent) {
        listOfDirectionsSideMenu.setVisible(true);
        stepByStepSideMenu.setVisible(false);
        treeViewSideMenu.setVisible(false);
        drawer.setSidePane(listOfDirectionsSideMenu);
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


