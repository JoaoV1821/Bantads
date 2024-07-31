package com.dac.auth.utils;

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

public class EmailUtils {
    
    private static Session criarSessionMail() {
        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", 465);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.port", 465);

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("joaovitoraraujo1821@gmail.com", "mcac xacl gyro pezr");
                }
        });

        session.setDebug(true);

        return session;
 }


    public static void enviarEmail(String msg, String assunto, String email) throws AddressException, MessagingException {

        String remetente = "joaovitoraraujo1821@gmail.com";
        System.out.println("__________________________________________________");
        System.out.println("Enviando email DE JV TESTE: " + remetente + " PARA: " + email);
        System.out.println("Assunto: " + assunto);

        Message message = new MimeMessage(criarSessionMail());
        message.setFrom(new InternetAddress(remetente)); // Remetente

        Address[] toUser = InternetAddress // Destinatário(s)
                    .parse(email.trim().toLowerCase());

        message.setRecipients(Message.RecipientType.TO, toUser);
        message.setSubject(assunto);// Assunto
        message.setContent(msg, "text/html");
        /** Método para enviar a mensagem criada */


        Transport.send(message);

        System.out.println("Email enviado com sucesso !");
        System.out.println("__________________________________________________");

    }
}
