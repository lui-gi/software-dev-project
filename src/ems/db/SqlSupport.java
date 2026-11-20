package ems.db;

import ems.error.DataAccessException;
import ems.error.ValidationException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/** Small helpers shared by the DAOs. */
final class SqlSupport {
    private SqlSupport() { }

    /** Turns a SQLException into an exception with a message the user can understand. */
    static RuntimeException translate(SQLException e) {
        if (e instanceof SQLIntegrityConstraintViolationException) {
            String m = String.valueOf(e.getMessage());
            if (m.contains("Duplicate entry")) return new ValidationException("That value already exists (for example the SSN is already used)");
            return new ValidationException("A related record does not exist (check division, job title or address)");
        }
        return new DataAccessException("Database error: " + e.getMessage(), e);
    }

    static void rollbackQuietly(Connection c) {
        try { c.rollback(); } catch (SQLException ignored) { /* connection is closed next */ }
    }
}
