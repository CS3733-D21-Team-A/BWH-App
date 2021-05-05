package edu.wpi.aquamarine_axolotls.pathplanning;

import java.util.Map;

public class Edge {

    private String edgeID;

    private String startNode;

    private String endNode;

    public Edge(String ID, String start, String end) {
        this.edgeID = ID;
        this.startNode = start;
        this.endNode = end;
    }

    Edge(Map<String, String> edgeMap){
        this.edgeID = edgeMap.get("EDGEID");
        this.startNode = edgeMap.get("STARTNODE");
        this.endNode = edgeMap.get("ENDNODE");
    }

    @Override
    public boolean equals(Object o) {
        try {
            Edge e = (Edge) o;
            return e.getEdgeID().equals(this.getEdgeID());
        } catch (Exception ex) {
            return false;
        }
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
