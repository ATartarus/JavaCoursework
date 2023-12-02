package exceptions;

/**
 * Represents exception thrown when parse of XML failed.
 * Contains XML file name and message of error.
 */
public class XMLParseException extends Exception {
    private static String fileName = "Undefined";

    /**
     * Sets file name that will be added to message for every new instance.
     * @param name file name.
     */
    public static void setFileName(String name) {
        if (name == null) fileName = "Undefined";
        else fileName = name;
    }
    public XMLParseException(String message) {
        super(fileName + ":: " + message);
    }
}
