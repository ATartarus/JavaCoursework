package components.addDialog;

import entity.Validator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class AddDialog extends JDialog {
    private final JPanel container;
    private final JTextField textField;
    private final JButton okButton;
    private final JButton cancelButton;
    public DialogListener listener;
    public AddDialog(JFrame parent, String title, boolean modal) {
        super(parent, title, modal);
        container = new JPanel(new FlowLayout());
        textField = new JTextField(20);
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        config();
    }

    private void config() {
        okButton.addActionListener(e -> onOKClick());
        cancelButton.addActionListener(e -> onCancelClick());

        textField.setPreferredSize(new Dimension(0, 27));
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
            listener.tryPerformAction(textField.getText());
        } catch (Exception e) {
            Validator.showValidationError(this, e.getMessage(), "Error");
            return;
        }
        dispose();
    }

    private void onCancelClick() {
        dispose();
    }

    public void addDialogListener(DialogListener l) { listener = l;}
}
