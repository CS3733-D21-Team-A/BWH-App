package edu.wpi.aquamarine_axolotls.views.pathplanning;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.opencsv.bean.CsvBindByName;

public class Node extends Edge{

    @CsvBindByName(column = "nodeID")
    private String nodeID;

    @CsvBindByName(column = "xcoord")
    private int xcoord;

    @CsvBindByName(column = "ycoord")
    private int ycoord;

    @CsvBindByName(column = "floor")
    private String floor;

    @CsvBindByName(column = "building")
    private String building;

    @CsvBindByName(column = "nodeType")
    private String nodeType;

    @CsvBindByName(column = "longName")
    private String longName;

    @CsvBindByName(column = "shortName")
    private String shortName;


    public String getNodeID() {
        return nodeID;
    }

    public int getXcoord() {
        return xcoord;
    }

    public int getYcoord() {
        return ycoord;
    }

    public String getFloor() {
        return floor;
    }

    public String getBuilding() {
        return building;
    }

    public String getNodeType() {
        return nodeType;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }


    /**
     * Gets a node by its ID from a list of nodes
     * @param id String, the id of the node to look for
     * @param nodes List of nodes to look through
     * @return The node once we find it, or null if that node doesn't exist
     */
    public static Node getNode(String id, List<Node> nodes){
        String nodeName;
        //Loop through the list of nodes
        for (int j = 0; j < nodes.size(); j++) {
            //Get the name of the current node we're looking at
            nodeName = nodes.get(j).getNodeID();

            //If this node is the one we're looking for, return it
            if (nodeName.equals(id)){
                return nodes.get(j);
            }
        }
        //If we go through all the nodes and don't find the one we were looking for, return null
        System.out.println("Couldn't find that node");
        return null;
    }

    /**
     * Gets all neighbors of this node
     * @param myNode The node we're looking from
     * @param edges List of all edges in the graph
     * @param nodes List of all nodes in the graph
     * @return
     */
    public static List<Node> getConnected(Node myNode, List<Edge> edges, List<Node> nodes){
        //Get the id of the node
        String nodeName = myNode.getNodeID();
        //Initialize the list we're using to store all the connected nodes
        List<Node> connectedNode = new ArrayList<>();

        //Loop through all the edges
        for (int j = 0; j < edges.size(); j++) {
            //Get the name of the node at the start of the edge
            String startNodeName = edges.get(j).getStartNode();
            //System.out.println(startNodeName);

            //Get the name of the node at the end of the edge
            String endNodeName = edges.get(j).getEndNode();
            //System.out.println(endNodeName);

            //If this node is the node at the start of the edge, add the end as its neighbor
            if (nodeName.equals(startNodeName)){
                connectedNode.add(getNode(endNodeName, nodes));
            }
            //If this node is the node at the end of the edge, add the start as its neighbor
            if (nodeName.equals(endNodeName)){
                connectedNode.add(getNode(startNodeName, nodes));
            }
        }
        System.out.println("getConnected complete");
        //Return all connected nodes
        return connectedNode;
    }

    /**
     * Helper function that turns a node into a string that can be printed out
     * @return A string consisting of the node ID, x coord and y coord separated by spaces
     */
    public String toString(){
        return getNodeID() + " " + getXcoord() + " " + getYcoord();
    }

    /**
     * Gets the cost to go DIRECTLY to another node
     * Note that this specifically measures the straight distance between the two, even  if they aren't neighbors
     * @param othernode The other node to go to
     * @return Double, the distance between them
     */
    public double getCostTo(Node othernode){
        double xsqre = Math.pow(othernode.getXcoord() - getXcoord(),2);
        double ysqre = Math.pow(othernode.getYcoord() - getYcoord(),2);
        double dist = Math.sqrt(xsqre+ysqre);

        return dist;
    }

}

