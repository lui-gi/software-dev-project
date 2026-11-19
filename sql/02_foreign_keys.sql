-- 02_foreign_keys.sql
-- Primary/foreign key connections required for the 11/13 submission.
-- The spec's address.empID -> employees.empID link is created in 03_normalization.sql, because the addresses table is defined there.

USE employeeData;

ALTER TABLE employee_division
    ADD CONSTRAINT fk_empdiv_emp
        FOREIGN KEY (empID) REFERENCES employees (empID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT fk_empdiv_div
        FOREIGN KEY (divID) REFERENCES division (divID);

ALTER TABLE employee_job_titles
    ADD CONSTRAINT fk_empjob_emp
        FOREIGN KEY (empID) REFERENCES employees (empID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT fk_empjob_job
        FOREIGN KEY (job_titleID) REFERENCES job_titles (job_titleID);

ALTER TABLE payroll
    ADD CONSTRAINT fk_payroll_emp
        FOREIGN KEY (empID) REFERENCES employees (empID)
        ON DELETE CASCADE ON UPDATE CASCADE;
