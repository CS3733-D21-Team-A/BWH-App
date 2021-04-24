package edu.wpi.aquamarine_axolotls.pathplanning;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsAlgorithmMethod implements ISearchAlgorithmStrategy{

    List<Node> nodes;
    List<Edge> edges;

    public Node getNode(String nodeID){
        //TODO: IMPLEMENT THIS
        return new Node("NODEID");
    }

    public List<Node> getConnected(Node node){
        //TODO: IMPLEMENT THIS
        return new ArrayList<Node>();
    }

    protected double getETASingleEdge(Node start, Node end){
        //TODO: IMPLEMENT THIS
        return 0.0;
    }

    public double getETA(List<Node> path){
        //TODO: IMPLEMENT THIS
        return 0.0;
    }

    protected boolean checkPath(List<Node> path, Node start, Node end){
        //TODO: IMPLEMENT THIS
        return false;
    }

    public List<String> getTextDirections(List<Node> path){
        //TODO: IMPLEMENT THIS
        return new ArrayList<String>();
    }
}
