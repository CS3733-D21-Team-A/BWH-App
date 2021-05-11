package edu.wpi.aquamarine_axolotls.pathplanning;

import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

public abstract class AbsHeuristicBased extends AbsAlgorithmMethod{

    public AbsHeuristicBased(){
        super();
    }

    public AbsHeuristicBased(List<Node> nodeList, List<Edge> edgeList) {
        super(nodeList, edgeList);
    }

    abstract double getPriorityHeuristic(Node start, Node end);
    abstract double calculateNewCost(Node current, Node next, Map<Node, Double> costSoFar);

    protected PriorityQueue<CostPair> frontier = new PriorityQueue<>();

    /**
     * Completely clears the frontier in the search algorithm
     */
    protected void clearFrontier() {
        frontier.clear();
    }

    /**
     * Check if the frontier is empty
     * @return Bool, whether it's empty
     */
    protected boolean isEmptyFrontier() {
        return frontier.isEmpty();
    }

    /**
     * Adds a node to the frontier
     * @param node The node to add
     * @param cost The current cost to get to that node from the starting node
     */
    protected void addToFrontier(Node node, double cost) {
        CostPair pair = new CostPair(node, cost);
        //System.out.println("pair " + pair);
        frontier.add(pair);
    }

    /**
     * Gets the next node from the front of the frontier priority queue
     * @return The next node
     */
    protected Node getNextFrontier() {
        if (frontier.isEmpty()) {
            return null;
        }
        //Use the poll() method to get the next node off the frontier
        return frontier.poll().getItem();
    }

    List<Node> getPathImpl(String startID, String goalID) {
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
                double newCost = calculateNewCost(current, next, costSoFar);
//                double newCost = costSoFar.get(current) + getCostTo(current, next);
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
        //Build the actual path that we can return
        List<Node> foundPath = buildPath(cameFrom, goal);
        //Check if the created path is valid and return if it is
        if (checkPath(foundPath,start,goal)){
            return foundPath;
        }
        return null;
    }
}
