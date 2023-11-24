package app;

import components.addDialog.AddDialog;
import components.managedTable.ManagedTable;
import components.managedTable.ManagedTableModel;
import components.managedTextField.ManagedTextField;
import filemanagment.Writable;
import entity.Data;
import exceptions.ValidationException;
import exceptions.XMLParseException;
import filemanagment.ProjectExporter;
import filemanagment.ProjectFileManager;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

public class MainWindow extends JFrame {
    private final Header header;
    private final Body body;
    private final Footer footer;
    private final Application app;

    public MainWindow(Application application) {
        this.app = application;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(createMenu());
        setContentPane(new JPanel(new GridBagLayout()));

        header = new Header();
        body = new Body();
        footer = new Footer();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;

        gbc.gridy = 0;
        gbc.weighty = 0;
        add(header, gbc);
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        add(body, gbc);
        gbc.gridy = 2;
        gbc.weighty = 0;
        add(footer, gbc);

        body.addTableModelListener(footer::updateData);

        pack();
    }

    private JMenuBar createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu project = new JMenu("Project");
        JMenu group = new JMenu("Group");
        JMenu export = new JMenu("Export");
        JMenu about = new JMenu("About");

        menuBar.add(project);
        menuBar.add(group);
        menuBar.add(export);
        menuBar.add(about);

        JMenuItem item = new JMenuItem("New");
        project.add(item);
        item.addActionListener(e -> onNewProjectClick());
        item = new JMenuItem("Open");
        item.addActionListener(e -> onOpenProjectClick());
        project.add(item);
        item = new JMenuItem("Save");
        item.addActionListener(e -> onSaveProjectClick());
        project.add(item);
        item = new JMenuItem("Save As");
        item.addActionListener(e -> onSaveAsProjectClick());
        project.add(item);

        item = new JMenuItem("Save");
        item.addActionListener(e -> onSaveGroupClick());
        group.add(item);
        item = new JMenuItem("Load");
        item.addActionListener(e -> onLoadGroupClick());
        group.add(item);

        item = new JMenuItem("As docx");
        item.addActionListener(e -> onExportClick());
        export.add(item);

        about.add(new JMenuItem("Author"));
        about.add(new JMenuItem("Program"));

