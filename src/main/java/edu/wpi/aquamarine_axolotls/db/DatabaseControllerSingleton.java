package edu.wpi.aquamarine_axolotls.db;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseControllerSingleton {

    private static DatabaseController instance;
    private Connection connection;


    private DatabaseControllerSingleton() throws SQLException {
        try {
            instance = new DatabaseController();
        } catch (SQLException | IOException | URISyntaxException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * getinstance of dbcontroller to then use
     * @return instance of dbcontroller
     * @throws SQLException
     * @throws IOException
     * @throws URISyntaxException
     */
    public static DatabaseController getInstance() throws SQLException, IOException, URISyntaxException {
        if(instance == null){
             instance = new DatabaseController();
        }
        return instance;
    }

}
