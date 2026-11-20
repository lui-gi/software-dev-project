package ems.ui;

import ems.model.Employee;
import ems.model.SearchField;
import ems.model.User;
import ems.service.EmployeeService;
import java.util.List;

/** The shared "pick a field, type a value, see matches" screen used by both menus. */
final class SearchPrompt {
    private SearchPrompt() { }

    static List<Employee> run(Console con, EmployeeService employees, User user) {
        con.say("Search by: 1. empID  2. Name  3. Date of birth  4. SSN");
        SearchField field;
        switch (con.ask("Choose")) {
            case "1": field = SearchField.EMPID; break;
            case "2": field = SearchField.NAME; break;
            case "3": field = SearchField.DOB; break;
            case "4": field = SearchField.SSN; break;
            default: con.say("Choose 1 to 4."); return List.of();
        }
        List<Employee> rows = employees.search(user, field, con.ask("Value"));
        if (rows.isEmpty()) con.say("No employees found"); else EmployeePrinter.list(con, rows);
        return rows;
    }
}
