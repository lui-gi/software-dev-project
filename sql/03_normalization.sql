-- 03_normalization.sql
-- Third normal form additions: states, cities, addresses, and
-- per-employee demographic columns.
-- Demographics (DOB, phone, emergency contact) live on employees, not
-- addresses, since roommates sharing an address have different values.
--
-- The spec lists two address links that point in opposite directions:
--   employees.addressID -> addresses.addressID (roommates share one row)
--   addresses.empID     -> employees.empID     (listed address.empID FK)
-- employees.addressID is the authoritative record of where an employee
-- lives. addresses.empID is kept only because the spec lists it; it is
-- nullable and carries no additional meaning.

USE employeeData;

CREATE TABLE states (
    stateID  INT     NOT NULL AUTO_INCREMENT,
    abbrev   CHAR(2) NOT NULL UNIQUE,
    PRIMARY KEY (stateID)
);

CREATE TABLE cities (
    cityID     INT         NOT NULL AUTO_INCREMENT,
    city_name  VARCHAR(25) NOT NULL,
    PRIMARY KEY (cityID)
);

CREATE TABLE addresses (
    addressID  INT         NOT NULL AUTO_INCREMENT,
    street     VARCHAR(65) NOT NULL,
    cityID     INT         NOT NULL,
    stateID    INT         NOT NULL,
    zip        VARCHAR(10) NOT NULL,
    empID      INT,
    PRIMARY KEY (addressID),
    CONSTRAINT fk_addr_city  FOREIGN KEY (cityID)  REFERENCES cities (cityID),
    CONSTRAINT fk_addr_state FOREIGN KEY (stateID) REFERENCES states (stateID),
    CONSTRAINT fk_addr_emp   FOREIGN KEY (empID)   REFERENCES employees (empID)
        ON DELETE SET NULL ON UPDATE CASCADE
);

ALTER TABLE employees
    ADD COLUMN addressID               INT,
    ADD COLUMN DOB                     DATE,
    ADD COLUMN phone                   VARCHAR(20),
    ADD COLUMN emergency_contact_name  VARCHAR(65),
    ADD COLUMN emergency_contact_phone VARCHAR(20),
    ADD CONSTRAINT fk_emp_address
        FOREIGN KEY (addressID) REFERENCES addresses (addressID)
        ON DELETE SET NULL ON UPDATE CASCADE;
