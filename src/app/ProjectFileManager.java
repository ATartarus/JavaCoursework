package app;

import components.managedTable.ManagedTable;
import components.managedTable.ManagedTableModel;
import containers.Writable;
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
    private String dataFileName;
    private HashMap<String, Writable> dataSources;

    private DocumentBuilder docBuilder;


    public ProjectFileManager(Writable[] dataSources) {
        this(dataSources, System.getProperty("user.dir"));
    }

    public ProjectFileManager(Writable[] dataSources, String PATH) {
        this.folderName = "data";
        this.dataSources = new HashMap<>();
        for (Writable source : dataSources) {
            this.dataSources.put(source.getClassName(), source);
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

    public void setProjectFileName(String name) {
        this.projectFileName = name;
    }

    public void setDataFileName(String name) {
        this.dataFileName = name;
    }

    public boolean saveProject() {
        Document projectDoc;

        File dataFile = new File(PATH + '\\' + projectFileName + ".xml");
        try {
            projectDoc = docBuilder.parse(dataFile);
            deleteIndentation(projectDoc);
        } catch (Exception e) {
            projectDoc = docBuilder.newDocument();
            projectDoc.appendChild(projectDoc.createElement("project"));
            System.out.println("project file is not found");
        }

        if (!saveHeader(projectDoc)) {
            System.err.println("saveProject:: header was not saved");
        }
        if (!saveBody(projectDoc)) {
            System.err.println("saveProject:: body was not saved");
        }

        try (FileOutputStream out =
                     new FileOutputStream(PATH + '\\' + projectFileName + ".xml")) {
            writeFile(projectDoc, out);
        } catch (Exception e) {
            System.err.println("saveProject:: " + e.getMessage());
            return false;
        }

        if (!saveData()) {
            System.err.println("Data was not saved");
        }
        return true;
    }

    private boolean saveData() {
        Document dataDoc;

        File dataFile = new File(PATH + '\\' + dataFileName + ".xml");
        try {
            dataDoc = docBuilder.parse(dataFile);
            deleteIndentation(dataDoc);
        } catch (Exception e) {
            dataDoc = docBuilder.newDocument();
            dataDoc.appendChild(dataDoc.createElement("data"));
            System.out.println("data file is not found");
        }

        if (!saveHeader(dataDoc)) {
            System.err.println("saveData:: header was not saved");
        }
        if (!saveBody(dataDoc)) {
            //TODO handle group save failure
            System.err.println("saveData:: body was not saved");
        }

        try (FileOutputStream out =
                     new FileOutputStream(PATH + '\\' + dataFileName + ".xml")) {
            writeFile(dataDoc, out);
        } catch (Exception e) {
            System.err.println("saveData:: " + e.getMessage());
            return false;
        }

        return true;
    }


    /**
     * Saves data of object with key "body" stored in "dataSource" field.
     * <br/>Doc must have first child element.
     * <br/>If first child element tag equals "project", will not check data validity and will
     * rewrite "group" tag.
     * <br/>If tag equals "data", will perform data validation and append new group tag or prompt user
     * to rewrite existing.
     * <br/>If doc does not contain appropriate nodes, this method will create them.
     * @param doc Document in which data will be written.
     * @return true if data was saved, false otherwise.
     */
    private boolean saveBody(Document doc) {
        Node root = doc.getFirstChild();
        if (root == null) {
            System.err.println("saveBody:: docs first child is null");
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

        ManagedTable table = (ManagedTable) dataSources.get("body").getComponentMap().get("table");
        bodyRoot.appendChild(
                createGroupElement(doc, groupID, (ManagedTableModel) table.getModel())
        );

        return true;
    }

    /**
     * Saves data of object with key "header" stored in "dataSources" field.
     * <br/>Doc must have first child element.
     * <br/>If first child element tag equals "project", will create properties tag for all components
     * and place in them only selected items.
     * <br/>If tag equals "data", will create properties tag only for JComboBox components
     * and place in them all existing items.
     * <br/>If doc does not contain appropriate nodes, this method will create them.
     * <br/>If doc already contains header data, it will be rewritten.
     * @param doc Document in which data will be written.
     */
    private boolean saveHeader(Document doc) {
        Node root = doc.getFirstChild();
        if (root == null) {
            System.err.println("saveHeader:: docs first child is null");
            return false;
        }
        boolean isProjectDoc = root.getNodeName().equals("project");

        //////////////////////////////Delete all nodes of header node///////////////////////////////

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

        /////////////////////////////////Fetch data from components/////////////////////////////////

        HashMap<String, JComponent> componentMap = dataSources.get("header").getComponentMap();
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
     * This component is being searched for in object with key "body" stored in "dataSource" field.
     * <br/>All possible errors are printed into standard error stream.
     * @return true if data is valid, false otherwise or if error occurred.
     */
    private boolean isGroupValid() {
        Writable source = dataSources.get("body");
        if (source == null) {
            System.err.println("isGroupValid:: source 'body' not found");
            return false;
        }
        JComponent tableObject = source.getComponentMap().get("table");
        if (tableObject == null) {
            System.err.println("saveGroup:: component with key 'table' not found");
            return false;
        }
        if (tableObject instanceof ManagedTable table) {
            if (!((ManagedTableModel) table.getModel()).isReadyToWrite()) {
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
     * This component is being searched for in object with key "header" stored in "dataSource" field.
     * <br/>All possible errors are printed into standard error stream.
     * @return Valid group id or null if error occurred.
     */
    private String findGroupID() {
        String groupName;
        Writable source = dataSources.get("header");

        if (source == null) {
            System.err.println("findGroupName:: source 'header' not found");
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
    private Element createGroupElement(Document doc, String groupID, ManagedTableModel source) {
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
    private Element createProperty(Document doc, String name, Object[] source) {
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
    private void deleteIndentation(Document doc) {
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
    private void deleteIndentation(Node node) {
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

    private void writeFile(Document doc, FileOutputStream out) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(out);

        transformer.transform(source, result);
    }
}