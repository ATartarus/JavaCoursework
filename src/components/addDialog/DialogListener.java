package components.addDialog;

import exceptions.ValidationException;

/**
 * Represents interface used in pair with AddDialog in order to add new items to JComboBoxes.
 */
public interface DialogListener {
    /**
     * This method is called whenever is needed to try to add new item to JComboBox model.
     * @param str New item.
     * @throws ValidationException If str parameter is invalid.
     */
    void addItem(String str) throws ValidationException;
}
