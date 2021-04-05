package edu.wpi.aquamarine_axolotls.views.pathplanning;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PathBuilder {
    static <T> List<T> buildPath(final Map<T, T> cameFrom, final T goal) {
        LinkedList<T> path = new LinkedList<>(); // We want Queue interface
        T next = goal;
        while (cameFrom.get(next) != null) {
            path.push(next);
            next = cameFrom.get(next);
        }
        path.push(next);
        return path;
    }
}
