package ems.error;

/** Wraps a database failure so the upper layers never handle SQLException. */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) { super(message, cause); }
}
