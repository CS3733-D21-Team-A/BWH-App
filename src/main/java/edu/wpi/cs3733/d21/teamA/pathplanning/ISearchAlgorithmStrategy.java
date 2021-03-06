package edu.wpi.cs3733.d21.teamA.pathplanning;

import java.util.List;
import java.util.Map;

public interface ISearchAlgorithmStrategy {
    public List<Map<String, String>> getPath(String startID, String endID);

    public double getETA(List<Map<String, String>> path);
    public double getETASingleEdge(Map<String, String> start, Map<String, String> end);
    public List<List<String>> getTextDirections(List<Map<String, String>> path);
    public List<List<String>> getTextDirectionsNodes(List<Node> path);

    public Node getNode(String id);

    public Node getNodeByLongName(String longName);

    public List<Node> getConnected(Node node);

    public boolean nodeIsUnimportant(List<Map<String, String>> path, Map<String, String> node);
}
