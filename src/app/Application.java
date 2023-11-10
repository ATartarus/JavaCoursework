package app;

import containers.*;
import exceptions.XMLParseException;
import filemanagment.*;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.IOException;
import java.util.Enumeration;


public class Application {
    private static final String appName = "Journal";
    private final JFrame mainWindow;
    private final Header header;
    private final Body body;
    private final Footer footer;
    private final ProjectData projectData;
    private final ProjectFileManager fileManager;

    public Application() {
        configUIDefaults();

        mainWindow = new JFrame(appName);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setJMenuBar(createMenu());
        mainWindow.setContentPane(new JPanel(new GridBagLayout()));

        header = new Header(mainWindow);
        body = new Body(mainWindow);
        footer = new Footer(mainWindow);
        body.addTableModelListener(footer::updateData);

        projectData = new ProjectData(new Writable[]{header, body});
        projectData.addPropertyChangeListener(
                evt -> mainWindow.setTitle(appName + " – " + evt.getNewValue())
        );
        fileManager = new filemanagment.ProjectFileManager(projectData);

        mainWindow.setTitle(appName + " – " + projectData.getProjectFileName());
        mainWindow.pack();
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
        mainWindow.setMinimumSize(mainWindow.getSize());
    }

    private void configUIDefaults() {
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

    private void onNewProjectClick() {
        int answer = JOptionPane.showConfirmDialog(
                mainWindow,
                "Сохранить текущий проект?",
                "Warning",
                JOptionPane.YES_NO_OPTION
        );
        if (answer == JOptionPane.OK_OPTION) {
            if (fileManager.isProjectFileExists()) {
                onSaveProjectClick();
            } else {
                onSaveAsProjectClick();
            }
        }

        fileManager.newProject();
    }

    private void onOpenProjectClick() {
        if (fileManager.showFileChooser(mainWindow, ProjectFileManager.OPEN_MODE)) {
            try {
                fileManager.loadProject();
            } catch (IOException ioException) {
                showErrorMessage(ioException.getMessage(), "Load file error");
            } catch (XMLParseException parseException) {
                showErrorMessage(parseException.getMessage(), "Project file corruption");
            }
        }
    }

    private void onSaveProjectClick() {
        if (fileManager.isProjectFileExists()) {
            saveProject();
        } else {
            onSaveAsProjectClick();
        }
    }

    private void onSaveAsProjectClick() {
        if (fileManager.showFileChooser(mainWindow, ProjectFileManager.SAVE_MODE)) {
            saveProject();
        }
    }

    private void saveProject() {
        try {
            fileManager.saveProject();
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
            if (fileManager.groupExists(groupID)) {
                answer = JOptionPane.showConfirmDialog(mainWindow,
                        "<html>Группа с данным номером уже существует<br>Перезаписать?</html>",
                        "Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
            }
            if (answer == JOptionPane.OK_OPTION) {
                fileManager.saveGroup(groupID);
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
            if (!fileManager.groupExists(groupID)) {
                JOptionPane.showMessageDialog(
                        mainWindow,
                        "Записи о данной группе не существует",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            fileManager.loadGroup(groupID);
        } catch (XMLParseException parseException) {
            showErrorMessage(parseException.getMessage(), "Data file corruption");
        } catch (IOException ioException) {
            showErrorMessage(ioException.getMessage(), "Save group error");
        }
    }

    private void onExportClick() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                ProjectExporter.export(projectData, projectData.getFolderPath() + "\\template.docx");
                return null;
            }
        }.execute();
    }

    private void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(
                mainWindow,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Application m = new Application();
        });
    }
}