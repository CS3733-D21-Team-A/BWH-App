package edu.wpi.aquamarine_axolotls.pathplanning;

import java.util.List;

public class SearchAlgorithmContext {

    ISearchAlgorithmStrategy context;

    public SearchAlgorithmContext(ISearchAlgorithmStrategy context){
        this.context = context;
    }

    public void setContext(ISearchAlgorithmStrategy context) {
        this.context = context;
    }

    public List<Node> getPath(String startID, String endID){
        return context.getPath(startID, endID);
    }

    public double getETA(List<Node> path){
        return context.getETA(path);
    }

    public List<String> getTextDirections(List<Node> path){
        return context.getTextDirections(path);
    }
}
