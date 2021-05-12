package edu.wpi.cs3733.d21.teamA.pathplanning;

import java.util.Map;

public class Node {

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

    /**
     * Alternate constructor for testing purposes that just takes an ID and fills everything else with placeholders
     * @param ID Id of the node
     */
    public Node(String ID) {
        this.nodeID = ID;
        this.xcoord = 0;
        this.ycoord = 0;
        this.floor = "placeholder";
        this.building = "placeholder";
        this.nodeType = "placeholder";
        this.longName = "placeholder";
        this.shortName = "placeholder";
    }

    public Node(Map<String, String> nodeMap){
        this.nodeID = nodeMap.get("NODEID");
        this.xcoord = Integer.parseInt(nodeMap.get("XCOORD"));
        this.ycoord = Integer.parseInt(nodeMap.get("YCOORD"));
        this.floor = nodeMap.get("FLOOR");
        this.building = nodeMap.get("BUILDING");
        this.nodeType = nodeMap.get("NODETYPE");
        this.longName = nodeMap.get("LONGNAME");
        this.shortName = nodeMap.get("SHORTNAME");
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

    @Override
    public boolean equals(Object o){
        try{
            Node n = (Node) o;
            return n.getNodeID().equals(this.getNodeID());
        } catch (Exception e){
            return false;
        }

    }

}

