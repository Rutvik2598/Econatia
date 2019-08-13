package com.company.econatia;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail extends AsyncTask<Void,Void,Void> {

    private Context context;
    private Session session;

    private String email;
    private String subjext;
    private String message;

    public SendMail(Context context, String email, String subjext, String message) {
        this.context = context;
        this.email = email;
        this.subjext = subjext;
        this.message = message;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        Properties props = new Properties();

        props.put("mail.smtp.host" , "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port" , "465");
        props.put("mail.smtp.socketFactory.class" , "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth" , "true");
        props.put("mail.smtp.port" , "465");

        session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Config.EMAIL , Config.PASSWORD);
            }
        });

        try {

            MimeMessage mm = new MimeMessage(session);
            mm.setFrom(new InternetAddress(Config.EMAIL));
            mm.addRecipient(Message.RecipientType.TO , new InternetAddress(email));
            mm.setSubject(subjext);
            mm.setText(message);
            Transport.send(mm);

        }
        catch (Exception e){

        }

        return null;
    }
}