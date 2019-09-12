package com.linxiao.framework.crash;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class MailSenderInfo {
    private String mailServerHost;
    private String mailServerPort = "25";
    private InternetAddress fromAddress;
    private String userName;
    private String password;
    private boolean validate = true;
    private String subject;
    private String content;
    private String[] attachFileNames;
    private InternetAddress[] toAddress;

    public MailSenderInfo() {
    }

    public Properties getProperties() {
        Properties p = new Properties();
        p.put("mail.smtp.host", this.mailServerHost);
        p.put("mail.smtp.port", this.mailServerPort);
        p.put("mail.smtp.auth", this.validate?"true":"false");
        return p;
    }

    public String getMailServerHost() {
        return this.mailServerHost;
    }

    public void setMailServerHost(String mailServerHost) {
        this.mailServerHost = mailServerHost;
    }

    public String getMailServerPort() {
        return this.mailServerPort;
    }

    public void setMailServerPort(String mailServerPort) {
        this.mailServerPort = mailServerPort;
    }

    public boolean isValidate() {
        return this.validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public String[] getAttachFileNames() {
        return this.attachFileNames;
    }

    public void setAttachFileNames(String[] fileNames) {
        this.attachFileNames = fileNames;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String textContent) {
        this.content = textContent;
    }

    public InternetAddress[] getToAddress() {
        return toAddress;
    }

    public void setToAddress(String[] toAddress) {
        this.toAddress = new InternetAddress[toAddress.length];
        try {
            for (int i = 0; i < toAddress.length; i++) {
                this.toAddress[i] = new InternetAddress(toAddress[i]);
            }
        } catch (AddressException e) {
            e.printStackTrace();
        }
    }

    public InternetAddress getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String address,String name) {
        try {
            this.fromAddress = new InternetAddress(address,name);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
