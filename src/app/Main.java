package app;

import containers.*;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;


public class Main {
    private final JFrame mainWindow;
    private final Header header;
    private final Body body;
    private final Footer footer;
    private final ProjectFileManager fileManager;

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

        fileManager = new ProjectFileManager(mainWindow.getRootPane(), new Writable[]{header, body, footer});
        //fileManager.load();

        mainWindow.pack();
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
        mainWindow.setMinimumSize(mainWindow.getSize());
    }


    private JMenuBar createMenu() {
        JMenuBar menuBar = new JMenuBar();
        mainWindow.setJMenuBar(menuBar);

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
        item = new JMenuItem("Open");
        item.addActionListener(e -> {
            if (fileManager.chooseProjectFile(ProjectFileManager.OPEN_MODE)) {
                fileManager.load();
            }
        });
        project.add(item);
        item = new JMenuItem("Save");
        item.addActionListener(e -> {
            fileManager.save();
        });
        project.add(item);
        item = new JMenuItem("Save As");
        item.addActionListener(e -> {
            if (fileManager.chooseProjectFile(ProjectFileManager.SAVE_MODE)) {
                fileManager.save();
            }
        });
        project.add(item);

        item = new JMenuItem("Save");
        item.addActionListener(e -> {
            fileManager.saveGroup();
        });
        group.add(item);
        item = new JMenuItem("Load");
        group.add(item);


        export.add(new JMenuItem("As docx"));

        about.add(new JMenuItem("Author"));
        about.add(new JMenuItem("Program"));

        return menuBar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main m = new Main();
        });
    }
}