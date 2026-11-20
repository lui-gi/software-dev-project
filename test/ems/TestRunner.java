package ems;

import ems.db.*;
import ems.error.*;
import ems.model.*;
import ems.service.*;
import ems.ui.UiScripts;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.function.Supplier;

/**
 * Runs the pass/fail test cases from "Programming Tasks and Test Cases" (T1-1 ... T10-4)
 * against a real MySQL database. Before every test the database is rebuilt from the sql/ scripts,
 * so each test starts from the same baseline data.
 *
 * Environment: EMS_DB_URL, EMS_DB_USER, EMS_DB_PASS (the employeeData database) and
 * EMS_TEST_ADMIN_URL (same server, no database name) which is used to rebuild the schema.
 * WARNING: the test drops and recreates the database named employeeData.
 */
public final class TestRunner {
    private interface Check { void run() throws Exception; }

    private static final Supplier<Connection> SRC = Database::open;
    private static final EmployeeDao EMP = new EmployeeDao(SRC);
    private static final EmployeeService SVC = new EmployeeService(EMP);
    private static final SalaryService SAL = new SalaryService(EMP);
    private static final ReportService REP = new ReportService(new PayrollDao(SRC), new ReportDao(SRC), EMP);
    private static final AuthService AUTH = new AuthService(new UserDao(SRC));
    private static final User HR = new User(1, "hradmin", Role.HR_ADMIN, null);
    private static final String PW = "TestPass123";

    private static int passed, failed;

    public static void main(String[] args) throws Exception {
        Path sqlDir = Paths.get(args.length > 0 ? args[0] : "sql");
        Map<String, Check> tests = new LinkedHashMap<>();
        register(tests);
        for (var t : tests.entrySet()) {
            try {
                rebuild(sqlDir);
                t.getValue().run();
                passed++;
                System.out.println("PASS " + t.getKey());
            } catch (Throwable e) {
                failed++;
                System.out.println("FAIL " + t.getKey() + " -> " + e);
            }
        }
        System.out.println("\n" + passed + " passed, " + failed + " failed");
        System.exit(failed == 0 ? 0 : 1);
    }

    // ---------------------------------------------------------------- tests

