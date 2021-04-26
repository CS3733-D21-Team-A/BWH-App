package edu.wpi.aquamarine_axolotls.pathplanning;

import edu.wpi.aquamarine_axolotls.db.DatabaseController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import java.util.*;

public class BreadthFirstSearch extends AbsAlgorithmMethod {

    public BreadthFirstSearch() {
        try {
            DatabaseController dbControl = new DatabaseController();

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
                Map<String, String> currEdgeMap = edgeMap.get(j);
                this.edges.add(new Edge(
                        edgeMap.get(j).get("EDGEID"),
                        edgeMap.get(j).get("STARTNODE"),
                        edgeMap.get(j).get("ENDNODE")
                ));
            }
        } catch (SQLException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public BreadthFirstSearch(List<Node> nodeList, List<Edge> edgeList) {
        for (int i = 0; i < nodeList.size(); i++) {
            this.nodes.add(nodeList.get(i));
        }
        for (int j = 0; j < edgeList.size(); j++) {
            this.edges.add(edgeList.get(j));
        }
    }

    /**
     * Determines the most efficient path from a start node to end node using the A* algorithm
     * @param startID The ID of the node to start at
     * @param endID The ID of the node to go to
     * @return
     */
    public List<Node> getPath(String startID, String endID) {
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

        return path;
    }
}
