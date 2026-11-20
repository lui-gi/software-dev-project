package ems.error;

/** Thrown when user input or a business rule is not satisfied. The message is safe to show to the user. */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) { super(message); }
}
