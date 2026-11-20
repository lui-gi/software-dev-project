package ems.ui;

import ems.model.Employee;
import ems.service.Validator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * One table of fields drives both "add" and "edit", so every employee field is handled the same way.
 * Typing nothing keeps the current value.
 */
final class EmployeeForm {
    private record Field(String label, Function<Employee, String> get, BiConsumer<Employee, String> set) { }

    private static final List<Field> FIELDS = List.of(
        new Field("First name", Employee::getFname, Employee::setFname),
        new Field("Last name", Employee::getLname, Employee::setLname),
        new Field("Email", Employee::getEmail, Employee::setEmail),
        new Field("Hire date (yyyy-mm-dd)", e -> str(e.getHireDate()), (e, v) -> e.setHireDate(Validator.date(v, "hire date"))),
        new Field("Salary", e -> str(e.getSalary()), (e, v) -> e.setSalary(Validator.decimal(v, "salary"))),
        new Field("SSN (###-##-####)", Employee::getSsn, Employee::setSsn),
        new Field("Date of birth (yyyy-mm-dd)", e -> str(e.getDob()), (e, v) -> e.setDob(Validator.date(v, "date of birth"))),
        new Field("Phone", Employee::getPhone, Employee::setPhone),
        new Field("Emergency contact name", Employee::getEmergencyName, Employee::setEmergencyName),
        new Field("Emergency contact phone", Employee::getEmergencyPhone, Employee::setEmergencyPhone),
        new Field("Street", e -> e.getAddress().getStreet(), (e, v) -> e.getAddress().setStreet(v)),
        new Field("City", e -> e.getAddress().getCity(), (e, v) -> e.getAddress().setCity(v)),
        new Field("State (2 letters)", e -> e.getAddress().getState(), (e, v) -> e.getAddress().setState(v)),
        new Field("Zip", e -> e.getAddress().getZip(), (e, v) -> e.getAddress().setZip(v)),
        new Field("Division ID", e -> e.getDivID() == 0 ? "" : String.valueOf(e.getDivID()), (e, v) -> e.setDivID(Validator.wholeNumber(v, "division ID"))),
        new Field("Job title ID", e -> e.getJobTitleID() == 0 ? "" : String.valueOf(e.getJobTitleID()), (e, v) -> e.setJobTitleID(Validator.wholeNumber(v, "job title ID")))
    );

    private EmployeeForm() { }

    /** Asks for each field and applies the answers to e. Bad answers are reported and asked again. */
    static void fill(Console con, Employee e) {
        for (Field f : FIELDS) {
            while (true) {
                String current = f.get().apply(e);
                String answer = con.ask(f.label() + (current == null || current.isBlank() ? "" : " [" + current + "]"));
                if (answer.isEmpty()) break;
                try { f.set().accept(e, answer); break; }
                catch (RuntimeException ex) { con.say("  " + ex.getMessage()); }
            }
        }
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }
}
