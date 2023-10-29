package app;

import javax.swing.*;

public abstract class ContainerManager {
    protected final JFrame parent;
    protected final JPanel container;

    public ContainerManager(JFrame parent) {
        this.parent = parent;
        container = new JPanel();
    }

    protected abstract void configContainer();
    protected abstract void configEventListeners();
}
