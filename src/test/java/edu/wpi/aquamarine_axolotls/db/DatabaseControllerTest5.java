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

public class DatabaseControllerTest5 {

    private final DatabaseController db = new DatabaseController();

    public DatabaseControllerTest5() throws SQLException, IOException, URISyntaxException {
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
            user3.put("PASSWORD", "PasswordIsMyPassword");
            db.addUser(user3);
            String generatedFAVID = db.addFavoriteNodeToUser("Alexa123", "aPARK001GG", "Favorite Bathroom");
            assertEquals(db.getFAVID("Alexa123", "Favorite Bathroom"),generatedFAVID);
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

}