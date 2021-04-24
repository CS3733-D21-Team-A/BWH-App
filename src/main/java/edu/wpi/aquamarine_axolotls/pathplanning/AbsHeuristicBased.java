package edu.wpi.aquamarine_axolotls.pathplanning;

import java.util.*;

public abstract class AbsHeuristicBased extends AbsAlgorithmMethod{

    protected PriorityQueue<CostPair> frontier;

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
}
