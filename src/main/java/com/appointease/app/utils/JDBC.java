package com.appointease.app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A helper interface for JDBC.
 */
public abstract class JDBC {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    private static String password = "Passw0rd!"; // Password
    public static Connection connection;  // Connection Interface

    /**
     * Opens a connection to the database.
     *
     * @return The connection.
     */
    public static Connection openConnection() {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Gets the connection.
     *
     * @return The connection.
     */
    public static Connection getConnection() {
        return connection;
    }

    /**
     * Closes the connection.
     */
    public static void closeConnection() {
        // Ignore exceptions (probably some race condition) as the program is closing anyway
        try {
            connection.close();
            System.out.println("Connection closed!");
        } catch(SQLException e) {
            // System.out.println("Error:" + e.getMessage());
            // e.printStackTrace();
        } catch(Exception e) {
            // e.printStackTrace();
        }
    }
}
