package containers;

import javax.swing.*;

public abstract class ComponentManager {
    protected final JFrame parent;
    protected final JPanel container;

    public ComponentManager(JFrame parent) {
        this.parent = parent;
        container = new JPanel();
    }

    protected abstract void configContainer();
    protected abstract void addComponents();
    protected abstract void configEventListeners();
}
