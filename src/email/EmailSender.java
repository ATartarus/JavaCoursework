package email;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * Utility class used to send emails.
 */
public class EmailSender {
    /**
     * Sends email with specified parameters.
     * @param recipientEmail Email address of the receiver.
     * @param subject Subject of this email.
     * @param message Message of this email.
     * @param smtpProp Properties of SMTP server used to send emails.
     * @param attachedFiles Any files that will be sent with email.
     * @throws MessagingException If any message issues happened.
     * @throws IOException If file attachment failed.
     */
    public static void send(String recipientEmail, String subject, String message,
                            Properties smtpProp, File[] attachedFiles)
            throws MessagingException, IOException {
        final String senderEmail = smtpProp.getProperty("mail.user");
        final String senderPassword = smtpProp.getProperty("mail.password");

        Session session = Session.getInstance(smtpProp, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        Message emailMessage = new MimeMessage(session);
        emailMessage.setFrom(new InternetAddress(senderEmail));
        emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        emailMessage.setSubject(subject);
        emailMessage.setSentDate(new Date());

        Multipart messageMultipart = new MimeMultipart();

        MimeBodyPart messageBody = new MimeBodyPart();
        messageBody.setText(message);
        messageMultipart.addBodyPart(messageBody);

        for (File attachment : attachedFiles) {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(attachment);
            messageMultipart.addBodyPart(attachmentPart);
        }

        emailMessage.setContent(messageMultipart);
        Transport.send(emailMessage);
    }
}
