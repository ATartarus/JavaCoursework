package components.addDialog;

/**
 * Provides methods to validate dialogs input
 * and perform some actions after validation
 */
public interface DialogListener {
    void tryAddItem(Object item) throws Exception;
}
