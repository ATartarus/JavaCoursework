package containers;

import components.managedTable.ManagedTable;
import components.managedTable.ManagedTableModel;
import entity.Data;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.util.HashMap;

public class Body extends ComponentManager implements Writable {
    private JScrollPane scroll;
    private ManagedTable table;
    private JButton addButton;

    private JButton removeButton;
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
        removeButton = new JButton();
    }

    @Override
    protected void configComponents() {
        scroll.setBorder(BorderFactory.createEmptyBorder());

        String path = System.getProperty("user.dir") + "/src/images/";
        ImageIcon icon = new ImageIcon(path + "plus_button_icon.png");
        icon = new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        addButton.setIcon(icon);
        addButton.setFocusPainted(false);
        icon = new ImageIcon(path + "minus_button_icon.png");
        icon = new ImageIcon(icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));
        removeButton.setIcon(icon);
        removeButton.setFocusPainted(false);
    }

    @Override
    protected void addComponents() {
        container.add(scroll, BorderLayout.CENTER);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        panel.setOpaque(false);
        panel.add(removeButton);
        panel.add(addButton);
        container.add(panel, BorderLayout.PAGE_END);
    }

    @Override
    protected void setSizeConstraints() {
        Dimension s = table.getMinimumSize();
        scroll.setMinimumSize(new Dimension(s.width + 20, 0));
        scroll.setPreferredSize(new Dimension(s.width + 20, 200));
        addButton.setPreferredSize(new Dimension(100, 25));
        removeButton.setPreferredSize(new Dimension(100, 25));
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

        removeButton.addActionListener(e -> {
            if (table.getSelectedColumn() != 0) {
                JOptionPane.showMessageDialog(
                        parent,
                        "Перед удалением необходимо выбрать запись, нажав на ее номер",
                        "Внимание!",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            else {
                ManagedTableModel tableModel = (ManagedTableModel) table.getModel();
                tableModel.removeRow(table.getSelectedRow());
            }
        });
    }


    public boolean isGroupReadyToWrite(){
        return ((ManagedTableModel) table.getModel()).isReadyToWrite();
    }

    public void addTableModelListener(TableModelListener l) {
        table.getModel().addTableModelListener(l);
    }
}
