package edu.wpi.aquamarine_axolotls.pathplanning;

import edu.wpi.aquamarine_axolotls.pathplanning.DepthFirstSearch;
import edu.wpi.aquamarine_axolotls.pathplanning.Edge;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.pathplanning.SearchAlgorithm;
import org.junit.jupiter.api.*;

import java.io.IOException;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DFSTest {

    Node A = new Node("A");
    Node B = new Node("B");
    Node C = new Node("C");
    Node D = new Node("D");
    Node E = new Node("E");
    Node F = new Node("F");
    Node G = new Node("G");
    Node H = new Node("H");
    Node I = new Node("I");


    /**
     * Test case where the start node is the same as the end node
     */
    @Test
    public void oneNodeTest() {

        List<Node> oneNodeList = new ArrayList<>();
        oneNodeList.add(A);

        List<Edge> oneEdgeList = new ArrayList<>();

        DepthFirstSearch depthFirstSearch = new DepthFirstSearch(oneNodeList, oneEdgeList);

        List<Node> expectedOut = new ArrayList<>();
        expectedOut.add(A);

        Assertions.assertEquals(depthFirstSearch.getPath("A", "A"), expectedOut);
    }

    /**
     * Test case with just two nodes (a start and an end) and one edge between them
     */
    @Test
    public void twoNodeTest() {

        List<Node> twoNodeList = new ArrayList<>();
        twoNodeList.add(A);
        twoNodeList.add(B);

        List<Edge> twoEdgeList = new ArrayList<>();
        twoEdgeList.add(new Edge("ab", "A", "B"));

        DepthFirstSearch depthFirstSearch = new DepthFirstSearch(twoNodeList, twoEdgeList);

        List<Node> expectedOut = new ArrayList<>();
        expectedOut.add(A);
        expectedOut.add(B);

        Assertions.assertEquals(depthFirstSearch.getPath("A", "B"), expectedOut);
    }

    /**
     * Test case with the end node able to be found down the first branch
     */
    @Test
    public void firstBranchTest() {

        List<Node> firstBranchNodes = new ArrayList<>();
        firstBranchNodes.add(A);
        firstBranchNodes.add(B);
        firstBranchNodes.add(C);
        firstBranchNodes.add(D);
        firstBranchNodes.add(E);

        List<Edge> firstBranchEdges = new ArrayList<>();
        firstBranchEdges.add(new Edge("ab", "A", "B"));
        firstBranchEdges.add(new Edge("bc", "B", "C"));
        firstBranchEdges.add(new Edge("ad", "A", "D"));
        firstBranchEdges.add(new Edge("de", "D", "E"));

        DepthFirstSearch depthFirstSearch = new DepthFirstSearch(firstBranchNodes, firstBranchEdges);

        List<Node> expectedOut = new ArrayList<>();
        expectedOut.add(A);
        expectedOut.add(B);
        expectedOut.add(C);

        Assertions.assertEquals(depthFirstSearch.getPath("A", "C"), expectedOut);
    }

    /**
     * Test case with the end node unreachable from the first branch,
     *      but accessible from the second branch
     */
    @Test
    public void secondBranchTest() {

        List<Node> secondBranchNodeList = new ArrayList<>();
        secondBranchNodeList.add(A);
        secondBranchNodeList.add(B);
        secondBranchNodeList.add(C);
        secondBranchNodeList.add(D);
        secondBranchNodeList.add(E);
        secondBranchNodeList.add(F);
        secondBranchNodeList.add(G);
        secondBranchNodeList.add(H);
        secondBranchNodeList.add(I);

        List<Edge> secondBranchEdgeList = new ArrayList<>();
        secondBranchEdgeList.add(new Edge("ab", "A", "B"));
        secondBranchEdgeList.add(new Edge("ac", "A", "C"));
        secondBranchEdgeList.add(new Edge("bd", "B", "D"));
        secondBranchEdgeList.add(new Edge("be", "B", "E"));
        secondBranchEdgeList.add(new Edge("cg", "C", "G"));
        secondBranchEdgeList.add(new Edge("ch", "C", "H"));
        secondBranchEdgeList.add(new Edge("df", "D", "F"));
        secondBranchEdgeList.add(new Edge("ef", "E", "F"));
        secondBranchEdgeList.add(new Edge("hi", "H", "I"));

        DepthFirstSearch depthFirstSearch = new DepthFirstSearch(secondBranchNodeList, secondBranchEdgeList);

        List<Node> expectedOut = new ArrayList<>();
        expectedOut.add(A);
        expectedOut.add(C);
        expectedOut.add(H);
        expectedOut.add(I);

        Assertions.assertEquals(depthFirstSearch.getPath("A", "I"), expectedOut);
    }

    /**
     * Test case where the end node cannot be reached from the start node
     */
    @Test
    public void nodeNotFoundTest() {

        List<Node> threeNodeList = new ArrayList<>();
        threeNodeList.add(A);
        threeNodeList.add(B);
        threeNodeList.add(C);
        threeNodeList.add(D);

        List<Edge> twoEdgeList = new ArrayList<>();
        twoEdgeList.add(new Edge("ab", "A", "B"));
        twoEdgeList.add(new Edge("bc", "B", "C"));

        DepthFirstSearch depthFirstSearch = new DepthFirstSearch(threeNodeList, twoEdgeList);

        List<String> expectedOut = null;

        Assertions.assertEquals(depthFirstSearch.getPath("A", "D"), null);
    }

    /**
     * Test case where the end node can be found through multiple paths.
     *      The algorithm should return the first path it finds, even if
     *      that path is longer.
     */
    @Test
    public void loopTest() {


        List<Node> loopNodeList = new ArrayList<>();
        loopNodeList.add(A);
        loopNodeList.add(B);
        loopNodeList.add(C);
        loopNodeList.add(D);
        loopNodeList.add(E);

        List<Edge> loopEdgeList = new ArrayList<>();
        loopEdgeList.add(new Edge("ab", "A", "B"));
        loopEdgeList.add(new Edge("ac", "A", "C"));
        loopEdgeList.add(new Edge("bd", "B", "D"));
        loopEdgeList.add(new Edge("de", "D", "E"));
        loopEdgeList.add(new Edge("ce", "C", "E"));

        List<Node> expectedOut = new ArrayList<>();
        expectedOut.add(A);
        expectedOut.add(B);
        expectedOut.add(D);
        expectedOut.add(E);
        expectedOut.add(C);

        DepthFirstSearch depthFirstSearch = new DepthFirstSearch(loopNodeList, loopEdgeList);

        Assertions.assertEquals(depthFirstSearch.getPath("A", "C"), expectedOut);
    }
}
