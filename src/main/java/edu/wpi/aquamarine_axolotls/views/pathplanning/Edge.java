package edu.wpi.aquamarine_axolotls.views.pathplanning;

import com.opencsv.bean.CsvBindByName;

import java.util.ArrayList;
import java.util.List;

public class Edge{

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
}
