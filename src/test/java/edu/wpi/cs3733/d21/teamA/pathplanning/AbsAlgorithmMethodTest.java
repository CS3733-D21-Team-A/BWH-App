//package edu.wpi.cs3733.d21.teamA.pathplanning;
//
//import edu.wpi.cs3733.d21.teamA.pathplanning.AStar;
//import edu.wpi.cs3733.d21.teamA.pathplanning.Node;
//import edu.wpi.cs3733.d21.teamA.pathplanning.SearchAlgorithm;
//import edu.wpi.cs3733.d21.teamA.pathplanning.SearchAlgorithmContext;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class AbsAlgorithmMethodTest {
//
//    private AStar aStar = new AStar();
//    private Dijkstra dijkstra = new Dijkstra();
//
//    private DepthFirstSearch dfs = new DepthFirstSearch();
//    private BreadthFirstSearch bfs = new BreadthFirstSearch();
//
//    @BeforeEach
//    void testSetup() {
//    }
//
//    @Test
//    public void getETASingleEdgeTest(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 13, 14, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//
//        double expectedVal = 5.0 / 825.0;
//
//        assertEquals(expectedVal, aStar.getETASingleEdge(node1, node2));
//    }
//
//    @Test
//    public void getETASingleEdgeTestReverse(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 13, 14, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//
//        double expectedVal = 5.0 / 825.0;
//
//        assertEquals(expectedVal, aStar.getETASingleEdge(node2, node1));
//    }
//
//    @Test
//    public void getETASingleEdgeTestSameNode(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//
//        double expectedVal = 0.0;
//
//        assertEquals(expectedVal, aStar.getETASingleEdge(node1, node1));
//    }
//
//    @Test
//    public void getETATest(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 13, 14, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//        Node node3 = new Node("NODE3", 25, 47, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//        Node node4 = new Node("NODE4", 85, 20, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//        Node node5 = new Node("NODE5", 14, 36, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//
//        ArrayList<Node> nodeList = new ArrayList<Node>();
//        nodeList.add(node1);
//        nodeList.add(node2);
//        nodeList.add(node3);
//        nodeList.add(node4);
//        nodeList.add(node5);
//
//        double edge1Length = Math.sqrt(Math.pow(3, 2) + Math.pow(4, 2));
//        double edge2Length = Math.sqrt(Math.pow(12, 2) + Math.pow(33, 2));
//        double edge3Length = Math.sqrt(Math.pow(60, 2) + Math.pow(-27, 2));
//        double edge4Length = Math.sqrt(Math.pow(-71, 2) + Math.pow(16, 2));
//        double totalLength = edge1Length + edge2Length + edge3Length + edge4Length;
//        double expectedVal = totalLength / 825.0;
//
//        SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
//        assertEquals(expectedVal, SearchAlgorithmContext.getSearchAlgorithmContext().getETA(nodeList), 0.0001);
//        SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
//        assertEquals(expectedVal, SearchAlgorithmContext.getSearchAlgorithmContext().getETA(nodeList), 0.0001);
//        SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
//        assertEquals(expectedVal, SearchAlgorithmContext.getSearchAlgorithmContext().getETA(nodeList), 0.0001);
//        SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
//        assertEquals(expectedVal, SearchAlgorithmContext.getSearchAlgorithmContext().getETA(nodeList), 0.0001);
//    }
//
//    @Test
//    public void getETATestSameNode(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//
//        ArrayList<Node> nodeList = new ArrayList<Node>();
//        nodeList.add(node1);
//
//        double expectedVal = 0.0;
//
//        SearchAlgorithmContext.getSearchAlgorithmContext().setContext(aStar);
//        assertEquals(expectedVal, SearchAlgorithmContext.getSearchAlgorithmContext().getETA(nodeList), 0.0001);
//        SearchAlgorithmContext.getSearchAlgorithmContext().setContext(dijkstra);
//        assertEquals(expectedVal, SearchAlgorithmContext.getSearchAlgorithmContext().getETA(nodeList), 0.0001);
//        SearchAlgorithmContext.getSearchAlgorithmContext().setContext(bfs);
//        assertEquals(expectedVal, SearchAlgorithmContext.getSearchAlgorithmContext().getETA(nodeList), 0.0001);
//        SearchAlgorithmContext.getSearchAlgorithmContext().setContext(dfs);
//        assertEquals(expectedVal, SearchAlgorithmContext.getSearchAlgorithmContext().getETA(nodeList), 0.0001);
//    }
//
//    @Test
//    public void getETATestEmptyList(){
//
//        ArrayList<Node> nodeList = new ArrayList<Node>();
//
//        double expectedVal = 0.0;
//
//        SearchAlgorithmContext.getSearchAlgorithmContext().setContext(aStar);
//        assertEquals(expectedVal, SearchAlgorithmContext.getSearchAlgorithmContext().getETA(nodeList), 0.0001);
//        SearchAlgorithmContext.getSearchAlgorithmContext().setContext(dijkstra);
//        assertEquals(expectedVal, SearchAlgorithmContext.getSearchAlgorithmContext().getETA(nodeList), 0.0001);
//        SearchAlgorithmContext.getSearchAlgorithmContext().setContext(bfs);
//        assertEquals(expectedVal, SearchAlgorithmContext.getSearchAlgorithmContext().getETA(nodeList), 0.0001);
//        SearchAlgorithmContext.getSearchAlgorithmContext().setContext(dfs);
//        assertEquals(expectedVal, SearchAlgorithmContext.getSearchAlgorithmContext().getETA(nodeList), 0.0001);
//    }
//
//    @Test
//    public void testAbsAngleEdge0(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 13, 10, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//
//        double expectedVal = 0.0;
//
//        assertEquals(expectedVal, aStar.absAngleEdge(node1, node2));
//
//    }
//
//    @Test
//    public void testAbsAngleEdge90(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 10, 13, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//
//        double expectedVal = 90.0;
//
//        assertEquals(expectedVal, aStar.absAngleEdge(node1, node2));
//
//    }
//
//    @Test
//    public void testAbsAngleEdge180(){
//
//        Node node1 = new Node("NODE1", 13, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 10, 10, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//
//        double expectedVal = 180.0;
//
//        assertEquals(expectedVal, aStar.absAngleEdge(node1, node2));
//
//    }
//
//    @Test
//    public void testAbsAngleEdge270(){
//
//        Node node1 = new Node("NODE1", 10, 13, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 10, 10, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//
//        double expectedVal = -90.0;
//
//        assertEquals(expectedVal, aStar.absAngleEdge(node1, node2));
//
//    }
//
//    @Test
//    public void testAbsAngleEdgeQ1(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 12, 11, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//
//        double expectedVal = Math.atan2(1, 2) * 180 / Math.PI;
//
//        assertEquals(expectedVal, aStar.absAngleEdge(node1, node2));
//
//    }
//
//    @Test
//    public void testAbsAngleEdgeQ2(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 8, 11, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//
//        double expectedVal = Math.atan2(1, -2) * 180 / Math.PI;
//
//        assertEquals(expectedVal, aStar.absAngleEdge(node1, node2));
//
//    }
//
//    @Test
//    public void testAbsAngleEdgeQ3(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 8, 9, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//
//        double expectedVal = Math.atan2(-1, -2) * 180 / Math.PI;
//
//        assertEquals(expectedVal, aStar.absAngleEdge(node1, node2));
//
//    }
//
//    @Test
//    public void testAbsAngleEdgeQ4(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 12, 9, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//
//        double expectedVal = Math.atan2(-1, 2) * 180 / Math.PI;
//
//        assertEquals(expectedVal, aStar.absAngleEdge(node1, node2));
//
//    }
//
//    @Test
//    public void testNodeIsUniportantTrue(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 13, 14, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//        Node node3 = new Node("NODE3", 54, 17, "1", "75 Francis", "HALL", "Test Node 2", "TN2");
//        Node node4 = new Node("NODE4", 85, 20, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//        Node node5 = new Node("NODE5", 14, 36, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//
//        ArrayList<Node> nodeList = new ArrayList<Node>();
//        nodeList.add(node1);
//        nodeList.add(node2);
//        nodeList.add(node3);
//        nodeList.add(node4);
//        nodeList.add(node5);
//
//        assertTrue(dijkstra.nodeIsUnimportant(nodeList, node3));
//    }
//
//    @Test
//    public void testNodeIsUniportantFalseLastNode(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 13, 14, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//        Node node3 = new Node("NODE3", 54, 17, "1", "75 Francis", "HALL", "Test Node 2", "TN2");
//        Node node4 = new Node("NODE4", 85, 20, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//        Node node5 = new Node("NODE5", 14, 36, "1", "75 Francis", "WALK", "Test Node 2", "TN2");
//
//        ArrayList<Node> nodeList = new ArrayList<Node>();
//        nodeList.add(node1);
//        nodeList.add(node2);
//        nodeList.add(node3);
//        nodeList.add(node4);
//        nodeList.add(node5);
//
//        assertFalse(dijkstra.nodeIsUnimportant(nodeList, node5));
//    }
//
//    @Test
//    public void testNodeIsUniportantFalseNodeType(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 13, 14, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//        Node node3 = new Node("NODE3", 54, 17, "1", "75 Francis", "EXIT", "Test Node 2", "TN2");
//        Node node4 = new Node("NODE4", 85, 20, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//        Node node5 = new Node("NODE5", 14, 36, "1", "75 Francis", "WALK", "Test Node 2", "TN2");
//
//        ArrayList<Node> nodeList = new ArrayList<Node>();
//        nodeList.add(node1);
//        nodeList.add(node2);
//        nodeList.add(node3);
//        nodeList.add(node4);
//        nodeList.add(node5);
//
//        assertFalse(dijkstra.nodeIsUnimportant(nodeList, node3));
//    }
//
//    @Test
//    public void testNodeIsUniportantFalseEdgeAngle(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 13, 14, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//        Node node3 = new Node("NODE3", 54, 99, "1", "75 Francis", "HALL", "Test Node 2", "TN2");
//        Node node4 = new Node("NODE4", 85, 20, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//        Node node5 = new Node("NODE5", 14, 36, "1", "75 Francis", "WALK", "Test Node 2", "TN2");
//
//        ArrayList<Node> nodeList = new ArrayList<Node>();
//        nodeList.add(node1);
//        nodeList.add(node2);
//        nodeList.add(node3);
//        nodeList.add(node4);
//        nodeList.add(node5);
//
//        assertFalse(dijkstra.nodeIsUnimportant(nodeList, node3));
//    }
//
//    @Test
//    public void testGetTextDirectionsEmpty(){
//
//        ArrayList<Node> nodeList = new ArrayList<Node>();
//
//        List<String> expectedValue = new ArrayList<String>();
//
//        SearchAlgorithmContext.getSearchAlgorithmContext().setContext(aStar);
//        assertEquals(expectedValue, SearchAlgorithmContext.getSearchAlgorithmContext().getTextDirectionsNodes(nodeList));
//    }
//
//    @Test
//    public void testGetTextDirectionsOneNode(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//
//        ArrayList<Node> nodeList = new ArrayList<Node>();
//        nodeList.add(node1);
//
//        List<String> expectedValue = new ArrayList<String>();
//
//        SearchAlgorithmContext.getSearchAlgorithmContext().setContext(aStar);
//        assertEquals(expectedValue, SearchAlgorithmContext.getSearchAlgorithmContext().getTextDirectionsNodes(nodeList));
//    }
//
//    @Test
//    public void testGetTextDirectionsTwoNodes(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 13, 14, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//
//        ArrayList<Node> nodeList = new ArrayList<Node>();
//        nodeList.add(node1);
//        nodeList.add(node2);
//
//        List<String> expectedValue = new ArrayList<String>();
//        expectedValue.add("1. Walk 12 feet towards Test Node 2.");
//
//        SearchAlgorithmContext.getSearchAlgorithmContext().setContext(aStar);
//        assertEquals(expectedValue, SearchAlgorithmContext.getSearchAlgorithmContext().getTextDirectionsNodes(nodeList));
//    }
//
//    @Test
//    public void testGetTextDirectionsFiveNodes(){
//
//        Node node1 = new Node("NODE1", 10, 10, "1", "75 Francis", "PARK", "Test Node 1", "TN1");
//        Node node2 = new Node("NODE2", 13, 14, "1", "75 Francis", "PARK", "Test Node 2", "TN2");
//        Node node3 = new Node("NODE3", 25, 47, "1", "75 Francis", "PARK", "Test Node 3", "TN3");
//        Node node4 = new Node("NODE4", 85, 20, "1", "75 Francis", "PARK", "Test Node 4", "TN4");
//        Node node5 = new Node("NODE5", 14, 36, "1", "75 Francis", "PARK", "Test Node 5", "TN5");
//
//        ArrayList<Node> nodeList = new ArrayList<Node>();
//        nodeList.add(node1);
//        nodeList.add(node2);
//        nodeList.add(node3);
//        nodeList.add(node4);
//        nodeList.add(node5);
//
//        List<String> expectedValue = new ArrayList<String>();
//        expectedValue.add("1. Walk 12 feet towards Test Node 2.");
//        expectedValue.add("2. Make a slight left turn.");
//        expectedValue.add("3. Walk 83 feet towards Test Node 3.");
//        expectedValue.add("4. Make a right turn.");
//        expectedValue.add("5. Walk 155 feet towards Test Node 4.");
//        expectedValue.add("6. Make an extreme right turn.");
//        expectedValue.add("7. Walk 171 feet towards Test Node 5.");
//
//        SearchAlgorithmContext.getSearchAlgorithmContext().setContext(aStar);
//        assertEquals(expectedValue, SearchAlgorithmContext.getSearchAlgorithmContext().getTextDirectionsNodes(nodeList));
//    }
//
//
//}
