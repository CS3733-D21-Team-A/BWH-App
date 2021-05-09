package edu.wpi.aquamarine_axolotls.pathplanning;

import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepthFirstSearch extends AbsAlgorithmMethod {

    public DepthFirstSearch(){
        super();
    }

    DepthFirstSearch(List<Node> nodeList, List<Edge> edgeList) {
        super(nodeList, edgeList);
    }

    public List<Map<String, String>> getPathImpl(String startLongName, String endLongName){

        List<Node> visited = new ArrayList<>();

        List<Node> result = new ArrayList<>();

        Node startNode = getNodeByLongName(startLongName);
        Node endNode = getNodeByLongName(endLongName);

        String startID = startNode.getNodeID();
        String endID = endNode.getNodeID();

        //If the start == the end, add that to the result; the result will just be the start ID
        if (startID.equals(endID)) result.add(getNode(startID));
            //Otherwise, actually do the search
        else {
            //Our result equals the result of running the recursive search with the start and end IDs
            result = depthFirstRecursive(startID, endID, visited);
            //If the result ends up as null, that means there was no path, so that will be the final output
            if (result == null) {
                System.out.println("Couldn't find a path between those nodes");
                return null;
            }
        }

        //IF we got a valid path, we'll initialize the final list of results from the IDs of the nodes on the path
        List<String> resultStrings = new ArrayList<>();
        for (int j = 0; j < result.size(); j++) {
            resultStrings.add(result.get(j).getNodeID());
        }

        //Print out path
        System.out.println("Successfully found a path from node " + startID + " to node " + endID + "!");
        System.out.println("Final path: ");
        for (int i = 0; i < resultStrings.size(); i++) {
            System.out.println(resultStrings.get(i));
        }

        List<Map<String, String>> nodeMapList = new ArrayList<Map<String, String>>();

        for (Node node: result){
            Map<String, String> nodeMap = new HashMap<String, String>();
            nodeMap.put("NODEID", node.getNodeID());
            nodeMap.put("XCOORD", String.valueOf(node.getXcoord()));
            nodeMap.put("YCOORD", String.valueOf(node.getYcoord()));
            nodeMap.put("NODETYPE", node.getNodeType());
            nodeMap.put("BUILDING", node.getBuilding());
            nodeMap.put("LONGNAME", node.getLongName());
            nodeMap.put("SHORTNAME", node.getShortName());
            nodeMap.put("FLOOR", node.getFloor());
            nodeMapList.add(nodeMap);
        }

        return nodeMapList;

    }

    /**
     * Recursive component of depth first search
     * @param startID ID of node to start from
     * @param endID ID of node to go to
     * @return Path from current node
     */
    public List<Node> depthFirstRecursive(String startID, String endID, List<Node> visited) {
        //If either start or end are null, indicate that they're invalid
        if (getNode(startID) == null || getNode(endID) == null) {
            System.out.println("Invalid start or end ID");
            return null;
        }

        //Initialize path
        List<Node> path = new ArrayList<>();

        //Current node starts as our current start ID
        Node currNode = getNode(startID);

        //Add the current node to the visited list
        visited.add(currNode);

        //Get the neighbors of the current node
        List<Node> currNodeNeighbors;
        currNodeNeighbors = getConnected(currNode);

        //Add the current node to the path
        path.add(currNode);

        //If the start node equals the end node, return the path since we've hit the end
        if (startID.equals(endID)) {
            return path;
        }
        //Otherwise...
        else {
            //Look through all the neighbors
            for (int j = 0; j < currNodeNeighbors.size(); j++) {
                //If the visited list does NOT contain the current neighbor
                if (!visited.contains(currNodeNeighbors.get(j))) {
                    //Initialize the path that we'll get from the neighbor node search
                    List<Node> nextPath;
                    //Run the recursive search starting from the current neighbor
                    nextPath = depthFirstRecursive(currNodeNeighbors.get(j).getNodeID(), endID, visited);
                    //If the path we got from that search is NOT null, add the new path to the current one
                    if (!(nextPath == null)) {
                        for (Node n: nextPath) {
                            path.add(n);
                        }
                        return path;
                    }
                }
            }
            //If we go through all the neighbors without finding a valid path from one, return null since we've hit a dead end
            return null;
        }
    }
}
