package containers;

import components.managedTable.ManagedTableModel;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.*;

public class Footer extends ComponentManager {
    private JLabel appearedStudents;
    private final String appearedStudentsPlaceholder =
            "<html><em>Количество обучающихся, присутствовавших на аттестации: <b>${val}</b></em></html>";
    private JLabel notAppearedStudents;
    private final String notAppearedStudentsPlaceholder =
            "<html><em>Количество обучающихся, не явившихся на аттестацию<br>" +
            "(в том числе, не допущенных к аттестации): <b>${val}</b></em></html>";

    public Footer(JFrame parent) {
        super(parent);
    }

    @Override
    protected void configContainer() {
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createLineBorder(Color.gray, 1));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridy = 2;
        gbc.weighty = 0;
        parent.getContentPane().add(container, gbc);
    }

    @Override
    protected void initComponents() {
        appearedStudents = new JLabel(appearedStudentsPlaceholder.replace("${val}", "0"));
        notAppearedStudents = new JLabel(notAppearedStudentsPlaceholder.replace("${val}", "0"));
    }

    @Override
    protected void configComponents() {
        appearedStudents.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        notAppearedStudents.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        appearedStudents.setFont(new Font(appearedStudents.getFont().getName(), Font.PLAIN, 16));
        notAppearedStudents.setFont(new Font(notAppearedStudents.getFont().getName(), Font.PLAIN, 16));
    }

    @Override
    protected void setSizeConstraints() { }

    @Override
    protected void addComponents() {
        container.add(appearedStudents);
        container.add(notAppearedStudents);
    }

    @Override
    protected void configEventListeners() {}

    public void updateData(TableModelEvent e) {
        Object source = e.getSource();
        if (source instanceof ManagedTableModel model) {
            int j = e.getColumn();
            if (j != 3 && j != TableModelEvent.ALL_COLUMNS) return;

            int notAppeared = model.countNotAppeared();
            appearedStudents.setText(appearedStudentsPlaceholder.replace(
                    "${val}", Integer.toString(model.getRowCount() - notAppeared)
            ));

            notAppearedStudents.setText(notAppearedStudentsPlaceholder.replace(
                    "${val}", Integer.toString(notAppeared)
            ));
        }
    }
}
