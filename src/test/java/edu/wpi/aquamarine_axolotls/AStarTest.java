package edu.wpi.aquamarine_axolotls;

import edu.wpi.aquamarine_axolotls.pathplanning.Edge;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.pathplanning.SearchAlgorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;

import java.io.FileReader;

import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AStarTest {



    SearchAlgorithm aStarDB;
    SearchAlgorithm aStarManual;
    String floor = "placeholder floor text";
    String building = "placeholder building text";
    String type = "placeholder node type text";
    String longName = "placeholder long name text";
    String shortName = "placeHolder short name text";

    List<Node> testNodes = new ArrayList<>();
    List<Edge> testEdges = new ArrayList<>();

    @BeforeAll
    public void setupTestNodesEdges() {
        String[] adbArgs = {"1"};
        Adb.main(adbArgs);

        aStarDB = new SearchAlgorithm();

        testNodes.add(new Node("A", 10, 6, floor, building, type, "Anesthesia Conf Floor L1", shortName));
        testNodes.add(new Node("B", 9, 2, floor, building, type, "Medical Records Conference Room Floor L1", shortName));
        testNodes.add(new Node("C", 5, 7, floor, building, type, "Abrams Conference Room", shortName));
        testNodes.add(new Node("D", 9, 9, floor, building, type, "Day Surgery Family Waiting Floor L1", shortName));
        testNodes.add(new Node("E", 1, 2, floor, building, type, "Day Surgery Family Waiting Exit Floor L1", shortName));
        testNodes.add(new Node("F", 10, 1, floor, building, type, "Medical Records Film Library Floor L1", shortName));
        testNodes.add(new Node("G", 7, 4, floor, building, type, "Hallway 1 Floor L1", shortName));
        testNodes.add(new Node("H", 3, 1, floor, building, type, "Hallway 2 Floor L1", shortName));
        testNodes.add(new Node("I", 5, 4, floor, building, type, "Hallway 3 Floor L1", shortName));
        testNodes.add(new Node("J", 9, 1, floor, building, type, "Hallway 4 Floor L1", shortName));
        testNodes.add(new Node("K", 10, 1, floor, building, type, "Hallway 4 Floor L1", shortName));

        testEdges.add(new Edge("EI", "E", "I"));
        testEdges.add(new Edge("EH", "E", "H"));
        testEdges.add(new Edge("HI", "H", "I"));
        testEdges.add(new Edge("IG", "I", "G"));
        testEdges.add(new Edge("IJ", "I", "J"));
        testEdges.add(new Edge("JB", "J", "B"));
        testEdges.add(new Edge("JF", "J", "F"));
        testEdges.add(new Edge("BF", "B", "F"));
        testEdges.add(new Edge("GB", "G", "B"));
        testEdges.add(new Edge("GA", "G", "A"));
        testEdges.add(new Edge("BA", "B", "A"));
        testEdges.add(new Edge("FA", "F", "A"));
        testEdges.add(new Edge("AD", "A", "D"));
        testEdges.add(new Edge("CD", "C", "D"));

        aStarManual = new SearchAlgorithm(testNodes, testEdges);
    }


    public AStarTest() throws IOException {
    }

    /*Test for the map with only one node in it, the start is the same as the goal*/

    @Test
    public void oneNodeTest() {
        Node A = new Node("A");
        List<Node> oneNode = new ArrayList<>();
        oneNode.add(A);
        List<Edge> noEdges = new ArrayList<>();

        SearchAlgorithm aStar = new SearchAlgorithm(oneNode, noEdges);

        List<Node> testPath = aStar.getPath("A", "A");
        List<Node> expectedPath = new ArrayList<>();
        expectedPath.add(A);

        System.out.println(expectedPath);
        System.out.println(testPath);

        Assertions.assertEquals(testPath, expectedPath);
    }

    /*Test for the map with only two node in it*/

    @Test
    public void twoNodeTest() {
        Node A = new Node("A");
        Node B = new Node("B");
        List<Node> twoNodes = new ArrayList<>();
        twoNodes.add(A);
        twoNodes.add(B);
        List<Edge> oneEdge = new ArrayList<>();
        oneEdge.add(new Edge("AB", "A", "B"));

        SearchAlgorithm aStar = new SearchAlgorithm(twoNodes, oneEdge);


        List<Node> testPath = aStar.getPath("A", "B");
        List<Node> expectedPath = new ArrayList<>();
        expectedPath.add(A);
        expectedPath.add(B);

        System.out.println(expectedPath);
        System.out.println(testPath);

        Assertions.assertEquals(expectedPath,testPath);
    }

    /*Test general search with input map*/

    @Test
    public void generalNodeTest1() {
        //Node start = Node.getNode("A",nodes);
        //Node goal = Node.getNode("C",nodes);

        List<Node> testPath = aStarManual.getPath("A", "C");
        List<Node> expectedPath = new ArrayList<>();
        expectedPath.add(aStarManual.getNode("A"));
        expectedPath.add(aStarManual.getNode("D"));
        expectedPath.add(aStarManual.getNode("C"));

        System.out.println(expectedPath);
        System.out.println(testPath);

        Assertions.assertEquals(expectedPath,testPath);
    }

    /*Test general search with input map*/

    @Test
    public void generalNodeTest2() {
        //Node start = Node.getNode("H",nodes);
        //Node goal = Node.getNode("F",nodes);

        List<Node> testPath = aStarManual.getPath("H", "F");
        List<Node> expectedPath = new ArrayList<>();
        expectedPath.add(aStarManual.getNode("H"));
        expectedPath.add(aStarManual.getNode("I"));
        expectedPath.add(aStarManual.getNode("J"));
        expectedPath.add(aStarManual.getNode("F"));

        System.out.println(expectedPath);
        System.out.println(testPath);

        Assertions.assertEquals(expectedPath,testPath);
    }


    /*Test for the case that the end is not connected to any of the node
    * and no path can be generated*/

    @Test
    public void noConnectedNodeTest() {
        //Node start = Node.getNode("E",nodes);
        //Node goal = Node.getNode("K",nodes);

        List<Node> testPath = aStarManual.getPath("E", "K");
        List<Node> expectedPath = null;

        System.out.println("expected Path: " + expectedPath);
        System.out.println("test Path: " + testPath);

        Assertions.assertEquals(expectedPath,testPath);
    }

    //TESTS TO MAKE
    //--Getting nodes by long name instead of ID
    //--Passing node ID for one param and long name for the other
    //--General path tests for the database integration
    //--Getting nodes by long name from the database
}
