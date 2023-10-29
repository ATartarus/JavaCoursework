package entity;

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
