package entity;

/**
 * Represents student with name, serial number, ex result and mark.
 */
public class Student {
    private Data name;
    private Data serialNumber;
    private String result;
    private Data mark;
    /**
     * Contains all possible states of result field.
     */
    public final static String[] results = new String[] {"Зачтено", "Не зачтено", "Не явился"};

    /**
     * Creates class instance with specified parameters.
     * @param name Students name.
     * @param serialNumber Students serial number.
     * @param result Students ex result.
     * @param mark Students mark.
     */
    public Student(Data name, Data serialNumber, String result, Data mark) {
        this.name = name;
        this.serialNumber = serialNumber;
        this.result = (result == null) ? results[0] : result;
        this.mark = mark;
    }

    public Data getName() {
        return name;
    }

    public void setName(Data name) {
        this.name = name;
    }

    public Data getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Data serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Data getMark() {
        return mark;
    }

    public void setMark(Data mark) {
        this.mark = mark;
    }
}
