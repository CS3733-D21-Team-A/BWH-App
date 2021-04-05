package edu.wpi.aquamarine_axolotls.views.pathplanning;

import com.opencsv.CSVReader;
import java.nio.file.Files;
import java.util.*;
import java.io.*;

public class NodeReader {

    private List<Node> allNodes;

    public NodeReader() {
        this.allNodes = new ArrayList<>();
    }

    public List<String[]> readAll(Reader reader) throws Exception{
        CSVReader csvReader = new CSVReader(reader);
        List<String[]> list = csvReader.readAll();
        reader.close();
        csvReader.close();
        return list;
    }

    public void setup() {

        Reader nodeReader;
        List<String[]> nodeData = new ArrayList<>();

        Reader edgeReader;
        List<String[]> edgeData = new ArrayList<>();

        try {
            nodeReader = new FileReader("src/main/resources/edu/wpi/aquamarine_axolotls/L1Nodes.csv");
            nodeData = readAll(nodeReader);

            edgeReader = new FileReader("src/main/resources/edu/wpi/aquamarine_axolotls/L1Edges.csv");
            edgeData = readAll(edgeReader);
        }
        catch (Exception e) {
            System.out.println("File not found.");
        }
    }
}



