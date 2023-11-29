package app;

import javax.swing.*;
import java.awt.*;

public class AboutProgramWindow extends JDialog {
    public AboutProgramWindow(JFrame parentFrame) {
        super(parentFrame, null, true);
        setResizable(false);
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getContentPane().setLayout(new BorderLayout(10, 10));

        JLabel l = new JLabel("Составление ведомости для проведения зачёта");
        l.setFont(new Font(l.getFont().getName(), Font.BOLD, 16));
        JPanel p = new JPanel();
        p.add(l);
        add(p, BorderLayout.NORTH);

        JLabel imageLabel = new JLabel();
        ImageIcon icon = new ImageIcon("src/images/about_program_image.png");
        icon = new ImageIcon(icon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH));
        imageLabel.setIcon(icon);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
        add(imageLabel, BorderLayout.WEST);

        JPanel mainPanel = new JPanel();
        add(mainPanel);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.add(new JLabel("<html><b>Программа позволяет</html>"));

        mainPanel.add(new JLabel("<html><b>1.</b> Заполнять информацию о зачёте (предмет, факультет и т.д.)</html>"));
        mainPanel.add(new JLabel("<html><b>2.</b> Заполнять список группы, сохранять его и загружать</html>"));
        mainPanel.add(new JLabel("<html><b>3.</b> Подсчитывать количество аттестованных/не аттестованных</html>"));
        mainPanel.add(new JLabel("<html><b>4.</b> Экспортировать полученную ведомость в формате docx</html>"));
        mainPanel.add(new JLabel("<html><b>5.</b> Изменять шаблон экспортированного документа</html>"));

        JPanel buttonPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Назад");
        backButton.setBackground(Color.white);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> onBackButtonClick());
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onBackButtonClick() {
        dispose();
    }
}
