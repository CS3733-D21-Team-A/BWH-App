package edu.wpi.aquamarine_axolotls.pathplanning;

public class Edge {

    private String edgeID;

    private String startNode;

    private String endNode;

    public Edge(String ID, String start, String end) {
        this.edgeID = ID;
        this.startNode = start;
        this.endNode = end;
    }

    public String getEdgeID(){
        return edgeID;
    }

    public String getStartNode(){
        return startNode;
    }

    public String getEndNode(){
        return endNode;
    }

    public String toString(){
        return getEdgeID() + " " + getStartNode() + " " + getEndNode();
    }
}
