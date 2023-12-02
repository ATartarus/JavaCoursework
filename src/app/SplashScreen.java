package app;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents window that will be shown on application startup.
 */
public class SplashScreen extends JWindow {
    private final JPanel mainPanel;
    private final JPanel innerGridPanel;
    private final JPanel buttonsPanel;
    private final Timer exitTimer;

    /**
     * Creates class instance with default configuration.
     * Window will be shown at center of the screen.
     */
    public SplashScreen() {
        exitTimer = new Timer(30000, e -> onExitButtonClick());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                exitTimer.start();
            }
        });
        setSize(700,  450);

        mainPanel = new JPanel();
        innerGridPanel = new JPanel(new GridLayout(1, 2));
        buttonsPanel = new JPanel(new GridLayout(1, 2, 5, 0));

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        populateMainPanel();
        populateInnerGridPanel();
        populateButtonsPanel();

        add(mainPanel, BorderLayout.CENTER);
        mainPanel.add(innerGridPanel);
        addCenteredLabel("Минск, 2023", mainPanel, null);
        add(buttonsPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void populateMainPanel() {
        String fontName = UIManager.getFont("Label.font").getName();

        addCenteredLabel("Белорусский национальный технический университет",
                mainPanel, null);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        addCenteredLabel("Факультет информационных технологий и робототехники",
                mainPanel, null);
        addCenteredLabel("Кафедра программного обеспечения информационных систем и технологии",
                mainPanel, null);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        addCenteredLabel("Курсовая работа",
                mainPanel, new Font(fontName, Font.BOLD,20));
        addCenteredLabel("по дисциплине «Программирование на языке Java»",
                mainPanel, new Font(fontName, Font.PLAIN, 18));

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        addCenteredLabel("Ведомость для проведения зачёта",
                mainPanel, new Font(fontName, Font.BOLD, 22));
    }

    private void populateInnerGridPanel() {
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        try (InputStream input = getClass().getResourceAsStream("/images/splash_icon.png")) {
            if (input != null) {
                ImageIcon icon = new ImageIcon(ImageIO.read(input));
                icon = new ImageIcon(icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH));
                imageLabel.setIcon(icon);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        innerGridPanel.add(imageLabel);

        JPanel innerInfo = new JPanel();
        innerInfo.setBorder(BorderFactory.createEmptyBorder(25, 10, 0, 0));
        innerGridPanel.add(innerInfo);
        innerInfo.setLayout(new BoxLayout(innerInfo, BoxLayout.PAGE_AXIS));
        innerInfo.add(new JLabel("Выполнил: Студент группы 10702221"));
        innerInfo.add(new JLabel("Пряжко Николай Кириллович"));

        innerInfo.add(Box.createRigidArea(new Dimension(0, 30)));

        innerInfo.add(new JLabel("Преподаватель: к.ф.-м.н.,доц."));
        innerInfo.add(new JLabel("Сидорик Валерий Владимирович"));
    }

    private void populateButtonsPanel() {
        JButton nextButton = new JButton("Далее");
        nextButton.setBackground(Color.white);
        JButton exitButton = new JButton("Выход");
        exitButton.setBackground(Color.white);
        buttonsPanel.add(nextButton);
        buttonsPanel.add(exitButton);

        nextButton.addActionListener(e -> onNextButtonClick());
        exitButton.addActionListener(e -> onExitButtonClick());
    }

    /**
     * Utility method used for adding JLabel to the center of JPanel with BoxLayout.
     * @param text Content of JLabel.
     * @param container JPanel with BoxLayout.
     * @param font Font instance for JLabel.
     */
    public static void addCenteredLabel(String text, JPanel container, Font font) {
        JLabel l = new JLabel(text);
        if (font != null) l.setFont(font);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(l);
    }

    private void onNextButtonClick() {
        exitTimer.stop();
        SwingUtilities.invokeLater(() -> {
            new Application();
            dispose();
        });
    }

    private void onExitButtonClick() {
        System.exit(0);
    }
}