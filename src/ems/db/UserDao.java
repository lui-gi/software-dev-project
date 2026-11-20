package ems.db;

import ems.model.Role;
import ems.model.User;
import java.sql.*;
import java.util.Optional;
import java.util.function.Supplier;

/** Reads and writes the users table (logins). */
public class UserDao {
    /** A user plus the stored salt and password hash, both as hex text. */
    public record Credentials(User user, String salt, String hash) { }

    private final Supplier<Connection> source;

    public UserDao(Supplier<Connection> source) { this.source = source; }

    public Optional<Credentials> findByUsername(String username) {
        try (Connection c = source.get();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT userID, username, role, empID, salt, password_hash FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                User u = new User(rs.getInt("userID"), rs.getString("username"), Role.valueOf(rs.getString("role")), (Integer) rs.getObject("empID"));
                return Optional.of(new Credentials(u, rs.getString("salt"), rs.getString("password_hash")));
            }
        } catch (SQLException e) {
            throw SqlSupport.translate(e);
        }
    }

    public void insert(String username, String saltHex, String hashHex, Role role, Integer empID) {
        try (Connection c = source.get();
             PreparedStatement ps = c.prepareStatement(
                 "INSERT INTO users (username, salt, password_hash, role, empID) VALUES (?,?,?,?,?)")) {
            ps.setString(1, username); ps.setString(2, saltHex); ps.setString(3, hashHex);
            ps.setString(4, role.name()); ps.setObject(5, empID);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw SqlSupport.translate(e);
        }
    }
}
