-- 01_schema.sql
-- base tables for the employeeData database
-- run this first

DROP DATABASE IF EXISTS employeeData;
CREATE DATABASE employeeData;
USE employeeData;

CREATE TABLE employees (
    empID      INT          NOT NULL AUTO_INCREMENT,
    fname      VARCHAR(65)  NOT NULL,
    lname      VARCHAR(65)  NOT NULL,
    email      VARCHAR(65)  NOT NULL,
    hireDate   DATE,
    salary     DECIMAL(10,2) NOT NULL,
    SSN        VARCHAR(12),
    PRIMARY KEY (empID)
);

CREATE TABLE division (
    divID         INT          NOT NULL,
    name          VARCHAR(100) NOT NULL,
    city          VARCHAR(50)  NOT NULL,
    addressLine1  VARCHAR(50)  NOT NULL,
    addressLine2  VARCHAR(50),
    state         VARCHAR(50),
    country       VARCHAR(50)  NOT NULL,
    postalCode    VARCHAR(15)  NOT NULL,
    PRIMARY KEY (divID)
);

CREATE TABLE job_titles (
    job_titleID  INT          NOT NULL,
    job_title    VARCHAR(125) NOT NULL,
    PRIMARY KEY (job_titleID)
);

CREATE TABLE employee_division (
    empID  INT NOT NULL,
    divID  INT NOT NULL,
    PRIMARY KEY (empID)
);

CREATE TABLE employee_job_titles (
    empID        INT NOT NULL,
    job_titleID  INT NOT NULL,
    PRIMARY KEY (empID)
);

CREATE TABLE payroll (
    payID         INT  NOT NULL AUTO_INCREMENT,
    pay_date      DATE,
    earnings      DECIMAL(8,2),
    taxes         DECIMAL(8,2),  -- total of all withholdings/deductions
    empID        INT  NOT NULL,
    PRIMARY KEY (payID)
);
