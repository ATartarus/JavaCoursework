package components.managedTable;

import entity.Data;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class LabelRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof Data data) {
            c.setText(data.getText());
            if (data.getType().equals(Data.Type.Mark)) {
                setHorizontalAlignment(CENTER);
            } else {
                setHorizontalAlignment(LEFT);
            }

            if (data.isErrorDisplayNeeded()) {
                c.setBorder(BorderFactory.createLineBorder(Color.red, 2));
            } else {
                c.setBorder(null);
            }
            if (hasFocus || isSelected) {
                c.setBackground(table.getSelectionBackground());
            } else {
                c.setBackground(null);
            }
        }
        return c;
    }
}