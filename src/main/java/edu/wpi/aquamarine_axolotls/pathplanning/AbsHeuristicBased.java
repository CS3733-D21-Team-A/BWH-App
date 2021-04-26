package edu.wpi.aquamarine_axolotls.pathplanning;

import java.util.*;

public abstract class AbsHeuristicBased extends AbsAlgorithmMethod{

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
}
