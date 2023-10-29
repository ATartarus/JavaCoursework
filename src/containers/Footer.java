package containers;

import components.managedTable.ManagedTableModel;
import entity.Student;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import java.awt.*;

public class Footer extends ComponentManager {
    private final JLabel showedStudents;
    private final String showedStudentsPlaceholder =
            "<html><em>Количество обучающихся, присутствовавших на аттестации: <b>{val}</b></em></html>";
    private final JLabel unshowedStudents;
    private final String unshowedStudentsPlaceholder =
            "<html><em>Количество обучающихся, не явившихся на аттестацию<br>" +
            "(в том числе, не допущенных к аттестации): <b>{val}</b></em></html>";

    public Footer(JFrame parent) {
        super(parent);
        showedStudents = new JLabel(showedStudentsPlaceholder.replace("{val}", "0"));
        unshowedStudents = new JLabel(unshowedStudentsPlaceholder.replace("{val}", "0"));
        showedStudents.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        unshowedStudents.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        showedStudents.setFont(new Font(showedStudents.getFont().getName(), Font.PLAIN, 16));
        unshowedStudents.setFont(new Font(unshowedStudents.getFont().getName(), Font.PLAIN, 16));
        configContainer();
        addComponents();
    }

    @Override
    protected void configContainer() {
        //container.setBackground(Color.green);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridy = 2;
        gbc.weighty = 0;
        parent.getContentPane().add(container, gbc);
    }

    @Override
    protected void addComponents() {
        container.add(showedStudents);
        container.add(unshowedStudents);
    }

    @Override
    protected void configEventListeners() {

    }

    public void updateData(TableModelEvent e) {
        Object source = e.getSource();
        if (source instanceof ManagedTableModel model) {
            int j = e.getColumn();
            if (j != 3 && j != TableModelEvent.ALL_COLUMNS) return;

            int count = model.getRowCount();
            int unshowed = 0;
            for (int i = 0; i < count; i++) {
                Student student = model.getEntity(i);
                if (student.getResult().equals(Student.results[2]))
                   unshowed++;
            }

            showedStudents.setText(showedStudentsPlaceholder.replace(
                    "{val}", Integer.toString(count - unshowed)
            ));

            unshowedStudents.setText(unshowedStudentsPlaceholder.replace(
                    "{val}", Integer.toString(unshowed)
            ));
        }
    }
}
