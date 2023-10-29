package app;

import components.addDialog.AddDialog;
import entity.Data;
import components.managedTextField.ManagedTextField;
import entity.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Header extends ContainerManager {
    private boolean cbModelChanged;
    private final JComboBox<String> semester;
    private final JComboBox<String> course;
    private final JComboBox<String> group;
    private final JComboBox<String> faculty;
    private final JComboBox<String> discipline;
    private final JComboBox<String> academic;

    private final JButton groupButton;
    private final JButton facultyButton;
    private final JButton disciplineButton;
    private final JButton academicButton;

    private final ManagedTextField studyYear;
    private final ManagedTextField studyHours;
    private final ManagedTextField examDate;

    private final HashMap<String, JComponent> componentMap;

    public Header (JFrame parent) {
        super(parent);
        cbModelChanged = false;
        semester = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        course = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"});
        group = new JComboBox<>();
        faculty = new JComboBox<>();
        discipline = new JComboBox<>();
        academic = new JComboBox<>();

        groupButton = new JButton();
        facultyButton = new JButton();
        disciplineButton = new JButton();
        academicButton = new JButton();

        studyYear = new ManagedTextField(Data.Type.Year);
        studyHours = new ManagedTextField(Data.Type.Hours);
        examDate = new ManagedTextField(Data.Type.Date);
        studyYear.putClientProperty("isValid", true);
        studyHours.putClientProperty("isValid", true);
        examDate.putClientProperty("isValid", true);

        ImageIcon icon = new ImageIcon("button.png");
        icon = new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        groupButton.setIcon(icon);
        facultyButton.setIcon(icon);
        disciplineButton.setIcon(icon);
        academicButton.setIcon(icon);

        componentMap = new HashMap<>();
        componentMap.put("year", studyYear);
        componentMap.put("semester", semester);
        componentMap.put("course", course);
        componentMap.put("group", group);
        componentMap.put("faculty", faculty);
        componentMap.put("discipline", discipline);
        componentMap.put("academic", academic);
        componentMap.put("hours", studyHours);


        configContainer();
        setSizeConstraints();
        createMarkUp();
        configEventListeners();
    }

    @Override
    protected void configContainer() {
        container.setLayout(new GridBagLayout());
        //container.setBackground(Color.red);
        container.setPreferredSize(new Dimension(100, 50));
        container.setBorder(BorderFactory.createEmptyBorder(
                10, 10, 10, 10)
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1;
        parent.getContentPane().add(container, gbc);
    }

    /**
     * Specifies event listeners for containers components
     * Also calls addTextFieldValidation
     */
    @Override
    protected void configEventListeners() {
        groupButton.addActionListener(e -> {
            AddDialog dial = new AddDialog(
                    parent, "Добавьте новую группу", true
            );

            dial.addDialogListener(item -> {
                Validator.validate(item, Data.Type.Group);
                tryAddItem(item, group);
            });

            dial.setVisible(true);
        });

        facultyButton.addActionListener(e -> {
            AddDialog dial = new AddDialog(
                    parent, "Добавьте новый факультет", true
            );

            dial.addDialogListener(item -> {
                Validator.validate(item, Data.Type.Faculty);
                String[] words = ((String)item).split("\\s");
                StringBuilder tmp = new StringBuilder();
                for (String word : words) {
                    tmp.append(word.substring(0, 1).toUpperCase()).
                            append(word.substring(1).toLowerCase()).append(' ');
                }
                item = tmp.toString();
                tryAddItem(item, faculty);
            });

            dial.setVisible(true);
        });

        disciplineButton.addActionListener(e -> {
            AddDialog dial = new AddDialog(
                    parent, "Добавьте новую дисциплину", true
            );

            dial.addDialogListener(item -> {
                Validator.validate(item, Data.Type.Discipline);
                String[] words = ((String)item).split("\\s");
                StringBuilder tmp = new StringBuilder();
                for (String word : words) {
                    tmp.append(word.substring(0, 1).toUpperCase()).
                            append(word.substring(1).toLowerCase()).append(" ");
                }
                item = tmp.toString();
                tryAddItem(item, discipline);
            });

            dial.setVisible(true);
        });

        academicButton.addActionListener(e -> {
            AddDialog dial = new AddDialog(
                    parent, "Добавьте нового преподавателя", true
            );

            dial.addDialogListener(item -> {
                Validator.validate(item, Data.Type.Name);
                String tmp = (String)item;
                int ind = tmp.indexOf('.');
                String dummy = tmp.charAt(ind + 1) == ' ' ? ""  : " ";
                item =  tmp.substring(0, 1).toUpperCase() +
                        tmp.substring(1, ind - 1).toLowerCase() +
                        tmp.substring(ind - 1, ind + 1).toUpperCase() + dummy +
                        tmp.substring(ind + 1).toUpperCase();
                tryAddItem(item, academic);
            });

            dial.setVisible(true);
        });
    }

    /**
     * Places components into cells created by createCell method
     * and places them on container with respect to GridBagConstraints
     */
    private void createMarkUp() {
        JPanel cell;
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.1;
        gbc.weighty = 0.2;

        gbc.gridx = 0;
        gbc.gridy = 0;
        container.add(new JLabel("Учебный год:"), gbc);
        gbc.gridy = 1;
        container.add(new JLabel("Факультет:"), gbc);
        gbc.gridy = 2;
        container.add(new JLabel("Дисциплина:"), gbc);
        gbc.gridy = 3;
        container.add(new JLabel("Преподаватель:"), gbc);
        gbc.gridy = 4;
        container.add(new JLabel("Всего часов:"), gbc);

        gbc.weightx = 0.1;
        gbc.gridx = 1;
        gbc.gridy = 0;
        cell = createCell(null, studyYear, null);
        container.add(cell, gbc);
        gbc.weightx = 0.25;
        gbc.gridx = 2;
        cell = createCell("Семестр:", semester, null);
        container.add(cell, gbc);
        gbc.weightx = 0.2;
        gbc.gridx = 3;
        cell = createCell("Курс:", course, null);
        container.add(cell, gbc);
        gbc.weightx = 0.4;
        gbc.gridx = 4;
        cell = createCell("Группа", group, groupButton);
        container.add(cell, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        cell = createCell(null, faculty, facultyButton);
        container.add(cell, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        cell = createCell(null, discipline, disciplineButton);
        container.add(cell, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        cell = createCell(null, academic, academicButton);
        container.add(cell, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        cell = createCell(null, studyHours, null);
        container.add(cell, gbc);

        gbc.gridx = 4;
        gbc.gridwidth = 2;
        cell = createCell("Дата проведения:", examDate, null);
        container.add(cell, gbc);
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
            cell.add(Box.createHorizontalStrut(10));
        }
        cell.setPreferredSize(new Dimension(50, 25));
        return cell;
    }

    /**
     * Sets size constraints to containers components
     */
    private void setSizeConstraints() {
        groupButton.setMinimumSize(new Dimension(25, 25));
        groupButton.setMaximumSize(new Dimension(25, 25));
        groupButton.setPreferredSize(new Dimension(25, 25));
        facultyButton.setMinimumSize(new Dimension(25, 25));
        facultyButton.setMaximumSize(new Dimension(25, 25));
        facultyButton.setPreferredSize(new Dimension(25, 25));
        disciplineButton.setMinimumSize(new Dimension(25, 25));
        disciplineButton.setMaximumSize(new Dimension(25, 25));
        disciplineButton.setPreferredSize(new Dimension(25, 25));
        academicButton.setMinimumSize(new Dimension(25, 25));
        academicButton.setMaximumSize(new Dimension(25, 25));
        academicButton.setPreferredSize(new Dimension(25, 25));

        semester.setPreferredSize(new Dimension(0, 0));
        course.setPreferredSize(new Dimension(0, 0));
        group.setPreferredSize(new Dimension(0, 0));
        faculty.setPreferredSize(new Dimension(0, 0));
        discipline.setPreferredSize(new Dimension(0, 0));
        academic.setPreferredSize(new Dimension(0, 0));
    }

    /**
     * Tries to add item to combobox. If item already exists throws RuntimeException
     */
    private void tryAddItem(Object item, JComboBox<String> comboBox) throws RuntimeException {
        if (item == null)
            throw new RuntimeException("item is null");
        ComboBoxModel<String> model = comboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).equals(item))
                throw new RuntimeException("Элемент уже существует");
        }
        comboBox.addItem(item.toString());
        cbModelChanged = true;
    }

    /**
     * Creates separate element with tag "header" in doc and writes
     * in it elements provided by supplementary method "createElement".
     * @param doc Document in which project data will be written
     */
    public void writeProjectData(Document doc) {
        Element root = doc.createElement("header");
        doc.getFirstChild().appendChild(root);
        for (String tag : componentMap.keySet()) {
            Element element = createElement(doc, tag);
            root.appendChild(element);
        }
    }

    /**
     * Creates XML element that contains selected data of JComponent or
     * refers to those stored in separate file via attributes.
     * @param doc Document used to create elements.
     * @param tag Key of JComponent in componentMap and also tag of future XML element.
     * @return Created element.
     */
    private Element createElement(Document doc, String tag) {
        Element element = doc.createElement("null");
        if (componentMap.get(tag) instanceof ManagedTextField textField) {
            element = doc.createElement(tag);
            String text = textField.getText();
            if (!textField.getData().isValid() || text.isEmpty())
                text = "null";
            element.appendChild(doc.createTextNode(text));
        } else if (componentMap.get(tag) instanceof JComboBox<?> comboBox) {
            element = doc.createElement("comboBox");
            element.setAttribute("tag", tag);
            element.setAttribute("id", "null");
            if (comboBox.getSelectedItem() instanceof String text) {
                ComboBoxModel<?> model = comboBox.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    if (model.getElementAt(i).equals(text)) {
                        element.setAttribute("id", Integer.toString(i));
                        break;
                    }
                }
            }
        }

        return element;
    }

    /**
     * Writes all items from combo boxes into separate document
     * which project file will refer to. Should be called only when
     * data is changed.
     * @param doc Document in which data will be written.
     */
    public void writeHeaderData(Document doc) {
        Element root = doc.createElement("data");
        doc.appendChild(root);

        for (String tag : componentMap.keySet()) {
            if (componentMap.get(tag) instanceof JComboBox<?> comboBox) {
                Element element = doc.createElement("comboBox");
                element.setAttribute("tag", tag);
                StringBuilder val = new StringBuilder();
                ComboBoxModel<?> model = comboBox.getModel();
                int size = model.getSize();
                for (int i = 0; i < size; i++) {
                    val.append(model.getElementAt(i).toString())
                            .append(i == size - 1 ? "" : ",");
                }
                element.appendChild(doc.createTextNode(
                        val.isEmpty() ? "null" : val.toString())
                );
                root.appendChild(element);
            }
        }

        cbModelChanged = false;
    }

    /**
     * Finds first element with "header" tag in project and calls "parseElement" method
     * on each child element of header
     * @param project Document containing project configuration
     * @param data Document containing project data to which project
     *            refers to via its "src" attribute.
     */
    public void loadProjectData(Document project, Document data) {
        Node root = project.getElementsByTagName("header").item(0);
        Node node = root.getFirstChild();
        while (node != null) {
            if (node instanceof Element element) {
                parseElement(element, data.getDocumentElement());
            }
            node = node.getNextSibling();
        }
    }

    /**
     * Parses element according to its type.
     * Case its JTextField, sets text to content of element.
     * Case its JComboBox, finds corresponding element from data and add all its values.
     * @param element XML element delivered from "loadProjectData" method
     * @param data XML element containing data to which element might refer to
     */
    private void parseElement(Element element, Element data) {
        String tag = element.getTagName();
        if (tag.equals("comboBox")) {
            JComboBox<String> comboBox = (JComboBox<String>)
                    componentMap.get(element.getAttribute("tag"));
            comboBox.removeAllItems();
            Node node = data.getFirstChild();
            String items = "null";
            while (node != null) {
                if (node instanceof Element dataElement) {
                    if (dataElement.getAttribute("tag")
                            .equals(element.getAttribute("tag"))) {
                        items = dataElement.getTextContent();
                        break;
                    }
                }
                node = node.getNextSibling();
            }

            if (!items.equals("null")) {
                for (String item : items.split(",")) {
                    comboBox.addItem(item);
                }
                comboBox.setSelectedIndex(
                        Integer.parseInt(element.getAttribute("id"))
                );
            }
        } else if (componentMap.get(element.getTagName()) instanceof ManagedTextField textField) {
            textField.setText(element.getTextContent().equals("null") ?
                    null : element.getTextContent());
        }
    }

    public boolean rewriteRequired() {
        return cbModelChanged;
    }
}
