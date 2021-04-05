package edu.wpi.aquamarine_axolotls.views.pathplanning;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.opencsv.bean.CsvBindByName;

public class Node {

    @CsvBindByName(column = "nodeID")
    private String nodeID;

    @CsvBindByName(column = "xcoord")
    private int xcoord;

    @CsvBindByName(column = "ycoord")
    private int ycoord;

    @CsvBindByName(column = "floor")
    private String floor;

    @CsvBindByName(column = "building")
    private String building;

    @CsvBindByName(column = "nodeType")
    private String nodeType;

    @CsvBindByName(column = "longName")
    private String longName;

    @CsvBindByName(column = "shortName")
    private String shortName;


    public String getNodeID() {
        return nodeID;
    }

    public int getXcoord() {
        return xcoord;
    }

    public int getYcoord() {
        return ycoord;
    }

    public String getFloor() {
        return floor;
    }

    public String getBuilding() {
        return building;
    }

    public String getNodeType() {
        return nodeType;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

    public static Node getNode(String id, List<Node> nodes){
        String nodeName;
        for (int j = 1; j < nodes.size(); j++) {
            nodeName = nodes.get(j).getNodeID();

            if (nodeName.equals(id)){
                return nodes.get(j);
            }
        }
        System.out.println("Couldn't find that node");
        return null;
    }

    public String toString(){
        return getNodeID() + " " + getXcoord() + " " + getYcoord();
    }
        public double getCostTo(Node othernode){
            double xsqre = Math.pow(othernode.getXcoord() - getXcoord(),2);
            double ysqre = Math.pow(othernode.getYcoord() - getYcoord(),2);
            double dist = Math.sqrt(xsqre+ysqre);

            return dist;
        }

}

