package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

public class Navigation extends GenericMap {

    @FXML private JFXComboBox startLocation;
    @FXML private JFXComboBox destination;
    @FXML private JFXButton findPathButton;
    @FXML private JFXButton cancelPath;
    @FXML private Label etaLabel;
    @FXML private Label curDirection;
    @FXML private ImageView arrow;
    @FXML private VBox stepByStep;
    @FXML private VBox listDirVBox;
    @FXML private VBox listOfDirections;

    ObservableList<String> options = FXCollections.observableArrayList();
    private int firstNodeSelect = 0;
    private String firstNode;
    private List<String> stopList = new ArrayList<>();
    private List<Node> currPath = new ArrayList<>();
    private int activePath = 0;
    private List<List<String>> currPathDir = new ArrayList<>();
    static int dirIndex = 0;

    @FXML
    public void initialize() {

        startUp();
        if(SearchAlgorithmContext.getSearchAlgorithmContext().context == null) {
            SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
        }
        startLocation.setItems(options);
        destination.setItems(options);

            stepByStep.setVisible(false);
            listDirVBox.setVisible(false);
            listDirVBox.toFront();

        } catch (SQLException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears out everything relating to navigation, including the stop list and path, and redraws the current floor
     */
    public void clearNav() {
        stopList.clear();
        currPath.clear();
        currPathDir.clear();
        activePath = 0;
        etaLabel.setText("");
        drawFloor(FLOOR);
        if (startLocation.getSelectionModel() != null && destination.getSelectionModel() != null) {
            startLocation.getSelectionModel().clearSelection();
            destination.getSelectionModel().clearSelection();
        }
        listDirVBox.setVisible(false);
    }


    /**
     * Takes the selections from the start and end dropdowns and uses them to find the full path from start to finish
     */
    public void findPath() {
        currPath.clear();
        stopList.clear();
        activePath = 0;
        if (startLocation.getSelectionModel().getSelectedItem() == null || destination.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        String start = startLocation.getSelectionModel().getSelectedItem().toString();
        String end = destination.getSelectionModel().getSelectedItem().toString();
        stopList.add(start);
        stopList.add(end);
        findPathSingleSegment(start, end);
        //drawFloor(FLOOR); // do we need this?
    }


    // draw path method
    public void drawPath() throws SQLException{
        if(currPath.isEmpty()) return;
        drawNodesAndFloor(currPath.get(0).getFloor(), new Color(0.0 , 0.0, 1.0, 0.4));
        for (int i = 0; i < currPath.size() - 1; i++) {
            if (currPath.get(i).getFloor().equals(FLOOR) &&
                    currPath.get(i + 1).getFloor().equals(FLOOR)) {
                drawNodes(currPath.get(i), currPath.get(i + 1), Color.BLUE, Color.BLUE, Color.BLACK);
            }
            if ((currPath.get(i).getNodeType().equals("STAI") && currPath.get(i+1).getNodeType().equals("STAI")) ||
                    (currPath.get(i).getNodeType().equals("ELEV") && currPath.get(i+1).getNodeType().equals("ELEV"))){
                drawArrow(currPath.get(i), currPath.get(i+1));
            }
        }
    }
    // draw floor that makes everything transparent


    /**
     * Uses the A-star algorithm to find the shortest path between a given start and end node
     * @param start String, long name of start node
     * @param end   String, long name of end node
     */
    public void findPathSingleSegment(String start, String end) {
        double etaTotal, minutes, seconds;

        currPath.addAll(SearchAlgorithmContext.getSearchAlgorithmContext().getPath(start, end));
        System.out.println(SearchAlgorithmContext.getSearchAlgorithmContext().context);

        etaTotal = SearchAlgorithmContext.getSearchAlgorithmContext().getETA(currPath);
        minutes = Math.floor(etaTotal);
        seconds = Math.floor((etaTotal - minutes) * 60);
        etaLabel.setText((int) minutes + " min " + (int) seconds + " sec");

        if (currPath.isEmpty()) return;

        firstNodeSelect = 0;
        activePath = 1;
        //drawFloor(FLOOR);
        //drawSingleNode(getNodeFromValid(stopList.get(stopList.size() - 1)));
        currPathDir.clear();
        currPathDir = SearchAlgorithmContext.getSearchAlgorithmContext().getTextDirections(currPath);
//        List<String> textDir = new ArrayList<String>();
//        textDir.add("left 1");
//        textDir.add("left 2");
//         = textDir;
        initializeDirections();
    }

    /**
     * Cancels the current set of text directions
     */
    public void cancelDir() {
        stepByStep.setVisible(false);
        listDirVBox.setVisible(true);
        listDirVBox.toFront();

        startLocation.setDisable(false);
        destination.setDisable(false);
        findPathButton.setDisable(false);
        cancelPath.setDisable(false);

        unHighlightDirection();
    }

    /**
     * Starts the text directions once they're initialized
     */
    public void startDir() {
        stepByStep.setVisible(true);
        listDirVBox.setVisible(false);
        stepByStep.toFront();

        startLocation.setDisable(true);
        destination.setDisable(true);
        findPathButton.setDisable(true);
        cancelPath.setDisable(true);

        dirIndex = 0;
        changeArrow(currPathDir.get(0).get(dirIndex));
        curDirection.setText(currPathDir.get(0).get(dirIndex)); //get first direction
        highlightDirection();
    }

    /**
     * Progresses to the next step in the text directions
     */
    public void progress() {
        if (dirIndex < currPathDir.get(0).size() - 1){
            unHighlightDirection();
            dirIndex += 1;
            String curNode = currPathDir.get(1).get(dirIndex);
            String curFloor = String curFloor = getInstructionsFloor(curNode);

            if(!curFloor.equals(FLOOR)); //drawFloor(FLOOR); // TODO: update to be method in navigation

            changeArrow(currPathDir.get(0).get(dirIndex));
            curDirection.setText(currPathDir.get(0).get(dirIndex)); //get next direction
            highlightDirection();
        }
    }

    /**
     * Moves back to the previous step in the text directions
     */
    public void regress() {
        if (dirIndex == 0){
            return;
        }else{
            unHighlightDirection();
            dirIndex -= 1;
            String curNode = currPathDir.get(1).get(dirIndex);
            String curFloor = getInstructionsFloor(curNode);
            if(!curFloor.equals(FLOOR)){
                //drawFloor(FLOOR); //TODO: update to be method in navigation
            }
            changeArrow(currPathDir.get(0).get(dirIndex));
            curDirection.setText(currPathDir.get(0).get(dirIndex));
            highlightDirection();
        }
    }

    /**
     * Extracts the floor from the provided node or nodes corresponding to a text direction.
     * @param nodeInstruction The node or nodes passed in are of the form NODEID or NODEID,NODEID where the second represents the
     * action of traveling between two nodes.
     * @return A floor that corresponds to the map
     */
    private String getInstructionsFloor(String nodeInstruction){
        String curFloor = null;
        if (nodeInstruction.contains(",")) {
            int index = nodeInstruction.indexOf(",");
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

    /**
     * Changes the icon for the current text direction
     * @param direction The current text direction, used to determine what icon is needed
     */
    public void changeArrow(String direction){ //update arrow
        Image arrowImg;
        if (direction.contains("left")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/leftArrow.png");
        else if (direction.contains("right")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/rightArrow.png");
        else if (direction.contains("elevator")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/elevator.png");
        else if (direction.contains("stairs")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/stairs.png");
        else if (direction.contains("Turn around")) arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/turn around.png");
        else arrowImg = new Image("/edu/wpi/aquamarine_axolotls/img/straight.png");

        arrow.setImage(arrowImg);
    }

    /**
     * Adds all of the text directions to the visual list of directions on the screen
     */
    public void initializeDirections() {                    // adds each step to the list of direction
        cancelDir();
        listOfDirections.getChildren().clear();
        for (int i = 0; i < currPathDir.get(0).size(); i++) {
            Label l = new Label(currPathDir.get(0).get(i));
            l.setWrapText(true);
            listOfDirections.getChildren().add(l);
        }
    }

    /**
     * Highlights the current portion of the map that the current direction is on
     */
    public void highlightDirection(){
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        String curNode = currPathDir.get(1).get(dirIndex);
        if (curNode.contains(",")){
            int index = curNode.indexOf(",");
            Node start = getNodeFromValidID(curNode.substring(0,index));
            System.out.println(start);

            Node end = getNodeFromValidID(curNode.substring(index+1));
            System.out.println(end);
            drawTwoNodesWithEdge(start, end, Color.RED, Color.BLUE, Color.RED );
            //drawSingleEdge(getNodeFromValidID(start), getNodeFromValidID(end), Color.RED);
            return;
        }
        drawSingleNode(getNodeFromValidID(curNode), gc, Color.RED);
    }

    /**
     * Removes the highlight from the current part of the map
     */
    public void unHighlightDirection(){
        String curNode = currPathDir.get(1).get(dirIndex);
        if (curNode.contains(",")){
            int index = curNode.indexOf(",");
            Node start = getNodeFromValidID(curNode.substring(0,index));
            Node end = getNodeFromValidID(curNode.substring(index+1));
            drawTwoNodesWithEdge(start, end, Color.BLUE, Color.BLUE, Color.BLACK );
        }
    }

    /**
     * Gets the current closest node to the mouse and uses it to navigate
     * If there's no active path, this function will define a new one -- otherwise, it will add more stops
     * @param event The mouseevent from the map when clicked on
     */
    public void addDestination(javafx.scene.input.MouseEvent event) {

        Node newDestination = getNearestNode(event.getX(), event.getY());

        if (newDestination == null) return;

        else {
            String currCloseName = newDestination.getLongName();

            if (activePath == 0) { //if there's no active path, we'll handle that
                if (firstNodeSelect == 0) {
                    firstNode = currCloseName;
                    firstNodeSelect = 1;
                } else if (firstNodeSelect == 1) {
                    stopList.clear();
                    stopList.add(firstNode);
                    stopList.add(currCloseName);
                    currPath.clear();
                    findPathSingleSegment(stopList.get(0), stopList.get(1));
                    drawFloor(FLOOR);
                }
            }
            else if (activePath == 1) {
                stopList.add(stopList.size() - 1, currCloseName);
                currPath.clear();
                for (int i = 0; i < stopList.size() - 1; i++) {
                    findPathSingleSegment(stopList.get(i), stopList.get(i + 1));
                }
                drawFloor(FLOOR);
            }
        }
    }
}


