package edu.wpi.cs3733.d21.teamA.db;

import edu.wpi.cs3733.d21.teamA.db.enums.TABLES;
import edu.wpi.cs3733.d21.teamA.extras.Security;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseControllerTest5 {
    private final DatabaseController db = DatabaseController.getInstance();

    public DatabaseControllerTest5() throws SQLException, IOException, URISyntaxException {
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
        user1.put("TAKENSURVEY", "NO");
        Security.addHashedPassword(user1,"aPassword");

        db.addUser(user1);

        CSVHandler csvHandler = new CSVHandler(db);
        csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.TEST_NODE_RESOURCE_PATH), TABLES.NODES, true);
        csvHandler.importCSV(DatabaseInfo.resourceAsStream(DatabaseInfo.TEST_EDGE_RESOURCE_PATH), TABLES.EDGES, true);
    }

    @AfterAll
    @BeforeAll
    static void cleanup() {
        DBTestUtil.resetDB();
    }

    // =========== TESTS =============

    //TODO: Test addSurvey with guests as user type
    @Test
    public void testAddAndGetCovidSurvey() throws SQLException {
        Map<String, String> survey = new HashMap<>();
        survey.put("USERNAME", "Seansta18");
        survey.put("AREQUAR", "No");
        survey.put("NAUSEADIARRHEA", "No");
        survey.put("SHORTBREATH", "Yes");
        survey.put("HASCOUGH", "No");
        survey.put("HASFEVER", "No");
        survey.put("NEWCHILLS", "No");
        survey.put("LOSSTASTESMELL", "No");
        survey.put("SORETHROAT", "No");
        survey.put("NASALCONGEST", "No");
        survey.put("MORETIRED", "No");
        survey.put("MUSCLEACHES", "Yes");

        db.addSurvey(survey);

        assertEquals(survey, db.getSurveyByUsername("Seansta18"));
    }

    @Test
    public void testAddCovidSurveyWithUserDNE() {
        Map<String, String> survey = new HashMap<>();
        survey.put("USERNAME", "Banana1234");
        survey.put("AREQUAR", "No");
        survey.put("NAUSEADIARRHEA", "No");
        survey.put("SHORTBREATH", "Yes");
        survey.put("HASCOUGH", "No");
        survey.put("HASFEVER", "No");
        survey.put("NEWCHILLS", "No");
        survey.put("LOSSTASTESMELL", "No");
        survey.put("SORETHROAT", "No");
        survey.put("NASALCONGEST", "No");
        survey.put("MORETIRED", "No");
        survey.put("MUSCLEACHES", "Yes");

        assertThrows(SQLException.class, () -> db.addSurvey(survey));
    }

    @Test
    public void testGetSurveyWithUserDNE() throws SQLException {
        Map<String, String> survey = new HashMap<>();
        survey.put("USERNAME", "Seansta18");
        survey.put("AREQUAR", "No");
        survey.put("NAUSEADIARRHEA", "No");
        survey.put("SHORTBREATH", "Yes");
        survey.put("HASCOUGH", "No");
        survey.put("HASFEVER", "No");
        survey.put("NEWCHILLS", "No");
        survey.put("LOSSTASTESMELL", "No");
        survey.put("SORETHROAT", "No");
        survey.put("NASALCONGEST", "No");
        survey.put("MORETIRED", "No");
        survey.put("MUSCLEACHES", "Yes");

        db.addSurvey(survey);
        assertThrows(SQLException.class, () -> db.getSurveyByUsername("Banana1234"));
    }

    @Test
    public void testHasTakenSurvey() throws SQLException
    {
        assertFalse(db.hasUserTakenCovidSurvey("Seansta18"));

        Map<String, String> survey = new HashMap<>();
        survey.put("USERNAME", "Seansta18");
        survey.put("AREQUAR", "No");
        survey.put("NAUSEADIARRHEA", "No");
        survey.put("SHORTBREATH", "Yes");
        survey.put("HASCOUGH", "No");
        survey.put("HASFEVER", "No");
        survey.put("NEWCHILLS", "No");
        survey.put("LOSSTASTESMELL", "No");
        survey.put("SORETHROAT", "No");
        survey.put("NASALCONGEST", "No");
        survey.put("MORETIRED", "No");
        survey.put("MUSCLEACHES", "Yes");

        db.addSurvey(survey);

        assertTrue(db.hasUserTakenCovidSurvey("Seansta18"));
    }
}
