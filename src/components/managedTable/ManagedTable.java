package components.managedTable;

import entity.Data;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.EventObject;


public class ManagedTable extends JTable {
    public ManagedTable() {
        setCellSelectionEnabled(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getTableHeader().setReorderingAllowed(false);
        ManagedTableModel model = new ManagedTableModel();
        setModel(model);

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (isEditing()) {
                    DefaultCellEditor editor = (DefaultCellEditor) getCellEditor(getEditingRow(), getEditingColumn());
                    Component component = editor.getComponent();
                    Point mousePosition = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen(mousePosition, component);

                    if (!component.contains(mousePosition)) {
                        editor.stopCellEditing();
                    }
                }
            }
        });


        setSizeConstraints();
        config();
    }

    private void config() {
        getColumnModel().getColumn(0).setCellEditor(
                new DefaultCellEditor(new JTextField()) {
                    @Override
                    public boolean isCellEditable(EventObject anEvent) {
                        return false;
                    }
                }
        );

        getColumnModel().getColumn(0).setCellRenderer(new IDCellRenderer());
        getColumnModel().getColumn(1).setCellEditor(new ManagedTextFieldEditor(Data.Type.Name));
        getColumnModel().getColumn(2).setCellEditor(new ManagedTextFieldEditor(Data.Type.SerialNumber));
        getColumnModel().getColumn(4).setCellEditor(new ManagedTextFieldEditor(Data.Type.Mark));
        setDefaultRenderer(Data.class, new LabelRenderer());
        setDefaultEditor(String.class, new ComboBoxEditor());
        setDefaultRenderer(String.class, new ComboBoxRenderer());
    }

    private void setSizeConstraints() {
        setRowHeight(25);
        getColumnModel().getColumn(0).setMinWidth(50);
        getColumnModel().getColumn(0).setMaxWidth(50);
        getColumnModel().getColumn(1).setMinWidth(250);
        getColumnModel().getColumn(2).setMinWidth(150);
        getColumnModel().getColumn(2).setMaxWidth(150);
        getColumnModel().getColumn(3).setMinWidth(200);
        getColumnModel().getColumn(4).setMinWidth(200);

        setMinimumSize(new Dimension(850, 0));
    }
}


