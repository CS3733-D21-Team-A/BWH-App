package edu.wpi.aquamarine_axolotls.pathplanning;

import edu.wpi.aquamarine_axolotls.db.DatabaseController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Dijkstra extends AbsHeuristicBased{

    public Dijkstra() throws SQLException, IOException, URISyntaxException {
        DatabaseController dbControl = new DatabaseController();

        List<Map<String, String>> nodeMap = new ArrayList<>();
        List<Map<String, String>> edgeMap = new ArrayList<>();

        try {
            nodeMap = dbControl.getNodes();
            edgeMap = dbControl.getEdges();
        } catch (SQLException e) {
            e.printStackTrace();
        }



        for (int i = 0; i < nodeMap.size(); i++) {
            Map<String, String> currNodeMap = nodeMap.get(i);
            this.nodes.add(new Node(
                            currNodeMap.get("NODEID"),
                            Integer.parseInt(currNodeMap.get("XCOORD")),
                            Integer.parseInt(currNodeMap.get("YCOORD")),
                            currNodeMap.get("FLOOR"),
                            currNodeMap.get("BUILDING"),
                            currNodeMap.get("NODETYPE"),
                            currNodeMap.get("LONGNAME"),
                            currNodeMap.get("SHORTNAME")
                    )
            );
        }

        for (int j = 0; j < edgeMap.size(); j++) {
            Map<String, String> currEdgeMap = edgeMap.get(j);
            this.edges.add(new Edge(
                    edgeMap.get(j).get("EDGEID"),
                    edgeMap.get(j).get("STARTNODE"),
                    edgeMap.get(j).get("ENDNODE")
            ));
        }
    }


    private double getPriorityHeuristic(Node start, Node end){
        // TODO: HAVE I ALREADY IMPLEMENTED THIS?
        return 0.0;
    }

    public List<Node> getPath(String startID, String endID){
        //TODO: IMPLEMENT THIS
        return new ArrayList<Node>();
    }
}
