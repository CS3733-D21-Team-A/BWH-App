package edu.wpi.aquamarine_axolotls.db;

public class EdgeDoesNotExistException extends Exception{
    String edgeID;

    public EdgeDoesNotExistException(String eID){
        super("The edge " + eID + " does not exist.");
        edgeID = eID;
    }
}
