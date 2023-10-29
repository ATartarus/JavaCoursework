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
    private String str;
    private Type type;
    private boolean valid;

    public Data(Type type) {
        this.type = type;
        str = null;
        valid = true;
    }

    public Data(Data prototype) {
        this.str = prototype.str;
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
     * @param str string to assign
     * @throws IllegalArgumentException failed validation message
     */
    public void setText(String str) throws IllegalArgumentException {
        this.str = str;
        valid = false;
        Validator.validate(str, type);
        valid = true;
    }

    public String getText() {
        return str;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
