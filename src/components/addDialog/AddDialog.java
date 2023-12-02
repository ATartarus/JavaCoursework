package components.addDialog;

import components.managedTextField.ManagedTextField;
import entity.Data;
import exceptions.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Represents dialog window used for adding new items to JComboBox.
 * Uses ManagedTextField for data validation.
 */
public class AddDialog extends JDialog {
    private final JPanel container;
    private final ManagedTextField textField;
    private final JButton okButton;
    private final JButton cancelButton;
    private DialogListener listener;

    /**
     * Creates class instance with specified parameters.
     * @param parent Parent frame of this dialog.
     * @param title Title of dialog.
     * @param modal true if modal; false otherwise.
     * @param validation Variable that specifies how data will be validated.
     */
    public AddDialog(JFrame parent, String title, boolean modal, Data.Type validation) {
        super(parent, title, modal);
        container = new JPanel(new FlowLayout());
        textField = new ManagedTextField(validation, 20);
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        ActionListener[] listeners = textField.getActionListeners();
        for (ActionListener listener : listeners) {
            textField.removeActionListener(listener);
        }
        config();
    }

    private void config() {
        okButton.addActionListener(e -> onOKClick());
        cancelButton.addActionListener(e -> onCancelClick());
        okButton.setFocusPainted(false);
        cancelButton.setFocusPainted(false);

        textField.setPreferredSize(new Dimension(0, 30));
        container.add(textField);
        container.add(okButton);
        container.add(cancelButton);
        setContentPane(container);
        setResizable(false);
        setLocation(MouseInfo.getPointerInfo().getLocation());
        getRootPane().setDefaultButton(okButton);

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                "ESCAPE"
        );
        getRootPane().getActionMap().put(
                "ESCAPE",
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        onCancelClick();
                    }
                }
        );

        pack();
    }

    private void onOKClick() {
        Data userInput = new Data(textField.getData());
        try {
            if (userInput.isValid()) {
                textField.formatData();
                listener.addItem(textField.getText());
            } else {
                throw new ValidationException(userInput.getValidationMessage());
            }
        } catch (ValidationException exception) {
            textField.setText(userInput.getText());
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        dispose();
    }

    private void onCancelClick() {
        dispose();
    }

    /**
     * Adds dialog listener to this instances listener list.
     * @param l DialogListener that will be added to the list.
     */
    public void addDialogListener(DialogListener l) { listener = l;}
}
