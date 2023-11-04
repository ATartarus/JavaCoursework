package app;

import components.managedTable.ManagedTable;
import components.managedTable.ManagedTableModel;
import containers.Writable;
import entity.Data;
import entity.Student;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ReassignedVariable")
public class ProjectFileManager {
    private String PATH;
    private String folderName;
    private String projectFileName;
    private final String dataFileName;
    private final HashMap<String, Writable> containers;
    private DocumentBuilder docBuilder;


    public ProjectFileManager(Writable[] containers) {
        this(containers, System.getProperty("user.dir"));
    }

    public ProjectFileManager(Writable[] containers, String PATH) {
        this.folderName = "data";
        this.containers = new HashMap<>();
        for (Writable source : containers) {
            this.containers.put(source.getClassName(), source);
        }
        this.PATH = PATH + '\\' + folderName;
        this.projectFileName = "project";
        this.dataFileName = "data";

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            System.err.println("ProjectFileManager:: " + e.getMessage());
        }
    }

    public void setFolderName(String name) {
        this.folderName = name;
        this.PATH = this.PATH.substring(0, this.PATH.lastIndexOf('\\') + 1) + this.folderName;
    }

    public void setProjectFileName(String projectFileName) {
        this.projectFileName = projectFileName;
    }

    /////////////////////////////////////////Save methods//////////////////////////////////////////

    public void save() {
        if (saveFile(dataFileName)) {
            System.out.println(dataFileName + " was saved successfully");
        } else {
            System.err.println(dataFileName + " was not saved");
        }

        if (saveFile(projectFileName)) {
            System.out.println(projectFileName + " was saved successfully");
        } else {
            System.err.println(projectFileName + " was not saved");
        }
    }

    private boolean saveFile(String fileName) {
        Document doc;

        File dataFile = new File(PATH + '\\' + fileName + ".xml");
        try {
            doc = docBuilder.parse(dataFile);
            deleteIndentation(doc);
        } catch (Exception e) {
            doc = docBuilder.newDocument();
            doc.appendChild(doc.createElement(fileName.equals(projectFileName) ?
                    "project" : "data"));
            System.out.println(fileName + " file is not found");
        }

        if (!saveHeader(doc)) {
            System.err.println("saveFile:: " + fileName + " header was not saved");
        }
        if (!saveBody(doc)) {
            System.err.println("saveFile:: " + fileName + " body was not saved");
        }

        try (FileOutputStream out =
                     new FileOutputStream(PATH + '\\' + fileName + ".xml")) {
            writeFile(doc, out);
        } catch (Exception e) {
            System.err.println("saveFile:: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Saves data of body component from containers field.
     * <br/>Doc must have first child element.
     * <br/>If first child element tag equals projectFileName, will not check data validity and will
     * rewrite "group" tag.
     * <br/>If tag equals dataFileName, will perform data validation and append new group tag or prompt user
     * to rewrite existing.
     * <br/>If doc does not contain appropriate nodes, this method will create them.
     * @param doc Document in which data will be written.
     * @return true if data was saved, false otherwise.
     */
    private boolean saveBody(Document doc) {
        Node root = doc.getFirstChild();
        if (root == null) {
            System.err.println("saveBody:: root node is not found");
            return false;
        }
        boolean isProjectDoc = root.getNodeName().equals("project");

        String groupID = null;
        if (!isProjectDoc) {
            if (!isGroupValid()) {
                return false;
            }
            groupID = findGroupID();
            if (groupID == null) {
                return false;
            }
        }

        Element bodyRoot;
        NodeList nodes = ((Element) root).getElementsByTagName("body");
        if (nodes.getLength() != 0) {
            bodyRoot = (Element) nodes.item(0);
        }
        else {
            bodyRoot = doc.createElement("body");
            root.appendChild(bodyRoot);
        }

        Node groupNode = bodyRoot.getFirstChild();
        while (groupNode != null) {
            Element tmp = (Element) groupNode;
            groupNode = groupNode.getNextSibling();
            if (isProjectDoc) {
                bodyRoot.removeChild(tmp);
            }
            else if (tmp.getAttribute("id").equals(groupID)) {
                //TODO Prompt user to rewrite group

                bodyRoot.removeChild(tmp);
            }
        }

        ManagedTable table = (ManagedTable) containers.get("body").getComponentMap().get("table");
        bodyRoot.appendChild(
                createGroupElement(doc, groupID, (ManagedTableModel) table.getModel())
        );

        return true;
    }

    /**
     * Saves data of header component from containers field.
     * <br/>Doc must have first child element.
     * <br/>If first child element tag equals projectFileName, will create properties tag for all components
     * and place in them only selected items.
     * <br/>If tag equals dataFileName, will create properties tag only for JComboBox components
     * and place in them all existing items.
     * <br/>If doc does not contain appropriate nodes, this method will create them.
     * <br/>If doc already contains header data, it will be rewritten.
     * @param doc Document in which data will be written.
     */
    private boolean saveHeader(Document doc) {
        Node root = doc.getFirstChild();
        if (root == null) {
            System.err.println("saveHeader:: root node is not found");
            return false;
        }
        boolean isProjectDoc = root.getNodeName().equals("project");

        //Delete all nodes of header node

        NodeList nodes = ((Element) root).getElementsByTagName("header");
        Element headerRoot;
        if (nodes.getLength() != 0) {
            headerRoot = (Element) nodes.item(0);
            Node property = headerRoot.getFirstChild();
            while (property != null) {
                Node tmp = property;
                property = property.getNextSibling();
                headerRoot.removeChild(tmp);
            }
        }
        else {
            headerRoot = doc.createElement("header");
            root.appendChild(headerRoot);
        }

        //Fetch data from components

        HashMap<String, JComponent> componentMap = containers.get("header").getComponentMap();
        for (Map.Entry<String, JComponent> pair : componentMap.entrySet()) {
            Object[] data = null;
            if (pair.getValue() instanceof JTextField tf && isProjectDoc) {
                data = new Object[]{tf.getText()};
            }
            else if (pair.getValue() instanceof JComboBox<?> cb) {
                if (isProjectDoc) {
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
                headerRoot.appendChild(property);
            }
        }

        return true;
    }

    /**
     * Checks if existing data of component with key "table" is valid.
     * This component is being searched for in body component from containers field.
     * <br/>All possible errors are printed into standard error stream.
     * @return true if data is valid, false otherwise or if error occurred.
     */
    private boolean isGroupValid() {
        Writable source = containers.get("body");
        if (source == null) {
            System.err.println("isGroupValid:: container 'body' not found");
            return false;
        }
        JComponent tableObject = source.getComponentMap().get("table");
        if (tableObject == null) {
            System.err.println("saveGroup:: component with key 'table' not found");
            return false;
        }
        if (tableObject instanceof ManagedTable table) {
            if (!((ManagedTableModel) table.getModel()).isReadyToWrite()) {
                //TODO prompt user that table cannot be saved
                System.out.println("table contains invalid elements. Save discarded");
                return false;
            }
        }
        else {
            System.err.println("saveGroup:: component with key 'table' is not ManagedTable instance");
            return false;
        }

        return true;
    }

    /**
     * Finds selected value of component with key "group".
     * This component is being searched for in header component from containers field.
     * <br/>All possible errors are printed into standard error stream.
     * @return Valid group id or null if error occurred.
     */
    private String findGroupID() {
        String groupName;
        Writable source = containers.get("header");

        if (source == null) {
            System.err.println("findGroupName:: container 'header' not found");
            return null;
        }
        JComponent group = source.getComponentMap().get("group");
        if (group instanceof JComboBox<?> cb) {
            Object selectedItem = cb.getSelectedItem();
            if (selectedItem == null) {
                System.err.println("findGroupName:: group is not selected");
                return null;
            }
            groupName = selectedItem.toString();
        }
        else {
            System.err.println("findGroupName:: component with key 'group' is not JComboBox instance");
            return null;
        }

        return groupName;
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
     * @return Element with "property" tag.
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

    private static void writeFile(Document doc, FileOutputStream out) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(out);

        transformer.transform(source, result);
    }


    /////////////////////////////////////////Load methods//////////////////////////////////////////


    public void load() {
        if (loadFile(dataFileName)) {
            System.out.println(dataFileName + " was loaded successfully");
        } else {
            System.err.println(dataFileName + " was not loaded");
        }

        if (loadFile(projectFileName)) {
            System.out.println(projectFileName + " was loaded successfully");
        } else {
            System.err.println(projectFileName + " was not loaded");
        }
    }

    private boolean loadFile(String fileName) {
        Document doc;

        File dataFile = new File(PATH + '\\' + fileName + ".xml");
        try {
            doc = docBuilder.parse(dataFile);
            deleteIndentation(doc);
        } catch (Exception e) {
            System.err.println(fileName + " file is not found");
            return false;
        }

        Node root = doc.getFirstChild();
        if (root == null) {
            System.err.println("loadFile:: root node is not found");
            return false;
        }

        Node sourceNode = root.getFirstChild();
        if (sourceNode == null) {
            System.err.println("loadFile:: source node is not found");
            return false;
        }

        while (sourceNode != null) {
            Writable destination = containers.get(sourceNode.getNodeName());
            if (destination == null) {
                System.err.println("loadFile:: source for key "
                        + sourceNode.getNodeName() + " is not found");
                return false;
            }

            if (!root.getNodeName().equals("data") || !sourceNode.getNodeName().equals("body")) {
                parseNode(sourceNode, destination);
            }
            sourceNode = sourceNode.getNextSibling();
        }

        return true;
    }

    public void parseNode(Node root, Writable destination) {
        Node contentNode = root.getFirstChild();
        if (contentNode == null) {
            System.err.println("parseNode:: node " + root + " is empty");
            return;
        }
        Element contentElement = (Element) contentNode;

        HashMap<String, JComponent> components = destination.getComponentMap();
        while (contentElement != null) {
            String tag = contentElement.getTagName();
            if (tag.equals("property")) {
                JComponent component = components.get(contentElement.getAttribute("name"));
                String[] textContent = contentElement.getTextContent().split(",");
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
            else if (tag.equals("group")) {
                JComponent component = components.get("table");
                if (component instanceof ManagedTable table) {
                    ((ManagedTableModel) table.getModel()).clear();
                    Element studentElement = (Element) contentElement.getFirstChild();
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
                }
            }

            contentElement = (Element) contentElement.getNextSibling();
        }
    }

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
}