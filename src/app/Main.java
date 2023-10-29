package app;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;


public class Main {
    private final String configPATH = "C:\\Code\\Java\\Coursework\\data";
    private final String projectFileName = "project.xml";
    private final String headerDataFileName = "headerData.xml";
    private final JFrame mainWindow;
    private final Header header;
    private final Body body;
    private final Footer footer;

    public Main() {
        mainWindow = new JFrame("Application");
        mainWindow.setSize(1000, 800);
        mainWindow.setMinimumSize(new Dimension(1000, 800));
        mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainWindow.setJMenuBar(createMenu());
        mainWindow.setContentPane(new JPanel(new GridBagLayout()));

        header = new Header(mainWindow);
        body = new Body(mainWindow);
        footer = new Footer(mainWindow);

        try {
            loadProject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        mainWindow.setVisible(true);
    }


    private JMenuBar createMenu() {
        JMenuBar menuBar = new JMenuBar();
        mainWindow.setJMenuBar(menuBar);

        JMenu file = new JMenu("File");
        JMenu imp = new JMenu("Import");
        JMenu about = new JMenu("About");

        menuBar.add(file);
        menuBar.add(imp);
        menuBar.add(about);

        JMenuItem item = new JMenuItem("Create");
        file.add(item);
        item = new JMenuItem("Open");
        file.add(item);
        item = new JMenuItem("Save");
        item.addActionListener(e -> saveProject());
        file.add(item);
        item = new JMenuItem("SaveAs");
        file.add(item);

        imp.add(new JMenuItem("From txt"));
        imp.add(new JMenuItem("From mySQL"));

        about.add(new JMenuItem("Author"));
        about.add(new JMenuItem("Program"));

        return menuBar;
    }

    private void saveProject() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            System.out.println(e.getMessage());
            return;
        }
        Document doc;

        if (header.rewriteRequired()) {
            doc = builder.newDocument();
            header.writeHeaderData(doc);
            try (FileOutputStream out =
                         new FileOutputStream(configPATH + "\\" + headerDataFileName)) {
                writeXmlFile(doc, out);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        doc = builder.newDocument();
        Element root = doc.createElement("project");
        doc.appendChild(root);
        header.writeProjectData(doc);
        ((Element)root.getFirstChild())
                .setAttribute("src", configPATH + "\\" + headerDataFileName);

        try (FileOutputStream out =
                     new FileOutputStream(configPATH + "\\" + projectFileName)) {
            writeXmlFile(doc, out);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadProject()
            throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        builder = dbFactory.newDocumentBuilder();
        Document project = builder.parse(new File(configPATH + "\\" + projectFileName));
        Element headerRoot = (Element) project.getElementsByTagName("header").item(0);
        Document data = builder.parse(new File(headerRoot.getAttribute("src")));
        header.loadProjectData(project, data);
    }


    private void createDocxFile() {

    }

    private void writeXmlFile(Document doc, FileOutputStream out)
            throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(out);

        transformer.transform(source, result);
    }

    private void writeDocxFile() {

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main m = new Main();
        });
    }
}