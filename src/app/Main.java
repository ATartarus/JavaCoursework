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

        fileManager = new ProjectFileManager(new Writable[]{header, body, footer});
        fileManager.load();

        mainWindow.pack();
        mainWindow.setVisible(true);
        mainWindow.setMinimumSize(mainWindow.getSize());
    }


    private JMenuBar createMenu() {
        JMenuBar menuBar = new JMenuBar();
        mainWindow.setJMenuBar(menuBar);

        JMenu file = new JMenu("Project");
        JMenu export = new JMenu("Export");
        JMenu about = new JMenu("About");

        menuBar.add(file);
        menuBar.add(export);
        menuBar.add(about);

        JMenuItem item = new JMenuItem("New");
        file.add(item);
        item = new JMenuItem("Open");
        item.addActionListener(e -> fileManager.load());
        file.add(item);
        item = new JMenuItem("Save");
        item.addActionListener(e -> fileManager.save());
        file.add(item);
        item = new JMenuItem("SaveAs");
        file.add(item);

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