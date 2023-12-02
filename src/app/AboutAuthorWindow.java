package app;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents dialog window with information about author.
 */
public class AboutAuthorWindow extends JDialog {
    /**
     * Creates class instance with parent frame.
     * Window will be shown at center of the screen.
     * @param parentFrame parent frame of this instance.
     */
    public AboutAuthorWindow(JFrame parentFrame) {
        super(parentFrame, null, true);
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        add(mainPanel, BorderLayout.SOUTH);

        JLabel authorPhoto = new JLabel();
        try (InputStream input = getClass().getResourceAsStream("/images/whoami.jpg")) {
            if (input != null) {
                ImageIcon icon = new ImageIcon(ImageIO.read(input));
                icon = new ImageIcon(icon.getImage().getScaledInstance(330, 330, Image.SCALE_SMOOTH));
                authorPhoto.setIcon(icon);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        authorPhoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(authorPhoto, BorderLayout.CENTER);

        Font font = new Font(UIManager.getFont("Label.font").getName(), Font.PLAIN, 16);
        SplashScreen.addCenteredLabel("Автор", mainPanel, font);
        SplashScreen.addCenteredLabel("Студент группы 10702221", mainPanel, font);
        SplashScreen.addCenteredLabel("Пряжко Николай Кириллович", mainPanel, font);
        SplashScreen.addCenteredLabel("kolyapryazko@gmail.com", mainPanel, font);
        mainPanel.add(Box.createVerticalStrut(5));

        JPanel buttonPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Назад");
        backButton.setBackground(Color.white);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> onBackButtonClick());
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onBackButtonClick() {
        dispose();
    }
}
