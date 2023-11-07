package components.managedTable;

import entity.Data;
import components.managedTextField.ManagedTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class ManagedTextFieldEditor extends DefaultCellEditor {
    private final ManagedTextField component;
    public ManagedTextFieldEditor(Data.Type type) {
        super(new JTextField());
        component = new ManagedTextField(type);
        if (type.equals(Data.Type.Mark))
            component.setHorizontalAlignment(SwingConstants.CENTER);
        component.setBackground(new Color(225, 225, 255));
        component.setValidBorder(null);
        component.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }
            @Override
            public void focusLost(FocusEvent e) {
                stopCellEditing();
            }
        });
        ActionListener[] listeners = component.getActionListeners();
        for (ActionListener listener : listeners) {
            component.removeActionListener(listener);
        }
        editorComponent = component;
    }

    @Override
    public Object getCellEditorValue() {
        if (component.getData().isValid()) {
            component.formatData();
        }
        return component.getData();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        ManagedTextField textField = (ManagedTextField)
                super.getTableCellEditorComponent(table, value, isSelected, row, column);
        if (value instanceof Data data) {
            textField.setText(data.getText());
        } else {
            textField.setText(null);
        }
        return textField;
    }
}
