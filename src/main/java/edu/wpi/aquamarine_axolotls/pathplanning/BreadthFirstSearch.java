package edu.wpi.aquamarine_axolotls.pathplanning;

import java.util.*;

public class BreadthFirstSearch extends AbsAlgorithmMethod{

    public BreadthFirstSearch(){
        //TODO: WRITE THE CONSTRUCTOR
    }

    public List<Node> getPath(String startID, String endID){
        Node start = getNode(startID);
        Node end = getNode(endID);
        LinkedList<Node> queue = new LinkedList<>();
        List<Node> visited = new LinkedList<>();
        Map<Node, Node> cameFrom = new HashMap<>();
        List<Node> path = null;

        boolean foundGoal = false;
        queue.add(start);
        Node current = start;
        cameFrom.put(start,null);

        while (!queue.isEmpty()){
            current = queue.poll();
            List<Node> connectedNodes = getConnected(current);

            if(current.equals(end)){
                foundGoal = true;
                break;
            }

            for(Node next: connectedNodes){
                if(!visited.contains(next)){
                    cameFrom.put(next,current);
                    visited.add(next);
                    queue.add(next);
                }
            }
        }

        if(foundGoal){
            path = buildPath(cameFrom,end);
        }

        return path;
    }
}
