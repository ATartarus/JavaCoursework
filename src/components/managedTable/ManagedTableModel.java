package components.managedTable;

import entity.Data;
import entity.TableRecord;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class ManagedTableModel extends AbstractTableModel {
    private final String[] columnHeaders = new String[] {
            "№", "Фамилия, инициалы обучающегося", "№ зачетной книжки", "Отметка о зачёте", "Оценка"
    };

    private final ArrayList<TableRecord> tableData = new ArrayList<>();

    @Override
    public int getRowCount() {
        return tableData.size();
    }

    @Override
    public int getColumnCount() {
        return columnHeaders.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnHeaders[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Object col = getValueAt(0, columnIndex);
        if (col == null) {
            Class<?> res = null;
            switch (columnIndex) {
                case 0 -> res = Integer.class;
                case 1, 2, 4 -> res = Data.class;
                case 3 -> res = String.class;
            }
            return res;
        }
        return col.getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (aValue instanceof Data data) {
            switch (columnIndex) {
                case 1 -> tableData.get(rowIndex).setName(new Data(data));
                case 2 -> tableData.get(rowIndex).setSerialNumber(new Data(data));
                case 4 -> tableData.get(rowIndex).setMark(new Data(data));
            }
        } else if (aValue instanceof String str) {
            tableData.get(rowIndex).setResult(str);
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = null;
        if (rowIndex < tableData.size()) {
            TableRecord record = tableData.get(rowIndex);
            switch (columnIndex) {
                case 0 -> value = record.getID();
                case 1 -> value = record.getName();
                case 2 -> value = record.getSerialNumber();
                case 3 -> value = record.getResult();
                case 4 -> value = record.getMark();
            }
        }

        return value;
    }

    public void addRow(TableRecord row) {
        TableRecord record;
        if (row == null) {
            record = new TableRecord(getRowCount() + 1,null, null, null, null);
        } else {
            record = new TableRecord(getRowCount() + 1,
                    new Data(row.getName()),
                    new Data(row.getSerialNumber()),
                    row.getResult(),
                    new Data(row.getMark())
            );
        }
        tableData.add(record);
        fireTableRowsInserted(getRowCount(), getRowCount());
    }
}
