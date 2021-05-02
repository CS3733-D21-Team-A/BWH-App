package edu.wpi.aquamarine_axolotls.pathplanning;

import java.util.List;

public class SearchAlgorithmContext {

    public ISearchAlgorithmStrategy context;

    private SearchAlgorithmContext(ISearchAlgorithmStrategy context){
        this.context = context;
    }

    private SearchAlgorithmContext() {}

    private static class ContextHelper{
        private static final SearchAlgorithmContext _context = new SearchAlgorithmContext();
    }

    public static SearchAlgorithmContext getSearchAlgorithmContext(){
        return ContextHelper._context;
    }

//    public static ISearchAlgorithmStrategy getContext(){
//        return context;
//    }

    /**
     * Changes the strategy that will be used throughout the package.
     * @param context The desired strategy
     */
    public void setContext(ISearchAlgorithmStrategy context) {
        this.context = context;
    }

    /**
     * Finds a path from a start node to end node using the set strategy
     * @param startID The ID of the node to start at
     * @param endID The ID of the node to go to
     * @return The list of nodes that constitute the path
     */
    public List<Node> getPath(String startID, String endID){
        return context.getPath(startID, endID);
    }

    /**
     * Calculates the estimated time, in minutes, that it will take for a patient to
     * walk the entire length of the path
     * @param path The path that the patient is traversing
     * @return The time, in minutes, that it will take for a patient to walk along the
     *          entire path
     */
    public double getETA(List<Node> path){
        return context.getETA(path);
    }

    public boolean nodeIsUnimportant(List<Node> path,Node n){
        return context.nodeIsUnimportant(path, n);
    }

    /**
     * Creates a list of text directions instructing the user how to navigate a path
     * @param path The path for which text directions are to be generated
     * @return The list of steps that a user must take to navigate from the start
     *          of the path to the end
     */
    public List<List<String>> getTextDirections(List<Node> path){
        return context.getTextDirections(path);
    }
}
