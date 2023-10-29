package containers;

import components.managedTable.ManagedTable;
import components.managedTable.ManagedTableModel;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import java.awt.*;

public class Body extends ComponentManager {
    private final JScrollPane scroll;
    private final ManagedTable table;
    private final JButton addButton;
    public Body(JFrame parent) {
        super(parent);
        table = new ManagedTable();
        scroll = new JScrollPane(table);
        addButton = new JButton();

        configContainer();
        addComponents();
        configEventListeners();
        setSizeConstraints();
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
    protected void addComponents() {
        scroll.getViewport().setOpaque(false);
        scroll.setBackground(new Color(0, 0, 0, 0));
        scroll.setBorder(BorderFactory.createEmptyBorder());

        container.add(scroll, BorderLayout.CENTER);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        panel.setOpaque(false);
        addButton.setFocusPainted(false);
        ImageIcon icon = new ImageIcon("button.png");
        icon = new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        addButton.setIcon(icon);
        panel.add(addButton);
        container.add(panel, BorderLayout.PAGE_END);
    }

    @Override
    protected void configEventListeners() {
        addButton.addActionListener(e -> {
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

    public void addTableModelListener(TableModelListener l) {
        table.getModel().addTableModelListener(l);
    }

    private void setSizeConstraints() {
        Dimension s = table.getMinimumSize();
        scroll.setMinimumSize(new Dimension(s.width, 0));
        scroll.setPreferredSize(new Dimension(s.width, 200));
        addButton.setPreferredSize(new Dimension(100, 25));
    }
}
