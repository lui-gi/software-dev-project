package ems.db;

import ems.error.DataAccessException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Opens connections to the employeeData MySQL database.
 * Settings come from environment variables so no password is stored in the source:
 * EMS_DB_URL, EMS_DB_USER, EMS_DB_PASS.
 */
public final class Database {
    private Database() { }

    public static Connection open() {
        String url = env("EMS_DB_URL", "jdbc:mysql://localhost:3306/employeeData");
        try {
            return DriverManager.getConnection(url, env("EMS_DB_USER", "ems_app"), env("EMS_DB_PASS", ""));
        } catch (SQLException e) {
            throw new DataAccessException("Cannot connect to the database: " + e.getMessage(), e);
        }
    }

    private static String env(String key, String fallback) {
        String v = System.getenv(key);
        return v == null ? fallback : v;
    }
}
