package edu.wpi.aquamarine_axolotls.views.pathplanning;

import org.jetbrains.annotations.NotNull;

import java.util.*;

import static edu.wpi.aquamarine_axolotls.views.pathplanning.PathBuilder.buildPath;
import static edu.wpi.aquamarine_axolotls.views.pathplanning.PathBuilder.checkPath;

public class SearchAlgorithm extends Node implements CostTo {

    public SearchAlgorithm(){

    }

    /*
     * Cost pair definition, the pair consist of a node and the cost
     * Used in priority queue
     * */

    protected static class CostPair implements Comparable<CostPair>{
        private final Node item;
        private final double cost;


        CostPair(Node item, double cost) {
            this.item = item;
            this.cost = cost;
        }

        public Node getItem() {
            return item;
        }

        public double getCost() {
            return cost;
        }

        public String toString() {
            return getItem() + "," + getCost();
        }

        public int compareTo(CostPair o) {
            return Double.compare(cost, o.cost);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CostPair costPair = (CostPair) o;
            return Objects.equals(item, costPair.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item);
        }
    }

        private final PriorityQueue<CostPair> frontier = new PriorityQueue<CostPair>();


         /*
            * Frontier operations
         * */
        void clearFrontier() {
            frontier.clear();
        }

        boolean isEmptyFrontier() {
            return frontier.isEmpty();
        }

        void addToFrontier(Node node, double cost) {
            CostPair pair = new CostPair(node, cost);
            //System.out.println("pair " + pair);
            frontier.add(pair);
        }

        Node getNextFrontier() {
            if (frontier.isEmpty()) {
                return null;
            }
            return frontier.poll().getItem();
        }

        double getPriorityHeuristic(Node next, Node goal) {
            return next.getCostTo(goal);
        }

        /*
        Find the path using Astar
         */
        public final List<Node> getPath(List<Edge> edges, List<Node> nodes, Node start, Node goal) {
            clearFrontier();
            final Map<Node, Node> cameFrom = new HashMap<>();
            final Map<Node, Double> costSoFar = new HashMap<>();

            addToFrontier(start, 0.0);
            cameFrom.put(start, null);
            costSoFar.put(start, 0.0);

            while (!isEmptyFrontier()) {
                Node current = getNextFrontier();

                if (current.equals(goal)) {
                    break;
                }

                List<Node> connectedNodes = getConnected(current, edges, nodes);
                for (Node next : connectedNodes) {
                    double newCost = costSoFar.get(current) + current.getCostTo(next);
                    if (!cameFrom.containsKey(next) || newCost < costSoFar.get(next)) {
                        costSoFar.put(next, newCost);
                        addToFrontier(next, newCost + getPriorityHeuristic(next, goal));
                        cameFrom.put(next, current);

                    }
                }
            }
            System.out.println("pathfound");
            List<Node> foundPath = buildPath(cameFrom, goal);
            if (checkPath(foundPath,start,goal)){
                return foundPath;
            }
            return null;
        }
}