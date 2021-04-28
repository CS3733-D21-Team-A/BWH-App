package edu.wpi.aquamarine_axolotls.pathplanning;

import java.util.List;

public interface ISearchAlgorithmStrategy {
    public List<Node> getPath(String startID, String endID);

    public double getETA(List<Node> path);

    public List<List<String>> getTextDirections(List<Node> path);

    public Node getNode(String id);

    public Node getNodeByLongName(String longName);

    public List<Node> getConnected(Node node);
}
