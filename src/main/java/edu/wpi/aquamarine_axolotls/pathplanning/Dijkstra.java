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

    public Dijkstra(){
        super();
    }

    public Dijkstra(List<Node> nodeList, List<Edge> edgeList) {
        super(nodeList, edgeList);
    }

    /**
     * Gets the heuristic to use for the priority queue
     * @param next The next node to go to
     * @param goal The goal node to go to
     * @return 0.0, because Dijkstra doesn't consider the cost of future travel
     */
    double getPriorityHeuristic(Node next, Node goal){
        return 0.0;
    }

    /**
     * Calculates the cost to get from the start node to the next node based on the current node
     * @param current The current node in the path
     * @param next The node for which we want the cost
     * @param costSoFar A map of nodes and the costs to get to those nodes
     * @return The cost to get from the start node to the next node via the current node
     */
    double calculateNewCost(Node current, Node next, Map<Node, Double> costSoFar){
        return costSoFar.get(current) + getCostTo(current, next);
    }
}
