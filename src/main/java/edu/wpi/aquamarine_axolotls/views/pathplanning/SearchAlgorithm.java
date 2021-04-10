package edu.wpi.aquamarine_axolotls.views.pathplanning;

import org.jetbrains.annotations.NotNull;

import java.util.*;

import static edu.wpi.aquamarine_axolotls.views.pathplanning.PathBuilder.buildPath;
import static edu.wpi.aquamarine_axolotls.views.pathplanning.PathBuilder.checkPath;

public class SearchAlgorithm implements CostTo {

    private final List<Node> nodes = new ArrayList<>();
    private final List<Edge> edges = new ArrayList<>();

    public SearchAlgorithm(List<Map<String, String>> nodeMap, List<Map<String, String>> egeMap){
        for (int i = 0; i < nodeMap.size(); i++) {
            nodes.add(new Node(
                    nodeMap.get(i).get("nodeID"),
                    Integer.parseInt(nodeMap.get(i).get("xcoord")),
                    Integer.parseInt(nodeMap.get(i).get("ycoord")),
                    nodeMap.get(i).get("floor"),
                    nodeMap.get(i).get("building"),
                    nodeMap.get(i).get("nodeType"),
                    nodeMap.get(i).get("longName"),
                    nodeMap.get(i).get("shortName")
                    )
            );
        }
    }

    /*
     * Cost pair definition, the pair consist of a node and the cost
     * Used in priority queue
     * */
    protected static class CostPair implements Comparable<CostPair>{
        private final Node item; //The item
        private final double cost; //The current cost to get to that item from the starting node


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

        /**
         * Alternate version of equals that checks if two costPairs are equal
         * @param o
         * @return
         */
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

    //Define the priority queue used for the algorithm, called the "frontier"
    private final PriorityQueue<CostPair> frontier = new PriorityQueue<CostPair>();


//==========FRONTIER OPERATIONS=========//


    /**
     * Completely clears the frontier in the search algorithm
     */
    void clearFrontier() {
        frontier.clear();
    }

    /**
     * Check if the frontier is empty
     * @return Bool, whether it's empty
     */
    boolean isEmptyFrontier() {
        return frontier.isEmpty();
    }

    /**
     * Adds a node to the frontier
     * @param node The node to add
     * @param cost The current cost to get to that node from the starting node
     */
    void addToFrontier(Node node, double cost) {
        CostPair pair = new CostPair(node, cost);
        //System.out.println("pair " + pair);
        frontier.add(pair);
    }

    /**
     * Gets the next node from the front of the frontier priority queue
     * @return The next node
     */
    Node getNextFrontier() {
        if (frontier.isEmpty()) {
            return null;
        }
        //Use the poll() method to get the next node off the frontier
        return frontier.poll().getItem();
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


//==========GETTING NODES=========//


    /**
     * Gets a node by its ID from a list of nodes
     * @param id String, the id of the node to look for
     * @return The node once we find it, or null if that node doesn't exist
     */
    public Node getNode(String id){
        String nodeName;
        //Loop through the list of nodes
        for (int j = 0; j < this.nodes.size(); j++) {
            //Get the name of the current node we're looking at
            nodeName = this.nodes.get(j).getNodeID();

            //If this node is the one we're looking for, return it
            if (nodeName.equals(id)){
                return this.nodes.get(j);
            }
        }
        //If we go through all the nodes and don't find the one we were looking for, return null
        System.out.println("Couldn't find that node");
        return null;
    }


    /**
     * Gets all neighbors of this node
     * @param myNode The node we're looking from
     * @return
     */
    public List<Node> getConnected(Node myNode){
        //Get the id of the node
        String nodeName = myNode.getNodeID();
        //Initialize the list we're using to store all the connected nodes
        List<Node> connectedNode = new ArrayList<>();

        //Loop through all the edges
        for (int j = 0; j < this.edges.size(); j++) {
            //Get the name of the node at the start of the edge
            String startNodeName = this.edges.get(j).getStartNode();
            //System.out.println(startNodeName);

            //Get the name of the node at the end of the edge
            String endNodeName = this.edges.get(j).getEndNode();
            //System.out.println(endNodeName);

            //If this node is the node at the start of the edge, add the end as its neighbor
            if (nodeName.equals(startNodeName)){
                connectedNode.add(getNode(endNodeName));
            }
            //If this node is the node at the end of the edge, add the start as its neighbor
            if (nodeName.equals(endNodeName)){
                connectedNode.add(getNode(startNodeName));
            }
        }
        System.out.println("getConnected complete");
        //Return all connected nodes
        return connectedNode;
    }


    /**
     * Gets the cost to go DIRECTLY to another node
     * Note that this specifically measures the straight distance between the two, even  if they aren't neighbors
     * @param othernode The other node to go to
     * @return Double, the distance between them
     */
    public double getCostTo(Node firstnode, Node othernode){
        double xsqre = Math.pow(othernode.getXcoord() - firstnode.getXcoord(),2);
        double ysqre = Math.pow(othernode.getYcoord() - firstnode.getYcoord(),2);
        double dist = Math.sqrt(xsqre+ysqre);

        return dist;
    }


    /**
     * Determines the most efficient path from a start node to end node using the A* algorithm
     * @param start The node to start at
     * @param goal The node to go to
     * @return
     */
    public final List<Node> getPath(Node start, Node goal) {
        //Empty out the priority queue
        clearFrontier();

        //Inititalize the list of nodes we've been to and costs to nodes so far
        final Map<Node, Node> cameFrom = new HashMap<>(); //This hashmap is structured with a node as a key and the node we came from to get there as its value
        final Map<Node, Double> costSoFar = new HashMap<>();

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