        return menuBar;
    }

    public Writable[] getWritableData() {
        return new Writable[] { header, body };
    }

    private void onNewProjectClick() {
        int answer = JOptionPane.showConfirmDialog(
                this,
                "Сохранить текущий проект?",
                "Warning",
                JOptionPane.YES_NO_OPTION
        );
        if (answer == JOptionPane.OK_OPTION) {
            onSaveProjectClick();
        }

        app.getFileManager().newProject();
    }

    private void onOpenProjectClick() {
        if (app.getFileManager().showFileChooser(this, ProjectFileManager.OPEN_MODE)) {
            int answer = JOptionPane.showConfirmDialog(
                    this,
                    "Сохранить текущий проект?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION
            );
            if (answer == JOptionPane.OK_OPTION) {
                onSaveProjectClick();
            }

            try {
                app.getFileManager().loadProject();
            } catch (IOException ioException) {
                showErrorMessage(ioException.getMessage(), "Load file error");
            } catch (XMLParseException parseException) {
                showErrorMessage(parseException.getMessage(), "Project file corruption");
            }
        }
    }

    private void onSaveProjectClick() {
        if (app.getFileManager().isProjectFileExists()) {
            saveProject();
        } else {
            onSaveAsProjectClick();
        }
    }

    private void onSaveAsProjectClick() {
        if (app.getFileManager().showFileChooser(this, ProjectFileManager.SAVE_MODE)) {
            saveProject();
        }
    }

    private void saveProject() {
        try {
            app.getFileManager().saveProject();
        } catch (IOException exception) {
            showErrorMessage(exception.getMessage(), "Save file error");
        }
    }

    private void onSaveGroupClick() {
        if (!body.isGroupReadyToWrite()) {
            showErrorMessage("<html>Для сохранения группы второй и третий столбцы<br>" +
                    "должны содержать валидные значения</html>", "Error"
            );
            return;
        }
        String groupID = header.getGroupID();
        if (groupID == null) {
            showErrorMessage("Группа не выбрана", "Error");
            return;
        }
        try {
            int answer = JOptionPane.OK_OPTION;
            if (app.getFileManager().groupExists(groupID)) {
                answer = JOptionPane.showConfirmDialog(
                        this,
                        "<html>Группа с данным номером уже существует<br>Перезаписать?</html>",
                        "Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
            }
            if (answer == JOptionPane.OK_OPTION) {
                app.getFileManager().saveGroup(groupID);
            }
        } catch (XMLParseException parseException) {
            showErrorMessage(parseException.getMessage(), "Data file corruption");
        } catch (IOException ioException) {
            showErrorMessage(ioException.getMessage(), "Load group error");
        }
    }

    private void onLoadGroupClick() {
        String groupID = header.getGroupID();
        if (groupID == null) {
            showErrorMessage("Группа не выбрана", "Error");
            return;
        }
        try {
            if (!app.getFileManager().groupExists(groupID)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Записи о данной группе не существует",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            app.getFileManager().loadGroup(groupID);
        } catch (XMLParseException parseException) {
            showErrorMessage(parseException.getMessage(), "Data file corruption");
        } catch (IOException ioException) {
            showErrorMessage(ioException.getMessage(), "Save group error");
        }
    }

    private void onExportClick() {
        String fileName = ProjectExporter.showFileChooser(this, app.getData().getProjectFileName());
        if (fileName == null) return;
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                ProjectExporter.export(app.getData(), fileName);
                return null;
            }
        }.execute();
    }

    private void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }


    private class Header extends JPanel implements Writable {
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

        public Header () {
            setLayout(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            initComponents();
            configComponents();
            addComponents();
            setSizeConstraints();
            configEventListeners();
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

        private void initComponents() {
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

        private void configComponents() {
            yearTextField.putClientProperty("isValid", true);
            hoursTextField.putClientProperty("isValid", true);
            dateTextField.putClientProperty("isValid", true);
            dateTextField.setHorizontalAlignment(SwingConstants.CENTER);

            ImageIcon icon = new ImageIcon(System.getProperty("user.dir") + "/src/images/plus_button_icon.png");
            icon = new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            JButton[] buttons = new JButton[] { groupButton, facultyButton, disciplineButton, academicButton };
            for (JButton button : buttons) {
                button.setIcon(icon);
                button.setBackground(Color.white);
                button.setFocusPainted(false);
            }
        }

        /**
         * Places components into cells created by createCell method
         * and places them on container with respect to GridBagConstraints
         */
        private void addComponents() {
            JPanel cell;
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 0;
            gbc.weighty = 0;

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(5,  0, 5, 0);
            add(yearLabel, gbc);
            gbc.gridy = 1;
            add(facultyLabel, gbc);
            gbc.gridy = 2;
            add(disciplineLabel, gbc);
            gbc.gridy = 3;
            add(academicLabel, gbc);
            gbc.gridy = 4;
            add(hoursLabel, gbc);

            gbc.weightx = 0.2;
            gbc.gridx = 1;
            gbc.gridy = 0;
            cell = createCell(null, yearTextField, null);
            add(cell, gbc);
            gbc.weightx = 0.2;
            gbc.gridx = 2;
            cell = createCell("Семестр:", semesterComboBox, null);
            add(cell, gbc);
            gbc.weightx = 0.2;
            gbc.gridx = 3;
            cell = createCell("Курс:", courseComboBox, null);
            add(cell, gbc);
            gbc.weightx = 0.4;
            gbc.gridx = 4;
            cell = createCell("Группа", groupComboBox, groupButton);
            add(cell, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.gridwidth = 4;
            cell = createCell(null, facultyComboBox, facultyButton);
            add(cell, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            cell = createCell(null, disciplineComboBox, disciplineButton);
            add(cell, gbc);

            gbc.gridx = 1;
            gbc.gridy = 3;
            cell = createCell(null, academicComboBox, academicButton);
            add(cell, gbc);

            gbc.gridx = 1;
            gbc.gridy = 4;
            gbc.gridwidth = 1;
            gbc.weightx = 0.3;
            cell = createCell(null, hoursTextField, null);
            add(cell, gbc);

            gbc.gridx = 4;
            gbc.gridwidth = 2;
            gbc.weightx = 0.3;
            cell = createCell("Дата проведения:", dateTextField, null);
            cell.remove(cell.getComponentCount() - 1);
            add(cell, gbc);
        }

        private void configEventListeners() {
            groupButton.addActionListener(e -> {
                AddDialog dial = new AddDialog(
                        MainWindow.this, "Добавьте новую группу", true, Data.Type.Group
                );

                dial.addDialogListener(item -> addItem(item, groupComboBox));
                dial.setVisible(true);
            });


            facultyButton.addActionListener(e -> {
                AddDialog dial = new AddDialog(
                        MainWindow.this, "Добавьте новый факультет", true, Data.Type.Faculty
                );

                dial.addDialogListener(item -> addItem(item, facultyComboBox));
                dial.setVisible(true);
            });

            disciplineButton.addActionListener(e -> {
                AddDialog dial = new AddDialog(
                        MainWindow.this, "Добавьте новую дисциплину", true, Data.Type.Discipline
                );

                dial.addDialogListener(item -> addItem(item, disciplineComboBox));
                dial.setVisible(true);
            });

            academicButton.addActionListener(e -> {
                AddDialog dial = new AddDialog(
                        MainWindow.this, "Добавьте нового преподавателя", true, Data.Type.Name
                );

                dial.addDialogListener(item -> addItem(item, academicComboBox));
                dial.setVisible(true);
            });
        }

        private void setSizeConstraints() {
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

    private class Body extends JPanel implements Writable {
        private JScrollPane scroll;
        private ManagedTable table;
        private JButton addButton;

        private JButton removeButton;
        public Body() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            initComponents();
            configComponents();
            addComponents();
            setSizeConstraints();
            configEventListeners();
        }

        @Override
        public HashMap<String, JComponent> getComponentMap() {
            HashMap<String, JComponent> map = new HashMap<>();
            map.put("table", table);
            return map;
        }

        private void initComponents() {
            table = new ManagedTable();
            scroll = new JScrollPane(table);
            addButton = new JButton();
            removeButton = new JButton();
        }

        private void configComponents() {
            scroll.setBorder(BorderFactory.createEmptyBorder());

            String path = System.getProperty("user.dir") + "/src/images/";
            ImageIcon icon = new ImageIcon(path + "plus_button_icon.png");
            icon = new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            addButton.setIcon(icon);
            addButton.setBackground(Color.white);
            addButton.setFocusPainted(false);
            icon = new ImageIcon(path + "minus_button_icon.png");
            icon = new ImageIcon(icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));
            removeButton.setIcon(icon);
            removeButton.setBackground(Color.white);
            removeButton.setFocusPainted(false);
        }

        private void addComponents() {
            add(scroll, BorderLayout.CENTER);

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
            panel.setOpaque(false);
            panel.add(removeButton);
            panel.add(addButton);
            add(panel, BorderLayout.PAGE_END);
        }

        private void setSizeConstraints() {
            Dimension s = table.getMinimumSize();
            scroll.setMinimumSize(new Dimension(s.width + 20, 0));
            scroll.setPreferredSize(new Dimension(s.width + 20, 200));
            addButton.setPreferredSize(new Dimension(100, 25));
            removeButton.setPreferredSize(new Dimension(100, 25));
        }

        private void configEventListeners() {
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
                        MainWindow.this,
                        "Необходимо заполнить второй столбец последней строки",
                        "Внимание!",
                        JOptionPane.ERROR_MESSAGE
                );
            });

            removeButton.addActionListener(e -> {
                if (table.getSelectedColumn() != 0) {
                    JOptionPane.showMessageDialog(
                            MainWindow.this,
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

    private class Footer extends JPanel {
        private JLabel appearedStudents;
        private final String appearedStudentsPlaceholder =
                "<html><em>Количество обучающихся, присутствовавших на аттестации: <b>${val}</b></em></html>";
        private JLabel notAppearedStudents;
        private final String notAppearedStudentsPlaceholder =
                "<html><em>Количество обучающихся, не явившихся на аттестацию<br>" +
                        "(в том числе, не допущенных к аттестации): <b>${val}</b></em></html>";

        public Footer() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createLineBorder(Color.gray, 1));

            initComponents();
            configComponents();
            addComponents();
        }


        private void initComponents() {
            appearedStudents = new JLabel(appearedStudentsPlaceholder.replace("${val}", "0"));
            notAppearedStudents = new JLabel(notAppearedStudentsPlaceholder.replace("${val}", "0"));
        }

        private void configComponents() {
            appearedStudents.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
            notAppearedStudents.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            appearedStudents.setFont(new Font(appearedStudents.getFont().getName(), Font.PLAIN, 16));
            notAppearedStudents.setFont(new Font(notAppearedStudents.getFont().getName(), Font.PLAIN, 16));
        }

        private void addComponents() {
            add(appearedStudents);
            add(notAppearedStudents);
        }

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
}