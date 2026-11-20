package ems.model;

/** A logged-in account. empID is null for an HR Admin who is not an employee record. */
public record User(int userID, String username, Role role, Integer empID) {
    public boolean isHrAdmin() { return role == Role.HR_ADMIN; }
}
