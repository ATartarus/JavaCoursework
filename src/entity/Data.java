package entity;

/**
 * Represents typed String with auto-validation
 * String is always stored in its original form despite validation result
 * String validity can be checked by isValid method
 */
public class Data {
    public enum Type {
        Group, Name, Faculty, Discipline, Year, Hours, Date, SerialNumber, Mark
    }
    private String text;
    private Type type;
    private boolean valid;
    private String message;

    public Data(Type type) {
        this(type, null);
    }

    public Data(Type type, String content) {
        this.type = type;
        setText(content);
    }

    public Data(Data prototype) {
        this.text = prototype.text;
        this.type = prototype.type;
        this.valid = prototype.valid;
        this.message = prototype.message;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isShowErrorNeeded() {
        return !valid && text != null && !text.isEmpty();
    }

    /**
     * Sets text of the data instance
     * Note: text is ALWAYS assigned, if passed string was invalid,
     * isValid method will return false
     * @param text string to assign
     * @throws IllegalArgumentException failed validation message
     */
    public void setText(String text) {
        this.text = text;
        try {
            Validator.validate(text, type);
            message = null;
            valid = true;
        } catch (Exception e) {
            message = e.getMessage();
            valid = false;
        }
    }

    public String getText() {
        return text;
    }

    public Type getType() {
        return type;
    }

    public String getValidationMessage() {
        return message;
    }
}
