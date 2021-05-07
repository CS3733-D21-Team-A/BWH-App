package edu.wpi.aquamarine_axolotls.db;

import edu.wpi.aquamarine_axolotls.TestUtil;
import edu.wpi.aquamarine_axolotls.db.enums.ATTRIBUTE;
import edu.wpi.aquamarine_axolotls.db.enums.SERVICEREQUEST;
import edu.wpi.aquamarine_axolotls.db.enums.STATUS;
import edu.wpi.aquamarine_axolotls.db.enums.TABLES;
import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static edu.wpi.aquamarine_axolotls.db.DatabaseUtil.*;

public class DatabaseControllerTest3 {

    private final DatabaseController db = DatabaseController.getInstance();

    public DatabaseControllerTest3() throws SQLException, IOException {
    }

    @BeforeEach
    void resetDB() throws IOException, SQLException {
        db.emptyEdgeTable();
        db.emptyNodeTable();
        db.emptyServiceRequestsTable();
        db.emptyUserTable();

        Map<String, String> user1 = new HashMap<String, String>();
        user1.put("USERNAME", "Seansta18");
        user1.put("FIRSTNAME", "Sean");
        user1.put("LASTNAME", "McMillan");
        user1.put("EMAIL", "Sean@gmail.com");
        user1.put("USERTYPE", DatabaseInfo.EMPLOYEE_TEXT);
        user1.put("PASSWORD", "PasswordIsMyPassword");

        db.addUser(user1);


        CSVHandler csvHandler = new CSVHandler(db);
        csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.TEST_NODE_RESOURCE_PATH), TABLES.NODES, true);
        csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.TEST_EDGE_RESOURCE_PATH), TABLES.EDGES, true);
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

    @Test
    public void testGetServiceRequestsEmpty(){
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetServiceRequestsDNE(){
        try {
            ArrayList<Map<String, String>> expectedResult = new ArrayList<>();
            Map<String, String> sharedValues = new HashMap<>();
            sharedValues.put("REQUESTID", "12345");
            sharedValues.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues.put("EMPLOYEEID", "WONG123");
            sharedValues.put("AUTHORID", "123");
            sharedValues.put("LOCATIONID", "aPARK009GG");
            sharedValues.put("FIRSTNAME", "Bob");
            sharedValues.put("LASTNAME", "Jones");
            sharedValues.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FOOD_DELIVERY));
            expectedResult.add(sharedValues);

            assertNotEquals(expectedResult, db.getServiceRequests());
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddGetServiceRequests(){
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
            Map<String, String> sharedValues = new HashMap<>();
            sharedValues.put("REQUESTID", "23456");
            sharedValues.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues.put("EMPLOYEEID", "WONG123");
            sharedValues.put("AUTHORID", "Seansta18");
            sharedValues.put("LOCATIONID", "aPARK009GG");
            sharedValues.put("FIRSTNAME", "Bob");
            sharedValues.put("LASTNAME", "Jones");
            sharedValues.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FOOD_DELIVERY));

            Map<String, String> requestValues = new HashMap<String, String>();
            requestValues.put("REQUESTID", "23456");
            requestValues.put("DELIVERYTIME", "NOW");
            requestValues.put("DIETARYRESTRICTIONS", "PEANUTS");
            requestValues.put("NOTE", "BLAH BLAH BLAH");
            db.addServiceRequest(sharedValues, requestValues);

            List<Map<String, String>> expectedResult = new ArrayList<Map<String, String>>();
            expectedResult.add(sharedValues);

            assertEquals(expectedResult, db.getServiceRequests());

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddGetServiceRequestsSomeEmpty(){
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
            Map<String, String> sharedValues = new HashMap<String, String>();
            sharedValues.put("REQUESTID", "34567");
            sharedValues.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues.put("LOCATIONID", "aPARK009GG");
            sharedValues.put("AUTHORID", "Seansta18");
            sharedValues.put("FIRSTNAME", "Bob");
            sharedValues.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FOOD_DELIVERY));

            Map<String, String> requestValues = new HashMap<String, String>();
            requestValues.put("REQUESTID", "34567");
            requestValues.put("DIETARYRESTRICTIONS", "PEANUTS");
            requestValues.put("NOTE", "BLAH BLAH BLAH");
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

    @Test
    public void testAddGetServiceRequestsNoPrimaryKey() {
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Map<String, String> sharedValues = new HashMap<String, String>();
            sharedValues.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues.put("LOCATIONID", "aPARK009GG");
            sharedValues.put("FIRSTNAME", "Bob");
            sharedValues.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FOOD_DELIVERY));

            Map<String, String> requestValues = new HashMap<String, String>();
            requestValues.put("REQUESTID", "45678");
            requestValues.put("DIETARYRESTRICTIONS", "PEANUTS");
            requestValues.put("NOTE", "BLAH BLAH BLAH");
            assertThrows(SQLException.class, () -> {
                db.addServiceRequest(sharedValues, requestValues);
            });
    }

    @Test
    public void testAddServiceRequestAddingSameRequestTwice() {
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
            Map<String, String> sharedValues = new HashMap<String, String>();
            sharedValues.put("REQUESTID", "123");
            sharedValues.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues.put("EMPLOYEEID", "WONG123");
            sharedValues.put("AUTHORID", "Seansta18");
            sharedValues.put("LOCATIONID", "aPARK009GG");
            sharedValues.put("FIRSTNAME", "Bob");
            sharedValues.put("LASTNAME", "Jones");
            sharedValues.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FOOD_DELIVERY));

            Map<String, String> requestValues = new HashMap<String, String>();
            requestValues.put("REQUESTID", "123");
            requestValues.put("DELIVERYTIME", "ASAP");
            requestValues.put("DIETARYRESTRICTIONS", "MILK");
            requestValues.put("NOTE", "TESTING");
            db.addServiceRequest(sharedValues, requestValues);

            List<Map<String, String>> expectedResult = new ArrayList<Map<String, String>>();
            expectedResult.add(sharedValues);

            assertEquals(expectedResult, db.getServiceRequests());

            assertThrows(SQLException.class, () -> {
                db.addServiceRequest(sharedValues, requestValues);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testChangeStatus() {
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
            Map<String, String> sharedValues = new HashMap<String, String>();
            sharedValues.put("REQUESTID", "56789");
            sharedValues.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues.put("EMPLOYEEID", "WONG123");
            sharedValues.put("AUTHORID", "Seansta18");
            sharedValues.put("LOCATIONID", "aPARK009GG");
            sharedValues.put("FIRSTNAME", "Bob");
            sharedValues.put("LASTNAME", "Jones");
            sharedValues.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FOOD_DELIVERY));

            Map<String, String> requestValues = new HashMap<String, String>();
            requestValues.put("REQUESTID", "56789");
            requestValues.put("DELIVERYTIME", "NOW");
            requestValues.put("DIETARYRESTRICTIONS", "PEANUTS");
            requestValues.put("NOTE", "BLAH BLAH BLAH");
            db.addServiceRequest(sharedValues, requestValues);

            List<Map<String, String>> expectedResult = new ArrayList<Map<String, String>>();
            expectedResult.add(sharedValues);

            assertEquals(expectedResult, db.getServiceRequests());

            db.changeStatus("56789", STATUS.IN_PROGRESS);

            expectedResult.remove(sharedValues);
            sharedValues.replace("STATUS", STATUS_NAMES.get(STATUS.IN_PROGRESS));
            expectedResult.add(sharedValues);

            assertEquals(expectedResult, db.getServiceRequests());

            db.changeStatus("56789", STATUS.DONE);

            expectedResult.remove(sharedValues);
            sharedValues.replace("STATUS", STATUS_NAMES.get(STATUS.DONE));
            expectedResult.add(sharedValues);

            assertEquals(expectedResult, db.getServiceRequests());

            db.changeStatus("56789", STATUS.CANCELED);

            expectedResult.remove(sharedValues);
            sharedValues.replace("STATUS", STATUS_NAMES.get(STATUS.CANCELED));
            expectedResult.add(sharedValues);

            assertEquals(expectedResult, db.getServiceRequests());

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testChangeStatusStatusDNE() {
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Map<String, String> sharedValues = new HashMap<String, String>();
            sharedValues.put("REQUESTID", "67890");
            sharedValues.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues.put("EMPLOYEEID", "WONG123");
            sharedValues.put("LOCATIONID", "aPARK009GG");
            sharedValues.put("FIRSTNAME", "Bob");
            sharedValues.put("LASTNAME", "Jones");
            sharedValues.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FOOD_DELIVERY));

            Map<String, String> requestValues = new HashMap<String, String>();
            requestValues.put("REQUESTID", "67890");
            requestValues.put("DELIVERYTIME", "NOW");
            requestValues.put("DIETARYRESTRICTIONS", "PEANUTS");
            requestValues.put("NOTE", "BLAH BLAH BLAH");
        try {
            db.addServiceRequest(sharedValues, requestValues);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        assertThrows(IllegalArgumentException.class, () -> {
            db.changeStatus("67890", STATUS.valueOf("NOT_A_REAL_STATUS"));
        });
    }

    @Test
    public void testChangeStatusRequestDNE() {
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Map<String, String> sharedValues = new HashMap<String, String>();
        sharedValues.put("REQUESTID", "78901");
        sharedValues.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
        sharedValues.put("EMPLOYEEID", "WONG123");
        sharedValues.put("LOCATIONID", "aPARK009GG");
        sharedValues.put("FIRSTNAME", "Bob");
        sharedValues.put("LASTNAME", "Jones");
        sharedValues.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FOOD_DELIVERY));

        Map<String, String> requestValues = new HashMap<String, String>();
        requestValues.put("REQUESTID", "78901");
        requestValues.put("DELIVERYTIME", "NOW");
        requestValues.put("DIETARYRESTRICTIONS", "PEANUTS");
        requestValues.put("NOTE", "BLAH BLAH BLAH");
        try {
            db.addServiceRequest(sharedValues, requestValues);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        assertThrows(NullPointerException.class, () -> {
            db.changeStatus("88888", STATUS.IN_PROGRESS);
        });
    }

    @Test
    public void testChangeEmployee() {
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
            Map<String, String> sharedValues = new HashMap<String, String>();
            sharedValues.put("REQUESTID", "24680");
            sharedValues.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues.put("EMPLOYEEID", "WONG123");
            sharedValues.put("AUTHORID", "Seansta18");
            sharedValues.put("LOCATIONID", "aPARK009GG");
            sharedValues.put("FIRSTNAME", "Bob");
            sharedValues.put("LASTNAME", "Jones");
            sharedValues.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FOOD_DELIVERY));

            Map<String, String> requestValues = new HashMap<String, String>();
            requestValues.put("REQUESTID", "24680");
            requestValues.put("DELIVERYTIME", "NOW");
            requestValues.put("DIETARYRESTRICTIONS", "PEANUTS");
            requestValues.put("NOTE", "BLAH BLAH BLAH");
            db.addServiceRequest(sharedValues, requestValues);

            List<Map<String, String>> expectedResult = new ArrayList<Map<String, String>>();
            expectedResult.add(sharedValues);

            assertEquals(expectedResult, db.getServiceRequests());

            db.changeEmployee("24680", "BONAVENTURA789");

            expectedResult.remove(sharedValues);
            sharedValues.replace("EMPLOYEEID", "BONAVENTURA789");
            expectedResult.add(sharedValues);

            assertEquals(expectedResult, db.getServiceRequests());

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testChangeEmployeeRequestDNE() {
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Map<String, String> sharedValues = new HashMap<String, String>();
        sharedValues.put("REQUESTID", "13579");
        sharedValues.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
        sharedValues.put("EMPLOYEEID", "WONG123");
        sharedValues.put("LOCATIONID", "aPARK009GG");
        sharedValues.put("FIRSTNAME", "Bob");
        sharedValues.put("LASTNAME", "Jones");
        sharedValues.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FOOD_DELIVERY));

        Map<String, String> requestValues = new HashMap<String, String>();
        requestValues.put("REQUESTID", "13579");
        requestValues.put("DELIVERYTIME", "NOW");
        requestValues.put("DIETARYRESTRICTIONS", "PEANUTS");
        requestValues.put("NOTE", "BLAH BLAH BLAH");
        try {
            db.addServiceRequest(sharedValues, requestValues);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        assertThrows(NullPointerException.class, () -> {
            db.changeEmployee("77777", "BONAVENTURA123");
        });
    }

    @Test
    public void testAssignEmployee() {
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
            Map<String, String> sharedValues = new HashMap<String, String>();
            sharedValues.put("REQUESTID", "753");
            sharedValues.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues.put("LOCATIONID", "aPARK009GG");
            sharedValues.put("AUTHORID", "Seansta18");
            sharedValues.put("FIRSTNAME", "Bob");
            sharedValues.put("LASTNAME", "Jones");
            sharedValues.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FOOD_DELIVERY));

            Map<String, String> requestValues = new HashMap<String, String>();
            requestValues.put("REQUESTID", "753");
            requestValues.put("DELIVERYTIME", "NOW");
            requestValues.put("DIETARYRESTRICTIONS", "PEANUTS");
            requestValues.put("NOTE", "BLAH BLAH BLAH");
            db.addServiceRequest(sharedValues, requestValues);

            List<Map<String, String>> expectedResult = new ArrayList<Map<String, String>>();
            sharedValues.put("EMPLOYEEID", null);
            expectedResult.add(sharedValues);

            assertEquals(expectedResult, db.getServiceRequests());

            db.assignEmployee("753", "WONG321");

            expectedResult.remove(sharedValues);
            sharedValues.replace("EMPLOYEEID", "WONG321");
            sharedValues.replace("STATUS", STATUS_NAMES.get(STATUS.ASSIGNED));
            expectedResult.add(sharedValues);

            assertEquals(expectedResult, db.getServiceRequests());

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAssignEmployeeRequestDNE() {
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Map<String, String> sharedValues = new HashMap<String, String>();
        sharedValues.put("REQUESTID", "13579");
        sharedValues.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
        sharedValues.put("LOCATIONID", "aPARK009GG");
        sharedValues.put("FIRSTNAME", "Bob");
        sharedValues.put("LASTNAME", "Jones");
        sharedValues.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FOOD_DELIVERY));

        Map<String, String> requestValues = new HashMap<String, String>();
        requestValues.put("REQUESTID", "13579");
        requestValues.put("DELIVERYTIME", "NOW");
        requestValues.put("DIETARYRESTRICTIONS", "PEANUTS");
        requestValues.put("NOTE", "BLAH BLAH BLAH");
        try {
            db.addServiceRequest(sharedValues, requestValues);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        assertThrows(NullPointerException.class, () -> {
            db.changeEmployee("99999", "BONAVENTURA123");
        });
    }

    @Test
    public void testGetServiceRequestsWithStatus(){
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
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
            requestValues2.put("NOTE", "HELLO");
            db.addServiceRequest(sharedValues2, requestValues2);

            Map<String, String> sharedValues3 = new HashMap<String, String>();
            sharedValues3.put("REQUESTID", "3");
            sharedValues3.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues3.put("EMPLOYEEID", "DIAMOND456");
            sharedValues3.put("AUTHORID", "Seansta18");
            sharedValues3.put("LOCATIONID", "aPARK002GG");
            sharedValues3.put("FIRSTNAME", "Mary");
            sharedValues3.put("LASTNAME", "Quinn");
            sharedValues3.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FLORAL_DELIVERY));

            Map<String, String> requestValues3 = new HashMap<String, String>();
            requestValues3.put("REQUESTID", "3");
            requestValues3.put("DELIVERYTIME", "LATER");
            requestValues3.put("NOTE", "GOODBYE");
            db.addServiceRequest(sharedValues3, requestValues3);

            List<Map<String, String>> expectedResult = new ArrayList<Map<String, String>>();
            expectedResult.add(sharedValues1);
            expectedResult.add(sharedValues2);
            expectedResult.add(sharedValues3);

            assertEquals(expectedResult, db.getServiceRequests());

            expectedResult.remove(sharedValues2);
            expectedResult.remove(sharedValues3);

            assertEquals(expectedResult, db.getServiceRequestsWithStatus(STATUS.ASSIGNED));

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetServiceRequestsWithStatusEmpty(){
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
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
            requestValues2.put("NOTE", "HELLO");
            db.addServiceRequest(sharedValues2, requestValues2);

            Map<String, String> sharedValues3 = new HashMap<String, String>();
            sharedValues3.put("REQUESTID", "3");
            sharedValues3.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues3.put("EMPLOYEEID", "DIAMOND456");
            sharedValues3.put("AUTHORID", "Seansta18");
            sharedValues3.put("LOCATIONID", "aPARK002GG");
            sharedValues3.put("FIRSTNAME", "Mary");
            sharedValues3.put("LASTNAME", "Quinn");
            sharedValues3.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FLORAL_DELIVERY));

            Map<String, String> requestValues3 = new HashMap<String, String>();
            requestValues3.put("REQUESTID", "3");
            requestValues3.put("DELIVERYTIME", "LATER");
            requestValues3.put("NOTE", "GOODBYE");
            db.addServiceRequest(sharedValues3, requestValues3);

            List<Map<String, String>> expectedResult = new ArrayList<Map<String, String>>();
            expectedResult.add(sharedValues1);
            expectedResult.add(sharedValues2);
            expectedResult.add(sharedValues3);

            assertEquals(expectedResult, db.getServiceRequests());

            expectedResult.remove(sharedValues1);
            expectedResult.remove(sharedValues2);
            expectedResult.remove(sharedValues3);

            assertEquals(expectedResult, db.getServiceRequestsWithStatus(STATUS.DONE));

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetServiceRequestsWithStatusMultipleTypes(){
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
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
            requestValues2.put("NOTE", "HELLO");
            db.addServiceRequest(sharedValues2, requestValues2);

            Map<String, String> sharedValues3 = new HashMap<String, String>();
            sharedValues3.put("REQUESTID", "3");
            sharedValues3.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues3.put("EMPLOYEEID", "DIAMOND456");
            sharedValues3.put("AUTHORID", "Seansta18");
            sharedValues3.put("LOCATIONID", "aPARK002GG");
            sharedValues3.put("FIRSTNAME", "Mary");
            sharedValues3.put("LASTNAME", "Quinn");
            sharedValues3.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FOOD_DELIVERY));

            Map<String, String> requestValues3 = new HashMap<String, String>();
            requestValues3.put("REQUESTID", "3");
            requestValues3.put("DIETARYRESTRICTIONS", "NONE");
            requestValues3.put("DELIVERYTIME", "LATER");
            requestValues3.put("NOTE", "GOODBYE");
            db.addServiceRequest(sharedValues3, requestValues3);

            List<Map<String, String>> expectedResult = new ArrayList<Map<String, String>>();
            expectedResult.add(sharedValues1);
            expectedResult.add(sharedValues2);
            expectedResult.add(sharedValues3);

            assertEquals(expectedResult, db.getServiceRequests());

            expectedResult.remove(sharedValues1);

            assertEquals(expectedResult, db.getServiceRequestsWithStatus(STATUS.UNASSIGNED));

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetServiceRequestsWithStatusDNE(){ //this test kinda isn't necessary cuz the only DNE case is null, which should never happen
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
            Map<String, String> sharedValues1 = new HashMap<String, String>();
            sharedValues1.put("REQUESTID", "1");
            sharedValues1.put("STATUS", STATUS_NAMES.get(STATUS.ASSIGNED));
            sharedValues1.put("EMPLOYEEID", "WONG123");
            sharedValues1.put("AUTHORID", "Seansta18");
            sharedValues1.put("LOCATIONID", "aPARK001GG");
            sharedValues1.put("FIRSTNAME", "Tim");
            sharedValues1.put("LASTNAME", "Smith");
            sharedValues1.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FLORAL_DELIVERY));

            Map<String, String> requestValues1 = new HashMap<String, String>();
            requestValues1.put("REQUESTID", "1");
            requestValues1.put("DELIVERYTIME", "NOW");
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
            requestValues2.put("NOTE", "HELLO");
            db.addServiceRequest(sharedValues2, requestValues2);

            Map<String, String> sharedValues3 = new HashMap<String, String>();
            sharedValues3.put("REQUESTID", "3");
            sharedValues3.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues3.put("EMPLOYEEID", "DIAMOND456");
            sharedValues3.put("AUTHORID", "Seansta18");
            sharedValues3.put("LOCATIONID", "aPARK002GG");
            sharedValues3.put("FIRSTNAME", "Mary");
            sharedValues3.put("LASTNAME", "Quinn");
            sharedValues3.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FLORAL_DELIVERY));

            Map<String, String> requestValues3 = new HashMap<String, String>();
            requestValues3.put("REQUESTID", "3");
            requestValues3.put("DELIVERYTIME", "LATER");
            requestValues3.put("NOTE", "GOODBYE");
            db.addServiceRequest(sharedValues3, requestValues3);


            assertEquals(0, db.getServiceRequestsWithStatus(null).size());
        } catch (SQLException e){
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void testGetServiceRequestsByType(){
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
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
            sharedValues3.put("AUTHORID", "Seansta18");
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

            //expectedResult.add(sharedValues2);
            expectedResult.add(requestValues2);
            //expectedResult.add(sharedValues3);
            expectedResult.add(requestValues3);

            assertEquals(expectedResult, db.getServiceRequestsByType(SERVICEREQUEST.FLORAL_DELIVERY));

            expectedResult = new ArrayList<Map<String, String>>();
            expectedResult.add(requestValues1);

           assertEquals(expectedResult, db.getServiceRequestsByType(SERVICEREQUEST.FOOD_DELIVERY));

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetServiceRequestsByTypeEmpty(){
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
            Map<String, String> sharedValues1 = new HashMap<String, String>();
            sharedValues1.put("REQUESTID", "1");
            sharedValues1.put("STATUS", STATUS_NAMES.get(STATUS.ASSIGNED));
            sharedValues1.put("EMPLOYEEID", "WONG123");
            sharedValues1.put("AUTHORID", "Seansta18");
            sharedValues1.put("LOCATIONID", "aPARK001GG");
            sharedValues1.put("FIRSTNAME", "Tim");
            sharedValues1.put("LASTNAME", "Smith");
            sharedValues1.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FLORAL_DELIVERY));

            Map<String, String> requestValues1 = new HashMap<String, String>();
            requestValues1.put("REQUESTID", "1");
            requestValues1.put("DELIVERYTIME", "NOW");
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
            requestValues2.put("NOTE", "HELLO");
            db.addServiceRequest(sharedValues2, requestValues2);

            Map<String, String> sharedValues3 = new HashMap<String, String>();
            sharedValues3.put("REQUESTID", "3");
            sharedValues3.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues3.put("EMPLOYEEID", "DIAMOND456");
            sharedValues3.put("AUTHORID", "Seansta18");
            sharedValues3.put("LOCATIONID", "aPARK002GG");
            sharedValues3.put("FIRSTNAME", "Mary");
            sharedValues3.put("LASTNAME", "Quinn");
            sharedValues3.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FLORAL_DELIVERY));

            Map<String, String> requestValues3 = new HashMap<String, String>();
            requestValues3.put("REQUESTID", "3");
            requestValues3.put("DELIVERYTIME", "LATER");
            requestValues3.put("NOTE", "GOODBYE");
            db.addServiceRequest(sharedValues3, requestValues3);

            List<Map<String, String>> expectedResult = new ArrayList<Map<String, String>>();
            expectedResult.add(sharedValues1);
            expectedResult.add(sharedValues2);
            expectedResult.add(sharedValues3);

            assertEquals(expectedResult, db.getServiceRequests());

            expectedResult.remove(sharedValues1);
            expectedResult.remove(sharedValues2);
            expectedResult.remove(sharedValues3);

            assertEquals(expectedResult, db.getServiceRequestsByType(SERVICEREQUEST.FOOD_DELIVERY));

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetServiceRequestsByTypeDNE(){
        try {
            assertEquals(new ArrayList<Map<String, String>>(), db.getServiceRequests());
            Map<String, String> sharedValues1 = new HashMap<String, String>();
            sharedValues1.put("REQUESTID", "1");
            sharedValues1.put("STATUS", STATUS_NAMES.get(STATUS.ASSIGNED));
            sharedValues1.put("EMPLOYEEID", "WONG123");
            sharedValues1.put("LOCATIONID", "aPARK001GG");
            sharedValues1.put("FIRSTNAME", "Tim");
            sharedValues1.put("LASTNAME", "Smith");
            sharedValues1.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FLORAL_DELIVERY));

            Map<String, String> requestValues1 = new HashMap<String, String>();
            requestValues1.put("REQUESTID", "1");
            requestValues1.put("DELIVERYTIME", "NOW");
            requestValues1.put("NOTE", "BLAH BLAH BLAH");
            db.addServiceRequest(sharedValues1, requestValues1);

            Map<String, String> sharedValues2 = new HashMap<String, String>();
            sharedValues2.put("REQUESTID", "2");
            sharedValues2.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues2.put("EMPLOYEEID", "BONAVENTURA123");
            sharedValues2.put("LOCATIONID", "aPARK010GG");
            sharedValues2.put("FIRSTNAME", "Bob");
            sharedValues2.put("LASTNAME", "Jones");
            sharedValues2.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FLORAL_DELIVERY));

            Map<String, String> requestValues2 = new HashMap<String, String>();
            requestValues2.put("REQUESTID", "2");
            requestValues2.put("DELIVERYTIME", "MEH");
            requestValues2.put("NOTE", "HELLO");
            db.addServiceRequest(sharedValues2, requestValues2);

            Map<String, String> sharedValues3 = new HashMap<String, String>();
            sharedValues3.put("REQUESTID", "3");
            sharedValues3.put("STATUS", STATUS_NAMES.get(STATUS.UNASSIGNED));
            sharedValues3.put("EMPLOYEEID", "DIAMOND456");
            sharedValues3.put("LOCATIONID", "aPARK002GG");
            sharedValues3.put("FIRSTNAME", "Mary");
            sharedValues3.put("LASTNAME", "Quinn");
            sharedValues3.put("REQUESTTYPE", SERVICEREQUEST_NAMES.get(SERVICEREQUEST.FLORAL_DELIVERY));

            Map<String, String> requestValues3 = new HashMap<String, String>();
            requestValues3.put("REQUESTID", "3");
            requestValues3.put("DELIVERYTIME", "LATER");
            requestValues3.put("NOTE", "GOODBYE");
            db.addServiceRequest(sharedValues3, requestValues3);
        } catch (SQLException e){
            e.printStackTrace();
            fail();
        }

        assertThrows(IllegalArgumentException.class, () -> db.getServiceRequestsByType(SERVICEREQUEST.valueOf("A_FAKE_TYPE")));

    }

    // Chris and Zhongchuan testing attributes:

    // has attribute

    @Test
    public void testHasAttributeNodeFalse(){
        try {
            assertFalse(db.hasAttribute("aPARK020GG", ATTRIBUTE.COVID_SAFE, true));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testHasAttributeEdgeFalse(){
        try {
            assertFalse(db.hasAttribute("aWALK002GG_aWALK003GG", ATTRIBUTE.COVID_SAFE, false));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    // add attribute

    @Test
    public void testAddAttributeNode(){
        try {
            assertTrue(db.addAttribute("aPARK020GG", ATTRIBUTE.HANDICAPPED_ACCESSIBLE, true));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddAttributeEdge(){
        try {
            assertTrue(db.addAttribute("aWALK002GG_aWALK003GG", ATTRIBUTE.HANDICAPPED_ACCESSIBLE, false));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddAttributeNodeDNE(){
        assertThrows(SQLException.class, () -> {
            db.addAttribute("aFakeNode", ATTRIBUTE.HANDICAPPED_ACCESSIBLE, true);
        });
    }

    @Test
    public void testAddAttributeEdgeDNE(){
        assertThrows(SQLException.class, () -> {
            db.addAttribute("aFakeEdge", ATTRIBUTE.HANDICAPPED_ACCESSIBLE, false);
        });
    }

    // has attribute cont'd

    @Test
    public void testHasAttributeNodeTrue(){
        try {
            db.addAttribute("aPARK020GG", ATTRIBUTE.COVID_SAFE, true);
            assertTrue(db.hasAttribute("aPARK020GG", ATTRIBUTE.COVID_SAFE, true));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testHasAttributeEdgeTrue(){
        try {
            db.addAttribute("aWALK002GG_aWALK003GG", ATTRIBUTE.COVID_SAFE, false);
            assertTrue(db.hasAttribute("aWALK002GG_aWALK003GG", ATTRIBUTE.COVID_SAFE, false));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testHasAttributeNodeFalse2(){
        try {
            db.addAttribute("aPARK020GG", ATTRIBUTE.COVID_SAFE, true);
            assertFalse(db.hasAttribute("aPARK050GG", ATTRIBUTE.COVID_SAFE, true));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testHasAttributeEdgeFalse2(){
        try {
            db.addAttribute("aWALK002GG_aWALK003GG", ATTRIBUTE.COVID_SAFE, false);
            assertFalse(db.hasAttribute("aWALK003GG_aWALK004GG", ATTRIBUTE.COVID_SAFE, false));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    // add attribute cont'd

    @Test
    public void testAddAttributeNode2(){
        try {
            assertFalse(db.hasAttribute("aPARK020GG", ATTRIBUTE.NOT_NAVIGABLE, true));
            assertTrue(db.addAttribute("aPARK020GG", ATTRIBUTE.NOT_NAVIGABLE, true));
            assertTrue(db.hasAttribute("aPARK020GG", ATTRIBUTE.NOT_NAVIGABLE, true));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddAttributeEdge2(){
        try {
            assertFalse(db.hasAttribute("aWALK002GG_aWALK003GG", ATTRIBUTE.NOT_NAVIGABLE, false));
            assertTrue(db.addAttribute("aWALK002GG_aWALK003GG", ATTRIBUTE.NOT_NAVIGABLE, false));
            assertTrue(db.hasAttribute("aWALK002GG_aWALK003GG", ATTRIBUTE.NOT_NAVIGABLE, false));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    // get attributes

    @Test
    public void testGetAttributesNode(){
        try {
            List<ATTRIBUTE> expectedList = new ArrayList<ATTRIBUTE>();
            List<ATTRIBUTE> actualList = db.getAttributes("aWALK002GG",true);
            assertEquals(expectedList, actualList);

            assertTrue(db.addAttribute("aWALK002GG", ATTRIBUTE.NOT_NAVIGABLE, true));
            assertTrue(db.addAttribute("aWALK002GG", ATTRIBUTE.COVID_SAFE, true));

            expectedList.add(ATTRIBUTE.NOT_NAVIGABLE);
            expectedList.add(ATTRIBUTE.COVID_SAFE);

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
            List<ATTRIBUTE> expectedList = new ArrayList<ATTRIBUTE>();
            List<ATTRIBUTE> actualList = db.getAttributes("aPARK001GG_aWALK001GG",false);
            assertEquals(expectedList, actualList);

            assertTrue(db.addAttribute("aPARK001GG_aWALK001GG", ATTRIBUTE.HANDICAPPED_ACCESSIBLE, false));
            assertTrue(db.addAttribute("aPARK001GG_aWALK001GG", ATTRIBUTE.COVID_SAFE, false));

            expectedList.add(ATTRIBUTE.HANDICAPPED_ACCESSIBLE);
            expectedList.add(ATTRIBUTE.COVID_SAFE);

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
            List<ATTRIBUTE> expectedList = new ArrayList<ATTRIBUTE>();
            List<ATTRIBUTE> actualList = db.getAttributes("aFakeNode2",true);
            assertEquals(expectedList, actualList);
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetAttributesEdgeDNE(){
        try {
            List<ATTRIBUTE> expectedList = new ArrayList<ATTRIBUTE>();
            List<ATTRIBUTE> actualList = db.getAttributes("aFakeEdge2",false);
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
            assertFalse(db.hasAttribute("aPARK019GG", ATTRIBUTE.COVID_SAFE, true));
            assertTrue(db.addAttribute("aPARK019GG", ATTRIBUTE.COVID_SAFE, true));
            assertTrue(db.hasAttribute("aPARK019GG", ATTRIBUTE.COVID_SAFE, true));

            List<String> expectedList = new ArrayList<>();
            List<String> actualList = db.getByAttribute(ATTRIBUTE.COVID_SAFE,true);

            expectedList.add("aPARK019GG");

            assertEquals(expectedList, actualList);
        } catch(SQLException e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetByAttributeTwoNodes(){
        try{
            assertFalse(db.hasAttribute("aPARK019GG", ATTRIBUTE.COVID_SAFE, true));
            assertTrue(db.addAttribute("aPARK019GG", ATTRIBUTE.COVID_SAFE, true));
            assertTrue(db.hasAttribute("aPARK019GG", ATTRIBUTE.COVID_SAFE, true));

            assertFalse(db.hasAttribute("aPARK020GG", ATTRIBUTE.COVID_SAFE, true));
            assertTrue(db.addAttribute("aPARK020GG", ATTRIBUTE.COVID_SAFE, true));
            assertTrue(db.hasAttribute("aPARK020GG", ATTRIBUTE.COVID_SAFE, true));

            List<String> expectedList = new ArrayList<>();
            List<String> actualList = db.getByAttribute(ATTRIBUTE.COVID_SAFE,true);

            expectedList.add("aPARK019GG");
            expectedList.add("aPARK020GG");

            assertEquals(expectedList, actualList);
        } catch(SQLException e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetByAttributeOneEdge(){
        try{
            assertFalse(db.hasAttribute("aWALK008GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));
            assertTrue(db.addAttribute("aWALK008GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));
            assertTrue(db.hasAttribute("aWALK008GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));



            List<String> expectedList = new ArrayList<>();
            List<String> actualList = db.getByAttribute(ATTRIBUTE.NOT_NAVIGABLE,false);

            expectedList.add("aWALK008GG_aWALK009GG");

            assertEquals(expectedList, actualList);
        } catch(SQLException e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetByAttributeTwoEdges(){
        try{
            assertFalse(db.hasAttribute("aWALK008GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));
            assertTrue(db.addAttribute("aWALK008GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));
            assertTrue(db.hasAttribute("aWALK008GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));

            assertFalse(db.hasAttribute("aPARK020GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));
            assertTrue(db.addAttribute("aPARK020GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));
            assertTrue(db.hasAttribute("aPARK020GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));

            List<String> expectedList = new ArrayList<>();
            List<String> actualList = db.getByAttribute(ATTRIBUTE.NOT_NAVIGABLE,false);

            expectedList.add("aWALK008GG_aWALK009GG");
            expectedList.add("aPARK020GG_aWALK009GG");

            assertEquals(expectedList, actualList);
        } catch(SQLException e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGetByAttributeNoEdges(){
        try{
            assertFalse(db.hasAttribute("aWALK008GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));
            assertTrue(db.addAttribute("aWALK008GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));
            assertTrue(db.hasAttribute("aWALK008GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));

            assertFalse(db.hasAttribute("aPARK020GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));
            assertTrue(db.addAttribute("aPARK020GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));
            assertTrue(db.hasAttribute("aPARK020GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));

            List<String> expectedList = new ArrayList<>();
            List<String> actualList = db.getByAttribute(ATTRIBUTE.COVID_SAFE,false);

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
            assertFalse(db.hasAttribute("aPARK019GG", ATTRIBUTE.COVID_SAFE, true));
            assertTrue(db.addAttribute("aPARK019GG", ATTRIBUTE.COVID_SAFE, true));
            assertTrue(db.hasAttribute("aPARK019GG", ATTRIBUTE.COVID_SAFE, true));
            db.deleteAttribute("aPARK019GG", ATTRIBUTE.COVID_SAFE, true);
            assertFalse(db.hasAttribute("aPARK019GG", ATTRIBUTE.COVID_SAFE, true));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDeleteAttributesEdge(){
        try {
            assertFalse(db.hasAttribute("aWALK008GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));
            assertTrue(db.addAttribute("aWALK008GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));
            assertTrue(db.hasAttribute("aWALK008GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));
            db.deleteAttribute("aWALK008GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false);
            assertFalse(db.hasAttribute("aWALK008GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDeleteAttributesNodeDNE(){
        try{
            List<ATTRIBUTE> expectedList = new ArrayList<ATTRIBUTE>();
            List<ATTRIBUTE> actualList = db.getAttributes("aFakeNode3",true);
            assertEquals(expectedList, actualList);

            db.deleteAttribute("aFakeNode3", ATTRIBUTE.NOT_NAVIGABLE, true);

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
            List<ATTRIBUTE> expectedList = new ArrayList<ATTRIBUTE>();
            List<ATTRIBUTE> actualList = db.getAttributes("aFakeEdge3",false);
            assertEquals(expectedList, actualList);

            db.deleteAttribute("aFakeEdge3", ATTRIBUTE.NOT_NAVIGABLE, false);

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
            List<ATTRIBUTE> expectedList = new ArrayList<ATTRIBUTE>();
            List<ATTRIBUTE> actualList = db.getAttributes("aWALK008GG",true);
            assertEquals(expectedList, actualList);

            assertTrue(db.addAttribute("aWALK008GG", ATTRIBUTE.COVID_SAFE, true));
            assertTrue(db.addAttribute("aWALK008GG", ATTRIBUTE.NOT_NAVIGABLE, true));
            assertTrue(db.addAttribute("aWALK008GG", ATTRIBUTE.HANDICAPPED_ACCESSIBLE, true));

            expectedList.add(ATTRIBUTE.COVID_SAFE);
            expectedList.add(ATTRIBUTE.NOT_NAVIGABLE);
            expectedList.add(ATTRIBUTE.HANDICAPPED_ACCESSIBLE);

            actualList = db.getAttributes("aWALK008GG",true);

            assertEquals(expectedList, actualList);

            db.clearAttributes("aWALK008GG", true);

            expectedList = new ArrayList<ATTRIBUTE>();
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
            List<ATTRIBUTE> expectedList = new ArrayList<ATTRIBUTE>();
            List<ATTRIBUTE> actualList = db.getAttributes("aPARK020GG_aWALK009GG",false);
            assertEquals(expectedList, actualList);

            assertTrue(db.addAttribute("aPARK020GG_aWALK009GG", ATTRIBUTE.COVID_SAFE, false));
            assertTrue(db.addAttribute("aPARK020GG_aWALK009GG", ATTRIBUTE.NOT_NAVIGABLE, false));
            assertTrue(db.addAttribute("aPARK020GG_aWALK009GG",ATTRIBUTE.HANDICAPPED_ACCESSIBLE, false));

            expectedList.add(ATTRIBUTE.COVID_SAFE);
            expectedList.add(ATTRIBUTE.NOT_NAVIGABLE);
            expectedList.add(ATTRIBUTE.HANDICAPPED_ACCESSIBLE);

            actualList = db.getAttributes("aPARK020GG_aWALK009GG",false);

            assertEquals(expectedList, actualList);

            db.clearAttributes("aPARK020GG_aWALK009GG", false);

            expectedList = new ArrayList<ATTRIBUTE>();
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
            List<ATTRIBUTE> expectedList = new ArrayList<ATTRIBUTE>();
            List<ATTRIBUTE> actualList = db.getAttributes("aFakeNode4",true);
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
            List<ATTRIBUTE> expectedList = new ArrayList<ATTRIBUTE>();
            List<ATTRIBUTE> actualList = db.getAttributes("aFakeEdge4",false);
            assertEquals(expectedList, actualList);

            db.clearAttributes("aFakeEdge4", false);

            actualList = db.getAttributes("aFakeEdge4",false);
            assertEquals(expectedList, actualList);
        } catch(SQLException e){
            e.printStackTrace();
            fail();
        }
    }

    //Testing Cascading

    @Test
    public void testCascadingDeleteNode(){
        try{
            List<ATTRIBUTE> expectedList = new ArrayList<ATTRIBUTE>();
            List<ATTRIBUTE> actualList = db.getAttributes("aWALK008GG",true);
            assertEquals(expectedList, actualList);

            assertTrue(db.addAttribute("aWALK008GG", ATTRIBUTE.COVID_SAFE, true));
            assertTrue(db.addAttribute("aWALK008GG", ATTRIBUTE.NOT_NAVIGABLE, true));
            assertTrue(db.addAttribute("aWALK008GG",ATTRIBUTE.HANDICAPPED_ACCESSIBLE, true));

            assertFalse(db.hasAttribute("aPARK020GG", ATTRIBUTE.COVID_SAFE, true));
            assertTrue(db.addAttribute("aPARK020GG", ATTRIBUTE.COVID_SAFE, true));
            assertTrue(db.hasAttribute("aPARK020GG", ATTRIBUTE.COVID_SAFE, true));

            expectedList.add(ATTRIBUTE.COVID_SAFE);
            expectedList.add(ATTRIBUTE.NOT_NAVIGABLE);
            expectedList.add(ATTRIBUTE.HANDICAPPED_ACCESSIBLE);

            actualList = db.getAttributes("aWALK008GG",true);

            assertEquals(expectedList, actualList);

            List<String> expectedList2 = new ArrayList<>();
            List<String> actualList2 = db.getByAttribute(ATTRIBUTE.COVID_SAFE,true);
            expectedList2.add("aWALK008GG");
            expectedList2.add("aPARK020GG");
            assertEquals(expectedList2, actualList2);

            db.deleteNode("aWALK008GG");
            expectedList2.remove("aWALK008GG");
            actualList2 = db.getByAttribute(ATTRIBUTE.COVID_SAFE,true);
            assertEquals(expectedList2, actualList2);

        } catch(SQLException e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddAttributeToNodeNotExist(){
        Exception exception = assertThrows(DerbySQLIntegrityConstraintViolationException.class, ()
                    -> db.addAttribute("aPARK030GG", ATTRIBUTE.COVID_SAFE, true));

        assertTrue(exception.getMessage().contains("caused a violation of foreign key constraint"));
    }
    

    // Emily testing getNodes and getEdges:
    @Test
    public void testGetNodeExistingIDSingle() throws SQLException {
        // declare lists and maps needed for testing
        List<Map<String,String>> testingNodesList = new ArrayList<>();
        Map<String,String> testingNodeExpectedValue = new HashMap<>();
        List<String> nodeIDs = new ArrayList<>();

        // add ids
        nodeIDs.add("Test1");

        // add elements of node to node map
        testingNodeExpectedValue.put("NODEID", "Test1");
        testingNodeExpectedValue.put("XCOORD", "12");
        testingNodeExpectedValue.put("YCOORD", "300");
        testingNodeExpectedValue.put("FLOOR", "G");
        testingNodeExpectedValue.put("BUILDING", "Mars");
        testingNodeExpectedValue.put("NODETYPE", "EXIT");
        testingNodeExpectedValue.put("LONGNAME", "Its a made up place!");
        testingNodeExpectedValue.put("SHORTNAME", "MRS");

        // add map to list of map for final expected value
        testingNodesList.add(testingNodeExpectedValue);
        // create node with given map of elements
        db.addNode(testingNodeExpectedValue);
        // test that correct list is returned
        assertEquals(testingNodesList,db.getNodes(nodeIDs));
    }
    @Test
    public void testGetNodeExistingIDMultiple() throws SQLException {
        Map<String,String> testingNodeExpectedValue1 = new HashMap<>();
        testingNodeExpectedValue1.put("NODEID", "Test1");
        testingNodeExpectedValue1.put("XCOORD", "12");
        testingNodeExpectedValue1.put("YCOORD", "300");
        testingNodeExpectedValue1.put("FLOOR", "G");
        testingNodeExpectedValue1.put("BUILDING", "Mars");
        testingNodeExpectedValue1.put("NODETYPE", "EXIT");
        testingNodeExpectedValue1.put("LONGNAME", "Its a made up place!");
        testingNodeExpectedValue1.put("SHORTNAME", "MRS");

        Map<String,String> testingNodeExpectedValue2 = new HashMap<>();
        testingNodeExpectedValue2.put("NODEID", "Test2");
        testingNodeExpectedValue2.put("XCOORD", "54");
        testingNodeExpectedValue2.put("YCOORD", "99");
        testingNodeExpectedValue2.put("FLOOR", "7");
        testingNodeExpectedValue2.put("BUILDING", "Saturn");
        testingNodeExpectedValue2.put("NODETYPE", "EXIT");
        testingNodeExpectedValue2.put("LONGNAME", "Its a made up place!");
        testingNodeExpectedValue2.put("SHORTNAME", "yeah");


        Map<String,String> testingNodeExpectedValue3 = new HashMap<>();
        testingNodeExpectedValue3.put("NODEID", "Test3");
        testingNodeExpectedValue3.put("XCOORD", "3");
        testingNodeExpectedValue3.put("YCOORD", "5");
        testingNodeExpectedValue3.put("FLOOR", "4");
        testingNodeExpectedValue3.put("BUILDING", "Jupiter");
        testingNodeExpectedValue3.put("NODETYPE", "EXIT");
        testingNodeExpectedValue3.put("LONGNAME", "Its a made up place!");
        testingNodeExpectedValue3.put("SHORTNAME", "duh");

        List<Map<String,String>> testingNodesList = Arrays.asList(testingNodeExpectedValue1, testingNodeExpectedValue2, testingNodeExpectedValue3);

        db.addNode(testingNodeExpectedValue1);
        db.addNode(testingNodeExpectedValue2);
        db.addNode(testingNodeExpectedValue3);

        assertEquals(testingNodesList,db.getNodes(Arrays.asList("Test1","MessupSomething","Test2","Test3")));
    }

    //arrays.asList()  don't hardcode use of arraylist
    @Test
    public void testGetNodeNonexistentID() throws SQLException { // if a value doesn't exist it should return an empty list
        List<Map<String,String>> testingNodeExpectedValue = new ArrayList<>();
        List<String> falseIDs = new ArrayList<>();
        falseIDs.add("fakestuff");
        falseIDs.add("somethingrandom");
        falseIDs.add("           ");
        assertEquals(testingNodeExpectedValue,db.getNodes(falseIDs));
    }

    @Test
    public void testGetNodeNoIDsErrors() {
        assertThrows(SQLException.class, () -> db.getNodes(new ArrayList<>()));
    }

    @Test
    public void testGetEdgeExistingIDSingle() throws SQLException {
        // test lists
        List<Map<String,String>> testingEdgeList = new ArrayList<>();
        Map<String,String> testingEdgeExpectedValue = new HashMap<>();
        // give edge elements to map
        testingEdgeExpectedValue.put("EDGEID","helloworld");
        testingEdgeExpectedValue.put("STARTNODE", "aPARK024GG");
        testingEdgeExpectedValue.put("ENDNODE", "aWALK012GG");
        // add map to list of map
        testingEdgeList.add(testingEdgeExpectedValue);
        // add edge id to list of edge ids
        List<String> edgeIDs = new ArrayList<>();
        edgeIDs.add("helloworld");

        // create new db edge
        db.addEdge(testingEdgeExpectedValue);

        // test that returns correct values
        assertEquals(testingEdgeList,db.getEdges(edgeIDs));
    }

    @Test
    public void testGetEdgeExistingIDMultiple() throws SQLException {
        List<String> edgeIDs = Arrays.asList("aPARK001GG_aWALK001GG","aPARK009GG_aWALK011GG","aWALK004GG_aEXIT00101","bogusEdge");

        List<Map<String,String>> testingEdgeList = edgeIDs.stream().map((id) -> {
            try {
                return db.getEdge(id);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        for(Map<String,String> edge : testingEdgeList.subList(0,2)) {
            assertTrue(db.getEdges(edgeIDs).contains(edge));
        }
        assertFalse(db.getEdges(edgeIDs).contains(testingEdgeList.get(3)));
    }

    @Test
    public void testGetEdgeNonexistentID() throws SQLException {
        List<Map<String,String>> testingEdgeExpectedValue = new ArrayList<>();
        List<String> falseIDs = new ArrayList<>();
        falseIDs.add("fakestuff");
        falseIDs.add("somethingrandom");
        falseIDs.add("           ");
        assertEquals(testingEdgeExpectedValue,db.getEdges(falseIDs));
    }

    @Test
    public void testGetEdgesNoIDsError() throws SQLException {
        assertThrows(SQLException.class, () -> db.getEdges(new ArrayList<>()));
    }
}
