package exceptions;

public class XMLParseException extends Exception {
    private static String fileName = "Undefined";
    public static void setFileName(String name) {
        if (name == null) fileName = "Undefined";
        else fileName = name;
    }
    public XMLParseException(String message) {
        super(fileName + ":: " + message);
    }
}
