package components.managedTextField;

import entity.Data;
import entity.Validator;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class ManagedTextField extends JTextField {
    private final Timer timer;
    private final Data data;
    private Border validBorder;
    private Border invalidBorder;

    public ManagedTextField(Data.Type type) {
        super();
        validBorder = getBorder();
        invalidBorder = BorderFactory.createLineBorder(Color.red, 2);

        timer = new Timer(500, null);
        data = new Data(type);
        addValidationListeners();
    }

    @Override
    public void setText(String t) {
        super.setText(t);
        try {
            data.setText(t);
        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }
        setBorder(data.isValid() ? validBorder : invalidBorder);
    }

    @Override
    public String getText() {
        return data.getText();
    }

    public Data getData() {
        return data;
    }

    public void setValidBorder(Border border) {
        validBorder = border;
        setBorder(data.isValid() ? validBorder : invalidBorder);
    }

    public void setInvalidBorder(Border border) {
        invalidBorder = border;
        setBorder(data.isValid() ? validBorder : invalidBorder);
    }


    public void addValidationListeners() {
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                timer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) { }
        });

        addActionListener(e -> validateText(true));

        timer.addActionListener(ev -> validateText(false));
    }

    public void validateText(boolean showMessage) {
        timer.stop();
        System.out.println("validation");
        try {
            data.setText(super.getText());
        } catch (IllegalArgumentException exception) {
            if (showMessage) {
                Validator.showValidationError(
                        this.getParent(), exception.getMessage(), "Error"
                );
            }
        }
        setBorder(data.isValid() ? validBorder : invalidBorder);
    }
}
