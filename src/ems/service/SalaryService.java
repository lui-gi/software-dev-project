package ems.service;

import ems.db.EmployeeDao;
import ems.error.ValidationException;
import ems.model.User;
import java.math.BigDecimal;

/** Percentage salary increases (HR Admin only). Each method returns how many salaries changed. */
public class SalaryService {
    private final EmployeeDao dao;

    public SalaryService(EmployeeDao dao) { this.dao = dao; }

    public int raiseBelow(User user, BigDecimal percent, BigDecimal limit) {
        Access.requireHr(user);
        if (percent == null || percent.signum() <= 0) throw new ValidationException("Invalid percent: it must be greater than 0");
        if (limit == null || limit.signum() <= 0) throw new ValidationException("Invalid limit: it must be greater than 0");
        return dao.raiseSalaryBelow(percent, limit);
    }

    public int raiseBetween(User user, BigDecimal percent, BigDecimal min, BigDecimal max) {
        Access.requireHr(user);
        if (percent == null || percent.signum() <= 0) throw new ValidationException("Invalid percent: it must be greater than 0");
        if (min == null || min.signum() < 0) throw new ValidationException("Invalid minimum salary");
        if (max == null || max.compareTo(min) < 0) throw new ValidationException("Maximum salary must not be below the minimum");
        return dao.raiseSalaryBetween(percent, min, max);
    }
}
