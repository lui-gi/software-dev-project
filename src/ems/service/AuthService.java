package ems.service;

import ems.db.UserDao;
import ems.model.Role;
import ems.model.User;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Optional;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Logon. Passwords are never stored: only a PBKDF2 hash and a random salt. */
public class AuthService {
    private static final int ITERATIONS = 120_000;
    private final UserDao users;

    public AuthService(UserDao users) { this.users = users; }

    /** Returns the user when the username and password match, otherwise empty (same result for either mistake). */
    public Optional<User> login(String username, String password) {
        if (username == null || password == null) return Optional.empty();
        Optional<UserDao.Credentials> found = users.findByUsername(username.trim());
        String salt = found.map(UserDao.Credentials::salt).orElse("00".repeat(16));
        String candidate = hash(password, salt);
        if (found.isEmpty()) return Optional.empty();
        boolean same = MessageDigest.isEqual(candidate.getBytes(), found.get().hash().getBytes());
        return same ? Optional.of(found.get().user()) : Optional.empty();
    }

    /** Used by the seed tool to create an account. */
    public void createUser(String username, String password, Role role, Integer empID) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        String saltHex = HexFormat.of().formatHex(salt);
        users.insert(username, saltHex, hash(password, saltHex), role, empID);
    }

    static String hash(String password, String saltHex) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), HexFormat.of().parseHex(saltHex), ITERATIONS, 256);
            return HexFormat.of().formatHex(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded());
        } catch (Exception e) {
            throw new IllegalStateException("Password hashing is not available", e);
        }
    }
}
