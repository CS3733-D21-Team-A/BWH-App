package edu.wpi.aquamarine_axolotls.pathplanning;

import edu.wpi.aquamarine_axolotls.db.DatabaseController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dijkstra extends AbsHeuristicBased{

    public Dijkstra() throws SQLException, IOException, URISyntaxException {
        DatabaseController dbControl = new DatabaseController();

        List<Map<String, String>> nodeMap = new ArrayList<>();
        List<Map<String, String>> edgeMap = new ArrayList<>();

        try {
            nodeMap = dbControl.getNodes();
            edgeMap = dbControl.getEdges();
        } catch (SQLException e) {
            e.printStackTrace();
        }



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
    }


    private double getPriorityHeuristic(Node start, Node end){
        // TODO: HAVE I ALREADY IMPLEMENTED THIS?
        return 0.0;
    }

    /**
     * Determines the most efficient path from a start node to end node using the A* algorithm
     * @param startID The ID of the node to start at
     * @param goalID The ID of the node to go to
     * @return
     */
    public List<Node> getPath(String startID, String goalID) {
        //Empty out the priority queue
        clearFrontier();

        Node start = getNode(startID);
        Node goal = getNode(goalID);

        if (start == null) {
            start = getNodeByLongName(startID);
        }
        if (goal == null) {
            goal = getNodeByLongName(goalID);
        }

        //Inititalize the list of nodes we've been to and costs to nodes so far
        final Map<Node, Node> cameFrom = new HashMap<>(); //This hashmap is structured with a node as a key and the node we came from to get there as its value
        final Map<Node, Double> costSoFar = new HashMap<>(); //This hashmap stores the cost to get to each node based on the current path to get to it

        //Add the starting node to the frontier with a cost of 0.0 since we're already there
        addToFrontier(start, 0.0);
        //The start node gets put in cameFrom, associated with null since we didn't come from anywhere to get there
        cameFrom.put(start, null);
        //We didn't go anywhere to get to the start node
        costSoFar.put(start, 0.0);

        //As long as the frontier isn't empty...
        while (!isEmptyFrontier()) {
            //Get the next node in the queue
            Node current = getNextFrontier();

            //If we're at the goal, just quit since we're done
            if (current.equals(goal)) {
                break;
            }

            //Otherwise...

            //Get the nodes connected to the current node
            List<Node> connectedNodes = getConnected(current);

            //Run through all the connected nodes
            for (Node next : connectedNodes) {
                //Get the newCost, which consists of the cost so far + the cost to the neighbor we're looking at
                double newCost = costSoFar.get(current) + getCostTo(current, next);
                //If the next node is NOT in the current path, or the new cost is less than the total cost to the neighbor node...
                if (!cameFrom.containsKey(next) || newCost < costSoFar.get(next)) {
                    //This means that we've found a cheaper path
                    //Put the new cost with the current neighbor
                    costSoFar.put(next, newCost);
                    //Add the neighbor node to the queue, updating its cost with the heuristic
                    addToFrontier(next, newCost + getPriorityHeuristic(next, goal));
                    //Indicate that the path goes from the current node to the next one
                    cameFrom.put(next, current);
                }
            }
        }
        System.out.println("pathfound");
        //Build the actual path that we can return
        List<Node> foundPath = buildPath(cameFrom, goal);
        //Check if the created path is valid and return if it is
        if (checkPath(foundPath,start,goal)){
            return foundPath;
        }
        return null;
    }
}
