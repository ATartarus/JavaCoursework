package app;

import email.EmailSender;
import filemanagment.ProjectData;
import filemanagment.ProjectExporter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SendEmailWindow extends JDialog {
    private FileCreator fileCreator;
    private final JPanel header;
    private final JPanel messagePanel;
    private JTextField senderTextField;
    private JPasswordField passwordTextField;
    private JTextField recipientTextField;
    private JTextField subjectTextField;
    private JTextArea messageTextArea;

    public SendEmailWindow(JFrame parentFrame, Application app) {
        super(parentFrame, "Send email", true);
        fileCreator = new FileCreator(app.getData());

        try (InputStream input = getClass().getResourceAsStream("/images/email_window_icon.png")) {
            if (input != null) {
                ImageIcon icon = new ImageIcon(ImageIO.read(input));
                setIconImage(icon.getImage());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        header = new JPanel();
        messagePanel = new JPanel(new BorderLayout(0, 5));

        populateHeader();
        populateMessagePanel();
        setSizeConstraints();

        add(header, BorderLayout.NORTH);
        add(messagePanel, BorderLayout.CENTER);

        JButton sendButton = new JButton("Отправить");
        sendButton.setBackground(Color.white);
        sendButton.addActionListener(e -> onSendButtonClick());
        add(sendButton, BorderLayout.SOUTH);


        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void populateHeader() {
        header.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.weighty = 0;
        gbc.weightx = 0;

        JLabel l = new JLabel("Email отправителя: ");
        gbc.gridx = 0;
        gbc.gridy = 0;
        header.add(l, gbc);
        l = new JLabel("Пароль:");
        gbc.gridy = 1;
        header.add(l, gbc);
        l = new JLabel("Email получателя:");
        gbc.gridy = 2;
        header.add(l, gbc);
        l = new JLabel("Тема:");
        gbc.gridy = 3;
        header.add(l, gbc);

        senderTextField = new JTextField();
        gbc.weightx = 1.0;
        gbc.gridx = 1;
        gbc.gridy = 0;
        header.add(senderTextField, gbc);
        passwordTextField = new JPasswordField();
        gbc.gridy = 1;
        header.add(passwordTextField, gbc);
        recipientTextField = new JTextField();
        gbc.gridy = 2;
        header.add(recipientTextField, gbc);
        subjectTextField = new JTextField();
        gbc.gridy = 3;
        header.add(subjectTextField, gbc);
    }

    private void populateMessagePanel() {
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        messagePanel.add(new JLabel("Текст письма:"), BorderLayout.NORTH);
        messageTextArea = new JTextArea();
        messageTextArea.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        messagePanel.add(messageTextArea);
    }

    private void setSizeConstraints() {
        senderTextField.setMinimumSize(new Dimension(100, 28));
        senderTextField.setPreferredSize(new Dimension(200, 28));
        passwordTextField.setMinimumSize(new Dimension(100, 28));
        passwordTextField.setPreferredSize(new Dimension(200, 28));
        recipientTextField.setMinimumSize(new Dimension(100, 28));
        recipientTextField.setPreferredSize(new Dimension(200, 28));
        subjectTextField.setMinimumSize(new Dimension(100, 28));
        subjectTextField.setPreferredSize(new Dimension(200, 28));
        messageTextArea.setMinimumSize(new Dimension(0, 100));
        messageTextArea.setPreferredSize(new Dimension(0, 100));
    }

    private void onSendButtonClick() {
        if (senderTextField.getText() == null || senderTextField.getText().isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Укажите email отправителя",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        else if (passwordTextField.getPassword() == null || passwordTextField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Укажите пароль отправителя",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        else if (recipientTextField.getText() == null || recipientTextField.getText().isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Укажите email получателя",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        //sendEmail();
        fileCreator.execute();
        fileCreator = new FileCreator(fileCreator.data);
    }

    protected void sendEmail() {
        Properties smtpProp = new Properties();
        smtpProp.put("mail.smtp.host", "smtp.gmail.com");
        smtpProp.put("mail.smtp.port", "587");
        smtpProp.put("mail.user", senderTextField.getText());
        smtpProp.put("mail.password", new String(passwordTextField.getPassword()));
        smtpProp.put("mail.smtp.starttls.enable", "true");
        smtpProp.put("mail.smtp.auth", "true");

        File attachment = new File(fileCreator.filePath);
        try {
            EmailSender.send(
                    recipientTextField.getText(),
                    subjectTextField.getText(),
                    messageTextArea.getText(),
                    smtpProp,
                    new File[] { attachment }
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        } finally {
            attachment.delete();
        }

        this.dispose();
        JOptionPane.showConfirmDialog(
                this,
                "Email отправлен успешно",
                "Success",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private class FileCreator extends SwingWorker<Void, Void> {
        private final ProjectData data;
        private final String filePath;
        public FileCreator(ProjectData data) {
            this.data = data;
            this.filePath = data.getFolderPath() + "tmp.docx";
        }
        @Override
        protected Void doInBackground() throws IOException {
            ProjectExporter.export(data, filePath);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                sendEmail();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        SendEmailWindow.this,
                        e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
