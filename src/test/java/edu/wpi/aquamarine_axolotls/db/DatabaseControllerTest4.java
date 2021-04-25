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

    private final DatabaseController db = new DatabaseController();

    public DatabaseControllerTest4() throws SQLException, IOException, URISyntaxException {
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
}
