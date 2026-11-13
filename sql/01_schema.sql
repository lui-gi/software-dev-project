-- 01_schema.sql
-- Base tables for the employeeData database.
-- Run first on a fresh database.

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
    fed_tax       DECIMAL(7,2),
    fed_med       DECIMAL(7,2),
    fed_SS        DECIMAL(7,2),
    state_tax     DECIMAL(7,2),
    retire_401k   DECIMAL(7,2),
    health_care   DECIMAL(7,2),
    empID         INT  NOT NULL,
    PRIMARY KEY (payID)
);
