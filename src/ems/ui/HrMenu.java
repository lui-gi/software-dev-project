package ems.ui;

import ems.model.Employee;
import ems.model.ReportRow;
import ems.model.User;
import ems.service.EmployeeService;
import ems.service.ReportService;
import ems.service.SalaryService;
import ems.service.Validator;
import java.util.List;
import java.util.Optional;

/** Menu for the HR Admin: search and edit, add, delete, salary increases and reports. */
final class HrMenu {
    private final Console con;
    private final EmployeeService employees;
    private final SalaryService salaries;
    private final ReportService reports;

    HrMenu(Console con, EmployeeService employees, SalaryService salaries, ReportService reports) {
        this.con = con; this.employees = employees; this.salaries = salaries; this.reports = reports;
    }

    void run(User user) {
        while (true) {
            con.say("\n=== HR Admin menu (" + user.username() + ") ===");
            con.say("1. Search and edit employee\n2. Add new employee\n3. Delete employee\n"
                + "4. Raise salaries below an amount\n5. Raise salaries in a range\n"
                + "6. Report: monthly pay by job title\n7. Report: monthly pay by division\n8. Report: new hires\n0. Log out");
            String choice = con.ask("Choose");
            if (choice.equals("0")) return;
            try {
                switch (choice) {
                    case "1": edit(user); break;
                    case "2": add(user); break;
                    case "3": delete(user); break;
                    case "4": raiseBelow(user); break;
                    case "5": raiseRange(user); break;
                    case "6": printTotals(reports.payByJobTitle(user, con.ask("Month (yyyy-mm)")), "Job title"); break;
                    case "7": printTotals(reports.payByDivision(user, con.ask("Month (yyyy-mm)")), "Division"); break;
                    case "8": newHires(user); break;
                    default: con.say("Choose a number from the menu.");
                }
            } catch (EndOfInputException ex) {
                throw ex;
            } catch (RuntimeException ex) {
                con.say("Error: " + ex.getMessage());
            }
        }
    }

    private void edit(User user) {
        List<Employee> rows = SearchPrompt.run(con, employees, user);
        if (rows.isEmpty()) return;
        int pick = Validator.wholeNumber(con.ask("Number of the employee to edit (see No. column)"), "number");
        if (pick < 1 || pick > rows.size()) throw new ems.error.ValidationException("Invalid number: choose from the list");
        Employee e = rows.get(pick - 1);
        EmployeePrinter.detail(con, e);
        con.say("Type new values. Press Enter to keep the current value.");
        EmployeeForm.fill(con, e);
        employees.update(user, e);
        con.say("Employee updated");
    }

    private void add(User user) {
        Employee e = new Employee();
        con.say("Enter the new employee's details (empID is generated automatically).");
        EmployeeForm.fill(con, e);
        con.say("Employee added, empID = " + employees.add(user, e));
    }

    private void delete(User user) {
        int id = Validator.wholeNumber(con.ask("Employee ID to delete"), "employee ID");
        Optional<Employee> e = employees.findForDelete(user, id);
        if (e.isEmpty()) { con.say("No match for employee"); return; }
        EmployeePrinter.detail(con, e.get());
        if (!con.confirm("Delete this employee?")) { con.say("Delete cancelled"); return; }
        employees.delete(user, id);
        con.say("Employee deleted");
    }

    private void raiseBelow(User user) {
        var pct = Validator.decimal(con.ask("Raise percent (for example 5 for 5%)"), "percent");
        var limit = Validator.decimal(con.ask("Only salaries below this amount"), "amount");
        int n = salaries.raiseBelow(user, pct, limit);
        con.say(n == 0 ? "No salaries updated" : "Salaries updated: " + n);
    }

    private void raiseRange(User user) {
        var pct = Validator.decimal(con.ask("Raise percent (for example 5 for 5%)"), "percent");
        var min = Validator.decimal(con.ask("Minimum salary"), "minimum salary");
        var max = Validator.decimal(con.ask("Maximum salary"), "maximum salary");
        int n = salaries.raiseBetween(user, pct, min, max);
        con.say(n == 0 ? "No salaries updated" : "Salaries updated: " + n);
    }

    private void printTotals(List<ReportRow> rows, String heading) {
        if (rows.isEmpty()) { con.say("No payroll for this month"); return; }
        con.say(String.format("%-28s %14s", heading, "Total pay"));
        for (ReportRow r : rows) con.say(String.format("%-28s %14s", r.label(), r.total()));
    }

    private void newHires(User user) {
        List<Employee> rows = reports.newHires(user, con.ask("Start date (yyyy-mm-dd)"), con.ask("End date (yyyy-mm-dd)"));
        if (rows.isEmpty()) { con.say("No new hires in that range"); return; }
        con.say(String.format("%-4s %-24s %-12s %s", "ID", "Name", "Hire date", "Job title"));
        for (Employee e : rows) con.say(String.format("%-4d %-24s %-12s %s", e.getEmpID(), e.fullName(), e.getHireDate(), e.getJobTitle()));
    }
}
