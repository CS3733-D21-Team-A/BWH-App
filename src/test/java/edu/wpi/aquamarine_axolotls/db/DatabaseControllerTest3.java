package edu.wpi.aquamarine_axolotls.db;

import edu.wpi.aquamarine_axolotls.TestUtil;
import org.apache.derby.iapi.services.io.FileUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

import static edu.wpi.aquamarine_axolotls.db.DatabaseInfo.TABLES.ATTRIBUTE.*;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseControllerTest3 {

    private final DatabaseController db = new DatabaseController();

    public DatabaseControllerTest3() throws SQLException, IOException, URISyntaxException {
    }

//    @BeforeEach
//    void resetDB() throws IOException, SQLException {
//        db.emptyEdgeTable();
//        db.emptyNodeTable();
//
//        CSVHandler csvHandler = new CSVHandler(db);
//        csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.nodeResourcePath), DatabaseInfo.TABLES.NODES, true);
//        csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.edgeResourcePath), DatabaseInfo.TABLES.EDGES, true);
//    }

    @AfterEach
    void closeDB() {
        try {
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @AfterAll
    @BeforeAll
    static void cleanup() {
        TestUtil.resetDB();
    }

    // Tests for iteration 1

    // Try to write tests for every functionally unique case of a method.
    // Test for alternate versions of the same case to make sure your tests don't pass as flukes.
    // Each method must have an absolute minimum of two tests, should have more.
    // If you're unsure if a method is sufficiently tested, run the tests with code coverage to see if you're missing any cases.

    // Reminders:
    //  - You can use assertThrows to test expected errors.
    //  - If you're testing a state change, ensure the state before AND after the state change.
    //  - Be careful using @BeforeEach, @AfterEach, @AfterALl, or @BeforeAll. These annotations will affect everyone's tests. If you need them, communicate with the team or make a new test file.


    // N'yoma testing CSV Handler:



    // CJ and Sean testing service requests:



    // Chris and Zhongchuan testing attributes:

    // has attribute

    @Test
    public void testHasAttributeNodeFalse(){
        try {
            assertFalse(db.hasAttribute("aPARK020GG", COVID_SAFE, true));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testHasAttributeEdgeFalse(){
        try {
            assertFalse(db.hasAttribute("aWALK002GG_aWALK003GG", COVID_SAFE, false));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    // add attribute

    @Test
    public void testAddAttributeNode(){
        try {
            assertTrue(db.addAttribute("aPARK020GG", HANDICAPPED_ACCESSIBLE, true));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddAttributeEdge(){
        try {
            assertTrue(db.addAttribute("aWALK002GG_aWALK003GG", HANDICAPPED_ACCESSIBLE, false));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddAttributeNodeDNE(){
        assertThrows(SQLException.class, () -> {
            db.addAttribute("aFakeNode", HANDICAPPED_ACCESSIBLE, true);
        });
    }

    @Test
    public void testAddAttributeEdgeDNE(){
        assertThrows(SQLException.class, () -> {
            db.addAttribute("aFakeEdge", HANDICAPPED_ACCESSIBLE, false);
        });
    }

    // has attribute cont'd

    @Test
    public void testHasAttributeNodeTrue(){
        try {
            db.addAttribute("aPARK020GG", COVID_SAFE, true);
            assertTrue(db.hasAttribute("aPARK020GG", COVID_SAFE, true));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testHasAttributeEdgeTrue(){
        try {
            db.addAttribute("aWALK002GG_aWALK003GG", COVID_SAFE, false);
            assertTrue(db.hasAttribute("aWALK002GG_aWALK003GG", COVID_SAFE, false));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testHasAttributeNodeFalse2(){
        try {
            db.addAttribute("aPARK020GG", COVID_SAFE, true);
            assertFalse(db.hasAttribute("aPARK050GG", COVID_SAFE, true));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testHasAttributeEdgeFalse2(){
        try {
            db.addAttribute("aWALK002GG_aWALK003GG", COVID_SAFE, false);
            assertFalse(db.hasAttribute("aWALK003GG_aWALK004GG", COVID_SAFE, false));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    // add attribute cont'd

    @Test
    public void testAddAttributeNode2(){
        try {
            assertFalse(db.hasAttribute("aPARK020GG", NOT_NAVIGABLE, true));
            assertTrue(db.addAttribute("aPARK020GG", NOT_NAVIGABLE, true));
            assertTrue(db.hasAttribute("aPARK020GG", NOT_NAVIGABLE, true));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddAttributeEdge2(){
        try {
            assertFalse(db.hasAttribute("aWALK002GG_aWALK003GG", NOT_NAVIGABLE, false));
            assertTrue(db.addAttribute("aWALK002GG_aWALK003GG", NOT_NAVIGABLE, false));
            assertTrue(db.hasAttribute("aWALK002GG_aWALK003GG", NOT_NAVIGABLE, false));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    // get attributes

    @Test
    public void testGetAttributesNode(){
        try {
            List<DatabaseInfo.TABLES.ATTRIBUTE> expectedList = new ArrayList<DatabaseInfo.TABLES.ATTRIBUTE>();
            List<DatabaseInfo.TABLES.ATTRIBUTE> actualList = db.getAttributes("aWALK002GG",true);
            assertEquals(expectedList, actualList);

            assertTrue(db.addAttribute("aWALK002GG", NOT_NAVIGABLE, true));
            assertTrue(db.addAttribute("aWALK002GG", COVID_SAFE, true));

            expectedList.add(NOT_NAVIGABLE);
            expectedList.add(COVID_SAFE);

            actualList = db.getAttributes("aWALK002GG",true);

            assertEquals(expectedList, actualList);

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetAttributesEdge(){
        try {
            List<DatabaseInfo.TABLES.ATTRIBUTE> expectedList = new ArrayList<DatabaseInfo.TABLES.ATTRIBUTE>();
            List<DatabaseInfo.TABLES.ATTRIBUTE> actualList = db.getAttributes("aPARK001GG_aWALK001GG",false);
            assertEquals(expectedList, actualList);

            assertTrue(db.addAttribute("aPARK001GG_aWALK001GG", HANDICAPPED_ACCESSIBLE, false));
            assertTrue(db.addAttribute("aPARK001GG_aWALK001GG", COVID_SAFE, false));

            expectedList.add(HANDICAPPED_ACCESSIBLE);
            expectedList.add(COVID_SAFE);

            actualList = db.getAttributes("aPARK001GG_aWALK001GG",false);

            assertEquals(expectedList, actualList);

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetAttributesNodeDNE(){
        try {
            List<DatabaseInfo.TABLES.ATTRIBUTE> expectedList = new ArrayList<DatabaseInfo.TABLES.ATTRIBUTE>();
            List<DatabaseInfo.TABLES.ATTRIBUTE> actualList = db.getAttributes("aFakeNode2",true);
            assertEquals(expectedList, actualList);
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetAttributesEdgeDNE(){
        try {
            List<DatabaseInfo.TABLES.ATTRIBUTE> expectedList = new ArrayList<DatabaseInfo.TABLES.ATTRIBUTE>();
            List<DatabaseInfo.TABLES.ATTRIBUTE> actualList = db.getAttributes("aFakeEdge2",false);
            assertEquals(expectedList, actualList);
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }
    //getByAttribute

    @Test
    public void testGetByAttributeOneNode(){
        try{
            assertFalse(db.hasAttribute("aPARK019GG", COVID_SAFE, true));
            assertTrue(db.addAttribute("aPARK019GG", COVID_SAFE, true));
            assertTrue(db.hasAttribute("aPARK019GG", COVID_SAFE, true));

            List<String> expectedList = new ArrayList<>();
            List<String> actualList = db.getByAttribute(COVID_SAFE,true);

            expectedList.add("aPARK019GG");

            assertEquals(expectedList, actualList);
        } catch(SQLException e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetByAttributeTwoNode(){
        try{
            assertFalse(db.hasAttribute("aPARK019GG", COVID_SAFE, true));
            assertTrue(db.addAttribute("aPARK019GG", COVID_SAFE, true));
            assertTrue(db.hasAttribute("aPARK019GG", COVID_SAFE, true));

            assertTrue(db.addAttribute("aPARK020GG", COVID_SAFE, true));
            assertTrue(db.hasAttribute("aPARK020GG", COVID_SAFE, true));

            List<String> expectedList = new ArrayList<>();
            List<String> actualList = db.getByAttribute(COVID_SAFE,true);

            expectedList.add("aPARK019GG");
            expectedList.add("aPARK020GG");

            assertEquals(expectedList, actualList);
        } catch(SQLException e){
            e.printStackTrace();
            fail();
        }
    }


    // delete attribute

    @Test
    public void testDeleteAttributesNode(){
        try {
            assertFalse(db.hasAttribute("aPARK019GG", COVID_SAFE, true));
            assertTrue(db.addAttribute("aPARK019GG", COVID_SAFE, true));
            assertTrue(db.hasAttribute("aPARK019GG", COVID_SAFE, true));
            db.deleteAttribute("aPARK019GG", COVID_SAFE, true);
            assertFalse(db.hasAttribute("aPARK019GG", COVID_SAFE, true));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDeleteAttributesEdge(){
        try {
            assertFalse(db.hasAttribute("aWALK008GG_aWALK009GG", NOT_NAVIGABLE, false));
            assertTrue(db.addAttribute("aWALK008GG_aWALK009GG", NOT_NAVIGABLE, false));
            assertTrue(db.hasAttribute("aWALK008GG_aWALK009GG", NOT_NAVIGABLE, false));
            db.deleteAttribute("aWALK008GG_aWALK009GG", NOT_NAVIGABLE, false);
            assertFalse(db.hasAttribute("aWALK008GG_aWALK009GG", NOT_NAVIGABLE, false));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDeleteAttributesNodeDNE(){
        try{
            List<DatabaseInfo.TABLES.ATTRIBUTE> expectedList = new ArrayList<DatabaseInfo.TABLES.ATTRIBUTE>();
            List<DatabaseInfo.TABLES.ATTRIBUTE> actualList = db.getAttributes("aFakeNode3",true);
            assertEquals(expectedList, actualList);

            db.deleteAttribute("aFakeNode3", NOT_NAVIGABLE, true);

            actualList = db.getAttributes("aFakeNode3",true);
            assertEquals(expectedList, actualList);
        } catch(SQLException e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDeleteAttributesEdgeDNE(){
        try{
            List<DatabaseInfo.TABLES.ATTRIBUTE> expectedList = new ArrayList<DatabaseInfo.TABLES.ATTRIBUTE>();
            List<DatabaseInfo.TABLES.ATTRIBUTE> actualList = db.getAttributes("aFakeEdge3",false);
            assertEquals(expectedList, actualList);

            db.deleteAttribute("aFakeEdge3", NOT_NAVIGABLE, false);

            actualList = db.getAttributes("aFakeEdge3",false);
            assertEquals(expectedList, actualList);
        } catch(SQLException e){
            e.printStackTrace();
            fail();
        }
    }

    // clear attributes

    @Test
    public void testClearAttributesNode(){
        try {
            List<DatabaseInfo.TABLES.ATTRIBUTE> expectedList = new ArrayList<DatabaseInfo.TABLES.ATTRIBUTE>();
            List<DatabaseInfo.TABLES.ATTRIBUTE> actualList = db.getAttributes("aWALK008GG",true);
            assertEquals(expectedList, actualList);

            assertTrue(db.addAttribute("aWALK008GG", COVID_SAFE, true));
            assertTrue(db.addAttribute("aWALK008GG", NOT_NAVIGABLE, true));
            assertTrue(db.addAttribute("aWALK008GG", HANDICAPPED_ACCESSIBLE, true));

            expectedList.add(COVID_SAFE);
            expectedList.add(NOT_NAVIGABLE);
            expectedList.add(HANDICAPPED_ACCESSIBLE);

            actualList = db.getAttributes("aWALK008GG",true);

            assertEquals(expectedList, actualList);

            db.clearAttributes("aWALK008GG", true);

            expectedList = new ArrayList<DatabaseInfo.TABLES.ATTRIBUTE>();
            actualList = db.getAttributes("aWALK008GG",true);
            assertEquals(expectedList, actualList);
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testClearAttributesEdge(){
        try {
            List<DatabaseInfo.TABLES.ATTRIBUTE> expectedList = new ArrayList<DatabaseInfo.TABLES.ATTRIBUTE>();
            List<DatabaseInfo.TABLES.ATTRIBUTE> actualList = db.getAttributes("aPARK020GG_aWALK009GG",false);
            assertEquals(expectedList, actualList);

            assertTrue(db.addAttribute("aPARK020GG_aWALK009GG", COVID_SAFE, false));
            assertTrue(db.addAttribute("aPARK020GG_aWALK009GG", NOT_NAVIGABLE, false));
            assertTrue(db.addAttribute("aPARK020GG_aWALK009GG", HANDICAPPED_ACCESSIBLE, false));

            expectedList.add(COVID_SAFE);
            expectedList.add(NOT_NAVIGABLE);
            expectedList.add(HANDICAPPED_ACCESSIBLE);

            actualList = db.getAttributes("aPARK020GG_aWALK009GG",false);

            assertEquals(expectedList, actualList);

            db.clearAttributes("aPARK020GG_aWALK009GG", false);

            expectedList = new ArrayList<DatabaseInfo.TABLES.ATTRIBUTE>();
            actualList = db.getAttributes("aPARK020GG_aWALK009GG",false);
            assertEquals(expectedList, actualList);
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testClearAttributesNodeDNE(){
        try{
            List<DatabaseInfo.TABLES.ATTRIBUTE> expectedList = new ArrayList<DatabaseInfo.TABLES.ATTRIBUTE>();
            List<DatabaseInfo.TABLES.ATTRIBUTE> actualList = db.getAttributes("aFakeNode4",true);
            assertEquals(expectedList, actualList);

            db.clearAttributes("aFakeNode4", true);

            actualList = db.getAttributes("aFakeNode4",true);
            assertEquals(expectedList, actualList);
        } catch(SQLException e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testClearAttributesEdgeDNE(){
        try{
            List<DatabaseInfo.TABLES.ATTRIBUTE> expectedList = new ArrayList<DatabaseInfo.TABLES.ATTRIBUTE>();
            List<DatabaseInfo.TABLES.ATTRIBUTE> actualList = db.getAttributes("aFakeEdge4",false);
            assertEquals(expectedList, actualList);

            db.clearAttributes("aFakeEdge4", false);

            actualList = db.getAttributes("aFakeEdge4",false);
            assertEquals(expectedList, actualList);
        } catch(SQLException e){
            e.printStackTrace();
            fail();
        }
    }



    // Emily testing getNodes and getEdges:
}
