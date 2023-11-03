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

    public Data(Type type) {
        this(type, null);
    }

    public Data(Type type, String content) {
        this.type = type;
        this.text = content;
        try {
            Validator.validate(this.text,  this.type);
            valid = true;
        } catch (Exception e) {
            valid = false;
        }
    }

    public Data(Data prototype) {
        this.text = prototype.text;
        this.type = prototype.type;
        this.valid = prototype.valid;
    }

    public boolean isValid() {
        return valid;
    }

    /**
     * Sets text of the data instance
     * Note: text is ALWAYS assigned, if passed string was invalid,
     * isValid method will return false
     * @param text string to assign
     * @throws IllegalArgumentException failed validation message
     */
    public void setText(String text) throws IllegalArgumentException {
        this.text = text;
        valid = false;
        Validator.validate(text, type);
        valid = true;
    }

    public String getText() {
        return text;
    }

    public Type getType() {
        return type;
    }
}
