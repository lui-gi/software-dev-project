# Project Context: Company Z Employee Management System

This file describes the system that the assignment asks for: the user story, roles, functionality, database, technology, and shared constraints. It is derived from [PROJECT.md](PROJECT.md), which remains the authoritative source.

Coursework submissions, checklists, deadlines, points, and grading details live in [PROJECT_DELIVERABLES.md](PROJECT_DELIVERABLES.md). Requirement IDs in this file (for example `FR-4` or `DB-3`) are referenced from there.

## Contents

- [Company Z Situation](#company-z-situation)
- [Consulting Team Objective](#consulting-team-objective)
- [User Roles and Permissions](#user-roles-and-permissions)
- [Required Functionality](#required-functionality)
- [Database Requirements](#database-requirements)
- [Technology Requirements and Restrictions](#technology-requirements-and-restrictions)
- [Shared Operational Constraints](#shared-operational-constraints)
- [Context Ambiguities](#context-ambiguities)
- [External Resources](#external-resources)

## Company Z Situation

| Topic | Source detail |
|---|---|
| Current workflow | One HR admin person maintains all employee data using dBeaver and MySQL scripts. |
| Current security | No security, except that the HR admin uses the MySQL root account and password. |
| Employee access today | A general employee can only get a PDF of their pay stub information, and a W2 at the end of the tax year. |
| Workforce | About 100 full-time employees. There are no hourly or part-time employees. |
| Expected growth | Company Z plans to triple its workforce over the next 24 months. |

**Problems implied by the current state** (restated from the facts above, not new requirements):

- All data maintenance depends on a single HR admin using database tools and scripts directly.
- The only access control is shared use of the MySQL root account.
- Employees have no self-service way to view their own data beyond pay stub PDFs and the year-end W2.
- The system must accommodate roughly three times the current headcount within 24 months.

## Consulting Team Objective

The software development team is hired as consultants to deliver a **new, working employee management system** for Company Z.

The project also requires a **software design document** that serves as the set of requirements and designs for the system, covering:

- data schemas
- UX
- general programming components
- security levels
- class diagrams
- other design elements ("etc." in the source)

## User Roles and Permissions

| Role | Permissions stated in source |
|---|---|
| **HR Admin** | Secure logon. CRUD (create, read, update, delete) functionality on the entire database. Exclusive access to search for **editing**, updating employee data, salary increases, and the monthly pay reports by job title and by division. |
| **General employee** (full-time) | General security level. Can see their own pay and demographic data. Can search for **viewing**. Can see their own pay statement history. |

See [CA-5](#context-ambiguities) about the scope of general employee searching, and [CA-3](#context-ambiguities) about the role for the new hires report.

## Required Functionality

| ID | Feature | Role restriction |
|---|---|---|
| FR-1 | User logon and authorization. | All users |
| FR-1.1 | Secure HR admin logon that ensures CRUD functionality on the entire database. | HR Admin |
| FR-1.2 | General security for all employees to see their pay and demographic data. | General employee |
| FR-2 | Search for employee data using **name, DOB, SSN, or empID** to show their information for **EDITING**. | HR Admin only |
| FR-3 | Search for employee data using **name, DOB, SSN, or empID** to show their information for **VIEWING**. | General employee only |
| FR-4 | Update an employee's data once the employee is selected from a search result list. | HR Admin only |
| FR-5 | Update an employee's salary for an increase by a **percentage**, only when salaries are within each range amount. See [CA-2](#context-ambiguities). | HR Admin only |
| FR-6 | Reports ("several reports"): | See each |
| FR-6.1 | Full-time employee pay statement history, sorted by most recent pay date. | General employee |
| FR-6.2 | Total pay for the month by job title. | HR Admin only |
| FR-6.3 | Total pay for the month by division. | HR Admin only |
| FR-6.4 | New employee hires within a given date range. | Not stated. See [CA-3](#context-ambiguities). |
| FR-7 | Must use a MySQL database (see [TR-2](#technology-requirements-and-restrictions)). | Not applicable |

**Additional behaviors named elsewhere in the source** (they appear in deliverable instructions but describe system behavior):

| ID | Behavior | Source location |
|---|---|---|
| FR-8 | General employee enters their empID to search the database and is shown their personal data. If not found, an error message is shown to the general employee. | 11/13 individual sequence diagram |
| FR-9 | Update employee data, made general enough (using the team's new classes) to cover all employee data. | 11/20 group task (a) |
| FR-10 | Search for an employee by empID for deletion. If found, ask to confirm the delete; otherwise report no matching employee. | 11/20 group task (b) |
| FR-11 | Update salary for all employees by a given percentage when salary is less than a given amount. See [CA-2](#context-ambiguities). | 11/20 group task (c) |
| FR-12 | Increase salary by a percentage for all employees whose salaries are in a specified range. If no salaries fall in the range, that simply means no updates. See [CA-2](#context-ambiguities). | 11/20 individual sequence diagram |
| FR-13 | Add a new employee to the employeeData database. No searching is involved; empID is autogenerated for uniqueness. | 11/20 individual sequence diagram |
| FR-14 | The demonstrated system supports, for employees: insert new, search, update, delete, and payroll reports. | 12/06 video |

## Database Requirements

### Platform

| ID | Requirement |
|---|---|
| DB-1 | The database is the MySQL database named **employeeData**. It may be local or hosted on FreeSQLDatabase.com. |

### Key Relationships

| ID | Primary key | Foreign key |
|---|---|---|
| DB-2.1 | `employees.empID` | `employee_division.empID` |
| DB-2.2 | `employees.empID` | `payroll.empID` |
| DB-2.3 | `employees.empID` | `address.empID` (see [CA-1](#context-ambiguities)) |
| DB-2.4 | `employees.empID` | `employee_job_titles.empID` |
| DB-2.5 | `division.divID` | `employee_division.divID` |
| DB-2.6 | `job_titles.job_titleID` | `employee_job_titles.job_titleID` |
| DB-2.7 | `addresses.addressID` | `employees.addressID` (see [CA-1](#context-ambiguities)) |

### Third Normal Form Additions

Additional tables are required to ensure third normal form (3NF).

| ID | Table | Columns stated in source |
|---|---|---|
| DB-3 | `addresses` | `addressID`, `street`, `cityID`, `stateID`, `zip`. Holds addresses for all employees. |
| DB-4 | `cities` | `cityID`, city name (`varchar(25)`). |
| DB-5 | `states` | `StateID`, 2-letter abbreviation. Must contain all 50 states. |

**Stated rationale for DB-2.7:** if more than one person lives at the same address (for example, roommates), the address is not repeated, avoiding wasted space in the database.

### Demographic Fields

| ID | Requirement |
|---|---|
| DB-6 | Add these additional demographic items: **DOB**, **mobile/phone**, **emergency contact name**, **emergency contact mobile/phone**. The source lists them alongside the `addresses` table without naming the table that holds them. See [CA-4](#context-ambiguities). |

### Indexes

| ID | Requirement |
|---|---|
| DB-7 | A help resource is provided for creating SQL indexes after a table is created (see [External Resources](#external-resources)). The source offers it as help and does not state a specific index requirement. |

## Technology Requirements and Restrictions

| ID | Requirement |
|---|---|
| TR-1 | All code shall be **Java**. |
| TR-2 | Must use a **MySQL** database, either local or FreeSQLDatabase.com. |
| TR-3 | Deliver a **fully working UX** in either **console** or **GUI** (Swing or JavaFX) that fully supports the employeeData MySQL database. |
| TR-4 | **No micro-frameworks.** You **cannot** use HTML, JavaScript, Node.js, React, or Spring Boot. (Stated reason: too difficult to grade across a huge number of different teams.) |
| TR-5 | Source code must not be one monolithic program file. Apply cohesion and coupling with separated, meaningful components. (Points are deducted for a monolithic file; see [P5.4](PROJECT_DELIVERABLES.md#p54-java-source-zip).) |
| TR-6 | UML diagrams may be drawn or created with any tool. Hand-drawn diagrams must be very neat and dark enough to be readable and understandable. |

## Shared Operational Constraints

These apply across all submissions. Submission-specific rules are in [PROJECT_DELIVERABLES.md](PROJECT_DELIVERABLES.md#shared-submission-rules).

| ID | Constraint |
|---|---|
| OC-1 | UML diagrams are graded on correct form and on the specifics of required elements. |
| OC-2 | **No late submissions are allowed.** There is one final time, and after that it is too late. This is a strict rule so the instructor and GTAs can grade the project. |
| OC-3 | Only ONE person per team needs to upload the video (single file). |
| OC-4 | Multiple submissions can be made; the latest file will be graded. |
| OC-5 | All students in the group must make a submission for both sections (A and B), even though it is a group project, **except for the video**. |

## Context Ambiguities

These affect system design. They are flagged, not resolved. Submission-level ambiguities are in the [Ambiguity Register](PROJECT_DELIVERABLES.md#ambiguity-register).

| ID | Ambiguity | Competing statements | Needs instructor clarification |
|---|---|---|---|
| CA-1 | **Address relationship direction.** | (1) "employees.empID (primary) to address.empID (foreign)", using a table named `address` keyed to an employee. (2) A normalized `addresses` table with "addresses.addressID (primary key) to employees.addressID (foreign)", so that roommates share one address row. | Whether both relationships are required, whether `address` and `addresses` are the same table, and which direction the employee/address link should take. |
| CA-2 | **Salary update criteria.** | (1) User story: increase by a percentage "only when salaries are in each range amounts." (2) Group task (c): "by a given percentage when salary less than a given amount." (3) Individual sequence diagram: "salaries in a specified range." | Whether the criterion is a range (min and max), an upper bound only, or multiple ranges with different amounts, and whether each artifact should model its own wording. |
| CA-3 | **Role for new hires report.** | FR-6.4 "New employee hires within a given date range" is the only report with no role stated. | Whether it is HR Admin only, general employee, or both. |
| CA-4 | **Placement of demographic fields.** | DOB, mobile/phone, emergency contact name, and emergency contact mobile/phone are listed in the same sentence as the `addresses` table columns. | Which table(s) should hold these fields. |
| CA-5 | **Scope of general employee search.** | (1) FR-3 lets a general employee search by name, DOB, SSN, or empID for viewing. (2) FR-1.2 and FR-8 describe employees seeing *their own* data via their empID. | Whether a general employee may search for and view other employees' data, or only their own. |

## External Resources

| Resource | Purpose | Used by |
|---|---|---|
| https://www.w3schools.com/mysql/mysql_create_index.asp | Help resource for SQL database index creation after a table is created. | [DB-7](#indexes), [P1](PROJECT_DELIVERABLES.md#p1-individual-uml-and-database-changes) |
| https://youtu.be/HL_PkylpbG8 | Video creation source (Mac, using QuickTime). | [P5](PROJECT_DELIVERABLES.md#p5-group-working-software-video-and-java-source-zip) |
| Zoom meeting, record | Video creation source. | [P5](PROJECT_DELIVERABLES.md#p5-group-working-software-video-and-java-source-zip) |
| MS Teams meeting, record | Video creation source. | [P5](PROJECT_DELIVERABLES.md#p5-group-working-software-video-and-java-source-zip) |
| WebEx meeting, record | Video creation source. | [P5](PROJECT_DELIVERABLES.md#p5-group-working-software-video-and-java-source-zip) |
| SDD template ("from a template provided") | Required basis for the final software design document. **External dependency: not included in PROJECT.md or found in this repository.** | [P4](PROJECT_DELIVERABLES.md#p4-group-final-software-design-document) |
| dBeaver or SQL Workbench | DBMS tool used to generate the schema diagram. | [P1.5](PROJECT_DELIVERABLES.md#p15-generated-schema-diagram) |
