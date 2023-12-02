package entity;

import exceptions.ValidationException;

/**
 * Represents typed String with auto-validation.
 * String is always stored in its original form despite validation result.
 * String validity can be checked by isValid method.
 */
public class Data {
    /**
     * Represents all types that have unique validation.
     */
    public enum Type {
        Group, Name, Faculty, Discipline, Year, Hours, Date, SerialNumber, Mark
    }
    private String text;
    private final Type type;
    private boolean valid;
    private String message;

    /**
     * Creates class instance with specified type.
     * @param type type of data stored in this instance.
     */
    public Data(Type type) {
        this(type, null);
    }

    /**
     * Creates class instance with specified type and initial data.
     * @param type of data stored in this instance.
     * @param content Data that will be stored.
     */
    public Data(Type type, String content) {
        this.type = type;
        setText(content);
    }

    /**
     * Creates copy of passed Data.
     * @param prototype Data instance to copy.
     */
    public Data(Data prototype) {
        this.text = prototype.text;
        this.type = prototype.type;
        this.valid = prototype.valid;
        this.message = prototype.message;
    }

    /**
     * Checks if data is valid.
     * <br/>Null and empty string considered invalid.
     * @return true if valid, false otherwise.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Checks conditions to see if an error should be displayed.
     * @return true if error should be displayed, otherwise false.
     */
    public boolean isErrorDisplayNeeded() {
        return !valid && text != null && !text.isEmpty();
    }

    /**
     * Sets text of the data instance.
     * Note: text is ALWAYS assigned, if passed string was invalid,
     * isValid method will return false.
     * @param text string to assign.
     */
    public void setText(String text) {
        this.text = text;
        try {
            Validator.validate(text, type);
            message = null;
            valid = true;
        } catch (ValidationException e) {
            message = e.getMessage();
            valid = false;
        }
    }

    /**
     * Retrieves text of stored data.
     * @return Data text.
     */
    public String getText() {
        return text;
    }

    /**
     * Retrieves data type.
     * @return Data type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Retrieves validation message if data is invalid.
     * @return Validation message if invalid; null otherwise.
     */
    public String getValidationMessage() {
        return message;
    }
}
