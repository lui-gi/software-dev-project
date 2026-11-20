package ems.service;

import ems.db.EmployeeDao;
import ems.db.PayrollDao;
import ems.db.ReportDao;
import ems.error.AccessDeniedException;
import ems.error.ValidationException;
import ems.model.Employee;
import ems.model.PayStatement;
import ems.model.ReportRow;
import ems.model.User;
import java.time.LocalDate;
import java.util.List;

public class ReportService {
    private final PayrollDao payroll;
    private final ReportDao reports;
    private final EmployeeDao employees;

    public ReportService(PayrollDao payroll, ReportDao reports, EmployeeDao employees) {
        this.payroll = payroll; this.reports = reports; this.employees = employees;
    }

    /** Most recent pay date first. A General Employee can only read their own history. */
    public List<PayStatement> payHistory(User user, int empID) {
        if (user == null) throw new AccessDeniedException();
        if (!user.isHrAdmin() && (user.empID() == null || user.empID() != empID)) throw new AccessDeniedException();
        return payroll.history(empID);
    }

    public List<ReportRow> payByJobTitle(User user, String month) {
        Access.requireHr(user);
        return reports.payByJobTitle(Validator.month(month));
    }

    public List<ReportRow> payByDivision(User user, String month) {
        Access.requireHr(user);
        return reports.payByDivision(Validator.month(month));
    }

    /** Hires between the two dates (inclusive), newest first. The assignment does not state a role; HR Admin only here. */
    public List<Employee> newHires(User user, String start, String end) {
        Access.requireHr(user);
        LocalDate s = Validator.date(start, "start date");
        LocalDate e = Validator.date(end, "end date");
        if (e.isBefore(s)) throw new ValidationException("End date must not be before start date");
        return employees.hiredBetween(s, e);
    }
}
