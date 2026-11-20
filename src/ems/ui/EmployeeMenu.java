package ems.ui;

import ems.model.Employee;
import ems.model.PayStatement;
import ems.model.User;
import ems.service.EmployeeService;
import ems.service.ReportService;
import java.util.List;
import java.util.Optional;

/** Menu for a General Employee: view own data, search (own record only) and see own pay history. */
final class EmployeeMenu {
    private final Console con;
    private final EmployeeService employees;
    private final ReportService reports;

    EmployeeMenu(Console con, EmployeeService employees, ReportService reports) {
        this.con = con; this.employees = employees; this.reports = reports;
    }

    void run(User user) {
        while (true) {
            con.say("\n=== Employee menu (" + user.username() + ") ===");
            con.say("1. View my information\n2. Search\n3. My pay statements\n0. Log out");
            switch (con.ask("Choose")) {
                case "1": guarded(() -> view(user)); break;
                case "2": guarded(() -> search(user)); break;
                case "3": guarded(() -> history(user)); break;
                case "0": return;
                default: con.say("Choose a number from the menu.");
            }
        }
    }

    private void view(User user) {
        Optional<Employee> e = employees.find(user, user.empID());
        if (e.isPresent()) EmployeePrinter.detail(con, e.get()); else con.say("Employee not found");
    }

    private void search(User user) {
        List<Employee> rows = SearchPrompt.run(con, employees, user);
        if (!rows.isEmpty()) EmployeePrinter.detail(con, rows.get(0));
    }

    private void history(User user) {
        List<PayStatement> rows = reports.payHistory(user, user.empID());
        if (rows.isEmpty()) { con.say("No pay statements found"); return; }
        con.say(String.format("%-12s %12s %12s %12s", "Pay date", "Earnings", "Taxes", "Net pay"));
        for (PayStatement p : rows) con.say(String.format("%-12s %12s %12s %12s", p.payDate(), p.earnings(), p.taxes(), p.net()));
    }

    private void guarded(Runnable action) {
        try { action.run(); }
        catch (EndOfInputException ex) { throw ex; }
        catch (RuntimeException ex) { con.say("Error: " + ex.getMessage()); }
    }
}
