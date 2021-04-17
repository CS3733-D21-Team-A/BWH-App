package edu.wpi.aquamarine_axolotls.pathplanning;

import edu.wpi.aquamarine_axolotls.db.DatabaseController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

public class SearchAlgorithm{

    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();

    public SearchAlgorithm() throws SQLException, IOException, URISyntaxException {
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

    public SearchAlgorithm(List<Node> nodeList, List<Edge> edgeList) {
        for (int i = 0; i < nodeList.size(); i++) {
            this.nodes.add(nodeList.get(i));
        }
        for (int j = 0; j < edgeList.size(); j++) {
            this.edges.add(edgeList.get(j));
        }
    }

    /**
     * Clears the SearchAlgorithm's current node and edge data and reloads it from the database
     * Use this if you have a persistent instance of SearchAlgorithm and want to update it based on database changes
     */
    public void updateSearchData() throws SQLException, IOException, URISyntaxException {
        DatabaseController dbControl = new DatabaseController();

        List<Map<String, String>> nodeMap = new ArrayList<>();
        List<Map<String, String>> edgeMap = new ArrayList<>();

        try {
            nodeMap = dbControl.getNodes();
            edgeMap = dbControl.getEdges();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        this.nodes.clear();
        this.edges.clear();

        for (int i = 0; i < nodeMap.size(); i++) {
            Map<String, String> currNodeMap = nodeMap.get(i);
            this.nodes.add(new Node(
                            currNodeMap.get("nodeID"),
                            Integer.parseInt(currNodeMap.get("xcoord")),
                            Integer.parseInt(currNodeMap.get("ycoord")),
                            currNodeMap.get("floor"),
                            currNodeMap.get("building"),
                            currNodeMap.get("nodeType"),
                            currNodeMap.get("longName"),
                            currNodeMap.get("shortName")
                    )
            );
        }

        for (int j = 0; j < edgeMap.size(); j++) {
            Map<String, String> currEdgeMap = edgeMap.get(j);
            this.edges.add(new Edge(
                    edgeMap.get(j).get("edgeID"),
                    edgeMap.get(j).get("startNode"),
                    edgeMap.get(j).get("endNode")
            ));
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
     * Gets a node by its ID from the search algorithm controller's list of nodes
     * @param id String, the id of the node to look for
     * @return The node once we find it, or null if that node doesn't exist
     */
    public Node getNode(String id) {
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

    public Node getNodeByLongName(String longName) {
        String nodeLongName;
        //Loop through the list of nodes
        for (int j = 0; j < this.nodes.size(); j++) {
            //Get the name of the current node we're looking at
            nodeLongName = this.nodes.get(j).getLongName();

            //If this node is the one we're looking for, return it
            if (nodeLongName.equals(longName)) {
                return this.nodes.get(j);
            }
        }
        //If we go through all the nodes and don't find the one we were looking for, return null
        System.out.println("Couldn't find that node");
        return null;
    }


    /**
     * Gets all neighbors of a given node
     * @param myNode The node we're looking from
     * @return A list of nodes that are connected to the given node
     */
    private List<Node> getConnected(Node myNode){
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
    private double getCostTo(Node firstnode, Node othernode){
        double xsqre = Math.pow(othernode.getXcoord() - firstnode.getXcoord(),2);
        double ysqre = Math.pow(othernode.getYcoord() - firstnode.getYcoord(),2);
        double dist = Math.sqrt(xsqre+ysqre);

        return dist;
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


    /**
     * Builds a list of nodes that show the path from the start to the end
     * Meant to be run AFTER the full search algorithm is complete
     * @param cameFrom The list of nodes listed alongside which nodes came before them in the path
     * @param goal The goal node
     * @return A list of all the nodes used to make the path
     */
    private List<Node> buildPath(final Map<Node, Node> cameFrom, final Node goal) {
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


    /**
     * Checks if a path is valid by checking if the start and end nodes are the same as the path that was generated by the algorithm
     * @param path The path we're checking, generated using the buildPath() method
     * @param start The start node
     * @param goal The end node
     * @return Boolean, whether the path accurately connects the start and the end
     */
    private boolean checkPath(List<Node> path, Node start, Node goal){
        //Get the start and end nodes in the path
        Node pathStart = path.get(0);
        Node pathGoal = path.get(path.size()-1);
        System.out.println("pathStart: " + pathStart);
        System.out.println("pathGoal: " + pathGoal);

        //Determine whether the start and end nodes in the given path are the same as the start and end nodes we pass in
        boolean startNodeisEqual = pathStart.getNodeID().equals(start.getNodeID());
        boolean goalNodeisEqual = pathGoal.getNodeID().equals(goal.getNodeID());

        System.out.println("pathiscorrect: " + (startNodeisEqual && goalNodeisEqual));
        return startNodeisEqual && goalNodeisEqual;
    }

    private double getETASingleEdge(Node start, Node goal){
        double walkingSpeed = 176; //2 miles/h
        double distance = getCostTo(start,goal);
        double ETASingleEdge;

        ETASingleEdge = distance/walkingSpeed;
        return ETASingleEdge;
    }

    private double getETA(List<Node> path){
        double ETASoFar = 0.0;
        for(int i = 0; i< path.size()-1;i++){
            ETASoFar += getETASingleEdge(path.get(i), path.get(i+1));
        }
        return ETASoFar;
    }
}