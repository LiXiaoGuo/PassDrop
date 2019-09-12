package com.linxiao.framework.crash;

import android.util.Log;

import com.sun.mail.util.MailSSLSocketFactory;

import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SimpleMailSender {
    public SimpleMailSender() {
    }

    public boolean sendTextMail(MailSenderInfo mailInfo) throws GeneralSecurityException {

        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if(mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        pro.put("mail.smtp.ssl.enable", "true");
        pro.put("mail.smtp.ssl.socketFactory", sf);
        Session sendMailSession = Session.getDefaultInstance(pro, authenticator);

        try {
            MimeMessage ex = new MimeMessage(sendMailSession);
            ex.setFrom(mailInfo.getFromAddress());
            ex.setRecipients(RecipientType.TO, mailInfo.getToAddress());
            ex.setSubject(mailInfo.getSubject());
            ex.setSentDate(new Date());
            String mailContent = mailInfo.getContent();
            ex.setText(mailContent);
            Transport.send(ex);
        } catch (Exception var9) {
            Log.e("-----------",var9.toString());
            var9.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean sendHtmlMail(MailSenderInfo mailInfo) {
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if(mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }

        Session sendMailSession = Session.getDefaultInstance(pro, authenticator);

        try {
            MimeMessage ex = new MimeMessage(sendMailSession);
            ex.setFrom(mailInfo.getFromAddress());
            ex.setRecipients(RecipientType.TO, mailInfo.getToAddress());
            ex.setSubject(mailInfo.getSubject());
            ex.setSentDate(new Date());
            MimeMultipart mainPart = new MimeMultipart();
            MimeBodyPart html = new MimeBodyPart();
            html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
            mainPart.addBodyPart(html);
            ex.setContent(mainPart);
            Transport.send(ex);
            return true;
        } catch (MessagingException var9) {
            var9.printStackTrace();
            return false;
        }
    }
}

