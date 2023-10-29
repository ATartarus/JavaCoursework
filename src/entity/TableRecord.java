package entity;

public class TableRecord {
    private final int ID;
    private Data name;
    private Data serialNumber;
    private String result;
    private Data mark;
    public final static String[] results = new String[] {"Зачтено", "Не зачтено", "Не явился"};

    public TableRecord(int ID, Data name, Data serialNumber, String result, Data mark) {
        this.ID = ID;
        this.name = name;
        this.serialNumber = serialNumber;
        this.result = (result == null) ? results[0] : result;
        this.mark = mark;
    }

    public int getID() {
        return ID;
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
