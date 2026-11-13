-- 05_indexes.sql
-- Indexes for the search fields (name, DOB, SSN) and report queries.

USE employeeData;

CREATE INDEX idx_emp_name     ON employees (lname, fname);
CREATE INDEX idx_emp_dob      ON employees (DOB);
CREATE UNIQUE INDEX idx_emp_ssn ON employees (SSN);
CREATE INDEX idx_emp_hiredate ON employees (hireDate);
CREATE INDEX idx_payroll_emp_date ON payroll (empID, pay_date);
CREATE INDEX idx_city_name    ON cities (city_name);
