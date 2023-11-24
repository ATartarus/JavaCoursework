package containers;

import components.addDialog.AddDialog;
import entity.Data;
import components.managedTextField.ManagedTextField;
import exceptions.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Header extends ComponentManager implements Writable {
    private JComboBox<String> semesterComboBox;
    private JComboBox<String> courseComboBox;
    private JComboBox<String> groupComboBox;
    private JComboBox<String> facultyComboBox;
    private JComboBox<String> disciplineComboBox;
    private JComboBox<String> academicComboBox;

    private JButton groupButton;
    private JButton facultyButton;
    private JButton disciplineButton;
    private JButton academicButton;

    private ManagedTextField yearTextField;
    private ManagedTextField hoursTextField;
    private ManagedTextField dateTextField;

    private JLabel yearLabel;
    private JLabel facultyLabel;
    private JLabel disciplineLabel;
    private JLabel academicLabel;
    private JLabel hoursLabel;

    public Header (JFrame parent) {
        super(parent);
    }

    @Override
    public HashMap<String, JComponent> getComponentMap() {
        HashMap<String, JComponent> map = new HashMap<>();
        map.put("year", yearTextField);
        map.put("semester", semesterComboBox);
        map.put("course", courseComboBox);
        map.put("group", groupComboBox);
        map.put("faculty", facultyComboBox);
        map.put("discipline", disciplineComboBox);
        map.put("academic", academicComboBox);
        map.put("hours", hoursTextField);
        map.put("date", dateTextField);

        return map;
    }

    @Override
    protected void configContainer() {
        container.setLayout(new GridBagLayout());
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.gridy = 0;
        gbc.weighty = 0;
        parent.getContentPane().add(container, gbc);
    }

    @Override
    protected void initComponents() {
        semesterComboBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        courseComboBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"});
        groupComboBox = new JComboBox<>();
        facultyComboBox = new JComboBox<>();
        disciplineComboBox = new JComboBox<>();
        academicComboBox = new JComboBox<>();

        groupButton = new JButton();
        facultyButton = new JButton();
        disciplineButton = new JButton();
        academicButton = new JButton();

        yearTextField = new ManagedTextField(Data.Type.Year);
        hoursTextField = new ManagedTextField(Data.Type.Hours);
        dateTextField = new ManagedTextField(Data.Type.Date);


        yearLabel = new JLabel("Учебный год:");
        facultyLabel = new JLabel("Факультет:");
        disciplineLabel = new JLabel("Дисциплина:");
        academicLabel = new JLabel("Преподаватель:");
        hoursLabel = new JLabel("Всего часов:");
    }

    @Override
    protected void configComponents() {
        yearTextField.putClientProperty("isValid", true);
        hoursTextField.putClientProperty("isValid", true);
        dateTextField.putClientProperty("isValid", true);
        dateTextField.setHorizontalAlignment(SwingConstants.CENTER);

        ImageIcon icon = new ImageIcon(System.getProperty("user.dir") + "/src/images/plus_button_icon.png");
        icon = new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        groupButton.setIcon(icon);
        facultyButton.setIcon(icon);
        disciplineButton.setIcon(icon);
        academicButton.setIcon(icon);
        groupButton.setFocusPainted(false);
        facultyButton.setFocusPainted(false);
        disciplineButton.setFocusPainted(false);
        academicButton.setFocusPainted(false);
    }

    /**
     * Places components into cells created by createCell method
     * and places them on container with respect to GridBagConstraints
     */
    @Override
    protected void addComponents() {
        JPanel cell;
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5,  0, 5, 0);
        container.add(yearLabel, gbc);
        gbc.gridy = 1;
        container.add(facultyLabel, gbc);
        gbc.gridy = 2;
        container.add(disciplineLabel, gbc);
        gbc.gridy = 3;
        container.add(academicLabel, gbc);
        gbc.gridy = 4;
        container.add(hoursLabel, gbc);

        gbc.weightx = 0.2;
        gbc.gridx = 1;
        gbc.gridy = 0;
        cell = createCell(null, yearTextField, null);
        container.add(cell, gbc);
        gbc.weightx = 0.2;
        gbc.gridx = 2;
        cell = createCell("Семестр:", semesterComboBox, null);
        container.add(cell, gbc);
        gbc.weightx = 0.2;
        gbc.gridx = 3;
        cell = createCell("Курс:", courseComboBox, null);
        container.add(cell, gbc);
        gbc.weightx = 0.4;
        gbc.gridx = 4;
        cell = createCell("Группа", groupComboBox, groupButton);
        container.add(cell, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        cell = createCell(null, facultyComboBox, facultyButton);
        container.add(cell, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        cell = createCell(null, disciplineComboBox, disciplineButton);
        container.add(cell, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        cell = createCell(null, academicComboBox, academicButton);
        container.add(cell, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        cell = createCell(null, hoursTextField, null);
        container.add(cell, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 0.3;
        cell = createCell("Дата проведения:", dateTextField, null);
        cell.remove(cell.getComponentCount() - 1);
        container.add(cell, gbc);
    }

    @Override
    protected void configEventListeners() {
        groupButton.addActionListener(e -> {
            AddDialog dial = new AddDialog(
                    parent, "Добавьте новую группу", true, Data.Type.Group
            );

            dial.addDialogListener(item -> addItem(item, groupComboBox));
            dial.setVisible(true);
        });


        facultyButton.addActionListener(e -> {
            AddDialog dial = new AddDialog(
                    parent, "Добавьте новый факультет", true, Data.Type.Faculty
            );

            dial.addDialogListener(item -> addItem(item, facultyComboBox));
            dial.setVisible(true);
        });

        disciplineButton.addActionListener(e -> {
            AddDialog dial = new AddDialog(
                    parent, "Добавьте новую дисциплину", true, Data.Type.Discipline
            );

            dial.addDialogListener(item -> addItem(item, disciplineComboBox));
            dial.setVisible(true);
        });

        academicButton.addActionListener(e -> {
            AddDialog dial = new AddDialog(
                    parent, "Добавьте нового преподавателя", true, Data.Type.Name
            );

            dial.addDialogListener(item -> addItem(item, academicComboBox));
            dial.setVisible(true);
        });
    }

    @Override
    protected void setSizeConstraints() {
        JButton[] buttons = new JButton[] {groupButton, facultyButton, disciplineButton, academicButton};
        for (JButton button : buttons) {
            button.setMinimumSize(new Dimension(28, 28));
            button.setMaximumSize(new Dimension(28, 28));
            button.setPreferredSize(new Dimension(28, 28));
        }

        JLabel[] labels = new JLabel[] {yearLabel, facultyLabel, disciplineLabel, academicLabel, hoursLabel};

        for (JLabel label : labels) {
            label.setMinimumSize(new Dimension(150, 28));
            label.setMaximumSize(new Dimension(150, 28));
            label.setPreferredSize(new Dimension(150, 28));
        }

        semesterComboBox.setMinimumSize(new Dimension(50, 28));
        semesterComboBox.setPreferredSize(new Dimension(50, 28));
        courseComboBox.setMinimumSize(new Dimension(50, 28));
        courseComboBox.setPreferredSize(new Dimension(50, 28));
        groupComboBox.setMinimumSize(new Dimension(100, 28));
        groupComboBox.setPreferredSize(new Dimension(120, 28));
        facultyComboBox.setMinimumSize(new Dimension(200, 28));
        facultyComboBox.setPreferredSize(new Dimension(300, 28));
        disciplineComboBox.setMinimumSize(new Dimension(200, 28));
        disciplineComboBox.setPreferredSize(new Dimension(300, 28));
        academicComboBox.setMinimumSize(new Dimension(200, 28));
        academicComboBox.setPreferredSize(new Dimension(300, 28));

        yearTextField.setMinimumSize(new Dimension(80, 28));
        yearTextField.setPreferredSize(new Dimension(100, 28));
        hoursTextField.setMinimumSize(new Dimension(80,  28));
        hoursTextField.setPreferredSize(new Dimension(100, 28));
        dateTextField.setMinimumSize(new Dimension(60, 28));
        dateTextField.setPreferredSize(new Dimension(60, 28));
    }

    /**
     * Creates cells containing components
     * Used by createMarkup method
     */
    private JPanel createCell(String label, JComponent input, JComponent btn) {
        JPanel cell = new JPanel();
        cell.setLayout(new BoxLayout(cell, BoxLayout.X_AXIS));
        if (label != null) {
            JLabel l = new JLabel(label);
            cell.add(l);
            cell.add(Box.createHorizontalStrut(10));
        }
        if (input != null) {
            cell.add(input);
            cell.add(Box.createHorizontalStrut(10));
        }
        if (btn != null) {
            cell.add(btn);
            //cell.add(Box.createHorizontalStrut(10));
        }
        return cell;
    }

    /**
     * Tries to add str to combobox. If str already exists throws RuntimeException
     */
    private void addItem(String str, JComboBox<String> comboBox) throws ValidationException {
        if (str == null || str.isBlank()) {
            throw new ValidationException("Элемент не может быть пустым");
        }

        ComboBoxModel<String> model = comboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).equals(str))
                throw new ValidationException("Элемент уже существует");
        }
        comboBox.addItem(str);
        comboBox.setSelectedItem(str);
    }

    public String getGroupID() {
        String groupID;
        Object selectedItem = groupComboBox.getSelectedItem();
        if (selectedItem == null) groupID = null;
        else groupID = selectedItem.toString();

        return groupID;
    }
}
