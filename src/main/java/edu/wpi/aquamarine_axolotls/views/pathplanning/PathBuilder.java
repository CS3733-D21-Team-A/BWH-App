package edu.wpi.aquamarine_axolotls.views.pathplanning;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PathBuilder {
    static  List<Node> buildPath(final Map<Node, Node> cameFrom, final Node goal) {
        LinkedList<Node> path = new LinkedList<>(); // We want Queue interface
        Node next = goal;
        while (cameFrom.get(next) != null) {
            path.push(next);
            next = cameFrom.get(next);
        }
        path.push(next);
        return path;
    }
}
