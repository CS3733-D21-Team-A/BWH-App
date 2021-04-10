package edu.wpi.aquamarine_axolotls.pathplanning;

public class Node{

    private String nodeID;

    private int xcoord;

    private int ycoord;

    private String floor;

    private String building;

    private String nodeType;

    private String longName;

    private String shortName;

    public Node(String ID, int x, int y, String floor, String building, String type, String longName, String shortName) {
        this.nodeID = ID;
        this.xcoord = x;
        this.ycoord = y;
        this.floor = floor;
        this.building = building;
        this.nodeType = type;
        this.longName = longName;
        this.shortName = shortName;
    }

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



    /**
     * Helper function that turns a node into a string that can be printed out
     * @return A string consisting of the node ID, x coord, y coord and short name separated by spaces
     */
    public String toString(){
        return "\n" + getNodeID() + " " + getXcoord() + " " + getYcoord() + " " + getShortName();
    }



}

