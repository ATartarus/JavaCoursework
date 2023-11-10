package filemanagment;

import components.managedTable.ManagedTable;
import components.managedTable.ManagedTableModel;
import containers.Writable;
import entity.Data;
import entity.Student;
import exceptions.XMLParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProjectFileManager {
    public static final int OPEN_MODE = 0;
    public static final int SAVE_MODE = 1;
    private final ProjectData projectData;
    private boolean projectFileExists;
    private DocumentBuilder docBuilder;

    public boolean isProjectFileExists() {
        return projectFileExists;
    }

    public ProjectFileManager(ProjectData projectData) {
        this.projectData = projectData;
        this.projectFileExists = false;

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            System.err.println("ProjectFileManager:: " + e.getMessage());
        }

        try {
            loadFile(projectData.getDataFileName());
        } catch (Exception e) {
            System.err.println("ProjectFileManager:: " + e.getMessage());
        }
    }

    public void newProject() {
        Writable header = projectData.getContainer("header");
        if (header == null) throw new IllegalArgumentException("No item with 'header' key");

        ManagedTable table = (ManagedTable) projectData.findComponent("body", "table");
        ((ManagedTableModel) table.getModel()).clear();
        Map<String, JComponent> componentMap = header.getComponentMap();
        for (JComponent component : componentMap.values()) {
            if (component instanceof JTextField textField) {
                textField.setText(null);
            }
        }

        projectData.setDefaultProjectName();
    }



    /**
     * Saves project data to file with projectFileName and header data to dataFileName.
     * <br/>If projectFileName is null, calls FileChooser to select project file.
     * <br/>If either of files is not present, creates new.
     * @throws IOException If file save fails.
     */
    public void saveProject() throws IOException {
        if (projectData.getProjectFileName() == null) throw new IOException("file name is null");
        Document dataDoc = getDocument(projectData.getDataFileName());
        if (dataDoc == null) {
            dataDoc = docBuilder.newDocument();
        }
        saveHeader(dataDoc, false);
        dataDoc.getFirstChild().appendChild(dataDoc.createElement("body"));

        Document projectDoc = getDocument(projectData.getProjectFileName());
        if (projectDoc == null) {
            projectDoc = docBuilder.newDocument();
        }
        saveHeader(projectDoc,  true);
        saveBody(projectDoc, null);

        StringBuilder message = new StringBuilder();
        try {
            writeFile(dataDoc, projectData.getDataFileName());
        } catch (IOException e) {
            message.append(e.getMessage()).append(". ");
        }
        try {
            writeFile(projectDoc, projectData.getProjectFileName());
            projectFileExists = true;
        } catch (IOException e) {
            message.append(e.getMessage()).append(". ");
        }
        if (!message.isEmpty()) throw new IOException(message.toString());
    }

    /**
     * Saves group data to file with dataFileName.
     * <br/>If data file is not present, creates new.
     * @param id ID of group to save.
     * @throws IOException If file save fails.
     */
    public void saveGroup(String id) throws IOException {
        Document dataDoc = getDocument(projectData.getDataFileName());
        if (dataDoc == null) {
            dataDoc = docBuilder.newDocument();
            dataDoc.appendChild(dataDoc.createElement("data"));
        }

        saveBody(dataDoc, id);
        writeFile(dataDoc, projectData.getDataFileName());
    }

    /**
     * Saves data of body component from containers field.
     * <br/>If doc does not contain appropriate nodes, will create them.
     * @param doc Document in which data will be written.
     * @param groupID ID attribute add to the group node.
     */
    private void saveBody(Document doc, String groupID) {
        Node root = doc.getFirstChild();
        if (root == null) {
            root = doc.createElement(groupID == null ? "project" : "data");
        }
        Node bodyNode = getChildNodeByName(root, "body");
        if (bodyNode == null) {
            bodyNode = doc.createElement("body");
            root.appendChild(bodyNode);
        }

        Node groupNode = bodyNode.getFirstChild();
        while (groupNode != null) {
            Element tmp = (Element) groupNode;
            groupNode = groupNode.getNextSibling();
            if (groupID == null || tmp.getAttribute("id").equals(groupID)) {
                bodyNode.removeChild(tmp);
            }
        }

        JComponent tableObject = projectData.findComponent("body", "table");
        ManagedTable table = (ManagedTable) tableObject;
        bodyNode.appendChild(
                createGroupElement(doc, groupID, (ManagedTableModel) table.getModel())
        );
    }

    /**
     * Saves data of header component from containers field.
     * <br/>If doc does not contain appropriate nodes, this method will create them.
     * <br/>If doc already contains header data, it will be rewritten.
     * @param doc Document in which data will be written.
     * @param project true if doc is project document, false otherwise.
     */
    private void saveHeader(Document doc, boolean project) {
        Node root = doc.getFirstChild();
        if (root == null) {
            root = doc.createElement(project ? "project" : "data");
            doc.appendChild(root);
        }

        //Delete all nodes of header node

        Node headerNode = getChildNodeByName(root, "header");
        if (headerNode == null) {
            headerNode = doc.createElement("header");
            root.appendChild(headerNode);
        } else {
            Node property = headerNode.getFirstChild();
            while (property != null) {
                Node tmp = property;
                property = property.getNextSibling();
                headerNode.removeChild(tmp);
            }
        }

        //Fetch data from components

        Writable source = projectData.getContainer("header");
        if (source == null) throw new IllegalArgumentException("No item with 'header' key");

        HashMap<String, JComponent> componentMap = source.getComponentMap();
        for (Map.Entry<String, JComponent> pair : componentMap.entrySet()) {
            Object[] data = null;
            if (pair.getValue() instanceof JTextField tf && project) {
                data = new Object[]{tf.getText().isEmpty() ? "null" : tf.getText()};
            }
            else if (pair.getValue() instanceof JComboBox<?> cb) {
                if (project) {
                    data = new Object[]{cb.getSelectedItem()};
                }
                else {
                    ComboBoxModel<?> model = cb.getModel();
                    if (model.getSize() != 0) {
                        data = new Object[model.getSize()];
                        for (int i = 0; i < data.length; i++) {
                            data[i] = model.getElementAt(i);
                        }
                    }
                    else {
                        data = new Object[]{null};
                    }
                }
            }
            Element property = createProperty(doc, pair.getKey(), data);
            if (property != null) {
                headerNode.appendChild(property);
            }
        }
    }

    /**
     * Creates element with tag "group" and attribute "id".
     * Inside group tag placed several "student" tags with attribute "id" and "serialNumber".
     * If groupID is null, then "id" attribute will not be added and all "student" tags will
     * have additional attributes.
     * @param doc Document in which group tag will be placed.
     * @param groupID Value of group "id" attribute. Can be null.
     * @param source Data that will be used for creation of student tags.
     * @return Element with "group" tag.
     */
    private static Element createGroupElement(Document doc, String groupID, ManagedTableModel source) {
        Element newGroup = doc.createElement("group");
        if (groupID != null) {
            newGroup.setAttribute("id", groupID);
        }
        for (int i = 0; i < source.getRowCount(); i++) {
            Student student = source.getEntity(i);
            Element studentElement = doc.createElement("student");
            studentElement.setAttribute("id", Integer.toString(student.getID()));
            studentElement.setAttribute("serialNumber", student.getSerialNumber().getText());
            studentElement.appendChild(doc.createTextNode(student.getName().getText()));

            if (groupID == null) {
                studentElement.setAttribute("result", student.getResult());
                studentElement.setAttribute("mark", student.getMark().getText());
            }

            newGroup.appendChild(studentElement);
        }
        return newGroup;
    }

    /**
     * Creates element with tag "property".
     * @param doc Document in which property element will be placed.
     * @param name Value of property "name" attribute.
     * @param source Array of objects that will be placed inside property tag as a String.
     * @return Element with "property" tag or null if source is null.
     */
    private static Element createProperty(Document doc, String name, Object[] source) {
        if (source == null) return null;
        Element newElement = doc.createElement("property");
        newElement.setAttribute("name", name);
        StringBuilder items = new StringBuilder();
        for (Object item : source) {
            items.append(item).append(',');
        }
        items.deleteCharAt(items.length() - 1);

        newElement.appendChild(doc.createTextNode(items.toString()));
        return newElement;
    }

    private void writeFile(Document doc, String fileName) throws IOException {
        try (FileOutputStream out = new FileOutputStream(
                projectData.getFolderPath() + '\\' + fileName + ".xml")
        ) {
            writeFile(doc, out);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    private static void writeFile(Document doc, FileOutputStream out) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(out);

        transformer.transform(source, result);
    }


    /////////////////////////////////////////Load methods//////////////////////////////////////////


    /**
     * Loads data and project files.
     * @throws IOException If file load fails.
     * @throws XMLParseException If file document has unexpected structure.
     */
    public void loadProject() throws IOException, XMLParseException {
        loadFile(projectData.getDataFileName());
        loadFile(projectData.getProjectFileName());
        projectFileExists = true;
    }

    /**
     * Loads group from file with dataFileName.
     * @param id ID of group to load.
     * @throws XMLParseException If file document has unexpected structure.
     * @throws IOException If file load fails.
     */
    public void loadGroup(String id) throws XMLParseException, IOException {
        XMLParseException.setFileName(projectData.getDataFileName());

        Document dataDoc = getDocument(projectData.getDataFileName());
        if (dataDoc == null) throw new IOException(projectData.getDataFileName() + " file not found");

        Node root = getRootNode(dataDoc);
        Node bodyNode = getChildNodeByName(root, "body");
        if (bodyNode == null) throw new XMLParseException("Body node not found");
        Node groupNode = bodyNode.getFirstChild();
        while (groupNode != null) {
            Element groupElement = (Element) groupNode;
            if (groupElement.hasAttribute("id") && groupElement.getAttribute("id").equals(id)) {
                parseGroup(groupNode, projectData.getContainer("body"));
                return;
            }
            groupNode = groupNode.getNextSibling();
        }

        XMLParseException.setFileName(null);
    }

    /**
     * Loads file with given name and parses its data.
     * @param fileName Name of file to load.
     * @throws IOException If file load fails.
     * @throws XMLParseException If file document has unexpected structure.
     */
    private void loadFile(String fileName) throws IOException, XMLParseException {
        XMLParseException.setFileName(fileName);

        Document doc = getDocument(fileName);
        if (doc == null) throw new IOException(fileName + " file not found");

        Node root = getRootNode(doc);
        Node sourceNode = root.getFirstChild();
        if (sourceNode == null) throw new XMLParseException("Source node not found");

        while (sourceNode != null) {
            Writable destination = projectData.getContainer(sourceNode.getNodeName());
            if (destination == null) {
                throw new IllegalArgumentException("Destination " + destination + " not found");
            }

            if (!root.getNodeName().equals("data") || !sourceNode.getNodeName().equals("body")) {
                parseSource(sourceNode, destination);
            }
            sourceNode = sourceNode.getNextSibling();
        }

        XMLParseException.setFileName(null);
    }

    /**
     * Parses source node to given destination.
     * @param root Source node to parse.
     * @param destination Writable object to which node is parsed.
     * @throws XMLParseException If source node is empty.
     */
    public void parseSource(Node root, Writable destination) throws XMLParseException {
        if (root == null) throw new IllegalArgumentException("Root is null");
        if (destination == null) throw new IllegalArgumentException("Destination is null");

        Node contentNode = root.getFirstChild();
        if (contentNode == null) throw new XMLParseException("Node " + root + " is empty");

        while (contentNode != null) {
            String tag = contentNode.getNodeName();
            if (tag.equals("property")) {
                parseProperty(contentNode, destination);
            }
            else if (tag.equals("group")) {
                parseGroup(contentNode, destination);
            }

            contentNode = contentNode.getNextSibling();
        }
    }

    /**
     * Parses group node to given destination.
     * @param group Group node to parse.
     * @param destination Writable object containing group component.
     * @throws XMLParseException If group node is empty.
     */
    private void parseGroup(Node group, Writable destination) throws XMLParseException {
        JComponent tableObject = projectData.findComponent(destination.getClassName(), "table");
        if (tableObject instanceof ManagedTable table) {
            ((ManagedTableModel) table.getModel()).clear();
            Node studentNode = group.getFirstChild();
            if (studentNode == null) throw new XMLParseException("Group node is empty");

            Element studentElement = (Element) studentNode;
            while (studentElement != null) {
                Student student = new Student(
                        Integer.parseInt(studentElement.getAttribute("id")),
                        new Data(Data.Type.Name, studentElement.getTextContent()),
                        new Data(Data.Type.SerialNumber,
                                studentElement.getAttribute("serialNumber")),
                        studentElement.getAttribute("result"),
                        new Data(Data.Type.Mark, studentElement.getAttribute("mark"))
                );
                ((ManagedTableModel) table.getModel()).addRow(student);

                studentElement = (Element) studentElement.getNextSibling();
            }
        } else {
            throw new IllegalArgumentException("tableObject is not instance of ManagedTable");
        }
    }

    /**
     * Parses property node to component of given destination.
     * @param property Property node to parse.
     * @param destination Writable object containing property component.
     */
    private void parseProperty(Node property, Writable destination) {
        JComponent component = projectData.findComponent(
                destination.getClassName(),
                ((Element) property).getAttribute("name")
        );
        String[] textContent = property.getTextContent().split(",");
        if (component instanceof JTextField textField) {
            textField.setText(textContent[0].equals("null") ?
                    null : textContent[0]);
        }
        if (component instanceof JComboBox<?>) {
            JComboBox<String> comboBox = (JComboBox<String>) component;
            fillComboBox(comboBox, textContent);
            comboBox.setSelectedItem(textContent[0]);
        }
    }

    /**
     * Adds strings to the model of given JComboBox.
     * Duplicates are not added.
     * @param comboBox JComboBox instance to fill.
     * @param textContent String array which will be added to combobox.
     */
    private static void fillComboBox(JComboBox<String> comboBox, String[] textContent) {
        ComboBoxModel<String> model = comboBox.getModel();
        for (String s : textContent) {
            if (s.isBlank()) continue;
            int j = 0;
            for (; j < model.getSize(); j++) {
                if (s.equals(model.getElementAt(j))) {
                    break;
                }
            }
            if (j == model.getSize()) {
                if (!s.equals("null")) {
                    comboBox.addItem(s);
                }
            }
        }
    }


    ///////////////////////////////////////////Utility/////////////////////////////////////////////


    /**
     * Creates JFileChooser instance and shows it in a given mode.
     * If file was chosen, sets projectFileName to this value.
     * @param mode Mode in which JFileChooser will be displayed.
     *             <br/>OPEN_MODE – calls showOpenDialog.
     *             <br/>SAVE_MODE – calls showSaveDialog.
     * @return true if file was chosen, false otherwise.
     */
    public boolean showFileChooser(Component parent, int mode) {
        File pathFile = new File(projectData.getFolderPath());
        JFileChooser fileChooser;
        int chooseResult;

        if (mode == OPEN_MODE) {
            fileChooser = new JFileChooser();
            setUpFileChooser(fileChooser, pathFile);
            chooseResult = fileChooser.showOpenDialog(parent);
        }
        else if (mode == SAVE_MODE) {
            fileChooser = new JFileChooser() {
                @Override
                public void approveSelection() {
                    if (getSelectedFile().exists()) {
                        int answer = JOptionPane.showConfirmDialog(
                                this,
                                "Rewrite file?",
                                "File already exists",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE
                        );
                        if (answer == JOptionPane.CANCEL_OPTION) {
                            return;
                        }
                    }
                    super.approveSelection();
                }
            };
            setUpFileChooser(fileChooser, pathFile);
            fileChooser.setSelectedFile(new File(projectData.getProjectFileName()));
            chooseResult = fileChooser.showSaveDialog(parent);
        }
        else {
            throw new IllegalArgumentException("Unsupported mode");
        }


        if (chooseResult == JFileChooser.APPROVE_OPTION) {
            String selectedFileName = fileChooser.getSelectedFile().getName();
            int pivot = selectedFileName.indexOf(".");
            projectData.setProjectFileName(pivot == -1 ? selectedFileName : selectedFileName.substring(0, pivot));
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Sets default settings for given JFileChooser and sets current directory according to passed parameter.
     * @param fileChooser JFileChooser configure to.
     * @param defaultDir Current directory.
     */
    private void setUpFileChooser(JFileChooser fileChooser, File defaultDir) {
        fileChooser.setCurrentDirectory(defaultDir);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return false;
                String fileName = f.getName();
                return fileName.substring(fileName.indexOf(".")).equals(".xml") &&
                        !fileName.substring(0, fileName.indexOf(".")).equals(projectData.getDataFileName());
            }

            @Override
            public String getDescription() {
                return "XML files (.xml)";
            }
        });
        fileChooser.setFileView(new FileView() {
            @Override
            public Boolean isTraversable(File f) {
                return defaultDir.equals(f);
            }
        });
    }

    /**
     * Creates document from file by given name and deletes its indentation.
     * @param fileName File name to load.
     * @return Created document or null, if error occurred or fileName is null.
     */
    private Document getDocument(String fileName) {
        if (fileName == null) return null;
        Document doc;
        File dataFile = new File(projectData.getFolderPath() + '\\' + fileName + ".xml");
        try {
            doc = docBuilder.parse(dataFile);
            deleteIndentation(doc);
        } catch (Exception e) {
            return null;
        }
        return doc;
    }

    /**
     * Retrieves root node from given document with additional verification.
     * @param doc Document.
     * @return Root node.
     * @throws XMLParseException If root node not present or its name different from "data" or "project".
     */
    private Node getRootNode(Document doc) throws XMLParseException {
        Node root = doc.getFirstChild();
        if (root == null) throw new XMLParseException("Root node not found");
        String rootNodeName = root.getNodeName();
        if (!rootNodeName.equals("data") && !rootNodeName.equals("project")) {
            throw new XMLParseException("Unexpected node: " + rootNodeName);
        }
        return root;
    }

    /**
     * Searches for child node with specified name in given node.
     * @param root Parent node.
     * @param childName Name of a child node.
     * @return Child node if found, null otherwise.
     */
    private static Node getChildNodeByName(Node root, String childName) {
        Node child = root.getFirstChild();
        while (child != null) {
            if (child.getNodeName().equals(childName)) {
                return child;
            }
            child = child.getNextSibling();
        }
        return null;
    }

    /**
     * Deletes all indentation nodes inside XML document.
     * @param doc Document that will be normalized.
     */
    private static void deleteIndentation(Document doc) {
        if (doc == null) {
            System.err.println("deleteIndentation:: doc is null");
            return;
        }

        deleteIndentation(doc.getFirstChild());
    }

    /**
     * Deletes all indentation nodes inside given node and all its descendants.
     * @param node Node that will be normalized.
     */
    private static void deleteIndentation(Node node) {
        while (node != null) {
            deleteIndentation(node.getFirstChild());
            Node tmp = node;
            node = node.getNextSibling();
            if (tmp.getNodeType() == Node.TEXT_NODE &&
                    tmp.getTextContent().charAt(0) == '\n') {
                tmp.getParentNode().removeChild(tmp);
            }
        }
    }



    /**
     * Checks if group with specified id exists in data file.
     * @param id id attribute of a group.
     * @return true if group exists, false otherwise.
     * @throws XMLParseException If document has unexpected structure.
     * @throws IOException If file load fails.
     */
    public boolean groupExists(String id) throws XMLParseException, IOException {
        Document doc = getDocument(projectData.getDataFileName());
        if (doc == null) throw new IOException(projectData.getDataFileName() + " file not found");

        Node root = doc.getFirstChild();
        if (root == null) throw new XMLParseException("Root node not found");

        Node bodyNode = getChildNodeByName(root, "body");
        if (bodyNode == null) throw new XMLParseException("Body node not found");
        Node groupNode = bodyNode.getFirstChild();
        while (groupNode != null) {
            Element groupElement = (Element) groupNode;
            if (groupElement.hasAttribute("id") &&
                    groupElement.getAttribute("id").equals(id)) {
                return true;
            }
            groupNode = groupNode.getNextSibling();
        }

        return false;
    }
}