package edu.wpi.aquamarine_axolotls.db;

public class EdgeAlreadyExistsException extends Exception {
    String edgeID;

    public EdgeAlreadyExistsException(String eID){
        super("The edge " + eID + " already exists.");
        edgeID = eID;
    }

}
