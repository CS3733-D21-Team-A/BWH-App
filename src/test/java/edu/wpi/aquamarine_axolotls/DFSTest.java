package edu.wpi.aquamarine_axolotls;

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
    /**
     * Test case where the start node is the same as the end node
     */
    @Test
    public void oneNodeTest() {

        List<Node> oneNodeList = new ArrayList<>();
        oneNodeList.add(new Node("A"));

        List<Edge> oneEdgeList = new ArrayList<>();

        SearchAlgorithm depthFirstSearch = new SearchAlgorithm(oneNodeList, oneEdgeList);

        List<String> expectedOut = new ArrayList<>();
        expectedOut.add("A");

        Assertions.assertEquals(depthFirstSearch.getPath("A", "A"), expectedOut);
    }

    /**
     * Test case with just two nodes (a start and an end) and one edge between them
     */
    @Test
    public void twoNodeTest() {


        List<Node> twoNodeList = new ArrayList<>();
        twoNodeList.add(new Node("A"));
        twoNodeList.add(new Node("B"));

        List<Edge> twoEdgeList = new ArrayList<>();
        twoEdgeList.add(new Edge("ab", "A", "B"));

        SearchAlgorithm depthFirstSearch = new SearchAlgorithm(twoNodeList, twoEdgeList);

        List<String> expectedOut = new ArrayList<>();
        expectedOut.add("A");
        expectedOut.add("B");

        Assertions.assertEquals(depthFirstSearch.getPath("A", "B"), expectedOut);
    }

    /**
     * Test case with the end node able to be found down the first branch
     */
    @Test
    public void firstBranchTest() {
        DepthFirstSearch depthFirstSearch = new DepthFirstSearch();

        List<String> firstBranchNodes = new ArrayList<>();
        firstBranchNodes.add("A");
        firstBranchNodes.add("B");
        firstBranchNodes.add("C");
        firstBranchNodes.add("D");
        firstBranchNodes.add("E");

        List<String[]> firstBranchEdges = new ArrayList<>();
        String[] ab = {"A", "B"};
        String[] bc = {"B", "C"};
        String[] ad = {"A", "D"};
        String[] de = {"D", "E"};
        firstBranchEdges.add(ab);
        firstBranchEdges.add(bc);
        firstBranchEdges.add(ad);
        firstBranchEdges.add(de);

        List<String> expectedOut = new ArrayList<>();
        expectedOut.add("A");
        expectedOut.add("B");
        expectedOut.add("C");

        depthFirstSearch.setup(firstBranchNodes, firstBranchEdges);
        Assertions.assertEquals(depthFirstSearch.getPath("A", "C"), expectedOut);
    }

    /**
     * Test case with the end node unreachable from the first branch,
     *      but accessible from the second branch
     */
    @Test
    public void secondBranchTest() {
        DepthFirstSearch depthFirstSearch = new DepthFirstSearch();

        List<String> secondBranchNodeList = new ArrayList<>();
        secondBranchNodeList.add("A");
        secondBranchNodeList.add("B");
        secondBranchNodeList.add("C");
        secondBranchNodeList.add("D");
        secondBranchNodeList.add("E");
        secondBranchNodeList.add("F");
        secondBranchNodeList.add("G");
        secondBranchNodeList.add("H");
        secondBranchNodeList.add("I");

        List<String[]> secondBranchEdgeList = new ArrayList<>();
        String[] ab = {"A", "B"};
        secondBranchEdgeList.add(ab);
        String[] ac = {"A", "C"};
        secondBranchEdgeList.add(ac);
        String[] bd = {"B", "D"};
        secondBranchEdgeList.add(bd);
        String[] be = {"B", "E"};
        secondBranchEdgeList.add(be);
        String[] cg = {"C", "G"};
        secondBranchEdgeList.add(cg);
        String[] ch = {"C", "H"};
        secondBranchEdgeList.add(ch);
        String[] df = {"D", "F"};
        secondBranchEdgeList.add(df);
        String[] ef = {"E", "F"};
        secondBranchEdgeList.add(ef);
        String[] hi = {"H", "I"};
        secondBranchEdgeList.add(hi);

        List<String> expectedOut = new ArrayList<>();
        expectedOut.add("A");
        expectedOut.add("C");
        expectedOut.add("H");
        expectedOut.add("I");

        depthFirstSearch.setup(secondBranchNodeList, secondBranchEdgeList);
        Assertions.assertEquals(depthFirstSearch.initiateSearch("A", "I"), expectedOut);
    }

    /**
     * Test case where the end node cannot be reached from the start node
     */
    @Test
    public void nodeNotFoundTest() {
        DepthFirstSearch depthFirstSearch = new DepthFirstSearch();

        List<String> threeNodeList = new ArrayList<>();
        threeNodeList.add("A");
        threeNodeList.add("B");
        threeNodeList.add("C");
        threeNodeList.add("D");

        List<String[]> twoEdgeList = new ArrayList<>();
        String[] ab = {"A", "B"};
        twoEdgeList.add(ab);
        String[] bc = {"B", "C"};
        twoEdgeList.add(bc);

        List<String> expectedOut = null;

        depthFirstSearch.setup(threeNodeList, twoEdgeList);
        Assertions.assertEquals(depthFirstSearch.initiateSearch("A", "D"), null);
    }

    /**
     * Test case where the end node can be found through multiple paths.
     *      The algorithm should return the first path it finds, even if
     *      that path is longer.
     */
    @Test
    public void loopTest() {
        DepthFirstSearch depthFirstSearch = new DepthFirstSearch();

        List<String> loopNodeList = new ArrayList<>();
        loopNodeList.add("A");
        loopNodeList.add("B");
        loopNodeList.add("C");
        loopNodeList.add("D");
        loopNodeList.add("E");

        List<String[]> loopEdgeList = new ArrayList<>();
        String[] ab = {"A", "B"};
        loopEdgeList.add(ab);
        String[] ac = {"A", "C"};
        loopEdgeList.add(ac);
        String[] bd = {"B", "D"};
        loopEdgeList.add(bd);
        String[] de = {"D", "E"};
        loopEdgeList.add(de);
        String[] ce = {"C", "E"};
        loopEdgeList.add(ce);

        List<String> expectedOut = new ArrayList<>();
        expectedOut.add("A");
        expectedOut.add("B");
        expectedOut.add("D");
        expectedOut.add("E");
        expectedOut.add("C");

        depthFirstSearch.setup(loopNodeList, loopEdgeList);
        Assertions.assertEquals(depthFirstSearch.initiateSearch("A", "C"), expectedOut);
    }
}
