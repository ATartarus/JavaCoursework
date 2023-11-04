package components.addDialog;

import components.managedTextField.ManagedTextField;
import entity.Data;
import entity.Validator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class AddDialog extends JDialog {
    private final JPanel container;
    private final ManagedTextField textField;
    private final JButton okButton;
    private final JButton cancelButton;
    public DialogListener listener;
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
        try {
            Data value = textField.getData();
            if (value.isValid()) {
                listener.tryAddItem(value.getText());
            } else {
                throw new Exception(value.getValidationMessage());
            }
        } catch (Exception exception) {
            Validator.showValidationError(this, exception.getMessage(), "Error");
            return;
        }
        dispose();
    }

    private void onCancelClick() {
        dispose();
    }

    public void addDialogListener(DialogListener l) { listener = l;}
}
