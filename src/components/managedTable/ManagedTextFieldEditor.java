package components.managedTable;

import entity.Data;
import components.managedTextField.ManagedTextField;

import javax.swing.*;
import java.awt.*;

public class ManagedTextFieldEditor extends DefaultCellEditor {
    private final ManagedTextField component;
    public ManagedTextFieldEditor(Data.Type type) {
        super(new JTextField());
        component = new ManagedTextField(type);
        if (type.equals(Data.Type.Mark))
            component.setHorizontalAlignment(SwingConstants.CENTER);
        component.setBackground(new Color(225, 225, 255));
        component.setValidBorder(null);
        editorComponent = component;
    }

    @Override
    public Object getCellEditorValue() {
        return component.getData();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        ManagedTextField textField = (ManagedTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        if (value instanceof Data data) {
            textField.setText(data.getText());
        } else {
            textField.setText(null);
        }
        return textField;
    }

    @Override
    public boolean stopCellEditing() {
        component.validateText(false);
        return super.stopCellEditing();
    }
}
