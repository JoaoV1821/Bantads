package ms.saga.util;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import io.github.cdimascio.dotenv.Dotenv;

public class Email {
    
    private static Dotenv dotenv = Dotenv.load();
    private static String senderEmail = dotenv.get("REMETENTE");
    private static String senderPassword = dotenv.get("SENHA_REMETENTE");


    private static Session criarSessionMail() {
        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", 465);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.port", 465);

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        session.setDebug(true);

        return session;
 }


        public static void enviarEmail(String msg, String assunto, String email) throws AddressException, MessagingException {

            Message message = new MimeMessage(criarSessionMail());
            message.setFrom(new InternetAddress(senderEmail));  

            Address[] toUser = InternetAddress.parse(email.trim().toLowerCase());
            message.setRecipients(Message.RecipientType.TO, toUser);
            message.setSubject(assunto);
            message.setContent(msg, "text/html");   
            
            Transport.send(message);    
        }
}