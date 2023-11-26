package filemanagment;

import app.Application;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

public class ProjectData {
    private final String folderPath;
    private String projectFileName;
    private final String dataFileName;
    private final HashMap<String, Writable> containers;
    private final PropertyChangeSupport propertySup;

    public ProjectData(Writable[] containers) {
        this(containers, System.getProperty("user.home"));
    }

    public ProjectData(Writable[] containers, String folderPath) {
        this.containers = new HashMap<>();
        for (Writable source : containers) {
            this.containers.put(source.getClassName(), source);
        }
        this.folderPath = folderPath + "/Documents/" + Application.NAME;
        this.dataFileName = "data";
        this.propertySup = new PropertyChangeSupport(this);

        File dataFolder = new File(this.folderPath);
        if (!dataFolder.exists()) {
            if (!dataFolder.mkdir()) {
                System.err.println("data folder was not created");
            }
        }

        Path template = Paths.get(this.folderPath + "/template.docx");
        if (!Files.exists(template)) {
            template = Paths.get(System.getProperty("user.dir") + "/data/template.docx");
            try {
                Files.copy(template, Paths.get(this.folderPath + "/template.docx"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println("Copy template failed");
            }
        }

        setDefaultProjectName();
    }

    protected Writable getContainer(String key) {
        return containers.get(key);
    }

    public String getFolderPath() {
        return folderPath;
    }

    public String getProjectFileName() {
        return projectFileName;
    }

    protected String getDataFileName() {
        return dataFileName;
    }

    public void setProjectFileName(String projectFileName) {
        String oldVal = this.projectFileName;
        this.projectFileName = projectFileName;
        propertySup.firePropertyChange("projectFileName", oldVal, projectFileName);
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertySup.addPropertyChangeListener(l);
    }

    protected void setDefaultProjectName() {
        File[] files = new File(folderPath).listFiles(
                (dir, name) -> name.endsWith(".xml") && name.startsWith("project")
        );

        int i = 0;
        if (files == null) {
            if (new File(folderPath).isDirectory()) throw new IllegalArgumentException("listFiles returned null");
        }
        else {
            for (; i < files.length; i++) {
                int j = 0;
                for (; j < files.length; j++) {
                    String fileName = files[j].getName();
                    if (fileName.substring(7, fileName.indexOf(".")).equals(Integer.toString(i + 1))) {
                        break;
                    }
                }
                if (j >= files.length) break;
            }
        }

        setProjectFileName("project" + (i + 1));
    }

    /**
     * Searches for component in container.
     * @param source Container key in containers field.
     * @param component Component key in containers component map.
     * @return Desired component.
     * */
    protected JComponent findComponent(String source, String component) {
        Writable src = containers.get(source);
        if (src == null) throw new IllegalArgumentException("Source " + source + " not found");
        JComponent cmp = src.getComponentMap().get(component);
        if (cmp == null) throw new IllegalArgumentException("Component " + component + " not found");
        return cmp;
    }
}
