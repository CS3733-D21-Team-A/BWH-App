package edu.wpi.aquamarine_axolotls.views.pathplanning;

public class Edge{
    private final String edgeID;
    private final String startNode;
    private final String finalNode;

    public Edge(final String edgeID, final String startNode, final String finalNode){
        this.edgeID = edgeID;
        this.startNode = startNode;
        this.finalNode = finalNode;
    }

    public String getEdgeID(){
        return edgeID;
    }

    public String getStartNode(){
        return startNode;
    }

    public String getFinalNode(){
        return finalNode;
    }
}
