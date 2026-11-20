package ems.tools;

import ems.db.Database;
import ems.db.UserDao;
import ems.model.Role;
import ems.service.AuthService;
import java.util.function.Supplier;

/**
 * Creates demo logins for the sample data: one HR Admin and one account per sample employee.
 * Usage: set EMS_SEED_PASSWORD, then run this class once after loading the SQL scripts.
 */
public final class SeedUsers {
    private SeedUsers() { }

    public static void main(String[] args) {
        String password = System.getenv("EMS_SEED_PASSWORD");
        if (password == null || password.length() < 8) {
            System.err.println("Set EMS_SEED_PASSWORD (at least 8 characters) first.");
            System.exit(1);
        }
        Supplier<java.sql.Connection> source = Database::open;
        AuthService auth = new AuthService(new UserDao(source));
        auth.createUser("hradmin", password, Role.HR_ADMIN, null);
        String[] names = {"ada", "alan", "grace"};
        for (int i = 0; i < names.length; i++) auth.createUser(names[i], password, Role.EMPLOYEE, i + 1);
        System.out.println("Created hradmin, ada, alan and grace.");
    }
}
