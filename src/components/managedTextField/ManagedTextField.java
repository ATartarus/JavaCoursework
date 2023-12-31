package components.managedTextField;

import entity.Data;
import entity.Validator;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Represents TextFiled with constant text validation.
 */
public class ManagedTextField extends JTextField {
    private final Timer timer;
    private final Data data;
    private Border validBorder;
    private final Border invalidBorder;

    /**
     * Creates class instance with specified data type.
     * @param type Specifies how text will be validated.
     */
    public ManagedTextField(Data.Type type) {
        this(type, 0);
    }

    /**
     * Creates class instance with specified data type and field width.
     * @param type Specifies how text will be validated.
     * @param columns Number of columns of text field.
     */
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

    /**
     * Updates data adn retrieves it.
     * @return Data instance.
     */
    public Data getData() {
        updateData();
        return data;
    }

    /**
     * Sets component Border that will be shown whenever it will contain valid Data.
     * @param border Border to be set.
     */
    public void setValidBorder(Border border) {
        validBorder = border;
        setBorder(data.isErrorDisplayNeeded() ? invalidBorder : validBorder);
    }

    private void addEventListeners() {
        addActionListener(e -> {
            checkData(true);
            formatData();
        });
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

        timer.addActionListener(ev -> checkData(false));
    }

    /**
     * Updates data and checks its validity.
     * <br/>Stops validation timer and sets appropriate border.
     * @param showMessage if true will show message in case data is invalid.
     */
    public void checkData(boolean showMessage) {
        timer.stop();
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
