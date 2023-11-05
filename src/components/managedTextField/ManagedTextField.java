package components.managedTextField;

import entity.Data;
import entity.Validator;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
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
        setBorder(data.isErrorDisplayNeeded() ? invalidBorder : validBorder);
    }

    @Override
    public String getText() {
        return super.getText();
    }

    public Data getData() {
        updateData();
        return data;
    }

    public void setValidBorder(Border border) {
        validBorder = border;
        setBorder(data.isErrorDisplayNeeded() ? invalidBorder : validBorder);
    }

    public void setInvalidBorder(Border border) {
        invalidBorder = border;
        setBorder(data.isErrorDisplayNeeded() ? invalidBorder : validBorder);
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
            checkData(true);
            formatData();
        });

        timer.addActionListener(ev -> {
            checkData(false);
        });
    }

    /**
     * Updates data and checks its validity.
     * <br/>Stops validation timer and sets appropriate border.
     * @param showMessage if true will show message in case data is invalid.
     */
    public void checkData(boolean showMessage) {
        timer.stop();
        //System.out.println("ManagedTextField:: checkData");
        updateData();
        if (showMessage && !data.isValid()) {
            JOptionPane.showMessageDialog(
                    this.getParent(),
                    data.getValidationMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        setBorder(data.isErrorDisplayNeeded() ? invalidBorder : validBorder);
    }

    /**
     * Sets data text to match JTextFiled text.
     * Unlike JTextField value data is not changed after each keystroke,
     * so call to this method is essential in order to store in data actual value.
     */
    private void updateData() {
        if (!super.getText().equals(data.getText())) {
            data.setText(super.getText());
        }
    }


    /**
     * If data is valid, changes its format according to the data type.
     */
    public void formatData() {
        updateData();
        if (data.isValid()) {
            String newText = Validator.getFormattedString(data.getText(), data.getType());
            if (!newText.equals(getText())) {
                setText(newText);
            }
        }
    }
}
