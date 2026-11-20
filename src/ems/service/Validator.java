package ems.service;

import ems.error.ValidationException;
import ems.model.Address;
import ems.model.Employee;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/** Input rules. Each failure throws a ValidationException that names the bad field. */
public final class Validator {
    private static final Pattern SSN = Pattern.compile("\\d{3}-\\d{2}-\\d{4}");
    private Validator() { }

    public static void employee(Employee e) {
        require(e.getFname(), "First name");
        require(e.getLname(), "Last name");
        require(e.getEmail(), "Email");
        if (!e.getEmail().contains("@")) throw new ValidationException("Invalid email: it must contain '@'");
        if (e.getSsn() == null || !SSN.matcher(e.getSsn()).matches()) throw new ValidationException("Invalid SSN: use the format ###-##-####");
        if (e.getHireDate() == null) throw new ValidationException("Hire date is required");
        if (e.getDob() == null) throw new ValidationException("Date of birth is required");
        if (!e.getDob().isBefore(LocalDate.now())) throw new ValidationException("Invalid date of birth: it must be in the past");
        if (e.getSalary() == null || e.getSalary().signum() <= 0) throw new ValidationException("Invalid salary: it must be greater than 0");
        if (e.getDivID() <= 0) throw new ValidationException("Division is required");
        if (e.getJobTitleID() <= 0) throw new ValidationException("Job title is required");
        address(e.getAddress());
    }

    private static void address(Address a) {
        if (a == null || a.isEmpty()) return;
        require(a.getStreet(), "Street");
        require(a.getCity(), "City");
        if (a.getState() == null || !a.getState().matches("[A-Za-z]{2}")) throw new ValidationException("Invalid state: use the 2-letter abbreviation");
        a.setState(a.getState().toUpperCase());
        require(a.getZip(), "Zip");
    }

    public static void ssn(String v) {
        if (v == null || !SSN.matcher(v).matches()) throw new ValidationException("Invalid SSN: use the format ###-##-####");
    }

    public static int wholeNumber(String v, String label) {
        try { return Integer.parseInt(v.trim()); }
        catch (RuntimeException e) { throw new ValidationException("Invalid " + label + ": enter a whole number"); }
    }

    public static LocalDate date(String v, String label) {
        try { return LocalDate.parse(v.trim()); }
        catch (RuntimeException e) { throw new ValidationException("Invalid " + label + ": use yyyy-mm-dd"); }
    }

    public static YearMonth month(String v) {
        try { return YearMonth.parse(v.trim()); }
        catch (DateTimeParseException | NullPointerException e) { throw new ValidationException("Invalid month: use yyyy-mm"); }
    }

    public static BigDecimal positive(String v, String label) {
        BigDecimal n = decimal(v, label);
        if (n.signum() <= 0) throw new ValidationException("Invalid " + label + ": it must be greater than 0");
        return n;
    }

    public static BigDecimal decimal(String v, String label) {
        try { return new BigDecimal(v.trim()); }
        catch (RuntimeException e) { throw new ValidationException("Invalid " + label + ": enter a number"); }
    }

    private static void require(String v, String label) {
        if (v == null || v.isBlank()) throw new ValidationException(label + " is required");
    }
}
