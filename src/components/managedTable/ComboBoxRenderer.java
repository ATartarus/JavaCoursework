package components.managedTable;

import entity.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ComboBoxRenderer extends DefaultTableCellRenderer {
    private final JComboBox<String> component;
    public ComboBoxRenderer() {
        component = new JComboBox<>(Student.results);
        component.setBorder(null);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null) {
            component.setSelectedItem(0);
        } else {
            component.setSelectedItem(value.toString());
        }
        if (isSelected) {
            component.setBackground(table.getSelectionBackground());
        } else {
            component.setBackground(null);
        }
        return component;
    }
}
