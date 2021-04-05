package edu.wpi.aquamarine_axolotls.views.pathplanning;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Objects;

public class Node {

    public enum NodeType {
        CONF("Conference"),
        ELEV("Elevator"),
        EXIT("Exit"),
        HALL("Hall"),
        DEPT("Department"),
        INFO("Information"),
        LABS("Lab"),
        REST("Restroom"),
        RETL("Retail"),
        SERV("Service"),
        STAI("Stair Case"),
        WORKZONE("Workzone"),
        PANTRY("Pantry"),
        CLASSROOM("Classroom"),
        AUDITORIUM("Auditorium");

        private final String name;
        NodeType(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final String nodeId;
    private final int xcoord;
    private final int ycoord;
    private final String floor;
    private final String building;
    private final NodeType nodeType;
    private final String longName;
    private final String shortName;

    public Node(final String nodeId, final int xcoord, final int ycoord, final String floor,
                final String building, final NodeType nodeType, final String longName,
                final String shortName) {
        this.nodeId = nodeId;
        this.xcoord = xcoord;
        this.ycoord = ycoord;
        this.floor = floor;
        this.building = building;
        this.nodeType = nodeType;
        this.longName = longName;
        this.shortName = shortName;
    }

    public String getNodeId() {
        return nodeId;
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

    public NodeType getNodeType() {
        return nodeType;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

        public double getCostTo(Node othernode){
            double xsqre = Math.pow(othernode.getXcoord() - getXcoord(),2);
            double ysqre = Math.pow(othernode.getYcoord() - getYcoord(),2);
            double dist = Math.sqrt(xsqre+ysqre);

            return dist;
        }
}

