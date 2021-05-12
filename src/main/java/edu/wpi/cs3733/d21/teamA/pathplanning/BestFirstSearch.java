package edu.wpi.cs3733.d21.teamA.pathplanning;

import java.util.List;
import java.util.Map;

public class BestFirstSearch extends AbsHeuristicBased{

    public BestFirstSearch() {
        super();
    }

    public BestFirstSearch(List<Node> nodeList, List<Edge> edgeList) {
        super(nodeList,edgeList);
    }

    /**
     * Gets the heuristic to use for the priority queue
     * @param next The next node to go to
     * @param goal The goal node to go to
     * @return The direct straight-line cost from next to goal
     */
    double getPriorityHeuristic(Node next, Node goal) {
        return getCostTo(next, goal);
    }

    /**
     * Calculates the cost to get from the start node to the next node based on the current node
     * @param current The current node in the path
     * @param next The node for which we want the cost
     * @param costSoFar A map of nodes and the costs to get to those nodes
     * @return 0.0, because BestFirst doesn't consider actual cost of travel
     */
    double calculateNewCost(Node current, Node next, Map<Node, Double> costSoFar){
        return 0.0;
    }
}