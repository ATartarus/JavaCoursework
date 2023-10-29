package components.addDialog;

/**
 * Provides methods to validate dialogs input
 * and perform some actions after validation
 */
public interface DialogListener {
    void tryPerformAction(Object item) throws Exception;
}
