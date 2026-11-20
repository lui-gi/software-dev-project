# Build, Run and Test

Console application in Java (no frameworks). Needs Java 17 or newer, a MySQL 8 (or newer) server, and the MySQL JDBC driver (`mysql-connector-j`, a single jar).

## 1. Create the database

Run the scripts in `sql/` in this order (DBeaver or the `mysql` client):

`01_schema.sql`, `02_foreign_keys.sql`, `03_normalization.sql`, `04_seed_states.sql`, `05_indexes.sql`, `06_users.sql`, `99_sample_data.sql` (sample data, optional).

`01_schema.sql` drops and recreates the database named `employeeData`.

## 2. Create a database account for the program

The program must not use the MySQL root account. Create a limited one (choose your own password):

```sql
CREATE USER 'ems_app'@'localhost' IDENTIFIED BY '<choose a password>';
GRANT SELECT, INSERT, UPDATE, DELETE ON employeeData.* TO 'ems_app'@'localhost';
```

## 3. Compile

The MySQL driver jar is already in `lib/`. From the project root:

```bash
mkdir -p out
javac -d out -cp "lib/*" $(find src -name '*.java')
```

## 4. Create the demo logins (once)

```bash
export EMS_DB_URL='jdbc:mysql://localhost:3306/employeeData'
export EMS_DB_USER=ems_app
export EMS_DB_PASS='<your password>'
export EMS_SEED_PASSWORD='<password for the demo logins, 8+ characters>'
java -cp "out:lib/*" ems.tools.SeedUsers
```

This creates the logins `hradmin` (HR Admin) and `ada`, `alan`, `grace` (General Employees for empID 1 to 3). The seed tool needs INSERT on `users`, which the account above has.

## 5. Run

```bash
java -cp "out:lib/*" EmployeeManagementSystem
```

## 6. Run the test cases

The tests rebuild the database from `sql/` before every test, so run them against a throwaway server, never against real data. The test account must be allowed to drop and create databases.

```bash
export EMS_TEST_ADMIN_URL='jdbc:mysql://localhost:3306/'
mkdir -p out-test
javac -d out-test -cp "out:lib/*" $(find test -name '*.java')
java -cp "out-test:out:lib/*" ems.TestRunner sql
```

Each line prints `PASS` or `FAIL` with the test ID from "Programming Tasks and Test Cases" (T1-1 to T10-4).
