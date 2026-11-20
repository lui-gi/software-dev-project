package ems.service;

import ems.db.EmployeeDao;
import ems.error.AccessDeniedException;
import ems.error.ValidationException;
import ems.model.Employee;
import ems.model.SearchField;
import ems.model.User;
import java.util.List;
import java.util.Optional;

/** Search, update, add and delete of employees, with role and input checks. */
public class EmployeeService {
    private final EmployeeDao dao;

    public EmployeeService(EmployeeDao dao) { this.dao = dao; }

    /** HR Admin sees every match; a General Employee only ever sees their own record. */
    public List<Employee> search(User user, SearchField field, String value) {
        if (user == null) throw new AccessDeniedException();
        if (value == null || value.isBlank()) throw new ValidationException("Enter a search value");
        switch (field) {
            case EMPID: Validator.wholeNumber(value, "employee ID"); break;
            case DOB:   Validator.date(value, "date of birth"); break;
            case SSN:   Validator.ssn(value); break;
            default:    break;
        }
        List<Employee> found = dao.search(field, value.trim());
        if (user.isHrAdmin()) return found;
        return found.stream().filter(e -> user.empID() != null && e.getEmpID() == user.empID()).toList();
    }

    public Optional<Employee> find(User user, int empID) {
        if (user == null) throw new AccessDeniedException();
        if (!user.isHrAdmin() && (user.empID() == null || user.empID() != empID)) throw new AccessDeniedException();
        return dao.findById(empID);
    }

    public void update(User user, Employee e) {
        Access.requireHr(user);
        Validator.employee(e);
        dao.update(e);
    }

    /** Returns the new empID. */
    public int add(User user, Employee e) {
        Access.requireHr(user);
        Validator.employee(e);
        return dao.insert(e);
    }

    /** Looks up the employee that is about to be deleted so the UI can ask for confirmation. */
    public Optional<Employee> findForDelete(User user, int empID) {
        Access.requireHr(user);
        return dao.findById(empID);
    }

    /** Call only after the user confirmed. */
    public void delete(User user, int empID) {
        Access.requireHr(user);
        if (!dao.delete(empID)) throw new ValidationException("No match for employee");
    }
}
