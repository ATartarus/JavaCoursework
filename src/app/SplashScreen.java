package app;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {
    public SplashScreen() {
        setSize(700,  450);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel, BorderLayout.CENTER);


        JLabel l = new JLabel("Белорусский национальный технический университет");
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(l);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        l = new JLabel("Факультет информационных технологий и робототехники");
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(l);
        l = new JLabel("Кафедра программного обеспечения информационных систем и технологии");
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(l);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        l = new JLabel("Курсовая работа");
        l.setFont(new Font(l.getFont().getName(), Font.BOLD,20));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(l);
        l = new JLabel("по дисциплине «Программирование на языке Java»");
        l.setFont(new Font(l.getFont().getName(), Font.PLAIN, 18));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(l);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        l = new JLabel("Ведомость для проведения зачёта");
        l.setFont(new Font(l.getFont().getName(), Font.BOLD, 22));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(l);


        JPanel innerGrid = new JPanel(new GridLayout(1, 2));
        mainPanel.add(innerGrid);
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon icon = new ImageIcon("src/images/splash_icon.png");
        icon = new ImageIcon(icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH));
        imageLabel.setIcon(icon);
        innerGrid.add(imageLabel);

        JPanel innerInfo = new JPanel();
        innerInfo.setBorder(BorderFactory.createEmptyBorder(25, 10, 0, 0));
        innerGrid.add(innerInfo);
        innerInfo.setLayout(new BoxLayout(innerInfo, BoxLayout.PAGE_AXIS));
        innerInfo.add( new JLabel("Выполнил: Студент группы 10702221"));
        innerInfo.add(new JLabel("Пряжко Николай Кириллович"));

        innerInfo.add(Box.createRigidArea(new Dimension(0, 30)));

        innerInfo.add(new JLabel("Преподаватель: к.ф.-м.н.,доц."));
        innerInfo.add(new JLabel("Сидорик Валерий Владимирович"));

        l = new JLabel("Минск, 2023");
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(l);


        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        add(buttonsPanel, BorderLayout.SOUTH);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JButton nextButton = new JButton("Далее");
        nextButton.setBackground(Color.white);
        JButton exitButton = new JButton("Выход");
        exitButton.setBackground(Color.white);
        buttonsPanel.add(nextButton);
        buttonsPanel.add(exitButton);

        nextButton.addActionListener(e -> onNextButtonClick());
        exitButton.addActionListener(e -> onExitButtonClick());
    }

    private void onNextButtonClick() {
        SwingUtilities.invokeLater(() -> {
            new Application();
            dispose();
        });
    }

    private void onExitButtonClick() {
        System.exit(0);
    }
}
