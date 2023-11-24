package components.managedTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class IDCellRenderer extends DefaultTableCellRenderer {
    public IDCellRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (hasFocus) {
            c.setBackground(new Color(125, 200, 125, 125));
        } else {
            c.setBackground(null);
        }
        return c;
    }
}
