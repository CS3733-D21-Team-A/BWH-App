package edu.wpi.aquamarine_axolotls.pathplanning;

import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbsAlgorithmMethod implements ISearchAlgorithmStrategy{

    //TODO: MAKE JAVADOCS :(

    List<Node> nodes = new ArrayList<>();
    List<Edge> edges = new ArrayList<>();
    DatabaseController dbControl;

    protected AbsAlgorithmMethod() {
        try {
            dbControl = DatabaseController.getInstance();
            List<Map<String, String>> nodeMap = new ArrayList<>();
            List<Map<String, String>> edgeMap = new ArrayList<>();

            nodeMap = dbControl.getNodes();
            edgeMap = dbControl.getEdges();

            for (int i = 0; i < nodeMap.size(); i++) {
                Map<String, String> currNodeMap = nodeMap.get(i);
                this.nodes.add(new Node(
                                currNodeMap.get("NODEID"),
                                Integer.parseInt(currNodeMap.get("XCOORD")),
                                Integer.parseInt(currNodeMap.get("YCOORD")),
                                currNodeMap.get("FLOOR"),
                                currNodeMap.get("BUILDING"),
                                currNodeMap.get("NODETYPE"),
                                currNodeMap.get("LONGNAME"),
                                currNodeMap.get("SHORTNAME")
                        )
                );
            }

            for (int j = 0; j < edgeMap.size(); j++) {
                {
                    Map<String, String> currEdgeMap = edgeMap.get(j);
                    this.edges.add(new Edge(
                            edgeMap.get(j).get("EDGEID"),
                            edgeMap.get(j).get("STARTNODE"),
                            edgeMap.get(j).get("ENDNODE")
                    ));
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    protected AbsAlgorithmMethod(List<Node> nodeList, List<Edge> edgeList){
        for (int i = 0; i < nodeList.size(); i++) {
            this.nodes.add(nodeList.get(i));
        }
        for (int j = 0; j < edgeList.size(); j++) {
            this.edges.add(edgeList.get(j));
        }
    }

    /**
     * Gets a node by its ID from the search algorithm controller's list of nodes
     * @param id String, the id of the node to look for
     * @return The node once we find it, or null if that node doesn't exist
     */
    public Node getNode(String id){
        String nodeName;
        //Loop through the list of nodes
        for (int j = 0; j < this.nodes.size(); j++) {
            //Get the name of the current node we're looking at
            nodeName = this.nodes.get(j).getNodeID();

            //If this node is the one we're looking for, return it
            if (nodeName.equals(id)){
                return this.nodes.get(j);
            }
        }

        //If we go through everything without finding the node with that ID, we'll instead look for it
        //based on its long name instead
        String nodeLongName;
        //Loop through the list of nodes
        for (int j = 0; j < this.nodes.size(); j++) {
            //Get the name of the current node we're looking at
            nodeLongName = this.nodes.get(j).getLongName();

            //If this node is the one we're looking for, return it
            if (nodeLongName.equals(id)) {
                return this.nodes.get(j);
            }
        }

        //If we go through all the nodes and don't find the one we were looking for, return null
        System.out.println("Couldn't find that node");
        return null;
    }

    //TODO: REMOVE THIS
    public Node getNodeByLongName(String longName) {
        String nodeLongName;
        //Loop through the list of nodes
        for (int j = 0; j < this.nodes.size(); j++) {
            //Get the name of the current node we're looking at
            nodeLongName = this.nodes.get(j).getLongName();

            //If this node is the one we're looking for, return it
            if (nodeLongName.equals(longName)) {
                return this.nodes.get(j);
            }
        }
        //If we go through all the nodes and don't find the one we were looking for, return null
        System.out.println("Couldn't find that node");
        return null;
    }

    /**
     * Gets all neighbors of a given node
     * @param myNode The node we're looking from
     * @return A list of nodes that are connected to the given node
     */
    public List<Node> getConnected(Node myNode){
        //Get the id of the node
        String nodeName = myNode.getNodeID();
        //Initialize the list we're using to store all the connected nodes
        List<Node> connectedNode = new ArrayList<>();

        //Loop through all the edges
        for (int j = 0; j < this.edges.size(); j++) {
            //Get the name of the node at the start of the edge
            String startNodeName = this.edges.get(j).getStartNode();
            //System.out.println(startNodeName);

            //Get the name of the node at the end of the edge
            String endNodeName = this.edges.get(j).getEndNode();
            //System.out.println(endNodeName);

            //If this node is the node at the start of the edge, add the end as its neighbor
            if (nodeName.equals(startNodeName)){
                connectedNode.add(getNode(endNodeName));
            }
            //If this node is the node at the end of the edge, add the start as its neighbor
            if (nodeName.equals(endNodeName)){
                connectedNode.add(getNode(startNodeName));
            }
        }
        System.out.println("getConnected complete");
        //Return all connected nodes
        return connectedNode;
    }

    /**
     * Calculates the estimated time it will take for a patient to walk in a straight
     * line between two nodes
     * @param start The node at which the patient is starting
     * @param goal The node to which the patient is walking
     * @return The time, in minutes, that it will take for a patient to walk from the
     *          first node to the second
     */
    double getETASingleEdge(Node start, Node goal){
        double walkingSpeed = 220 * 3.75; //2.5 miles/h
        double distance = getCostTo(start,goal);
        double ETASingleEdge;

        ETASingleEdge = distance/walkingSpeed;
        return ETASingleEdge;
    }

    /**
     * Calculates the estimated time, in minutes, that it will take for a patient to
     * walk the entire length of the path
     * @param path The path that the patient is traversing
     * @return The time, in minutes, that it will take for a patient to walk along the
     *          entire path
     */
    public double getETA(List<Node> path){
        double ETASoFar = 0.0;
        for(int i = 0; i < path.size()-1;i++){
            ETASoFar += getETASingleEdge(path.get(i), path.get(i+1));
        }
        return ETASoFar;
    }

    /**
     * Gets the cost to go DIRECTLY to another node
     * Note that this specifically measures the straight distance between the two, even  if they aren't neighbors
     * @param othernode The other node to go to
     * @return Double, the distance between them
     */
    protected double getCostTo(Node firstnode, Node othernode){
        double xsqre = Math.pow(othernode.getXcoord() - firstnode.getXcoord(),2);
        double ysqre = Math.pow(othernode.getYcoord() - firstnode.getYcoord(),2);
        double dist = Math.sqrt(xsqre+ysqre);

        return dist;
    }

    /**
     * Builds a list of nodes that show the path from the start to the end
     * Meant to be run AFTER the full search algorithm is complete
     * @param cameFrom The list of nodes listed alongside which nodes came before them in the path
     * @param goal The goal node
     * @return A list of all the nodes used to make the path
     */
    protected List<Node> buildPath(final Map<Node, Node> cameFrom, final Node goal) {
        //Initialize the path
        LinkedList<Node> path = new LinkedList<>();
        //Current node is the goal
        Node next = goal;
        //Trace the path backwards from the goal
        //Each loop, we'll get the next node back from the current one on the path
        while (cameFrom.get(next) != null) {
            //Add that next node
            path.push(next);
            //Move back on the path another step
            next = cameFrom.get(next);
        }
        //Add the last node
        path.push(next);
        return path;
    }

    /**
     * Checks if a path is valid by checking if the start and end nodes are the same as the path that was generated by the algorithm
     * @param path The path we're checking, generated using the buildPath() method
     * @param start The start node
     * @param goal The end node
     * @return Boolean, whether the path accurately connects the start and the end
     */
    protected boolean checkPath(List<Node> path, Node start, Node goal) {
        //Get the start and end nodes in the path
        Node pathStart = path.get(0);
        Node pathGoal = path.get(path.size() - 1);
        System.out.println("pathStart: " + pathStart);
        System.out.println("pathGoal: " + pathGoal);

        //Determine whether the start and end nodes in the given path are the same as the start and end nodes we pass in
        boolean startNodeisEqual = pathStart.getNodeID().equals(start.getNodeID());
        boolean goalNodeisEqual = pathGoal.getNodeID().equals(goal.getNodeID());

        System.out.println("pathiscorrect: " + (startNodeisEqual && goalNodeisEqual));
        return startNodeisEqual && goalNodeisEqual;
    }

    /**
     * Creates a list of text directions instructing the user how to navigate a path
     * @param path The path for which text directions are to be generated
     * @return The list of steps that a user must take to navigate from the start
     *          of the path to the end
     */
    public List<List<String>> getTextDirections(List<Node> path){

        List<List<String>> returnList = new ArrayList<List<String>>();
        ArrayList<String> instructions = new ArrayList<String>();
        ArrayList<String> nodeIDS = new ArrayList<String>();

        if(path.size() == 0){
            returnList.add(instructions);
            returnList.add(nodeIDS);
            return returnList;
        }
        if(path.size() == 1) {
            instructions.add("1. You have arrived at your destination");
            nodeIDS.add(path.get(0).getNodeID()); // TODO : could error because path could be mt??
            returnList.add(instructions);
            returnList.add(nodeIDS);
            return returnList;
        }

        int stepNum = 1;
//
//
//        List<Node> modifiedPath = new ArrayList<Node>();
//        for(int i = 0; i < path.size(); i++) {
//            if(!nodeIsUnimportant(path, path.get(i))){
//                modifiedPath.add(path.get(i));
//            }
//        }
//        path = modifiedPath;


        if(path.get(0).getNodeType().equals("ELEV") && path.get(1).getNodeType().equals("ELEV")){
            instructions.add(stepNum + ". Take the elevator to floor " + path.get(1).getFloor() + ".");
            nodeIDS.add(path.get(0).getNodeID());
            stepNum++;
        } else if(path.get(0).getNodeType().equals("STAI") && path.get(1).getNodeType().equals("STAI")){
            instructions.add(stepNum + ". Take the stairs to floor " + path.get(1).getFloor() + ".");
            nodeIDS.add(path.get(0).getNodeID());
            stepNum++;
        } else {
            double firstEdgeDistancePixels = getCostTo(path.get(0), path.get(1));
            double firstEdgeDistanceFeet = firstEdgeDistancePixels / 2.35;
            instructions.add(stepNum + ". Walk " + (int) firstEdgeDistanceFeet + " feet towards " + path.get(1).getLongName() + ".");
            nodeIDS.add(path.get(0).getNodeID() + "_" + path.get(1).getNodeID());
            stepNum++;
        }


        for (int i = 1; i < path.size() - 1; i++){
            if(path.get(i).getNodeType().equals("ELEV") && path.get(i+1).getNodeType().equals("ELEV")){
                instructions.add(stepNum + ". Take the elevator to floor " + path.get(i+1).getFloor() + ".");
                nodeIDS.add(path.get(i).getNodeID());
                stepNum++;
            } else if(path.get(i).getNodeType().equals("STAI") && path.get(i+1).getNodeType().equals("STAI")){
                instructions.add(stepNum + ". Take the stairs to floor " + path.get(i+1).getFloor() + ".");
                nodeIDS.add(path.get(i).getNodeID());
                stepNum++;
            } else {

                if(!path.get(i).getFloor().equals(path.get(i-1).getFloor())) {
                    instructions.add(stepNum + ". You are now on floor " + path.get(i).getFloor() + ".");
                    nodeIDS.add(path.get(i).getNodeID());
                    stepNum++;
                } else{
                    double angleIn = absAngleEdge(path.get(i-1), path.get(i));
                    double angleOut = absAngleEdge(path.get(i), path.get(i+1));
                    double turnAngle = angleOut - angleIn;

                    if(turnAngle <= -180.0) turnAngle += 360;
                    if(turnAngle > 180.0) turnAngle -= 360;

                    if(turnAngle > 10 && turnAngle < 60){
                        instructions.add(stepNum + ". Make a slight right turn.");
                        stepNum++;
                    } else if (turnAngle >= 60 && turnAngle < 120){
                        instructions.add(stepNum + ". Make a right turn.");
                        stepNum++;
                    } else if (turnAngle >= 120 && turnAngle < 178){
                        instructions.add(stepNum + ". Make an extreme right turn.");
                        stepNum++;
                    } else if (turnAngle < -10 && turnAngle > -60){
                        instructions.add(stepNum + ". Make a slight left turn.");
                        stepNum++;
                    } else if (turnAngle <= -60 && turnAngle > -120){
                        instructions.add(stepNum + ". Make a left turn.");
                        stepNum++;
                    } else if (turnAngle <= -120 && turnAngle > -178){
                        instructions.add(stepNum + ". Make an extreme left turn.");
                        stepNum++;
                    } else if (turnAngle <= -178.0 || turnAngle >= 178.0){
                        instructions.add(stepNum + ". Turn around.");
                        stepNum++;
                    }
                    nodeIDS.add(path.get(i).getNodeID());
                }

                double edgeDistancePixels = getCostTo(path.get(i), path.get(i+1));
                double edgeDistanceFeet = edgeDistancePixels / 2.35;

                instructions.add(stepNum + ". Walk " + (int) edgeDistanceFeet + " feet towards " + path.get(i+1).getLongName() + ".");
                stepNum++;
                nodeIDS.add(path.get(i).getNodeID() + "_" + path.get(i+1).getNodeID());

            }

        }
        instructions.add(stepNum + ". You have arrived at your destination.");
        nodeIDS.add(path.get(path.size()-1).getNodeID());
        returnList.add(instructions);
        returnList.add(nodeIDS);
        return returnList;
    }

    /**
     * Calculates the absolute angle, in degrees, of a line connecting two nodes with respect
     * to the x-axis
     * @param start The starting node
     * @param end The ending node
     * @return The absolute angle, in degrees of a line connecting the two nodes.
     *          The angle will be in the range of -180 to 180.
     */
    protected double absAngleEdge(Map<String, String> start, Map<String, String> end){
        double deltaX = Integer.parseInt(end.get("XCOORD")) - Integer.parseInt(start.get("XCOORD"));
        double deltaY = Integer.parseInt(end.get("YCOORD")) - Integer.parseInt(start.get("YCOORD"));
        double radians = Math.atan2(deltaY, deltaX);
        double degrees = radians * 180.0 / Math.PI;
        return degrees;
    }

    /**
     * Calculates the absolute angle, in degrees, of a line connecting two nodes with respect
     * to the x-axis
     * @param start The starting node
     * @param end The ending node
     * @return The absolute angle, in degrees of a line connecting the two nodes.
     *          The angle will be in the range of -180 to 180.
     */
    protected double absAngleEdge(Node start, Node end){
        double deltaX = end.getXcoord() - start.getXcoord();
        double deltaY = end.getYcoord() - start.getYcoord();
        double radians = Math.atan2(deltaY, deltaX);
        double degrees = radians * 180.0 / Math.PI;
        return degrees;
    }

    /**
     * Determines whether a given node is unimportant to a path's text directions, based on the
     * node's type and whether a user must turn at that node
     * @param path The path for which a node's importance is in question
     * @param node The node in question
     * @return true if the given node is unimportant to the path's text directions
     */
    public boolean nodeIsUnimportant(List<Map<String, String>> path, Map<String, String> node){

        int nodeIndex = path.indexOf(node);

        if (nodeIndex == 0 || nodeIndex == path.size() - 1) return false;

        if(path.get(nodeIndex).equals(path.get(nodeIndex+1)) &&
                (path.get(nodeIndex).get("NODETYPE").equals("ELEV")
                        || (path.get(nodeIndex).get("NODETYPE").equals("STAI")))) return true;

        if((node.get("NODETYPE").equals("STAI") &&
                (path.get(nodeIndex+1).get("NODETYPE").equals("STAI") ||
                        path.get(nodeIndex-1).get("NODETYPE").equals("STAI"))) ||
           (node.get("NODETYPE").equals("ELEV") &&
                   (path.get(nodeIndex+1).get("NODETYPE").equals("ELEV") ||
                           path.get(nodeIndex-1).get("NODETYPE").equals("ELEV")))) return false;

        double angleIn = absAngleEdge(path.get(nodeIndex-1), path.get(nodeIndex));
        double angleOut = absAngleEdge(path.get(nodeIndex), path.get(nodeIndex+1));
        double turnAngle = angleOut - angleIn;

        if (turnAngle <= -180.0) turnAngle += 360.0;
        if (turnAngle > 180.0) turnAngle -= 360.0;

        return (path.get(nodeIndex).get("FLOOR").equals(path.get(nodeIndex+1).get("FLOOR"))) &&
                (Math.abs(turnAngle) < 10);
    }

    public void handleCovidStatus() throws SQLException {
        String covidLikely = dbControl.getUserByUsername(Aapp.username != null ? Aapp.username : "guest").get("COVIDLIKELY");

        Node francis = new Node(dbControl.getNode("FEXIT00201"));
        Node emergency = new Node(dbControl.getNode("FEXIT00301"));

        if (covidLikely != null) {
            nodes.remove(covidLikely.equals("true") ? francis : emergency);

            Node toAdd = covidLikely.equals("true") ? emergency : francis;
            if(!nodes.contains(toAdd)) nodes.add(toAdd);

            List<Edge> francisEdges = dbControl.getEdgesConnectedToNode("FEXIT00201").stream().map((edge) -> new Edge(edge)).collect(Collectors.toList());
            List<Edge> emergencyEdges = dbControl.getEdgesConnectedToNode("FEXIT00301").stream().map((edge) -> new Edge(edge)).collect(Collectors.toList());

            edges.removeAll(covidLikely.equals("true") ? francisEdges : emergencyEdges);

            for (Edge edge : covidLikely.equals("true") ? emergencyEdges : francisEdges) {
                if (!edges.contains(edge)) edges.add(edge);
            }
        }


    }

    abstract List<Map<String, String>> getPathImpl(String startLongName, String endLongName);

    public List<Map<String, String>> getPath(String startLongName, String endLongName) {
        System.out.println("GETTING PATH");
        try {
            handleCovidStatus();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getPathImpl(startLongName,endLongName);
    }

}
