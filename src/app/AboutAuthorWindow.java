package app;

import javax.swing.*;
import java.awt.*;

public class AboutAuthorWindow extends JDialog {
    public AboutAuthorWindow(JFrame parentFrame) {
        super(parentFrame, null, true);
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        add(mainPanel, BorderLayout.SOUTH);

        JLabel authorPhoto = new JLabel();
        ImageIcon image = new ImageIcon("src/images/whoami.jpg");
        image = new ImageIcon(image.getImage().getScaledInstance(330, 330, Image.SCALE_SMOOTH));
        authorPhoto.setIcon(image);
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