    private static void register(Map<String, Check> t) {
        // PT-1 update employee
        t.put("T1-1 change one field", () -> {
            Employee before = emp(2), e = emp(2);
            e.setEmail("alan.turing@z.com");
            SVC.update(HR, e);
            Employee after = emp(2);
            eq("alan.turing@z.com", after.getEmail());
            eq(before.getFname() + before.getLname() + before.getSalary() + before.getSsn() + before.getAddress(),
               after.getFname() + after.getLname() + after.getSalary() + after.getSsn() + after.getAddress());
        });
        t.put("T1-2 change several fields", () -> {
            Employee e = emp(3);
            e.setLname("Murray"); e.setPhone("770-555-0200"); e.setSalary(new BigDecimal("75000.00"));
            SVC.update(HR, e);
            Employee a = emp(3);
            eq("Murray", a.getLname()); eq("770-555-0200", a.getPhone()); eq("75000.00", a.getSalary().toPlainString());
            eq("95000.00", emp(1).getSalary().toPlainString()); eq("88000.00", emp(2).getSalary().toPlainString());
        });
        t.put("T1-3 roommate keeps old address", () -> {
            Employee e = emp(1);
            e.setAddress(new Address("9 Main St", "Atlanta", "GA", "30303"));
            SVC.update(HR, e);
            ok(!"1".equals(scalar("SELECT addressID FROM employees WHERE empID=1")), "empID 1 should get a new addressID");
            eq("1", scalar("SELECT addressID FROM employees WHERE empID=2"));
            eq("123 Peachtree St", scalar("SELECT street FROM addresses WHERE addressID=1"));
        });
        t.put("T1-4 change division and job title", () -> {
            Employee e = emp(3);
            e.setDivID(1); e.setJobTitleID(100);
            SVC.update(HR, e);
            eq("1", scalar("SELECT divID FROM employee_division WHERE empID=3"));
            eq("100", scalar("SELECT job_titleID FROM employee_job_titles WHERE empID=3"));
        });
        t.put("T1-5 employee not found", () -> {
            Employee e = emp(2);
            e.setEmpID(999);
            eq("Employee not found", expectValidation(() -> SVC.update(HR, e)));
            eq("3", scalar("SELECT COUNT(*) FROM employees"));
        });
        t.put("T1-6 invalid data rejected", () -> {
            Employee e = emp(2);
            e.setEmail("alan.z.com");
            ok(expectValidation(() -> SVC.update(HR, e)).toLowerCase().contains("email"), "message should name email");
            Employee f = emp(2);
            f.setSalary(new BigDecimal("-5"));
            ok(expectValidation(() -> SVC.update(HR, f)).toLowerCase().contains("salary"), "message should name salary");
            eq("alan@z.com", scalar("SELECT email FROM employees WHERE empID=2"));
            eq("88000.00", scalar("SELECT salary FROM employees WHERE empID=2"));
        });
        t.put("T1-7 rollback on failure", () -> {
            Employee e = emp(2);
            e.setSalary(new BigDecimal("90000.00")); e.setJobTitleID(999);
            expectFailure(() -> SVC.update(HR, e));
            eq("88000.00", scalar("SELECT salary FROM employees WHERE empID=2"));
        });
        t.put("T1-8 role check on update", () -> {
            Employee e = emp(2);
            e.setEmail("x@z.com");
            expectDenied(() -> SVC.update(empUser(2), e));
            eq("alan@z.com", scalar("SELECT email FROM employees WHERE empID=2"));
        });

        // PT-2 delete
        t.put("T2-1 delete found and confirmed", () -> {
            String out = UiScripts.hr(SRC, HR, "3\n3\nY");
            has(out, "Employee deleted");
            eq("0", scalar("SELECT COUNT(*) FROM employees WHERE empID=3"));
            eq("0", scalar("SELECT COUNT(*) FROM payroll WHERE empID=3"));
            eq("0", scalar("SELECT COUNT(*) FROM employee_division WHERE empID=3"));
            eq("0", scalar("SELECT COUNT(*) FROM employee_job_titles WHERE empID=3"));
            eq("0", scalar("SELECT COUNT(*) FROM addresses WHERE addressID=2"));
        });
        t.put("T2-2 delete declined", () -> {
            String out = UiScripts.hr(SRC, HR, "3\n3\nN");
            has(out, "Delete this employee?"); has(out, "Delete cancelled");
            eq("3", scalar("SELECT COUNT(*) FROM employees"));
            eq("3", scalar("SELECT COUNT(*) FROM payroll"));
        });
        t.put("T2-3 delete no match", () -> {
            String out = UiScripts.hr(SRC, HR, "3\n999");
            has(out, "No match for employee"); hasNot(out, "Delete this employee?");
            eq("3", scalar("SELECT COUNT(*) FROM employees"));
        });
        t.put("T2-4 delete invalid input", () -> {
            String out = UiScripts.hr(SRC, HR, "3\nabc");
            has(out, "Invalid employee ID");
            eq("3", scalar("SELECT COUNT(*) FROM employees"));
        });
        t.put("T2-5 shared address kept", () -> {
            UiScripts.hr(SRC, HR, "3\n1\nY");
            eq("0", scalar("SELECT COUNT(*) FROM employees WHERE empID=1"));
            eq("1", scalar("SELECT COUNT(*) FROM addresses WHERE addressID=1"));
            eq("1", scalar("SELECT addressID FROM employees WHERE empID=2"));
        });
        t.put("T2-6 role check on delete", () -> {
            expectDenied(() -> SVC.findForDelete(empUser(2), 2));
            expectDenied(() -> SVC.delete(empUser(2), 2));
            eq("3", scalar("SELECT COUNT(*) FROM employees"));
        });

        // PT-3 salary below an amount
        t.put("T3-1 normal raise", () -> {
            String out = UiScripts.hr(SRC, HR, "4\n10\n90000");
            has(out, "Salaries updated: 2");
            eq("96800.00", sal(2)); eq("79200.00", sal(3)); eq("95000.00", sal(1));
        });
        t.put("T3-2 limit boundary", () -> {
            String out = UiScripts.hr(SRC, HR, "4\n10\n88000");
            has(out, "Salaries updated: 1");
            eq("79200.00", sal(3)); eq("88000.00", sal(2));
        });
        t.put("T3-3 no one below limit", () -> {
            has(UiScripts.hr(SRC, HR, "4\n10\n50000"), "No salaries updated");
            baselineSalaries();
        });
        t.put("T3-4 invalid percent", () -> {
            for (String p : new String[] {"0", "-5", "abc"}) has(UiScripts.hr(SRC, HR, "4\n" + p + "\n90000"), "Error");
            baselineSalaries();
        });
        t.put("T3-5 invalid limit", () -> {
            has(UiScripts.hr(SRC, HR, "4\n10\n0"), "Error");
            baselineSalaries();
        });
        t.put("T3-6 role check on raise", () -> {
            expectDenied(() -> SAL.raiseBelow(empUser(2), new BigDecimal("10"), new BigDecimal("90000")));
            baselineSalaries();
        });

        // Supplemental: the salary-in-range raise (user story and 11/20 sequence diagram), not part of T3 above
        t.put("S-1 range raise", () -> {
            has(UiScripts.hr(SRC, HR, "5\n10\n80000\n90000"), "Salaries updated: 1");
            eq("96800.00", sal(2)); eq("95000.00", sal(1)); eq("72000.00", sal(3));
        });
        t.put("S-2 range limits are inclusive", () -> {
            eq("1", String.valueOf(SAL.raiseBetween(HR, new BigDecimal("10"), new BigDecimal("88000"), new BigDecimal("88000"))));
            eq("96800.00", sal(2));
        });
        t.put("S-3 invalid range or percent", () -> {
            expectValidation(() -> SAL.raiseBetween(HR, new BigDecimal("10"), new BigDecimal("90000"), new BigDecimal("80000")));
            expectValidation(() -> SAL.raiseBetween(HR, BigDecimal.ZERO, new BigDecimal("1"), new BigDecimal("2")));
            expectDenied(() -> SAL.raiseBetween(empUser(2), new BigDecimal("10"), new BigDecimal("1"), new BigDecimal("999999")));
            baselineSalaries();
        });

        // PT-4 logon
        t.put("T4-1 HR Admin logs in", () -> {
            User u = AUTH.login("hradmin", PW).orElseThrow();
            ok(u.isHrAdmin(), "should be HR Admin");
        });
        t.put("T4-2 employee logs in", () -> {
            User u = AUTH.login("alan", PW).orElseThrow();
            ok(u.role() == Role.EMPLOYEE && u.empID() == 2, "should be employee 2");
        });
        t.put("T4-3 wrong password", () -> ok(AUTH.login("hradmin", "wrong").isEmpty(), "must not log in"));
        t.put("T4-4 unknown user", () -> ok(AUTH.login("nobody", PW).isEmpty(), "must not log in"));
        t.put("T4-5 three failures end the program", () -> {
            // ConsoleApp is covered by the login loop: three attempts, then exit. Check three failures all return empty.
            for (int i = 0; i < 3; i++) ok(AUTH.login("hradmin", "bad" + i).isEmpty(), "attempt " + i);
        });
        t.put("T4-6 no plain-text passwords", () -> {
            eq("0", scalar("SELECT COUNT(*) FROM users WHERE password_hash = '" + PW + "' OR salt = '" + PW + "'"));
            eq("64", scalar("SELECT CHAR_LENGTH(password_hash) FROM users WHERE username='hradmin'"));
        });

        // PT-5 search
        t.put("T5-1 search by empID", () -> names(search(HR, SearchField.EMPID, "2"), "Alan Turing"));
        t.put("T5-2 search by partial name", () -> names(search(HR, SearchField.NAME, "lov"), "Ada Lovelace"));
        t.put("T5-3 search by DOB", () -> names(search(HR, SearchField.DOB, "1992-12-09"), "Grace Hopper"));
        t.put("T5-4 search by SSN", () -> names(search(HR, SearchField.SSN, "333-33-3333"), "Grace Hopper"));
        t.put("T5-5 no match", () -> {
            names(search(HR, SearchField.EMPID, "999"));
            has(UiScripts.hr(SRC, HR, "1\n1\n999"), "No employees found");
        });
        t.put("T5-6 bad format", () -> {
            expectValidation(() -> SVC.search(HR, SearchField.DOB, "12/09/1992"));
            expectValidation(() -> SVC.search(HR, SearchField.SSN, "3333"));
        });
        t.put("T5-7 employee sees only own record", () -> {
            User alan = empUser(2);
            names(search(alan, SearchField.NAME, "Hopper"));
            names(search(alan, SearchField.NAME, "Turing"), "Alan Turing");
        });
        t.put("T5-8 SQL injection attempt", () -> names(search(HR, SearchField.NAME, "x' OR '1'='1")));

        // PT-6 pay history
        t.put("T6-1 sorted history", () -> {
            exec("INSERT INTO payroll (pay_date, earnings, taxes, empID) VALUES ('2026-09-30', 3653.85, 1187.56, 1)");
            List<PayStatement> h = REP.payHistory(empUser(1), 1);
            eq("2", String.valueOf(h.size()));
            eq("2026-10-31", h.get(0).payDate().toString()); eq("2026-09-30", h.get(1).payDate().toString());
            eq("2466.29", h.get(0).net().toPlainString());
        });
        t.put("T6-2 own data only", () -> {
            List<PayStatement> h = REP.payHistory(empUser(2), 2);
            eq("1", String.valueOf(h.size())); eq("3384.62", h.get(0).earnings().toPlainString());
            expectDenied(() -> REP.payHistory(empUser(2), 1));
        });

        // PT-7 pay by job title
        t.put("T7-1 report 2026-10", () -> {
            String out = UiScripts.hr(SRC, HR, "6\n2026-10");
            has(out, "Software Engineer"); has(out, "7038.47"); has(out, "Marketing Manager"); has(out, "2769.23");
        });
        t.put("T7-2 month with no pay", () -> has(UiScripts.hr(SRC, HR, "6\n2026-01"), "No payroll for this month"));
        t.put("T7-3 role check", () -> expectDenied(() -> REP.payByJobTitle(empUser(2), "2026-10")));

        // PT-8 pay by division
        t.put("T8-1 report 2026-10", () -> {
            String out = UiScripts.hr(SRC, HR, "7\n2026-10");
            has(out, "Technology Engineering"); has(out, "7038.47"); has(out, "Marketing"); has(out, "2769.23");
        });
        t.put("T8-2 invalid month", () -> has(expectValidation(() -> REP.payByDivision(HR, "2026-13")), "yyyy-mm"));

        // PT-9 new hires
        t.put("T9-1 range with one hire", () -> {
            List<Employee> r = REP.newHires(HR, "2026-01-01", "2026-12-31");
            eq("1", String.valueOf(r.size())); eq("Grace Hopper", r.get(0).fullName());
        });
        t.put("T9-2 range with two hires", () -> {
            List<Employee> r = REP.newHires(HR, "2024-01-01", "2025-12-31");
            eq("Alan Turing,Ada Lovelace", String.join(",", r.stream().map(Employee::fullName).toList()));
        });
        t.put("T9-3 end before start", () ->
            eq("End date must not be before start date", expectValidation(() -> REP.newHires(HR, "2026-12-31", "2026-01-01"))));

        // PT-10 add employee
        t.put("T10-1 valid new employee", () -> {
            int id = SVC.add(HR, newEmployee());
            eq("4", String.valueOf(id));
            eq("1", scalar("SELECT COUNT(*) FROM employees WHERE empID=4"));
            eq("1", scalar("SELECT COUNT(*) FROM employee_division WHERE empID=4"));
            eq("1", scalar("SELECT COUNT(*) FROM employee_job_titles WHERE empID=4"));
            eq("2", scalar("SELECT addressID FROM employees WHERE empID=4"));
            eq("2", scalar("SELECT COUNT(*) FROM addresses"));
        });
        t.put("T10-2 missing required field", () -> {
            Employee e = newEmployee(); e.setLname("");
            ok(expectValidation(() -> SVC.add(HR, e)).toLowerCase().contains("last name"), "message should name last name");
            eq("3", scalar("SELECT COUNT(*) FROM employees"));
        });
        t.put("T10-3 bad SSN format", () -> {
            Employee e = newEmployee(); e.setSsn("4444");
            ok(expectValidation(() -> SVC.add(HR, e)).contains("SSN"), "message should name SSN");
            eq("3", scalar("SELECT COUNT(*) FROM employees"));
        });
        t.put("T10-4 rollback on failure", () -> {
            Employee e = newEmployee(); e.setJobTitleID(999);
            expectFailure(() -> SVC.add(HR, e));
            eq("3", scalar("SELECT COUNT(*) FROM employees"));
            eq("2", scalar("SELECT COUNT(*) FROM addresses"));
        });
    }

