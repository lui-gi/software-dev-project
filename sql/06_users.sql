-- 06_users.sql
-- Logins for the application. Passwords are stored as a PBKDF2 hash plus a random salt,
-- never in plain text. Accounts are created with the SeedUsers tool, not by hand.

USE employeeData;

CREATE TABLE users (
    userID         INT         NOT NULL AUTO_INCREMENT,
    username       VARCHAR(50) NOT NULL,
    password_hash  CHAR(64)    NOT NULL,
    salt           CHAR(32)    NOT NULL,
    role           ENUM('HR_ADMIN', 'EMPLOYEE') NOT NULL,
    empID          INT         NULL,
    PRIMARY KEY (userID),
    UNIQUE KEY uq_users_username (username),
    CONSTRAINT fk_users_emp FOREIGN KEY (empID) REFERENCES employees (empID)
        ON DELETE CASCADE ON UPDATE CASCADE
);
