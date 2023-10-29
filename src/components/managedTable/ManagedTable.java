package components.managedTable;

import entity.Data;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.EventObject;

public class ManagedTable extends JTable {
/*    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame mainWindow;
            mainWindow = new JFrame("Application");
            mainWindow.setSize(1000, 800);
            mainWindow.setMinimumSize(new Dimension(1000, 800));
            mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ManagedTable t = new ManagedTable();
            JScrollPane scroll = new JScrollPane(t);
            scroll.getViewport().setOpaque(false);
            scroll.setBackground(new Color(0, 0, 0, 0));
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.setPreferredSize(new Dimension(0, 0));

            mainWindow.add(scroll);
            mainWindow.setVisible(true);
        });
    }*/

    public ManagedTable() {
        setCellSelectionEnabled(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

/*        DefaultTableModel model = new DefaultTableModel(
                new String[]{"№", "Фамилия, инициалы обучающегося", "№ зачетной книжки",
                        "Отметка о зачёте", "Оценка"}, 10
        );
        setModel(model);*/
        ManagedTableModel model = new ManagedTableModel();
        setModel(model);
        getTableHeader().setReorderingAllowed(false);
        getColumnModel().getColumn(0).setCellEditor(
            new DefaultCellEditor(new JTextField()) {
                @Override
                public boolean isCellEditable(EventObject anEvent) {
                    return false;
                }
            }
        );
        DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
        cr.setHorizontalAlignment(JLabel.CENTER);
        getColumnModel().getColumn(0).setCellRenderer(cr);


        setRowHeight(25);
        getColumnModel().getColumn(0).setMaxWidth(50);
        getColumnModel().getColumn(0).setMinWidth(50);
        getColumnModel().getColumn(1).setMinWidth(250);
        getColumnModel().getColumn(2).setMaxWidth(150);
        getColumnModel().getColumn(2).setMinWidth(150);
        getColumnModel().getColumn(3).setMinWidth(200);
        getColumnModel().getColumn(4).setMinWidth(200);

        setBorder(BorderFactory.createLineBorder(Color.gray, 1));

        getColumnModel().getColumn(1).setCellEditor(new ManagedTextFieldEditor(Data.Type.Name));
        getColumnModel().getColumn(2).setCellEditor(new ManagedTextFieldEditor(Data.Type.SerialNumber));
        getColumnModel().getColumn(4).setCellEditor(new ManagedTextFieldEditor(Data.Type.Mark));
        setDefaultRenderer(Data.class, new LabelRenderer());
        setDefaultEditor(String.class, new ComboBoxEditor());
        setDefaultRenderer(String.class, new ComboBoxRenderer());
    }
}


