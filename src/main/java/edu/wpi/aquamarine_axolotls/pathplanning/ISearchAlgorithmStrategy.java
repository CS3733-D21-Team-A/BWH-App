package edu.wpi.aquamarine_axolotls.pathplanning;

import java.util.List;

public interface ISearchAlgorithmStrategy {
    public List<Node> getPath(String startID, String endID);

    public double getETA(List<Node> path);

    public List<String> getTextDirections(List<Node> path);

    public Node getNode(String nodeID);

    public List<Node> getConnected(Node node);
}
