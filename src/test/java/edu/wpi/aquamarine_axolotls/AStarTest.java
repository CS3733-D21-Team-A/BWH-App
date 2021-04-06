package edu.wpi.aquamarine_axolotls;

import com.opencsv.bean.CsvToBeanBuilder;
import edu.wpi.aquamarine_axolotls.views.pathplanning.Edge;
import edu.wpi.aquamarine_axolotls.views.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.views.pathplanning.SearchAlgorithm;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.util.*;

public class AStarTest {

    SearchAlgorithm aStar = new SearchAlgorithm();
    Edge newEdge = new Edge();
    Node newNode = new Node();

    String nodeFileName = "src/test/resources/edu/wpi/aquamarine_axolotls/testNodes.csv";
    String edgeFileName = "src/test/resources/edu/wpi/aquamarine_axolotls/testEdges.csv";

    List<Node> nodes = new CsvToBeanBuilder(new FileReader(nodeFileName)).withType(Node.class).build().parse();
    List<Edge> edges = new CsvToBeanBuilder(new FileReader(edgeFileName)).withType(Edge.class).build().parse();

    public AStarTest() throws IOException {
    }

    @Test
    public void oneNodeTest() {
        Node start = Node.getNode("A",nodes);
        Node goal = Node.getNode("A",nodes);

        List<Node> testPath = aStar.getPath(edges,nodes,start,goal);
        List<Node> expectedPath = new ArrayList<>();
        expectedPath.add(newNode.getNode("A",nodes));

        System.out.println(expectedPath);
        System.out.println(testPath);

        Assertions.assertEquals(testPath, expectedPath);
    }
    @Test
    public void twoNodeTest1() {
        Node start = Node.getNode("A",nodes);
        Node goal = Node.getNode("C",nodes);

        List<Node> testPath = aStar.getPath(edges,nodes,start,goal);
        List<Node> expectedPath = new ArrayList<>();
        expectedPath.add(newNode.getNode("A",nodes));
        expectedPath.add(newNode.getNode("D",nodes));
        expectedPath.add(newNode.getNode("C",nodes));

        System.out.println(expectedPath);
        System.out.println(testPath);

        Assertions.assertEquals(testPath, expectedPath);
    }

    @Test
    public void twoNodeTest2() {
        Node start = Node.getNode("E",nodes);
        Node goal = Node.getNode("K",nodes);

        List<Node> testPath = aStar.getPath(edges,nodes,start,goal);
        List<Node> expectedPath = new ArrayList<>();
        expectedPath.add(newNode.getNode("H",nodes));
        expectedPath.add(newNode.getNode("I",nodes));
        expectedPath.add(newNode.getNode("J",nodes));
        expectedPath.add(newNode.getNode("F",nodes));

        System.out.println(expectedPath);
        System.out.println(testPath);

        Assertions.assertEquals(testPath, expectedPath);
    }

    @Test
    public void noConnectedNodeTest() {
        Node start = Node.getNode("E",nodes);
        Node goal = Node.getNode("K",nodes);

        List<Node> testPath = aStar.getPath(edges,nodes,start,goal);
        List<Node> expectedPath = null;

        System.out.println("expected Path: " + expectedPath);
        System.out.println("test Path: " + testPath);

        Assertions.assertEquals(testPath, expectedPath);
    }
}
