package containers;

import components.managedTable.ManagedTable;
import components.managedTable.ManagedTableModel;
import components.managedTable.ManagedTextFieldEditor;
import components.managedTextField.ManagedTextField;
import entity.Data;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;

public class Body extends ComponentManager implements Writable {
    private JScrollPane scroll;
    private ManagedTable table;
    private JButton addButton;
    public Body(JFrame parent) {
        super(parent);
    }

    @Override
    public HashMap<String, JComponent> getComponentMap() {
        HashMap<String, JComponent> map = new HashMap<>();
        map.put("table", table);
        return map;
    }

    @Override
    protected void configContainer() {
        container.setLayout(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(
                10, 10, 10, 10)
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.gridy = 1;
        gbc.weighty = 1;
        parent.getContentPane().add(container, gbc);
    }

    @Override
    protected void initComponents() {
        table = new ManagedTable();
        scroll = new JScrollPane(table);
        addButton = new JButton();
    }

    @Override
    protected void configComponents() {
/*        scroll.getViewport().setOpaque(false);
        scroll.setBackground(new Color(0, 0, 0, 0));*/
        scroll.setBorder(BorderFactory.createEmptyBorder());

        ImageIcon icon = new ImageIcon("button.png");
        icon = new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        addButton.setIcon(icon);
        addButton.setFocusPainted(false);
    }

    @Override
    protected void addComponents() {
        container.add(scroll, BorderLayout.CENTER);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        panel.setOpaque(false);
        panel.add(addButton);
        container.add(panel, BorderLayout.PAGE_END);
    }

    @Override
    protected void setSizeConstraints() {
        Dimension s = table.getMinimumSize();
        scroll.setMinimumSize(new Dimension(s.width + 20, 0));
        scroll.setPreferredSize(new Dimension(s.width + 20, 200));
        addButton.setPreferredSize(new Dimension(100, 25));
    }

    @Override
    protected void configEventListeners() {
        addButton.addActionListener(e -> {
            ManagedTableModel tableModel = (ManagedTableModel) table.getModel();
            int rows = tableModel.getRowCount();

            if (rows == 0 ||
                (tableModel.getValueAt(rows - 1, 1) instanceof Data data &&
                data.isValid())) {
                tableModel.addRow(null);
                return;
            }
            JOptionPane.showMessageDialog(
                    parent,
                    "Необходимо заполнить второй столбец последней строки",
                    "Внимание!",
                    JOptionPane.ERROR_MESSAGE
            );
        });
        table.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (table.isEditing()) {
                    DefaultCellEditor editor = (DefaultCellEditor) table.getCellEditor(table.getEditingRow(), table.getEditingColumn());
                    Component component = editor.getComponent();
                    Point mousePosition = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen(mousePosition, component);

                    if (!component.contains(mousePosition)) {
                        editor.stopCellEditing();
                    }
                }
            }
        });
    }

    public void addTableModelListener(TableModelListener l) {
        table.getModel().addTableModelListener(l);
    }
}
