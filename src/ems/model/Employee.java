package ems.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** One employee with all demographic, address, division and job title data. */
public class Employee {
    private int empID;
    private String fname, lname, email, ssn, phone, emergencyName, emergencyPhone;
    private LocalDate hireDate, dob;
    private BigDecimal salary;
    private Address address = new Address();
    private int divID, jobTitleID;
    private String divisionName, jobTitle; // read-only, filled by searches

    public int getEmpID() { return empID; }
    public void setEmpID(int v) { empID = v; }
    public String getFname() { return fname; }
    public void setFname(String v) { fname = v; }
    public String getLname() { return lname; }
    public void setLname(String v) { lname = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate v) { hireDate = v; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal v) { salary = v; }
    public String getSsn() { return ssn; }
    public void setSsn(String v) { ssn = v; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate v) { dob = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { phone = v; }
    public String getEmergencyName() { return emergencyName; }
    public void setEmergencyName(String v) { emergencyName = v; }
    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String v) { emergencyPhone = v; }
    public Address getAddress() { return address; }
    public void setAddress(Address v) { address = v == null ? new Address() : v; }
    public int getDivID() { return divID; }
    public void setDivID(int v) { divID = v; }
    public int getJobTitleID() { return jobTitleID; }
    public void setJobTitleID(int v) { jobTitleID = v; }
    public String getDivisionName() { return divisionName; }
    public void setDivisionName(String v) { divisionName = v; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String v) { jobTitle = v; }

    public String fullName() { return fname + " " + lname; }
}
