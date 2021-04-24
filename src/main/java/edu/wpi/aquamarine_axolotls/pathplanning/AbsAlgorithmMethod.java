package edu.wpi.aquamarine_axolotls.pathplanning;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbsAlgorithmMethod implements ISearchAlgorithmStrategy{

    List<Node> nodes;
    List<Edge> edges;

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

    protected double getETASingleEdge(Node start, Node goal){
        double walkingSpeed = 220 * 3.75; //2 miles/h
        double distance = getCostTo(start,goal);
        double ETASingleEdge;

        ETASingleEdge = distance/walkingSpeed;
        return ETASingleEdge;
    }

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
    protected boolean checkPath(List<Node> path, Node start, Node goal){
        //Get the start and end nodes in the path
        Node pathStart = path.get(0);
        Node pathGoal = path.get(path.size()-1);
        System.out.println("pathStart: " + pathStart);
        System.out.println("pathGoal: " + pathGoal);

        //Determine whether the start and end nodes in the given path are the same as the start and end nodes we pass in
        boolean startNodeisEqual = pathStart.getNodeID().equals(start.getNodeID());
        boolean goalNodeisEqual = pathGoal.getNodeID().equals(goal.getNodeID());

        System.out.println("pathiscorrect: " + (startNodeisEqual && goalNodeisEqual));
        return startNodeisEqual && goalNodeisEqual;
    }

    public List<String> getTextDirections(List<Node> path){
        //TODO: IMPLEMENT THIS
        return new ArrayList<String>();
    }
}
