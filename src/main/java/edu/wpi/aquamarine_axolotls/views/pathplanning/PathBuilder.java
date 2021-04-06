package edu.wpi.aquamarine_axolotls.views.pathplanning;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PathBuilder {
    static  List<Node> buildPath(final Map<Node, Node> cameFrom, final Node goal) {
        LinkedList<Node> path = new LinkedList<>();
        Node next = goal;
        while (cameFrom.get(next) != null) {
            path.push(next);
            next = cameFrom.get(next);
        }
        path.push(next);
        return path;
    }

    static boolean checkPath(List<Node> path, Node start, Node goal){
        Node pathStart = path.get(0);
        Node pathGoal = path.get(path.size()-1);
        System.out.println("pathStart: " + pathStart);
        System.out.println("pathGoal: " + pathStart);

        boolean startNodeisEqual = pathStart.getNodeID().equals(start.getNodeID());
        boolean goalNodeisEqual = pathGoal.getNodeID().equals(goal.getNodeID());

        System.out.println("pathiscorrect: " + (startNodeisEqual && goalNodeisEqual));
        return startNodeisEqual && goalNodeisEqual;
    }
}
