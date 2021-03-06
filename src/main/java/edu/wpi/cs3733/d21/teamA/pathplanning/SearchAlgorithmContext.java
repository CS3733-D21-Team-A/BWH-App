package edu.wpi.cs3733.d21.teamA.pathplanning;

import java.util.List;
import java.util.Map;

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
     * @param startLongName The ID of the node to start at
     * @param endLongName The ID of the node to go to
     * @return The list of nodes that constitute the path
     */
    public List<Map<String, String>> getPath(String startLongName, String endLongName){
        return context.getPath(startLongName, endLongName);
    }

    /**
     * Calculates the estimated time, in minutes, that it will take for a patient to
     * walk the entire length of the path
     * @param path The path that the patient is traversing
     * @return The time, in minutes, that it will take for a patient to walk along the
     *          entire path
     */
    public double getETA(List<Map<String, String>> path){
        return context.getETA(path);
    }

    public double getETASingleEdge(Map<String, String> start, Map<String, String> end ){
        return context.getETASingleEdge(start, end);
    }

    public boolean nodeIsUnimportant(List<Map<String,String>> path, Map<String, String> node){
        return context.nodeIsUnimportant(path, node);
    }

    /**
     * Creates a list of text directions instructing the user how to navigate a path
     * @param path The path for which text directions are to be generated
     * @return The list of steps that a user must take to navigate from the start
     *          of the path to the end
     */
    public List<List<String>> getTextDirections(List<Map<String, String>> path){
        return context.getTextDirections(path);
    }

    /**
     * Creates a list of text directions instructing the user how to navigate a path
     * @param path The path for which text directions are to be generated
     * @return The list of steps that a user must take to navigate from the start
     *          of the path to the end
     */
    public List<List<String>> getTextDirectionsNodes(List<Node> path){
        return context.getTextDirectionsNodes(path);
    }
}
