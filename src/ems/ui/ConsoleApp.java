package ems.ui;

import ems.db.*;
import ems.model.User;
import ems.service.*;
import java.util.Optional;

/** Entry point. Wires the layers together, logs the user in and opens the menu for their role. */
public final class ConsoleApp {
    private static final int MAX_ATTEMPTS = 3;

    private ConsoleApp() { }

    public static void main(String[] args) {
        var source = (java.util.function.Supplier<java.sql.Connection>) Database::open;
        var employeeDao = new EmployeeDao(source);
        var auth = new AuthService(new UserDao(source));
        var employees = new EmployeeService(employeeDao);
        var salaries = new SalaryService(employeeDao);
        var reports = new ReportService(new PayrollDao(source), new ReportDao(source), employeeDao);
        Console con = new Console();

        con.say("Company Z Employee Management System");
        try {
            Optional<User> user = login(con, auth);
            if (user.isEmpty()) { con.say("Too many failed attempts. Goodbye."); return; }
            if (user.get().isHrAdmin()) new HrMenu(con, employees, salaries, reports).run(user.get());
            else new EmployeeMenu(con, employees, reports).run(user.get());
        } catch (RuntimeException ex) {
            con.say("Error: " + ex.getMessage());
        }
        con.say("Goodbye.");
    }

    private static Optional<User> login(Console con, AuthService auth) {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            Optional<User> user = auth.login(con.ask("Username"), con.askPassword("Password"));
            if (user.isPresent()) return user;
            con.say("Invalid username or password");
        }
        return Optional.empty();
    }
}
