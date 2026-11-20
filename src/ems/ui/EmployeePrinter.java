package ems.ui;

import ems.model.Employee;
import java.util.List;

/** Formats employees for the screen. */
final class EmployeePrinter {
    private EmployeePrinter() { }

    static void list(Console con, List<Employee> rows) {
        con.say(String.format("%-4s %-4s %-24s %-26s %s", "No.", "ID", "Name", "Division", "Job title"));
        for (int i = 0; i < rows.size(); i++) {
            Employee e = rows.get(i);
            con.say(String.format("%-4d %-4d %-24s %-26s %s", i + 1, e.getEmpID(), e.fullName(), e.getDivisionName(), e.getJobTitle()));
        }
    }

    static void detail(Console con, Employee e) {
        con.say("----------------------------------------");
        con.say("Employee ID : " + e.getEmpID());
        con.say("Name        : " + e.fullName());
        con.say("Email       : " + e.getEmail());
        con.say("Hire date   : " + e.getHireDate());
        con.say("Salary      : " + e.getSalary());
        con.say("SSN         : " + e.getSsn());
        con.say("DOB         : " + e.getDob());
        con.say("Phone       : " + e.getPhone());
        con.say("Emergency   : " + e.getEmergencyName() + " " + e.getEmergencyPhone());
        con.say("Address     : " + e.getAddress());
        con.say("Division    : " + e.getDivID() + " " + e.getDivisionName());
        con.say("Job title   : " + e.getJobTitleID() + " " + e.getJobTitle());
        con.say("----------------------------------------");
    }
}
