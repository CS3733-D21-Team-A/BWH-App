package edu.wpi.aquamarine_axolotls.pathplanning;

import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import java.util.*;

public class BreadthFirstSearch extends AbsAlgorithmMethod {

    public BreadthFirstSearch(){
        super();
    }
    BreadthFirstSearch(List<Node> nodeList, List<Edge> edgeList) {
        super(nodeList, edgeList);
    }

    /**
     * Determines the most efficient path from a start node to end node using the BFS algorithm
     * @param startID The ID of the node to start at
     * @param endID The ID of the node to go to
     * @return
     */
    public List<Map<String, String>> getPathImpl(String startID, String endID) {
        Node start = getNode(startID);
        Node end = getNode(endID);
        LinkedList<Node> queue = new LinkedList<>(); //queue for tracking the next nodes to visit
        List<Node> visited = new LinkedList<>(); // list for recording the visited nodes, so the search will not come back
        Map<Node, Node> cameFrom = new HashMap<>(); // map to track the progress of the search, for building the path at the end
        List<Node> path = null;

        boolean foundGoal = false;
        visited.add(start);

        queue.add(start);
        Node current = start;
        cameFrom.put(start, null);

        while (!queue.isEmpty()) {
            current = queue.poll();
            List<Node> connectedNodes = getConnected(current);

            if (current.equals(end)) {
                foundGoal = true;
                break;
            }

            for (Node next : connectedNodes) {
                if (!visited.contains(next)) {
                    cameFrom.put(next, current);
                    visited.add(next);
                    queue.add(next);
                }
            }
        }

        if (foundGoal) {
            path = buildPath(cameFrom, end);
        }

        List<Map<String, String>> nodeMapList = new ArrayList<Map<String, String>>();

        for (Node node: path){
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
}
