package app;

import filemanagment.*;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;

/**
 * Main class with entry point that contains application data.
 */
public class Application {
    public static final String NAME = "Journal";
    private final ProjectData projectData;
    private final ProjectFileManager fileManager;

    /**
     * Creates class instance, creates and shows main window.
     */
    public Application() {
        MainWindow mainWindow = new MainWindow(this);
        projectData = new ProjectData(mainWindow.getWritableData());
        projectData.addPropertyChangeListener(
                evt -> mainWindow.setTitle(NAME + " – " + evt.getNewValue())
        );
        fileManager = new ProjectFileManager(projectData);

        mainWindow.setTitle(NAME + " – " + projectData.getProjectFileName());
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
        mainWindow.setMinimumSize(mainWindow.getSize());
    }

    private static void configUIDefaults() {
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
    }

    /**
     * Provides file manager responsible for data manipulation.
     * @return ProjectFileManager instance.
     */
    public ProjectFileManager getFileManager() {
        return fileManager;
    }

    /**
     * Provides data of current project.
     * @return ProjectData instance.
     */
    public ProjectData getData() {
        return projectData;
    }

    /**
     * Application entry point. Sets up default configuration and shows SplashScreen.
     * @param args console arguments.
     */
    public static void main(String[] args) {
        configUIDefaults();

        SwingUtilities.invokeLater(SplashScreen::new);
    }
}