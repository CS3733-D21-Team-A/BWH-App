package edu.wpi.aquamarine_axolotls.pathplanning;

import java.util.Objects;

class CostPair implements Comparable<CostPair> {
    private final Node item; //The item
    private final double cost; //The current cost to get to that item from the starting node


    CostPair(Node item, double cost) {
        this.item = item;
        this.cost = cost;
    }

    public Node getItem() {
        return item;
    }

    public double getCost() { return cost; }

    public String toString() {
        return getItem() + "," + getCost();
    }

    public int compareTo(CostPair o) {
        return Double.compare(cost, o.getCost());
    }

    /**
     * Alternate version of equals that checks if two costPairs are equal
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CostPair costPair = (CostPair) o;
        return Objects.equals(item, costPair.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item);
    }
}
