package com.ajarastay.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = System.getenv().getOrDefault("AJARA_DB_URL", "jdbc:mysql://localhost:3306/ajara_stay_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
    private static final String USER = System.getenv().getOrDefault("AJARA_DB_USER", "root");
    private static final String PASS = System.getenv().getOrDefault("AJARA_DB_PASS", "");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

