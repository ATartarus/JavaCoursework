package components.managedTable;

import entity.Student;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;

public class ComboBoxEditor extends DefaultCellEditor {
    private final JComboBox<String> component;
    public ComboBoxEditor() {
        super(new JTextField());
        setClickCountToStart(1);
        component = new JComboBox<>(Student.results);
        component.setBorder(null);
        editorComponent = component;
        component.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                component.getRootPane().getRootPane().repaint();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                component.getRootPane().getRootPane().repaint();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JComboBox<?> c = (JComboBox<?>) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        c.setSelectedItem(value);
        return c;
    }

    @Override
    public Object getCellEditorValue() {
        return component.getSelectedItem();
    }
}
