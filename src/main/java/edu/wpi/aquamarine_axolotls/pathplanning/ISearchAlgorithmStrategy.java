package edu.wpi.aquamarine_axolotls.pathplanning;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public interface ISearchAlgorithmStrategy {
    public List<Map<String, String>> getPath(String startID, String endID);

    public double getETA(List<Node> path);

    public List<List<String>> getTextDirections(List<Map<String, String>> path);
    public List<List<String>> getTextDirectionsNodes(List<Node> path);

    public Node getNode(String id);

    public Node getNodeByLongName(String longName);

    public List<Node> getConnected(Node node);

    public boolean nodeIsUnimportant(List<Map<String, String>> path, Map<String, String> node);
}