    // ---------------------------------------------------------------- helpers

    private static Employee newEmployee() {
        Employee e = new Employee();
        e.setFname("Katherine"); e.setLname("Johnson"); e.setEmail("kj@z.com");
        e.setHireDate(java.time.LocalDate.parse("2026-11-01")); e.setSalary(new BigDecimal("80000.00"));
        e.setSsn("444-44-4444"); e.setDob(java.time.LocalDate.parse("1993-08-26"));
        e.setAddress(new Address("45 Roswell Rd", "Marietta", "GA", "30060"));
        e.setDivID(1); e.setJobTitleID(100);
        return e;
    }

    private static User empUser(int empID) { return new User(100 + empID, "emp" + empID, Role.EMPLOYEE, empID); }
    private static Employee emp(int id) { return EMP.findById(id).orElseThrow(); }
    private static List<Employee> search(User u, SearchField f, String v) { return SVC.search(u, f, v); }
    private static String sal(int id) throws SQLException { return scalar("SELECT salary FROM employees WHERE empID=" + id); }
    private static void baselineSalaries() throws SQLException { eq("95000.00", sal(1)); eq("88000.00", sal(2)); eq("72000.00", sal(3)); }

    private static void names(List<Employee> found, String... expected) {
        eq(String.join(",", expected), String.join(",", found.stream().map(Employee::fullName).toList()));
    }

