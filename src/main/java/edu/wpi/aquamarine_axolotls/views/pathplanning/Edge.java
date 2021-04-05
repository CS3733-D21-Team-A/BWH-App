package edu.wpi.aquamarine_axolotls.views.pathplanning;

import com.opencsv.bean.CsvBindByName;

import java.util.ArrayList;
import java.util.List;

public class Edge extends Node{

    @CsvBindByName(column = "edgeID")
    private String edgeID;

    @CsvBindByName(column = "startNode")
    private String startNode;

    @CsvBindByName(column = "endNode")
    private String endNode;

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

    public static List<Node> getConnected(String myNode, List<Edge> edges, List<Node> nodes){
        String nodeName = myNode;
        List<Node> connectedNode = new ArrayList<>();

        for (int j = 1; j < edges.size(); j++) {
            String startNodeName = edges.get(j).getStartNode();
            System.out.println(startNodeName);
            String endNodeName = edges.get(j).getEndNode();

            if (nodeName.equals(startNodeName)){
                connectedNode.add(getNode(endNodeName, nodes));
            }
            if (nodeName.equals(endNodeName)){
                connectedNode.add(getNode(startNodeName, nodes));
            }
        }
        System.out.println("getConnected complete");
        return connectedNode;
    }

}
