package edu.wpi.aquamarine_axolotls.db;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseControllerTest {

    @Test
    void edgeFlip() {

        DatabaseController db = new DatabaseController();

        try{ assertEquals(db.edgeFlip("Node1_Node2"), "Node2_Node1");}
        catch(InvalidEdgeFormatException e){assertTrue(false);}

        try{ assertEquals(db.edgeFlip("1_2"), "2_1");}
        catch(InvalidEdgeFormatException e){assertTrue(false);}

        try{ db.edgeFlip("1_2_");}
        catch(InvalidEdgeFormatException e){assertTrue(true);}

        try{db.edgeFlip(" _2");}
        catch(InvalidEdgeFormatException e){assertTrue(true);}

        try{db.edgeFlip("_2");}
        catch(InvalidEdgeFormatException e){assertTrue(true);}

        try{db.edgeFlip("2_ ");}
        catch(InvalidEdgeFormatException e){assertTrue(true);}

        try{db.edgeFlip("2_");}
        catch(InvalidEdgeFormatException e){assertTrue(true);}

        try{db.edgeFlip("_");}
        catch(InvalidEdgeFormatException e){assertTrue(true);}

        try{db.edgeFlip("");}
        catch(InvalidEdgeFormatException e){assertTrue(true);}


    }
}