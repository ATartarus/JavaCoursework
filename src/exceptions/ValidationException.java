package exceptions;

/**
 * Represents exception thrown when validation failed.
 * Contains message specifying what went wrong.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