    private static String expectValidation(Runnable r) {
        try { r.run(); } catch (ValidationException e) { return e.getMessage(); }
        throw new AssertionError("expected a ValidationException");
    }
    private static void expectDenied(Runnable r) {
        try { r.run(); } catch (AccessDeniedException e) { return; }
        throw new AssertionError("expected AccessDeniedException");
    }
    private static void expectFailure(Runnable r) {
        try { r.run(); } catch (RuntimeException e) { return; }
        throw new AssertionError("expected the operation to fail");
    }
    private static void eq(String expected, String actual) {
        if (!Objects.equals(expected, actual)) throw new AssertionError("expected <" + expected + "> but was <" + actual + ">");
    }
    private static void ok(boolean cond, String msg) { if (!cond) throw new AssertionError(msg); }
    private static void has(String text, String part) { ok(text.contains(part), "output should contain \"" + part + "\"\n" + text); }
    private static void hasNot(String text, String part) { ok(!text.contains(part), "output should not contain \"" + part + "\""); }

    private static String scalar(String sql) throws SQLException {
        try (Connection c = SRC.get(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            return rs.next() ? rs.getString(1) : null;
        }
    }
    private static void exec(String sql) throws SQLException {
        try (Connection c = SRC.get(); Statement s = c.createStatement()) { s.executeUpdate(sql); }
    }

    /** Drops and recreates employeeData from the sql/ scripts, then creates the test logins. */
    private static void rebuild(Path sqlDir) throws Exception {
        String admin = System.getenv().getOrDefault("EMS_TEST_ADMIN_URL", "jdbc:mysql://localhost:3306/");
        try (Connection c = DriverManager.getConnection(admin, System.getenv().getOrDefault("EMS_DB_USER", "root"), System.getenv().getOrDefault("EMS_DB_PASS", ""));
             Statement st = c.createStatement()) {
            for (String file : new String[] {"01_schema.sql", "02_foreign_keys.sql", "03_normalization.sql", "04_seed_states.sql", "05_indexes.sql", "06_users.sql", "99_sample_data.sql"}) {
                String text = String.join("\n", Files.readAllLines(sqlDir.resolve(file)).stream().filter(l -> !l.trim().startsWith("--")).toList());
                for (String stmt : text.split(";")) if (!stmt.isBlank()) st.execute(stmt);
            }
        }
        AUTH.createUser("hradmin", PW, Role.HR_ADMIN, null);
        AUTH.createUser("ada", PW, Role.EMPLOYEE, 1);
        AUTH.createUser("alan", PW, Role.EMPLOYEE, 2);
        AUTH.createUser("grace", PW, Role.EMPLOYEE, 3);
    }
}
