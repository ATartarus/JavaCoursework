package app;

import javax.swing.*;
import java.awt.*;

public class Footer extends ContainerManager {
    public Footer(JFrame parent) {
        super(parent);
        configContainer();
    }

    @Override
    protected void configContainer() {
        container.setBackground(Color.green);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.gridy = 2;
        gbc.weighty = 1;
        parent.getContentPane().add(container, gbc);
    }

    @Override
    protected void configEventListeners() {

    }
}
