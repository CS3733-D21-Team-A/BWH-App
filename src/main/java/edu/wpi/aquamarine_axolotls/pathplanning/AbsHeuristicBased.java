package edu.wpi.aquamarine_axolotls.pathplanning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public abstract class AbsHeuristicBased extends AbsAlgorithmMethod{

    protected PriorityQueue<CostPair> frontier;

    public void updateSearchData() {
        //TODO: DON'T IMPLEMENT THIS
    }

    protected void clearFrontier(){
        //TODO: IMPLEMENT THIS
    }

    protected boolean isEmptyFrontier(){
        //TODO: IMPLEMENT THIS
        return false;
    }

    protected void addToFrontier(Node node, double cost){
        //TODO: IMPLEMENT THIS
    }

    protected Node getNextFrontier(){
        //TODO: IMPLEMENT THIS
        return new Node("NODEID");
    }

    protected double getCostTo(Node start, Node end){
        //TODO: IMPLEMENT THIS
        return 0.0;
    }

    protected List<Node> buildPath(Map<Node, Node> cameFrom, Node goal){
        //TODO: IMPLEMENT THIS
        return new ArrayList<Node>();
    }
}
