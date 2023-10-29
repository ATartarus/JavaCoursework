package containers;

import javax.swing.*;

public abstract class ComponentManager {
    protected final JFrame parent;
    protected final JPanel container;

    public ComponentManager(JFrame parent) {
        this.parent = parent;
        container = new JPanel();

        configContainer();
        initComponents();
        configComponents();
        addComponents();
        setSizeConstraints();
        configEventListeners();
    }

    /**
     * Configures container and adds it to the parent component
     */
    protected abstract void configContainer();

    /**
     * Initializes all components of container
     * Can carry out actions essential for initialization
     */
    protected abstract void initComponents();

    /**
     * Configures all components of container
     */
    protected abstract void configComponents();

    /**
     * Adds all components to container
     * Performs additional actions essential for layout
     */
    protected abstract void addComponents();

    /**
     * Sets size for containers components if needed
     * Includes setPreferredSize, setMinimumSize, setMaximumSize
     */
    protected abstract void setSizeConstraints();

    /**
     * Adds various event listeners to containers components if needed
     */
    protected abstract void configEventListeners();
}
