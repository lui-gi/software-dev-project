package ems.error;

/** Thrown when the logged-in user's role does not allow the requested action. */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() { super("Access denied"); }
}
