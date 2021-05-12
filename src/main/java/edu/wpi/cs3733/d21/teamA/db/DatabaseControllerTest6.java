package edu.wpi.cs3733.d21.teamA.db;

import edu.wpi.cs3733.d21.teamA.db.enums.TABLES;
import edu.wpi.cs3733.d21.teamA.extras.Security;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseControllerTest6 {

    private final DatabaseController db = DatabaseController.getInstance();

    public DatabaseControllerTest6() throws SQLException, IOException, URISyntaxException {
    }

    @BeforeEach
    void resetDB() throws IOException, SQLException {
        db.emptyEdgeTable();
        db.emptyNodeTable();
        db.emptyServiceRequestsTable();
        db.emptyUserTable();
        db.emptyFavoriteNodesTable();

        CSVHandler csvHandler = new CSVHandler(db);
        csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.TEST_NODE_RESOURCE_PATH), TABLES.NODES, true);
        csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.TEST_EDGE_RESOURCE_PATH), TABLES.EDGES, true);
    }

    @AfterAll
    @BeforeAll
    static void cleanup() {
        DBTestUtil.resetDB();
    }

    @Test
    public void testAddFavoriteNode()
    {
        try{
            Map<String, String> user3 = new HashMap<String, String>();
            user3.put("USERNAME", "Alexa123");
            user3.put("FIRSTNAME", "Alexa");
            user3.put("LASTNAME", "Freglette");
            user3.put("EMAIL", "alexa@gmail.com");
            user3.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            Security.addHashedPassword(user3, "fjdasklgjlskf");
            db.addUser(user3);
            String generatedFAVID = db.addFavoriteNodeToUser("Alexa123", "aPARK001GG", "Favorite Bathroom");
            assertEquals(db.getFAVID("Alexa123", "Favorite Bathroom"),generatedFAVID);
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetFavoriteNode()
    {
        try{
            Map<String, String> user3 = new HashMap<String, String>();
            user3.put("USERNAME", "Alexa123");
            user3.put("FIRSTNAME", "Alexa");
            user3.put("LASTNAME", "Freglette");
            user3.put("EMAIL", "alexa@gmail.com");
            user3.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            Security.addHashedPassword(user3, "fjdasklgjlskf");
            db.addUser(user3);

            String generatedFAVID = db.addFavoriteNodeToUser("Alexa123", "aPARK001GG", "Favorite Bathroom");
            Map<String,String> newFav = new HashMap<>();
            newFav.put("USERID", "Alexa123");
            newFav.put("LOCATIONID", "aPARK001GG");
            newFav.put("FAVID", generatedFAVID);
            newFav.put("NODENAME", "Favorite Bathroom");

            List<Map<String,String>> newFavList = new ArrayList<>();
            newFavList.add(newFav);

            assertEquals(db.getFavoriteNodeByUserAndName("Alexa123", "Favorite Bathroom"),newFav);
            assertEquals(db.getFavoriteNodesForUser("Alexa123"),newFavList);
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetFavoriteNodeMultFaves()
    {
        try{
            Map<String, String> user3 = new HashMap<String, String>();
            user3.put("USERNAME", "Alexa123");
            user3.put("FIRSTNAME", "Alexa");
            user3.put("LASTNAME", "Freglette");
            user3.put("EMAIL", "alexa@gmail.com");
            user3.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            Security.addHashedPassword(user3, "fjdasklgjlskf");
            db.addUser(user3);

            String generatedFAVID = db.addFavoriteNodeToUser("Alexa123", "aPARK001GG", "Favorite Bathroom");
            Map<String,String> newFav = new HashMap<>();
            newFav.put("USERID", "Alexa123");
            newFav.put("LOCATIONID", "aPARK001GG");
            newFav.put("FAVID", generatedFAVID);
            newFav.put("NODENAME", "Favorite Bathroom");

            String generatedFAVID2 = db.addFavoriteNodeToUser("Alexa123", "aPARK002GG", "Parking Spot");
            Map<String,String> newFav2 = new HashMap<>();
            newFav2.put("USERID", "Alexa123");
            newFav2.put("LOCATIONID", "aPARK002GG");
            newFav2.put("FAVID", generatedFAVID2);
            newFav2.put("NODENAME", "Parking Spot");

            List<Map<String,String>> newFavList = new ArrayList<>();
            newFavList.add(newFav);
            newFavList.add(newFav2);

            assertEquals(db.getFavoriteNodeByUserAndName("Alexa123", "Favorite Bathroom"),newFav);
            assertEquals(db.getFavoriteNodeByUserAndName("Alexa123", "Parking Spot"),newFav2);
            assertEquals(db.getFavoriteNodesForUser("Alexa123"),newFavList);
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetFavoriteNodeMultUsersMultFaves()
    {
        try{
            Map<String, String> user3 = new HashMap<String, String>();
            user3.put("USERNAME", "Alexa123");
            user3.put("FIRSTNAME", "Alexa");
            user3.put("LASTNAME", "Freglette");
            user3.put("EMAIL", "alexa@gmail.com");
            user3.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            Security.addHashedPassword(user3, "fjdasklgjlskf");
            db.addUser(user3);

            Map<String, String> user1 = new HashMap<String, String>();
            user1.put("USERNAME", "Seansta18");
            user1.put("FIRSTNAME", "Sean");
            user1.put("LASTNAME", "McMillan");
            user1.put("EMAIL", "Sean@gmail.com");
            user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            Security.addHashedPassword(user1, "fjdasklgjlskf");

            db.addUser(user1);

            String generatedFAVID = db.addFavoriteNodeToUser("Alexa123", "aPARK001GG", "Favorite Bathroom");
            Map<String,String> newFav = new HashMap<>();
            newFav.put("USERID", "Alexa123");
            newFav.put("LOCATIONID", "aPARK001GG");
            newFav.put("FAVID", generatedFAVID);
            newFav.put("NODENAME", "Favorite Bathroom");

            String generatedFAVID2 = db.addFavoriteNodeToUser("Alexa123", "aPARK002GG", "Parking Spot");
            Map<String,String> newFav2 = new HashMap<>();
            newFav2.put("USERID", "Alexa123");
            newFav2.put("LOCATIONID", "aPARK002GG");
            newFav2.put("FAVID", generatedFAVID2);
            newFav2.put("NODENAME", "Parking Spot");

            String generatedFAVID3 = db.addFavoriteNodeToUser("Seansta18", "aPARK001GG", "Favorite Bathroom");
            Map<String,String> newFav3 = new HashMap<>();
            newFav3.put("USERID", "Seansta18");
            newFav3.put("LOCATIONID", "aPARK001GG");
            newFav3.put("FAVID", generatedFAVID3);
            newFav3.put("NODENAME", "Favorite Bathroom");

            String generatedFAVID4 = db.addFavoriteNodeToUser("Seansta18", "aPARK002GG", "Parking Spot");
            Map<String,String> newFav4 = new HashMap<>();
            newFav4.put("USERID", "Seansta18");
            newFav4.put("LOCATIONID", "aPARK002GG");
            newFav4.put("FAVID", generatedFAVID4);
            newFav4.put("NODENAME", "Parking Spot");

            List<Map<String,String>> newFavList = new ArrayList<>();
            newFavList.add(newFav);
            newFavList.add(newFav2);
            List<Map<String,String>> newFavList2 = new ArrayList<>();
            newFavList2.add(newFav3);
            newFavList2.add(newFav4);

            assertEquals(db.getFavoriteNodeByUserAndName("Alexa123", "Favorite Bathroom"),newFav);
            assertEquals(db.getFavoriteNodeByUserAndName("Alexa123", "Parking Spot"),newFav2);
            assertEquals(db.getFavoriteNodesForUser("Alexa123"),newFavList);
            assertEquals(db.getFavoriteNodeByUserAndName("Seansta18", "Favorite Bathroom"),newFav3);
            assertEquals(db.getFavoriteNodeByUserAndName("Seansta18", "Parking Spot"),newFav4);
            assertEquals(db.getFavoriteNodesForUser("Seansta18"),newFavList2);
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDeleteFavNodeFromUser()
    {
        try{
            Map<String, String> user3 = new HashMap<String, String>();
            user3.put("USERNAME", "Alexa123");
            user3.put("FIRSTNAME", "Alexa");
            user3.put("LASTNAME", "Freglette");
            user3.put("EMAIL", "alexa@gmail.com");
            user3.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            Security.addHashedPassword(user3, "fjdasklgjlskf");
            db.addUser(user3);

            Map<String, String> user1 = new HashMap<String, String>();
            user1.put("USERNAME", "Seansta18");
            user1.put("FIRSTNAME", "Sean");
            user1.put("LASTNAME", "McMillan");
            user1.put("EMAIL", "Sean@gmail.com");
            user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            Security.addHashedPassword(user1, "fjdasklgjlskf");

            db.addUser(user1);

            String generatedFAVID = db.addFavoriteNodeToUser("Alexa123", "aPARK001GG", "Favorite Bathroom");
            Map<String,String> newFav = new HashMap<>();
            newFav.put("USERID", "Alexa123");
            newFav.put("LOCATIONID", "aPARK001GG");
            newFav.put("FAVID", generatedFAVID);
            newFav.put("NODENAME", "Favorite Bathroom");

            String generatedFAVID2 = db.addFavoriteNodeToUser("Alexa123", "aPARK002GG", "Parking Spot");
            Map<String,String> newFav2 = new HashMap<>();
            newFav2.put("USERID", "Alexa123");
            newFav2.put("LOCATIONID", "aPARK002GG");
            newFav2.put("FAVID", generatedFAVID2);
            newFav2.put("NODENAME", "Parking Spot");

            String generatedFAVID3 = db.addFavoriteNodeToUser("Seansta18", "aPARK001GG", "Favorite Bathroom");
            Map<String,String> newFav3 = new HashMap<>();
            newFav3.put("USERID", "Seansta18");
            newFav3.put("LOCATIONID", "aPARK001GG");
            newFav3.put("FAVID", generatedFAVID3);
            newFav3.put("NODENAME", "Favorite Bathroom");

            String generatedFAVID4 = db.addFavoriteNodeToUser("Seansta18", "aPARK002GG", "Parking Spot");
            Map<String,String> newFav4 = new HashMap<>();
            newFav4.put("USERID", "Seansta18");
            newFav4.put("LOCATIONID", "aPARK002GG");
            newFav4.put("FAVID", generatedFAVID4);
            newFav4.put("NODENAME", "Parking Spot");

            List<Map<String,String>> newFavList = new ArrayList<>();
            newFavList.add(newFav);
            List<Map<String,String>> newFavList2 = new ArrayList<>();
            newFavList2.add(newFav3);
            newFavList2.add(newFav4);


            db.deleteFavoriteNodeFromUser("Alexa123","Parking Spot");
            assertEquals(newFav,db.getFavoriteNodeByUserAndName("Alexa123","Favorite Bathroom"));
            assertEquals(db.getFavoriteNodesForUser("Alexa123"),newFavList);
            assertEquals(db.getFavoriteNodeByUserAndName("Seansta18", "Favorite Bathroom"),newFav3);
            assertEquals(db.getFavoriteNodeByUserAndName("Seansta18", "Parking Spot"),newFav4);
            assertEquals(db.getFavoriteNodesForUser("Seansta18"),newFavList2);
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetNodeFromFAVID()
    {
        try{
            Map<String, String> user3 = new HashMap<String, String>();
            user3.put("USERNAME", "Alexa123");
            user3.put("FIRSTNAME", "Alexa");
            user3.put("LASTNAME", "Freglette");
            user3.put("EMAIL", "alexa@gmail.com");
            user3.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            Security.addHashedPassword(user3, "fjdasklgjlskf");
            db.addUser(user3);

            Map<String, String> user1 = new HashMap<String, String>();
            user1.put("USERNAME", "Seansta18");
            user1.put("FIRSTNAME", "Sean");
            user1.put("LASTNAME", "McMillan");
            user1.put("EMAIL", "Sean@gmail.com");
            user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            Security.addHashedPassword(user1, "fjdasklgjlskf");

            db.addUser(user1);

            String generatedFAVID = db.addFavoriteNodeToUser("Alexa123", "aPARK001GG", "Favorite Bathroom");
            Map<String,String> newFav = new HashMap<>();
            newFav.put("USERID", "Alexa123");
            newFav.put("LOCATIONID", "aPARK001GG");
            newFav.put("FAVID", generatedFAVID);
            newFav.put("NODENAME", "Favorite Bathroom");

            String generatedFAVID2 = db.addFavoriteNodeToUser("Alexa123", "aPARK002GG", "Parking Spot");
            Map<String,String> newFav2 = new HashMap<>();
            newFav2.put("USERID", "Alexa123");
            newFav2.put("LOCATIONID", "aPARK002GG");
            newFav2.put("FAVID", generatedFAVID2);
            newFav2.put("NODENAME", "Parking Spot");

            String generatedFAVID3 = db.addFavoriteNodeToUser("Seansta18", "aPARK001GG", "Favorite Bathroom");
            Map<String,String> newFav3 = new HashMap<>();
            newFav3.put("USERID", "Seansta18");
            newFav3.put("LOCATIONID", "aPARK001GG");
            newFav3.put("FAVID", generatedFAVID3);
            newFav3.put("NODENAME", "Favorite Bathroom");

            String generatedFAVID4 = db.addFavoriteNodeToUser("Seansta18", "aPARK002GG", "Parking Spot");
            Map<String,String> newFav4 = new HashMap<>();
            newFav4.put("USERID", "Seansta18");
            newFav4.put("LOCATIONID", "aPARK002GG");
            newFav4.put("FAVID", generatedFAVID4);
            newFav4.put("NODENAME", "Parking Spot");

            assertEquals(newFav,db.getFavoriteNodeByFAVID(generatedFAVID));
            assertEquals(newFav2,db.getFavoriteNodeByFAVID(generatedFAVID2));
            assertEquals(newFav3,db.getFavoriteNodeByFAVID(generatedFAVID3));
            assertEquals(newFav4,db.getFavoriteNodeByFAVID(generatedFAVID4));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }
}