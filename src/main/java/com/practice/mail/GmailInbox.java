package com.practice.mail;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

public class GmailInbox {

    public static void main(String[] args) {
//        GmailInbox gmail = new GmailInbox();
//        gmail.read();

        String key = new BigInteger(130, new SecureRandom()).toString(32);
        System.out.println(key);
    }

    public void read() {

//        Properties props = System.getProperties();
//        props.setProperty("mail.store.protocol", "imaps");
//        try {
//            Session session = Session.getDefaultInstance(props, null);
//            Store store = session.getStore("imaps");
//            store.connect("imap.gmail.com", "<username>@gmail.com", "<password>");
//            ...
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//            System.exit(1);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            System.exit(2);
//        }
        Properties props = new Properties();
        try {
            props.setProperty("mail.store.protocol", "imaps");
            Session session = Session.getDefaultInstance(props, null);

            Store store = session.getStore("imaps");
            store.connect("smtp.gmail.com", "word.practice001@gmail.com","nmsnms003");

            Folder inbox = store.getFolder("inbox");
            inbox.open(Folder.READ_ONLY);
            int messageCount = inbox.getMessageCount();

            System.out.println("Total Messages:- " + messageCount);

            Message[] messages = inbox.getMessages();
            System.out.println("------------------------------");
            for (int i = 0; i < 10; i++) {
                System.out.println("Mail Subject:- " + messages[i].getSubject());
            }
            inbox.close(true);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}