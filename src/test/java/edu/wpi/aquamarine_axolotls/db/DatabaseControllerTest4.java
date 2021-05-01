package edu.wpi.aquamarine_axolotls.db;

import edu.wpi.aquamarine_axolotls.TestUtil;
import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static edu.wpi.aquamarine_axolotls.db.DatabaseUtil.*;

public class DatabaseControllerTest4 {

    private final DatabaseController db = DatabaseController.getInstance();

    public DatabaseControllerTest4() throws SQLException, IOException, URISyntaxException {
    }

    @BeforeEach
    void resetDB() throws IOException, SQLException {
        db.emptyEdgeTable();
        db.emptyNodeTable();
        db.emptyServiceRequestsTable();
        db.emptyUserTable();

        CSVHandler csvHandler = new CSVHandler(db);
        csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.TEST_NODE_RESOURCE_PATH), TABLES.NODES, true);
        csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.TEST_EDGE_RESOURCE_PATH), TABLES.EDGES, true);
    }

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
/*
    @Test
    public void testAddServiceRequestEXTERNAL_TRANSPORTATION(){
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
            Map<String, String> sharedValues = new HashMap<String, String>();
            sharedValues.put("REQUESTID", "34567");
            sharedValues.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues.put("LOCATIONID", "aPARK009GG");
            sharedValues.put("FIRSTNAME", "Bob");
            sharedValues.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.EXTERNAL_TRANSPORT));

            Map<String, String> requestValues = new HashMap<String, String>();
            requestValues.put("REQUESTID", "34567");
            requestValues.put("NEWLOCATION", "WHITE HOUSE");
            db.addServiceRequest(sharedValues, requestValues);

            sharedValues.put("EMPLOYEEID", null);
            sharedValues.put("LASTNAME", null);

            List<Map<String, String>> expectedResult = new ArrayList<Map<String, String>>();
            expectedResult.add(sharedValues);

            assertEquals(expectedResult, db.getServiceRequests());

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }
*/

    //CJ
    @Test
    public void testAddUser()
    {
        try{
            Map<String, String> user1 = new HashMap<String, String>();
            user1.put("USERNAME", "Seansta18");
            user1.put("FIRSTNAME", "Sean");
            user1.put("LASTNAME", "McMillan");
            user1.put("EMAIL", "Sean@gmail.com");
            user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user1.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user1);

            Map<String, String> user2 = new HashMap<String, String>();
            user2.put("USERNAME", "Eakelley");
            user2.put("FIRSTNAME", "Emily");
            user2.put("LASTNAME", "Kelley");
            user2.put("EMAIL", "emily@gmail.com");
            user2.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user2.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user2);

            Map<String, String> user3 = new HashMap<String, String>();
            user3.put("USERNAME", "Alexa123");
            user3.put("FIRSTNAME", "Alexa");
            user3.put("LASTNAME", "Freglette");
            user3.put("EMAIL", "alexa@gmail.com");
            user3.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user3.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user3);

            assertEquals(user1, db.getUserByUsername("Seansta18"));
            assertEquals(user2, db.getUserByUsername("Eakelley"));
            assertEquals(user3, db.getUserByUsername("Alexa123"));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddUserThatUsernameExists()
    {
        try {
            Map<String, String> user1 = new HashMap<String, String>();
            user1.put("USERNAME", "Seansta18");
            user1.put("FIRSTNAME", "Sean");
            user1.put("LASTNAME", "McMillan");
            user1.put("EMAIL", "Sean@gmail.com");
            user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user1.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user1);
            assertThrows(SQLException.class, () -> {
                db.addUser(user1);
            });
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddUserThatEmailExists()
    {
        try {
            List<Map<String,String>> testBoi = new ArrayList<>();
            Map<String, String> user1 = new HashMap<String, String>();
            user1.put("USERNAME", "Seansta18");
            user1.put("FIRSTNAME", "Sean");
            user1.put("LASTNAME", "McMillan");
            user1.put("EMAIL", "Sean@gmail.com");
            user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user1.put("PASSWORD", "PasswordIsMyPassword");

            Map<String, String> user2 = new HashMap<String, String>();
            user2.put("USERNAME", "BananasTho");
            user2.put("FIRSTNAME", "Craig");
            user2.put("LASTNAME", "CJ");
            user2.put("EMAIL", "Sean@gmail.com");
            user2.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user2.put("PASSWORD", "YourPassword");

            db.addUser(user1);
            assertThrows(SQLException.class, () -> {
                db.addUser(user2);
            });
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testEditUser()
    {
        try {
            Map<String, String> user1 = new HashMap<String, String>();
            user1.put("USERNAME", "Seansta18");
            user1.put("FIRSTNAME", "Sean");
            user1.put("LASTNAME", "McMillan");
            user1.put("EMAIL", "Sean@gmail.com");
            user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user1.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user1);
            user1.replace("LASTNAME", "DeMaio");
            db.editUser("Seansta18", user1);

            assertEquals(user1, db.getUserByUsername("Seansta18"));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testEditUserThatDNE()
    {
        try {
            Map<String, String> user1 = new HashMap<String, String>();
            user1.put("USERNAME", "Seansta18");
            user1.put("FIRSTNAME", "Sean");
            user1.put("LASTNAME", "McMillan");
            user1.put("EMAIL", "Sean@gmail.com");
            user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user1.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user1);
            user1.replace("LASTNAME", "DeMaio");
            db.editUser("Seansta18", user1);

            assertThrows(SQLException.class, () -> {
                db.editUser("Chistopher", user1);
            });
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDeleteUser()
    {
        try {
            Map<String, String> user1 = new HashMap<String, String>();
            user1.put("USERNAME", "Seansta18");
            user1.put("FIRSTNAME", "Sean");
            user1.put("LASTNAME", "McMillan");
            user1.put("EMAIL", "Sean@gmail.com");
            user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user1.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user1);
            assertEquals(user1, db.getUserByUsername("Seansta18"));

            db.deleteUser("Seansta18");
            assertEquals(null, db.getUserByUsername("Seansta18"));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDeleteUserThatDNE() throws SQLException
    {

            Map<String, String> user1 = new HashMap<String, String>();
            user1.put("USERNAME", "Seansta18");
            user1.put("FIRSTNAME", "Sean");
            user1.put("LASTNAME", "McMillan");
            user1.put("EMAIL", "Sean@gmail.com");
            user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user1.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user1);
            assertEquals(user1, db.getUserByUsername("Seansta18"));
            db.deleteUser("Seansta18");
        assertThrows(SQLException.class, () -> {
            db.deleteUser("Seansta18");
        });
    }


    //Emily

    // Testing List<Map<String, String>> getUsers() throws SQLException
    @Test
    public void testGetUsers(){
        try{
            List<Map<String,String>> testBoi = new ArrayList<>();
            Map<String, String> user1 = new HashMap<String, String>();
            user1.put("USERNAME", "Seansta18");
            user1.put("FIRSTNAME", "Sean");
            user1.put("LASTNAME", "McMillan");
            user1.put("EMAIL", "Sean@gmail.com");
            user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user1.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user1);
            testBoi.add(user1);

            Map<String, String> user2 = new HashMap<String, String>();
            user2.put("USERNAME", "Eakelley");
            user2.put("FIRSTNAME", "Emily");
            user2.put("LASTNAME", "Kelley");
            user2.put("EMAIL", "emily@gmail.com");
            user2.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user2.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user2);
            testBoi.add(user2);

            Map<String, String> user3 = new HashMap<String, String>();
            user3.put("USERNAME", "Alexa123");
            user3.put("FIRSTNAME", "Alexa");
            user3.put("LASTNAME", "Freglette");
            user3.put("EMAIL", "alexa@gmail.com");
            user3.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user3.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user3);
            testBoi.add(user3);

            assertEquals(testBoi, db.getUsers());
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    // Testing public boolean checkUserExists(String username)
    @Test
    public void testCheckUserExistsExistingUsername(){
        try{
            Map<String, String> user1 = new HashMap<String, String>();
            user1.put("USERNAME", "Seansta18");
            user1.put("FIRSTNAME", "Sean");
            user1.put("LASTNAME", "McMillan");
            user1.put("EMAIL", "Sean@gmail.com");
            user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user1.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user1);

            Map<String, String> user2 = new HashMap<String, String>();
            user2.put("USERNAME", "Eakelley");
            user2.put("FIRSTNAME", "Emily");
            user2.put("LASTNAME", "Kelley");
            user2.put("EMAIL", "emily@gmail.com");
            user2.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user2.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user2);

            Map<String, String> user3 = new HashMap<String, String>();
            user3.put("USERNAME", "Alexa123");
            user3.put("FIRSTNAME", "Alexa");
            user3.put("LASTNAME", "Freglette");
            user3.put("EMAIL", "alexa@gmail.com");
            user3.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user3.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user3);

            assertTrue(db.checkUserExists("Alexa123"));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testCheckUserExistsUsernameDNE(){
        try{
            Map<String, String> user1 = new HashMap<String, String>();
            user1.put("USERNAME", "Seansta18");
            user1.put("FIRSTNAME", "Sean");
            user1.put("LASTNAME", "McMillan");
            user1.put("EMAIL", "Sean@gmail.com");
            user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user1.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user1);

            Map<String, String> user2 = new HashMap<String, String>();
            user2.put("USERNAME", "Eakelley");
            user2.put("FIRSTNAME", "Emily");
            user2.put("LASTNAME", "Kelley");
            user2.put("EMAIL", "emily@gmail.com");
            user2.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user2.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user2);

            Map<String, String> user3 = new HashMap<String, String>();
            user3.put("USERNAME", "Alexa123");
            user3.put("FIRSTNAME", "Alexa");
            user3.put("LASTNAME", "Freglette");
            user3.put("EMAIL", "alexa@gmail.com");
            user3.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user3.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user3);

            assertFalse(db.checkUserExists("Bumblebee"));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    // Testing checkUserMatchesPass();
    @Test
    public void testCheckUserMatchesPass(){
        try{
            Map<String, String> user1 = new HashMap<String, String>();
            user1.put("USERNAME", "Seansta18");
            user1.put("FIRSTNAME", "Sean");
            user1.put("LASTNAME", "McMillan");
            user1.put("EMAIL", "Sean@gmail.com");
            user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user1.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user1);

            Map<String, String> user2 = new HashMap<String, String>();
            user2.put("USERNAME", "Eakelley");
            user2.put("FIRSTNAME", "Emily");
            user2.put("LASTNAME", "Kelley");
            user2.put("EMAIL", "emily@gmail.com");
            user2.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user2.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user2);

            Map<String, String> user3 = new HashMap<String, String>();
            user3.put("USERNAME", "Alexa123");
            user3.put("FIRSTNAME", "Alexa");
            user3.put("LASTNAME", "Freglette");
            user3.put("EMAIL", "alexa@gmail.com");
            user3.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user3.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user3);

            assertTrue(db.checkUserMatchesPass("Alexa123","PasswordIsMyPassword"));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testCheckUserMatchesFail(){
        try{
            Map<String, String> user1 = new HashMap<String, String>();
            user1.put("USERNAME", "Seansta18");
            user1.put("FIRSTNAME", "Sean");
            user1.put("LASTNAME", "McMillan");
            user1.put("EMAIL", "Sean@gmail.com");
            user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user1.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user1);

            Map<String, String> user2 = new HashMap<String, String>();
            user2.put("USERNAME", "Eakelley");
            user2.put("FIRSTNAME", "Emily");
            user2.put("LASTNAME", "Kelley");
            user2.put("EMAIL", "emily@gmail.com");
            user2.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user2.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user2);

            Map<String, String> user3 = new HashMap<String, String>();
            user3.put("USERNAME", "Alexa123");
            user3.put("FIRSTNAME", "Alexa");
            user3.put("LASTNAME", "Freglette");
            user3.put("EMAIL", "alexa@gmail.com");
            user3.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user3.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user3);

            assertFalse(db.checkUserMatchesPass("Alexa123","Something Random"));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testCheckUserMatchesUserDNE(){
        try{
            Map<String, String> user1 = new HashMap<String, String>();
            user1.put("USERNAME", "Seansta18");
            user1.put("FIRSTNAME", "Sean");
            user1.put("LASTNAME", "McMillan");
            user1.put("EMAIL", "Sean@gmail.com");
            user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user1.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user1);

            Map<String, String> user2 = new HashMap<String, String>();
            user2.put("USERNAME", "Eakelley");
            user2.put("FIRSTNAME", "Emily");
            user2.put("LASTNAME", "Kelley");
            user2.put("EMAIL", "emily@gmail.com");
            user2.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user2.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user2);

            Map<String, String> user3 = new HashMap<String, String>();
            user3.put("USERNAME", "Alexa123");
            user3.put("FIRSTNAME", "Alexa");
            user3.put("LASTNAME", "Freglette");
            user3.put("EMAIL", "alexa@gmail.com");
            user3.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user3.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user3);

            assertFalse(db.checkUserMatchesPass("Bumblebee","PasswordIsMyPassword"));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }


    //Sean

    @Test
    public void testGetUserByUsername()
    {
        try{
            Map<String, String> user = new HashMap<String, String>();
            user.put("USERNAME", "Seansta18");
            user.put("FIRSTNAME", "Sean");
            user.put("LASTNAME", "McMillan");
            user.put("EMAIL", "Sean@gmail.com");
            user.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user);

            assertEquals(user, db.getUserByUsername("Seansta18"));

            Map<String, String> user2 = new HashMap<String, String>();
            user2.put("USERNAME", "EKelley");
            user2.put("FIRSTNAME", "Emily");
            user2.put("LASTNAME", "Kelley");
            user2.put("EMAIL", "emily@gmail.com");
            user2.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user2.put("PASSWORD", "ILoveSoftEng");

            db.addUser(user2);

            assertEquals(user, db.getUserByUsername("Seansta18"));
            assertEquals(user2, db.getUserByUsername("EKelley"));

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetUserByUsernameThatDNE()
    {
        try{
            Map<String, String> user = new HashMap<String, String>();
            user.put("USERNAME", "Seansta18");
            user.put("FIRSTNAME", "Sean");
            user.put("LASTNAME", "McMillan");
            user.put("EMAIL", "Sean@gmail.com");
            user.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user.put("PASSWORD", "PasswordIsMyPassword");

            assertEquals(null, db.getUserByUsername("Seansta18"));

            db.addUser(user);

            assertEquals(user, db.getUserByUsername("Seansta18"));

            Map<String, String> user2 = new HashMap<String, String>();
            user2.put("USERNAME", "EKelley");
            user2.put("FIRSTNAME", "Emily");
            user2.put("LASTNAME", "Kelley");
            user2.put("EMAIL", "emily@gmail.com");
            user2.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user2.put("PASSWORD", "ILoveSoftEng");

            assertEquals(null, db.getUserByUsername("EKelley"));
            db.addUser(user2);
            assertEquals(user2, db.getUserByUsername("EKelley"));

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetUserByEmail()
    {
        try{
            Map<String, String> user = new HashMap<String, String>();
            user.put("USERNAME", "Seansta18");
            user.put("FIRSTNAME", "Sean");
            user.put("LASTNAME", "McMillan");
            user.put("EMAIL", "Sean@gmail.com");
            user.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user);

            assertEquals(user, db.getUserByEmail("Sean@gmail.com"));

            Map<String, String> user2 = new HashMap<String, String>();
            user2.put("USERNAME", "EKelley");
            user2.put("FIRSTNAME", "Emily");
            user2.put("LASTNAME", "Kelley");
            user2.put("EMAIL", "emily@gmail.com");
            user2.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user2.put("PASSWORD", "ILoveSoftEng");

            db.addUser(user2);

            assertEquals(user, db.getUserByEmail("Sean@gmail.com"));
            assertEquals(user2, db.getUserByEmail("emily@gmail.com"));

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetUserByEmailThatDNE()
    {
        try{
            Map<String, String> user = new HashMap<String, String>();
            user.put("USERNAME", "Seansta18");
            user.put("FIRSTNAME", "Sean");
            user.put("LASTNAME", "McMillan");
            user.put("EMAIL", "Sean@gmail.com");
            user.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user);

            assertEquals(user, db.getUserByEmail("Sean@gmail.com"));
            assertEquals(null, db.getUserByEmail("seansta@gmail.com"));

            Map<String, String> user2 = new HashMap<String, String>();
            user2.put("USERNAME", "EKelley");
            user2.put("FIRSTNAME", "Emily");
            user2.put("LASTNAME", "Kelley");
            user2.put("EMAIL", "emily@gmail.com");
            user2.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user2.put("PASSWORD", "ILoveSoftEng");

            assertEquals(null, db.getUserByEmail("emily@gmail.com"));
            db.addUser(user2);
            assertEquals(user2, db.getUserByEmail("emily@gmail.com"));

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testUpdatePassword()
    {
        try{
            Map<String, String> user = new HashMap<String, String>();
            user.put("USERNAME", "Seansta18");
            user.put("FIRSTNAME", "Sean");
            user.put("LASTNAME", "McMillan");
            user.put("EMAIL", "Sean@gmail.com");
            user.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user.put("PASSWORD", "PasswordIsMyPassword");

            db.addUser(user);

            db.updatePassword("Seansta18", "Sean@gmail.com", "NewPassword");

            user.replace("PASSWORD", "NewPassword");

            assertEquals(user, db.getUserByUsername("Seansta18"));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testUpdatePasswordWithTwoUsers()
    {
        try{
            Map<String, String> user = new HashMap<String, String>();
            user.put("USERNAME", "Seansta18");
            user.put("FIRSTNAME", "Sean");
            user.put("LASTNAME", "McMillan");
            user.put("EMAIL", "Sean@gmail.com");
            user.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user.put("PASSWORD", "PasswordIsMyPassword");

            Map<String, String> user2 = new HashMap<String, String>();
            user2.put("USERNAME", "EKelley");
            user2.put("FIRSTNAME", "Emily");
            user2.put("LASTNAME", "Kelley");
            user2.put("EMAIL", "emily@gmail.com");
            user2.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user2.put("PASSWORD", "ILoveSoftEng");

            db.addUser(user);
            db.addUser(user2);

            assertThrows(SQLException.class, () -> {
                db.updatePassword("Seansta18", "emily@gmail.com", "SoftEngIsLife");
            });

            db.updatePassword("EKelley", "emily@gmail.com", "Tennis");
            user2.replace("PASSWORD", "Tennis");

            assertEquals(user2, db.getUserByUsername("EKelley"));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }


    //Test getServiceRequestsByAuthor
    @Test
    public void testGetServiceRequestsByAuthor()
    {
        try {
            Map<String, String> user = new HashMap<String, String>();
            user.put("USERNAME", "Seansta18");
            user.put("FIRSTNAME", "Sean");
            user.put("LASTNAME", "McMillan");
            user.put("EMAIL", "Sean@gmail.com");
            user.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user.put("PASSWORD", "PasswordIsMyPassword");
            db.addUser(user);

            Map<String, String> user2 = new HashMap<String, String>();
            user.put("USERNAME", "JohnH");
            user.put("FIRSTNAME", "John");
            user.put("LASTNAME", "H");
            user.put("EMAIL", "John@gmail.com");
            user.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user.put("PASSWORD", "Chris");
            db.addUser(user);

            Map<String, String> sharedValues1 = new HashMap<String, String>();
            sharedValues1.put("REQUESTID", "1");
            sharedValues1.put("STATUS", STATUS_NAMES.get(STATUS.ASSIGNED));
            sharedValues1.put("EMPLOYEEID", "WONG123");
            sharedValues1.put("AUTHORID", "Seansta18");
            sharedValues1.put("LOCATIONID", "aPARK001GG");
            sharedValues1.put("FIRSTNAME", "Tim");
            sharedValues1.put("LASTNAME", "Smith");
            sharedValues1.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FOOD_DELIVERY));

            Map<String, String> requestValues1 = new HashMap<String, String>();
            requestValues1.put("REQUESTID", "1");
            requestValues1.put("DELIVERYTIME", "NOW");
            requestValues1.put("NUMBEROFSERVINGS", "2");
            requestValues1.put("DRINKOPTIONS", "aqua");
            requestValues1.put("CONTACTNUMBER", "1-800");
            requestValues1.put("FOODOPTION", "Pizza");
            requestValues1.put("DIETARYRESTRICTIONS", "PEANUTS");
            requestValues1.put("NOTE", "BLAH BLAH BLAH");
            db.addServiceRequest(sharedValues1, requestValues1);

            Map<String, String> sharedValues2 = new HashMap<String, String>();
            sharedValues2.put("REQUESTID", "2");
            sharedValues2.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues2.put("EMPLOYEEID", "BONAVENTURA123");
            sharedValues2.put("AUTHORID", "Seansta18");
            sharedValues2.put("LOCATIONID", "aPARK010GG");
            sharedValues2.put("FIRSTNAME", "Bob");
            sharedValues2.put("LASTNAME", "Jones");
            sharedValues2.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FLORAL_DELIVERY));

            Map<String, String> requestValues2 = new HashMap<String, String>();
            requestValues2.put("REQUESTID", "2");
            requestValues2.put("DELIVERYTIME", "MEH");
            requestValues2.put("DELIVERYDATE", "NOW");
            requestValues2.put("VASEOPTION", "Pink");
            requestValues2.put("FLOWEROPTION", null);
            requestValues2.put("CONTACTNUMBER", null);
            requestValues2.put("NOTE", "HELLO");
            db.addServiceRequest(sharedValues2, requestValues2);

            Map<String, String> sharedValues3 = new HashMap<String, String>();
            sharedValues3.put("REQUESTID", "3");
            sharedValues3.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues3.put("EMPLOYEEID", "DIAMOND456");
            sharedValues3.put("AUTHORID", "JohnH");
            sharedValues3.put("LOCATIONID", "aPARK002GG");
            sharedValues3.put("FIRSTNAME", "Mary");
            sharedValues3.put("LASTNAME", "Quinn");
            sharedValues3.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FLORAL_DELIVERY));

            Map<String, String> requestValues3 = new HashMap<String, String>();
            requestValues3.put("REQUESTID", "3");
            requestValues3.put("DELIVERYTIME", "LATER");
            requestValues3.put("DELIVERYDATE", "NOW");
            requestValues3.put("VASEOPTION", "Pink");
            requestValues3.put("FLOWEROPTION", null);
            requestValues3.put("CONTACTNUMBER", null);
            requestValues3.put("NOTE", "GOODBYE");
            db.addServiceRequest(sharedValues3, requestValues3);

            List<Map<String, String>> expectedResult = new ArrayList<Map<String, String>>();
            expectedResult.add(sharedValues1);
            expectedResult.add(sharedValues2);

            assertEquals(expectedResult, db.getServiceRequestsByAuthor("Seansta18"));

            expectedResult = new ArrayList<Map<String, String>>();
            expectedResult.add(sharedValues3);

            assertEquals(expectedResult, db.getServiceRequestsByAuthor("JohnH"));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetServiceRequestsByAuthorDNE()
    {
        try {
            Map<String, String> user = new HashMap<String, String>();
            user.put("USERNAME", "Seansta18");
            user.put("FIRSTNAME", "Sean");
            user.put("LASTNAME", "McMillan");
            user.put("EMAIL", "Sean@gmail.com");
            user.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user.put("PASSWORD", "PasswordIsMyPassword");
            db.addUser(user);

            Map<String, String> user2 = new HashMap<String, String>();
            user.put("USERNAME", "JohnH");
            user.put("FIRSTNAME", "John");
            user.put("LASTNAME", "H");
            user.put("EMAIL", "John@gmail.com");
            user.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
            user.put("PASSWORD", "Chris");
            db.addUser(user);

            Map<String, String> sharedValues1 = new HashMap<String, String>();
            sharedValues1.put("REQUESTID", "1");
            sharedValues1.put("STATUS", STATUS_NAMES.get(STATUS.ASSIGNED));
            sharedValues1.put("EMPLOYEEID", "WONG123");
            sharedValues1.put("AUTHORID", "Seansta18");
            sharedValues1.put("LOCATIONID", "aPARK001GG");
            sharedValues1.put("FIRSTNAME", "Tim");
            sharedValues1.put("LASTNAME", "Smith");
            sharedValues1.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FOOD_DELIVERY));

            Map<String, String> requestValues1 = new HashMap<String, String>();
            requestValues1.put("REQUESTID", "1");
            requestValues1.put("DELIVERYTIME", "NOW");
            requestValues1.put("NUMBEROFSERVINGS", "2");
            requestValues1.put("DRINKOPTIONS", "aqua");
            requestValues1.put("CONTACTNUMBER", "1-800");
            requestValues1.put("FOODOPTION", "Pizza");
            requestValues1.put("DIETARYRESTRICTIONS", "PEANUTS");
            requestValues1.put("NOTE", "BLAH BLAH BLAH");
            db.addServiceRequest(sharedValues1, requestValues1);

            Map<String, String> sharedValues2 = new HashMap<String, String>();
            sharedValues2.put("REQUESTID", "2");
            sharedValues2.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues2.put("EMPLOYEEID", "BONAVENTURA123");
            sharedValues2.put("AUTHORID", "Seansta18");
            sharedValues2.put("LOCATIONID", "aPARK010GG");
            sharedValues2.put("FIRSTNAME", "Bob");
            sharedValues2.put("LASTNAME", "Jones");
            sharedValues2.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FLORAL_DELIVERY));

            Map<String, String> requestValues2 = new HashMap<String, String>();
            requestValues2.put("REQUESTID", "2");
            requestValues2.put("DELIVERYTIME", "MEH");
            requestValues2.put("DELIVERYDATE", "NOW");
            requestValues2.put("VASEOPTION", "Pink");
            requestValues2.put("FLOWEROPTION", null);
            requestValues2.put("CONTACTNUMBER", null);
            requestValues2.put("NOTE", "HELLO");
            db.addServiceRequest(sharedValues2, requestValues2);

            Map<String, String> sharedValues3 = new HashMap<String, String>();
            sharedValues3.put("REQUESTID", "3");
            sharedValues3.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues3.put("EMPLOYEEID", "DIAMOND456");
            sharedValues3.put("AUTHORID", "JohnH");
            sharedValues3.put("LOCATIONID", "aPARK002GG");
            sharedValues3.put("FIRSTNAME", "Mary");
            sharedValues3.put("LASTNAME", "Quinn");
            sharedValues3.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FLORAL_DELIVERY));

            Map<String, String> requestValues3 = new HashMap<String, String>();
            requestValues3.put("REQUESTID", "3");
            requestValues3.put("DELIVERYTIME", "LATER");
            requestValues3.put("DELIVERYDATE", "NOW");
            requestValues3.put("VASEOPTION", "Pink");
            requestValues3.put("FLOWEROPTION", null);
            requestValues3.put("CONTACTNUMBER", null);
            requestValues3.put("NOTE", "GOODBYE");
            db.addServiceRequest(sharedValues3, requestValues3);

            List<Map<String, String>> expectedResult = new ArrayList<Map<String, String>>();
            expectedResult.add(sharedValues1);
            expectedResult.add(sharedValues2);

            assertEquals(expectedResult, db.getServiceRequestsByAuthor("Seansta18"));

            expectedResult = new ArrayList<Map<String, String>>();
            expectedResult.add(sharedValues3);

            assertEquals(expectedResult, db.getServiceRequestsByAuthor("JohnH"));

            assertThrows(SQLException.class, () -> {
                db.getServiceRequestsByAuthor("BillyCross");
            });
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }
}
