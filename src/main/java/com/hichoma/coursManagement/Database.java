package com.hichoma.coursManagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Database {
    private final String url = "jdbc:postgresql://127.0.0.1/mini-projet-java-1";
    private final String user = "postgres";
    private final String password = "1234";

    private static Connection conn;

    public Connection connect() {
        if ( conn == null ) {
            try {
                conn = DriverManager.getConnection(url, user, password);
                System.out.println("Connected to the PostgreSQL server successfully.");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("PostegreSQL already connected.");
        }
        return conn;
    }

    public static Connection getConn() {
        return conn;
    }
}

