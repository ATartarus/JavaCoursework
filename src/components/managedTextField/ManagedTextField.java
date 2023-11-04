package components.managedTextField;

import entity.Data;
import entity.Validator;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ManagedTextField extends JTextField {
    private final Timer timer;
    private final Data data;
    private Border validBorder;
    private Border invalidBorder;

    public ManagedTextField(Data.Type type) {
        this(type, 0);
    }

    public ManagedTextField(Data.Type type, int columns) {
        super(columns);
        validBorder = getBorder();
        invalidBorder = BorderFactory.createLineBorder(Color.red, 2);

        timer = new Timer(500, null);
        data = new Data(type);
        addEventListeners();
    }

    @Override
    public void setText(String t) {
        super.setText(t);
        data.setText(t);
        setBorder(data.isShowErrorNeeded() ? invalidBorder : validBorder);
    }

    @Override
    public String getText() {
        return super.getText();
    }

    public Data getData() {
        setText(super.getText());
        return data;
    }

    public void setValidBorder(Border border) {
        validBorder = border;
        setBorder(data.isShowErrorNeeded() ? invalidBorder : validBorder);
    }

    public void setInvalidBorder(Border border) {
        invalidBorder = border;
        setBorder(data.isShowErrorNeeded() ? invalidBorder : validBorder);
    }


    public void addEventListeners() {
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

        addActionListener(e -> {
            validateText(true);
            formatData();
        });

        timer.addActionListener(ev -> {
            if (this.hasFocus()) {
                validateText(false);
            }
        });

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {}

            @Override
            public void focusLost(FocusEvent e) {
                formatData();
            }
        });
    }

    public void validateText(boolean showMessage) {
        timer.stop();
        System.out.println("ManagedTextField:: validation");
        data.setText(super.getText());
        if (showMessage) {
            Validator.showValidationError(
                    this.getParent(), data.getValidationMessage(), "Error"
            );
        }
        setBorder(data.isShowErrorNeeded() ? invalidBorder : validBorder);
    }

    public void formatData() {
        if (data.isValid()) {
            String newText = Validator.getFormattedString(data.getText(), data.getType());
            setText(newText);
        }
    }
}
