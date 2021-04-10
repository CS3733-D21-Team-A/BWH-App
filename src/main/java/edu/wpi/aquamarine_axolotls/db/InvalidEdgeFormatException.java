package edu.wpi.aquamarine_axolotls.db;

public class InvalidEdgeFormatException extends Exception {

    String edgeID;

    public InvalidEdgeFormatException(String eID){
        super("The edge " + eID + " is in an incorrect format.");
        edgeID = eID;
    }

}
