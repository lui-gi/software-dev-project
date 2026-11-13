Final Project

Deadlines :

Section A (individual) - 2 submissions

Section B(group) - 3 submissions

Deadlines :

11/13/2027

11/20/2027

12/06/2027
Project/System description:

User Story:

                  Your software development team is hired as consultants and tasked with delivering a new, working employee management system for company ‘Z’. The project requires a software design document as a set of requirements and designs for this software system involving its data schemas, UX, general programming components, security levels, class diagrams, etc. They currently have one HR admin person maintaining their employee data using dBeaver and MySQL scripts and not any security except for the HR admin using the MySQL root account and password. Currently a general employee can only get a pdf of their pay stub information and W2 at end of tax year. They have about 100 full-time employees (no hourly or part-time). However, they plan to triple this over the next 24 months.

 

These are several software system features/functionalities to be built:

    A user logon/authorization with this functionality:
        Secure HR admin to ensure CRUD functionality on the entire database
        A general security for all employees to see their pay and demographic data
    Search for employee data using name, DOB, SSN, empid to show their information for EDITING: HR Admin only.
    Search for employee data using name, DOB, SSN, empid to show their information for VIEWING: general employee only.
    Update an employee’s data once selected from a search result list. (HR Admin only)
    Update employee’s salary for an increase with a percentage only when salaries are in each range amounts. (HR Admin only)
    Several reports.
        Full-time employee can see their pay statement history sorted by most recent pay date. (general employee)
        Total pay for month by job title. (HR Admin only)
        Total pay for month by Division.(HR Admin only)
        New employee hires within a given date range
    Must use MySQL database. (either local or FreeSQLDatabase.com)

 

Operational Information:

    You may use any tool to draw/create UML diagrams. If you hand-draw them, be very neat and dark enough to be readable and understandable. These will be graded on correct form and specifics of required elements.
    All code shall be Java to deliver a fully working UX in either console or GUI (Swing or JavaFX) to fully support the employeeData MySQL database
    No use of micro-frameworks, you CANNOT use HTML, java script, Node.js, React, or Spring Boot. This is too difficult to grade across huge number of different teams.
    No allowed late submissions. There is one final time and after that, it is too late. This is a strict rule to allow me and GTAs to grade the project.
    Only ONE person per team need upload the video (single file). Multiple submissions can be made, the latest file will be graded.

 

 

Timeline for Deliverable Items:

    11/13/2026: 11:59PM (NO LATE PENALTY ALLOWED)  INDIVIDUAL-150pts
        One UML use case depicting the overall components and their connections (internal and external components; actors)
        One UML sequence diagram showing a general employee to input their ‘empID’ to search the database and (show) their personal data if not found, error messge is shown to the general employee.
        Create primary/foreign key connections:
            employees.empID (primary) to employee_division.empID (foreign)
            employees.empID (primary) to payroll.empID (foreign)
            employees.empID (primary) to address.empID (foreign)
            employees.empID (primary) to employee_job_titles.empID (foreign)
            employee_division.divID (foreign) to division.divID (primary)
            employee_job_titles.job_titleID (foreign) to job_titles.job_titleID (primary)
        Additional tables to ensure third normal form:
            ‘addresses’ (addressID, street, cityID, stateID, zip) for all employees and additional demographic items: (DOB, mobile/phone, emergency contact name, emergency contact mobile/phone.)
            Some additional tables: One for ‘cities’, use a cityID and city name (varchar 25). One for ‘states’, use a StateID and 2-letter abbreviation (all 50 of them).
            addresses. addressID (primary key) to employees.addressID (foreign)

This ensures if more than one person lives at the same address (roommates) it is not repeated and wasted space in the database.

        After these additions/changes, create a database schema diagram generated from a DBMS tool (dBeaver or SQL Workbench). Generate an image of the diagram.
        Help resource for SQL database index creation after table is created found here: https://www.w3schools.com/mysql/mysql_create_index.asp

 

    11/20/2026: 11:59PM (NO LATE PENALTY ALLOWED)  GROUP- 100pts
        Programming tasks (PT), 5 from user story (Design document will require and additional 5  PT)
        Test cases that ensure the features/functions are going to work and update data correctly:

        Update employee data (make it general enough using your new classes for all employee data)
        Search for employee (empID) for deletion. If found, then ask to confirm delete, otherwise no match for employee
        Update salary for all employees by a given percentage when salary less than a given amount

NOTE: Start with describing ‘a,b,c’ each as a detailed programming task, then create pass/fail test case(s) from each (minimum one per each ‘a,b,c’ but can be more than one)

 

    11/20/2026: 11:59PM (NO LATE PENALTY ALLOWED)  INDIVIDUAL-150pts

Sequence diagrams (UML) for these:

        Increase salary by % for all employees with their salaries in a specified range (what are inputs?)

NOTE: don’t worry about if there are no changed salaries in the specified range, that would indicate no updates

        Add new employee to employeeData database (NO SEARCHING, empID is autogenerated for uniqueness)

 

    12/06/2026: 11:59PM (NO LATE PENALTY ALLOWED) GROUP-100pts

Company ‘Z’ needs a final software design document from given template.

        A software design document (SDD) from a template provided. Final document in PDF that has the above items, each in  a separate section, sub-section(s), and a correct (accurate) table of contents.

 

    12/06/2026: 11:59PM (NO LATE PENALTY ALLOWED) GROUP-200pts
        One, continuous video demonstration (7 to 10 minutes max) of the working software system (for employee: insert new, search, update, delete, and payroll reports) where all of your team is maneuvering through the software’s functionality with onscreen showing and speaking showing yourself on screen. Use very limited power point in the demo (no more than 5 slides) as I want to see you present a visual demonstration of the final software system.
        Video can be given by one or many team members.
            Video creation sources:
                https://youtu.be/HL_PkylpbG8 (mac using QuickTime)
                Zoom meeting, record
                MS Teams meeting, record
                WebEx meeting, record
        This is a recorded video in a format compatible for Mac and windows: (MPEG-4, MPEG, WMV, AVI, MKV, WebM, 3GP, or MOV)
        UPLOAD your java source code files ONLY in a ZIP compressed file.  I will deduct points if one monolithic program file. REMEMBER cohesion and coupling with separated, meaningful components.

All the students in the group must make a submission for both the sections (even if it is group project, except for the video). 
