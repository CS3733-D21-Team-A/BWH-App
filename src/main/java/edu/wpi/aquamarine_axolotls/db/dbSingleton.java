package edu.wpi.aquamarine_axolotls.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbSingleton {
    private static dbSingleton instance;
    private Connection connection;
    private String url = ""; //TODO
    private String username = "root"; //wtf
    private String password = "localhost"; //wtf

    private dbSingleton() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException ex){
            System.out.println("NO CLASS BAD CODE CJ SUCKS");
        }
    }

    public Connection getConnection(){
        return connection;
    }

    public static dbSingleton getInstance() throws SQLException {
        if(instance == null){
            instance = new dbSingleton();
        } else if(instance.getConnection().isClosed()){
            instance = new dbSingleton();
        }
        return instance;
    }

}
