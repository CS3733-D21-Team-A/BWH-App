package edu.wpi.aquamarine_axolotls.views.pathplanning;

import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SearchAlgorithm<T extends CostTo<T>> {

    protected static class CostPair<T> {
        private final T item;
        private final double cost;

        CostPair(T item, double cost) {
            this.item = item;
            this.cost = cost;
        }

        public T getItem() {
            return item;
        }

        public double getCost() {
            return cost;
        }

        private final PriorityQueue<CostPair<T>> frontier = new PriorityQueue<>();

        void clearFrontier() {
            frontier.clear();
        }

        boolean isEmptyFrontier() {
            return frontier.isEmpty();
        }

        void addToFrontier(T node, double cost) {
            frontier.add(new CostPair<>(node, cost));
        }

        T getNextFrontier() {
            if (frontier.isEmpty()) {
                return null;
            }
            return frontier.poll().getItem();
        }

        double getPriorityHeuristic(T next, T goal) {
            return next.getCostTo(goal);
        }
        public final List<T> getPath(somethingfromDB, T start, T goal) {
            clearFrontier();
            final Map<T, T> cameFrom = new HashMap<>();
            final Map<T, Double> costSoFar = new HashMap<>();

            addToFrontier(start, 0.0);
            cameFrom.put(start, null);
            costSoFar.put(start, 0.0);

            while (!isEmptyFrontier()) {
                T current = getNextFrontier();

                if (current.equals(goal)) {
                    break;
                }

                for (T next : getConnectingnodesfromDB) {
                    double newCost = costSoFar.get(current) + current.getCostTo(next);
                    if (!cameFrom.containsKey(next) || newCost < costSoFar.get(next)) {
                        costSoFar.put(next, newCost);
                        addToFrontier(next, newCost + getPriorityHeuristic(next, goal));
                        cameFrom.put(next, current);
                    }
                }
            }
            return buildPath(cameFrom, goal); //need to implement build path here
        }

    }
}
