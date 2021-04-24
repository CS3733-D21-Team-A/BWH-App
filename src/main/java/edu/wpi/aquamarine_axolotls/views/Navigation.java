package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBasicCloseTransition;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.pathplanning.SearchAlgorithm;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.*;

public class Navigation  extends SPage{

    @FXML
    private AnchorPane anchor;
    @FXML
    private AnchorPane nodeGridAnchor;
    @FXML
    private ImageView groundFloor;
    @FXML
    private ImageView floor1;
    @FXML
    private JFXButton homeButton;
    @FXML
    private JFXButton helpB;
    @FXML
    private JFXComboBox startLocation;
    @FXML
    private JFXComboBox destination;
    @FXML
    private JFXButton findPathButton;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private AnchorPane anchor1;
    @FXML
    private JFXButton addStopbtn;
    @FXML
    private JFXComboBox intermediate;
    @FXML
    private Label etaLabel;
    @FXML
    private AnchorPane mainAnchor;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private Text time;
    @FXML
    private ScrollPane scrollPane;
    @FXML private Image img;


    ObservableList<String> options = FXCollections.observableArrayList();
    DatabaseController db;
    List<Map<String, String>> validNodes = new ArrayList<>();
    private int firstNodeSelect = 0;
    private String firstNode;
    private List<String> pathList = new ArrayList<>();
    private int activePath = 0;


    @FXML
    private JFXHamburger burger;
    @FXML
    private JFXDrawer menuDrawer;

    @FXML
    private VBox box;

    private HamburgerBasicCloseTransition transition;
    static Double SCALE_DELTA = 1.25;
    static Double SCALE_TOTAL = 2.0;
    //final Double w = img.getWidth();
    //final Double h = img.getHeight();

