-- 99_sample_data.sql
-- Small sample dataset for testing searches and reports.

USE employeeData;

INSERT INTO cities (city_name) VALUES ('Atlanta'), ('Marietta');

INSERT INTO addresses (street, cityID, stateID, zip) VALUES
('123 Peachtree St', 1, (SELECT stateID FROM states WHERE abbrev = 'GA'), '30303'),
('45 Roswell Rd',    2, (SELECT stateID FROM states WHERE abbrev = 'GA'), '30060');

INSERT INTO division (divID, name, city, addressLine1, state, country, postalCode) VALUES
(1, 'Technology Engineering', 'Atlanta', '200 17th St NW', 'GA', 'United States', '30363'),
(2, 'Marketing',              'Atlanta', '200 17th St NW', 'GA', 'United States', '30363');

INSERT INTO job_titles (job_titleID, job_title) VALUES
(100, 'Software Engineer'),
(200, 'Marketing Manager');

-- Employees 1 and 2 are roommates sharing addressID 1.
INSERT INTO employees
(fname, lname, email, hireDate, salary, SSN, addressID, DOB, phone,
 emergency_contact_name, emergency_contact_phone) VALUES
('Ada',   'Lovelace', 'ada@z.com',   '2024-01-15', 95000.00, '111-11-1111', 1, '1990-12-10', '404-555-0101', 'Byron Lovelace', '404-555-0199'),
('Alan',  'Turing',   'alan@z.com',  '2025-03-01', 88000.00, '222-22-2222', 1, '1991-06-23', '404-555-0102', 'Ethel Turing',   '404-555-0198'),
('Grace', 'Hopper',   'grace@z.com', '2026-06-20', 72000.00, '333-33-3333', 2, '1992-12-09', '770-555-0103', 'Vincent Hopper', '770-555-0197');

INSERT INTO employee_division (empID, divID) VALUES (1, 1), (2, 1), (3, 2);
INSERT INTO employee_job_titles (empID, job_titleID) VALUES (1, 100), (2, 100), (3, 200);

INSERT INTO payroll (pay_date, earnings, fed_tax, fed_med, fed_SS, state_tax, retire_401k, health_care, empID) VALUES
('2026-10-31', 3653.85, 584.62, 52.98, 226.54, 182.69, 109.62, 31.11, 1),
('2026-10-31', 3384.62, 541.54, 49.08, 209.85, 169.23, 101.54, 31.11, 2),
('2026-10-31', 2769.23, 443.08, 40.15, 171.69, 138.46,  83.08, 31.11, 3);
