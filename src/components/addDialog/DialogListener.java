package components.addDialog;

import exceptions.ValidationException;

/**
 * Provides methods to validate dialogs input
 * and perform some actions after validation
 */
public interface DialogListener {
    void addItem(String str) throws ValidationException;
}
