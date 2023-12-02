package components.managedTable;

import entity.Data;
import entity.Student;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Represents TableModel with Student instances as data and predefined headers.
 */
public class ManagedTableModel extends AbstractTableModel {
    private final String[] columnHeaders = new String[] {
            "№", "Фамилия, инициалы обучающегося", "№ зачетной книжки", "Отметка о зачёте", "Оценка"
    };

    private final ArrayList<Student> tableData = new ArrayList<>();

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
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = null;
        if (rowIndex >= 0 && rowIndex < tableData.size()) {
            Student record = tableData.get(rowIndex);
            switch (columnIndex) {
                case 0 -> value = rowIndex + 1;
                case 1 -> value = record.getName();
                case 2 -> value = record.getSerialNumber();
                case 3 -> value = record.getResult();
                case 4 -> value = record.getMark();
            }
        }

        return value;
    }

    /**
     * Adds new row to the model as a new Student instance.
     * @param row Student instance whose data will be copied and placed in this model.
     */
    public void addRow(Student row) {
        Student record;
        if (row == null) {
            record = new Student(
                    new Data(Data.Type.Name),
                    new Data(Data.Type.SerialNumber),
                    null,
                    new Data(Data.Type.Mark)
            );
        } else {
            record = new Student(
                    new Data(row.getName()),
                    new Data(row.getSerialNumber()),
                    row.getResult(),
                    new Data(row.getMark())
            );
        }
        tableData.add(record);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    /**
     * Removes models row by its index.
     * @param ind Index of row to remove.
     */
    public void removeRow(int ind) {
        tableData.remove(ind);
        fireTableDataChanged();
    }

    /**
     * Retrieves data of specified row as a Student instance.
     * @param row Row index to retrieve.
     * @return Student instance with row data.
     */
    public Student getEntity(int row) {
        return tableData.get(row);
    }

    /**
     * Deletes all data.
     */
    public void clear() {
        tableData.clear();
        fireTableRowsDeleted(0, getRowCount());
    }

    /**
     * Checks whether data contained in this model can be written.
     * @return true if data is writable; false otherwise.
     */
    public boolean isReadyToWrite() {
        if (tableData.isEmpty()) {
            return false;
        }
        for (Student student : tableData) {
            if (!student.getName().isValid() ||
                !student.getSerialNumber().isValid())
                return false;
        }
        return true;
    }

    /**
     * Counts students with "Не явился" result value.
     * @return Number of students.
     */
    public int countNotAppeared() {
        int count = 0;
        for (Student student : tableData) {
            if (student.getResult().equals(Student.results[2])) count++;
        }
        return count;
    }
}
