package app;

import containers.Body;
import containers.Footer;
import containers.Header;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;


public class Main {
    private final String configPATH = "C:\\Code\\Java\\Coursework\\data";
    private final String projectFileName = "project.xml";
    private final String headerDataFileName = "headerData.xml";
    private final JFrame mainWindow;
    private final Header header;
    private final Body body;
    private final Footer footer;

    public Main() {
        String font = UIManager.getDefaults().getFont("Label.font").getName();
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put (key, new FontUIResource(new Font(font, Font.PLAIN, 14)));
        }
        UIManager.put("MenuBar.font", new FontUIResource(new Font(font, Font.PLAIN, 14)));
        UIManager.put("MenuItem.font", new FontUIResource(new Font(font, Font.PLAIN, 14)));
        UIManager.put("Menu.font", new FontUIResource(new Font(font, Font.PLAIN, 14)));

        mainWindow = new JFrame("Application");
        mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainWindow.setJMenuBar(createMenu());
        mainWindow.setContentPane(new JPanel(new GridBagLayout()));



        header = new Header(mainWindow);
        body = new Body(mainWindow);
        footer = new Footer(mainWindow);

        body.addTableModelListener(footer::updateData);

        try {
            loadProject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        mainWindow.pack();
        mainWindow.setVisible(true);
        mainWindow.setMinimumSize(mainWindow.getSize());
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