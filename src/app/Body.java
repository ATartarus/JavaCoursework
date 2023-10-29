package app;

import components.managedTable.ManagedTable;
import components.managedTable.ManagedTableModel;

import javax.swing.*;
import java.awt.*;

public class Body extends ContainerManager {
    private final ManagedTable table;
    private final JButton addButton;
    public Body(JFrame parent) {
        super(parent);
        table = new ManagedTable();

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        panel.setOpaque(false);
        addButton = new JButton();
        addButton.setPreferredSize(new Dimension(100, 25));
        ImageIcon icon = new ImageIcon("button.png");
        icon = new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        addButton.setIcon(icon);
        panel.add(addButton);

        configContainer();
        addScrollTable();
        container.add(panel, BorderLayout.PAGE_END);
        configEventListeners();
    }

    @Override
    protected void configContainer() {
        container.setBackground(Color.yellow);
        container.setLayout(new BorderLayout());
        container.setPreferredSize(new Dimension(100, 50));
        container.setBorder(BorderFactory.createEmptyBorder(
                10, 10, 10, 10)
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridy = 1;
        gbc.weighty = 3;
        parent.getContentPane().add(container, gbc);
    }

    @Override
    protected void configEventListeners() {
        addButton.addActionListener(e -> {
            //DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
            ManagedTableModel tableModel = (ManagedTableModel) table.getModel();
            int rows = tableModel.getRowCount();
            if (rows > 0 && tableModel.getValueAt(rows - 1, 1) == null) {
                JOptionPane.showMessageDialog(
                        parent,
                        "Необходимо заполнить второй столбец последней строки",
                        "Внимание!",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            tableModel.addRow(null);
        });
    }

    private void addScrollTable() {
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setOpaque(false);
        scroll.setBackground(new Color(100, 0, 0, 0));
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(0, 0));
        container.add(scroll, BorderLayout.CENTER);
    }
}