    @FXML
    public void initialize() {
        try {
            db = new DatabaseController();
            List<Map<String, String>> nodes = db.getNodes();
            for (Map<String, String> node : nodes) {
                if (( node.get("NODETYPE").equals("WALK"))
                        || node.get("NODETYPE").equals("PARK")
                        || ( node.get("BUILDING").equals("45 Francis") && node.get("FLOOR").equals("1"))
                        || ( node.get("BUILDING").equals("Tower") && node.get("FLOOR").equals("1"))) {
                    options.add(node.get("NODEID"));
                    validNodes.add(node);
                }

            }
            floor1.setVisible(false);
            changeFloorNodes();

            startLocation.setItems(options);
            destination.setItems(options);
            intermediate.setItems(options);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void zoom(){

//        //Adds functionality to scrolling
//        scrollPane.addEventFilter(ScrollEvent.ANY, e ->{
//            //Consumes the event
//            e.consume();
//            if(e.getDeltaY() == 0) return;
//            double scaleFactor = (e.getDeltaY() > 0) ? SCALE_DELTA : 1/SCALE_DELTA;
            double newWidth = 1102.0 * SCALE_DELTA;
            double newHeight = 669.0 * SCALE_DELTA;

            System.out.println(String.valueOf(groundFloor.getFitHeight()) + "   " + newHeight);
            //Ensures that you do not exceed the limits of the map
            if(SCALE_DELTA * SCALE_TOTAL >= 1)
            {
               groundFloor.setFitWidth(newWidth);
               groundFloor.setFitHeight(newHeight);
            }
        //});
    }

    /**
     * Scales the x coordinate from table for image (5000,3400)
     * @param xCoord x coordinate from table
     * @return scaled X coordinate
     */
    public Double xScale(int xCoord) {
        Double xCoordDouble = new Double(xCoord);
        Double imgWidth = 438.0;
        Double proportion = imgWidth / 5000;

        Double newXCoord = xCoordDouble * proportion; //may need to add margins depending on how it's placed on

        return newXCoord;
    }

    public Double yScale(int yCoord) {
        Double yCoordDouble = new Double(yCoord);
        Double imgWidth = 298.0;
        Double proportion = imgWidth / 3400;

        Double newYCoord = yCoordDouble * proportion; //may need to add margins depending on how it's placed on

        return newYCoord;
    }


    public Double xScaleDouble(double xCoord) {
        Double imgWidth = 438.0;
        Double proportion = imgWidth / 5000;

        Double newXCoord = xCoord * proportion; //may need to add margins depending on how it's placed on

        return newXCoord;
    }


    public Double yScaleDouble(double yCoord) {
        Double imgWidth = 298.0;
        Double proportion = imgWidth / 3400;

        Double newYCoord = yCoord * proportion; //may need to add margins depending on how it's placed on

        return newYCoord;
    }


    public void findPath() {
        if (startLocation.getSelectionModel().getSelectedItem() == null || destination.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        String start = startLocation.getSelectionModel().getSelectedItem().toString();
        String end = destination.getSelectionModel().getSelectedItem().toString();
        anchor.getChildren().clear();
        pathList.clear();
        pathList.add(start);
        if (intermediate.getSelectionModel().getSelectedItem() != null) {
            pathList.add(intermediate.getSelectionModel().getSelectedItem().toString());
        }
        pathList.add(end);
        SearchAlgorithm searchAlgorithm;
        double etaTotal;
        double minutes;
        double seconds;
        List<Node> pathNodes = new ArrayList<>();
        try {
            searchAlgorithm = new SearchAlgorithm();
            for (int i = 0; i < pathList.size() - 1; i++ ){
                pathNodes.addAll(searchAlgorithm.getPath(pathList.get(i), pathList.get(i+1)));
            }
            etaTotal = searchAlgorithm.getETA(pathNodes);
            minutes = Math.floor(etaTotal);
            seconds = Math.floor((etaTotal - minutes) * 60);
            etaLabel.setText((int) minutes + ":" + (int) seconds);



        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (URISyntaxException ue) {
            ue.printStackTrace();
        } catch (SQLException sq) {
            sq.printStackTrace();
        }


        Double prevX = xScaleDouble(pathNodes.get(0).getXcoord()); // TODO : fix this jank code
        Double prevY = yScaleDouble(pathNodes.get(0).getYcoord());


        for (Node node : pathNodes) {
            Circle circ = new Circle();
            Line line = new Line();
            Double scaledX = xScale(node.getXcoord());
            Double scaledY = yScale(node.getYcoord());

            circ.setCenterX(scaledX);
            circ.setCenterY(scaledY);
            circ.setRadius(2);
            circ.setFill(Color.RED);

            line.setStartX(scaledX);
            line.setStartY(scaledY);
            line.setEndX(prevX);
            line.setEndY(prevY);
            line.setStroke(Color.RED);


            anchor.getChildren().addAll(circ,line);
            prevX = scaledX;
            prevY = scaledY;
        }
        firstNodeSelect = 0;
        activePath = 1;
    }


    public void startEndlocation() {
        if (activePath == 0) anchor.getChildren().clear();
        String start = startLocation.getSelectionModel().getSelectedItem().toString();
        String end = destination.getSelectionModel().getSelectedItem().toString();
        SearchAlgorithm searchAlgorithm;
        List<Node> pathNodes = new ArrayList<>();
        try {
            searchAlgorithm = new SearchAlgorithm();
            pathNodes = searchAlgorithm.getPath(start, end);

        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (URISyntaxException ue) {
            ue.printStackTrace();
        } catch (SQLException sq) {
            sq.printStackTrace();
        }



        Double prevX = xScale(pathNodes.get(0).getXcoord()); // TODO : fix this jank code
        Double prevY = yScale(pathNodes.get(0).getYcoord());

        for (Node node : pathNodes) {
            Circle circ = new Circle();
            Line line = new Line();
            Double scaledX = xScale(node.getXcoord());
            Double scaledY = yScale(node.getYcoord());

            circ.setCenterX(scaledX);
            circ.setCenterY(scaledY);
            circ.setRadius(2);
            circ.setFill(Color.RED);

            line.setStartX(scaledX);
            line.setStartY(scaledY);
            line.setEndX(prevX);
            line.setEndY(prevY);
            line.setStroke(Color.RED);



            anchor.getChildren().addAll(circ,line);
            prevX = scaledX;
            prevY = scaledY;
        }
        firstNodeSelect = 0;
    }


    /**
     * Alternate declaration of findPath() that takes a specific start and end, used for clicking nodes on the map directly
     * @param start String, long name of start node
     * @param end String, long name of end node
     */
    public void findPath(String start, String end) {
        if(activePath == 0) anchor.getChildren().clear();
        SearchAlgorithm searchAlgorithm;
        List<Node> pathNodes = new ArrayList<>();
        double etaTotal;
        double minutes;
        double seconds;
        try {
            searchAlgorithm = new SearchAlgorithm();
            for (int i = 0; i < pathList.size() - 1; i++ ){
                pathNodes.addAll(searchAlgorithm.getPath(pathList.get(i), pathList.get(i+1)));
            }
            etaTotal = searchAlgorithm.getETA(pathNodes);
            minutes = Math.floor(etaTotal);
            seconds = Math.floor((etaTotal - minutes) * 60);
            etaLabel.setText((int) minutes + ":" + (int) seconds);



        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (URISyntaxException ue) {
            ue.printStackTrace();
        } catch (SQLException sq) {
            sq.printStackTrace();
        }


        Double prevX = xScaleDouble(pathNodes.get(0).getXcoord()); // TODO : fix this jank code
        Double prevY = yScaleDouble(pathNodes.get(0).getYcoord());


        for (Node node : pathNodes) {
            Circle circ = new Circle();
            Line line = new Line();
            Double scaledX = xScale(node.getXcoord());
            Double scaledY = yScale(node.getYcoord());

            circ.setCenterX(scaledX);
            circ.setCenterY(scaledY);
            circ.setRadius(2);
            circ.setFill(Color.RED);

            line.setStartX(scaledX);
            line.setStartY(scaledY);
            line.setEndX(prevX);
            line.setEndY(prevY);
            line.setStroke(Color.RED);


            anchor.getChildren().addAll(circ,line);
            prevX = scaledX;
            prevY = scaledY;
        }
        firstNodeSelect = 0;
        activePath = 1;
    }



    /**
     * Gets the closest node to the mouse cursor when clicked
     */
    public void getNearestNode(javafx.scene.input.MouseEvent event) {

        System.out.println("Clicked map");

        //double x = xScaleDouble(event.getX());
        //double y = yScaleDouble(event.getY());
        double x = event.getX();
        double y = event.getY();

        System.out.println(x + " " + y);
        double radius = 20;

        //Establish current closest recorded node and current least distance
        Map<String, String> currClosest = new HashMap<>();
        double currLeastDist = 100000;

        //Loop through nodes
        for (Map<String, String> n : validNodes) {
            if ((groundFloor.isVisible() && n.get("FLOOR").equals("G"))
                || (floor1.isVisible() && n.get("FLOOR").equals("1"))) {
                //Get the x and y of that node
                double currNodeX = xScaleDouble(Double.parseDouble(n.get("XCOORD")));
                double currNodeY = yScaleDouble(Double.parseDouble(n.get("YCOORD")));

                //Get the difference in x and y between input coords and current node coords
                double xOff = x - currNodeX;
                double yOff = y - currNodeY;

                //Give 'em the ol' pythagoras maneuver
                double dist = (Math.pow(xOff, 2) + Math.pow(yOff, 2));
                dist = Math.sqrt(dist);

                //If the distance is LESS than the given radius...
                if (dist < radius) {
                    //...AND the distance is less than the current min, update current closest node
                    if (dist < currLeastDist) {
                        currClosest = n;
                        currLeastDist = dist;
                    }
                }
            }
        }

        if (currClosest.isEmpty()) return;

        String currCloseName = currClosest.get("LONGNAME");
        System.out.println(currClosest.get("LONGNAME"));

        if (activePath == 0) {
            if (this.firstNodeSelect == 0) {
                firstNodeSelect = 1;
                this.firstNode = currCloseName;
            }
            else if (this.firstNodeSelect == 1) {
                if (this.firstNode != null && currCloseName != null) {
                    firstNodeSelect = 0;
                    pathList.add(this.firstNode);
                    pathList.add(currCloseName);
                    findPath(pathList.get(0), pathList.get(1));
                    activePath = 1;
                }
            }
        }
        else if (activePath == 1) {
            anchor.getChildren().clear();
            pathList.add(pathList.size() - 1, currCloseName);
            for (int i = 0; i < pathList.size() - 1; i++) {
                findPath(pathList.get(i), pathList.get(i + 1));
            }
        }
    }



    public void changeFloorNodes(){
        nodeGridAnchor.getChildren().clear();
        int count = 0;
        try {
            List<Map<String, String>> edges = db.getEdges();
            List<String> nodesList = new ArrayList<>();
            for (Map<String, String> edge : edges) {
                String startNode = edge.get("STARTNODE");
                String endNode = edge.get("ENDNODE");
                String bothNodes = startNode.concat(endNode);
                if (!nodesList.contains(bothNodes) || (!nodesList.contains(endNode.concat(startNode)))) { //??
                    try {
                        Map<String, String> snode = db.getNode(startNode);
                        Map<String, String> enode = db.getNode(endNode);
                        if (floor1.isVisible() &&
                                (( snode.get("FLOOR").equals("1")) && (snode.get("BUILDING").equals("Tower") || snode.get("BUILDING").equals("45 Francis")) ) &&
                                (( enode.get("FLOOR").equals("1")) && (enode.get("BUILDING").equals("Tower") || enode.get("BUILDING").equals("45 Francis")) )) {
                            drawNodes(snode,enode);
                            nodesList.add(startNode + endNode);
                            count++;
                        }else if (groundFloor.isVisible() &&
                                (snode.get("FLOOR").equals("G") && enode.get("FLOOR").equals("G")) ){
                            drawNodes(snode,enode);
                            nodesList.add(startNode + endNode);
                            count++;
                        }
                    } catch (SQLException sq) {
                        sq.printStackTrace();
                    }
                }
            }
        }catch (SQLException sq) {
            sq.printStackTrace();
        } System.out.println(count);
    }

    public void drawNodes(Map<String, String> snode, Map<String,String> enode) {
        Circle circ1 = new Circle();
        Circle circ2 = new Circle();

        Double startX = xScale((int) Double.parseDouble(snode.get("XCOORD")));
        Double startY = yScale((int) Double.parseDouble(snode.get("YCOORD")));
        Double endX = xScale((int) Double.parseDouble(enode.get("XCOORD")));
        Double endY = yScale((int) Double.parseDouble(enode.get("YCOORD")));

        circ1.setCenterX(startX);
        circ1.setCenterY(startY);
        circ2.setCenterX(endX);
        circ2.setCenterY(endY);
        circ1.setRadius(2);
        circ2.setRadius(2);
        circ1.setFill(Color.RED);
        circ2.setFill(Color.RED);

        Line line = new Line();
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);
        line.setStroke(Color.WHITE);
        nodeGridAnchor.getChildren().addAll(circ1, circ2, line);

    }

    public void clearNodes() {
        anchor.getChildren().clear();
        pathList.clear();
        startLocation.getSelectionModel().clearSelection();
        destination.getSelectionModel().clearSelection();
        intermediate.getSelectionModel().clearSelection();
        activePath = 0;
    }

    public void addStop(){
        if (!(startLocation.getSelectionModel().getSelectedItem() == null)&& (!(destination.getSelectionModel().getSelectedItem() == null))){
            intermediate.setVisible(true);
        }
    }

    public void menu(MouseEvent mouseEvent) {
        if(transition.getRate() == -1) menuDrawer.open();
        else menuDrawer.close();
        transition.setRate(transition.getRate() * -1);
        transition.play();
    }

    public void zoomIn(ActionEvent actionEvent) {
        SCALE_DELTA+=0.1;
        zoom();
    }

    public void zoomOut(ActionEvent actionEvent) {
        SCALE_DELTA-=0.1;
        zoom();
    }



    public void changeFloor1() throws FileNotFoundException {
        Image image = new Image(new FileInputStream("src/main/resources/edu/wpi/aquamarine_axolotls/img/lowerLevel1.png"));

        groundFloor.setImage(image);
    }

    public void changeGroundFloor() throws FileNotFoundException {
        Image image = new Image(new FileInputStream("src/main/resources/edu/wpi/aquamarine_axolotls/img/groundFloor.png"));

        groundFloor.setImage(image);
    }

}


