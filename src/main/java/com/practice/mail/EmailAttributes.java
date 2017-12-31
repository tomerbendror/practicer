package com.practice.mail;

import com.google.common.collect.Lists;
import com.practice.model.EmailType;
import com.practice.model.SendEmail;
import com.practice.model.SendEmail.EmailStatus;
import com.practice.model.SendEmail.PublishType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: tomer
 */
public class EmailAttributes {
    private String fromName;
    private String subject;
    private String content;
    private EmailType emailType;
    private String to;
    private String cc;
    private String bcc;
    private String replyToEmail;
    private String replyToName;
    private PublishType publishType = PublishType.SINGLE_MAIL_FOR_ALL;

    public EmailAttributes(String subject, String content, String to, EmailType emailType) {
        this(subject, content, Lists.newArrayList(to), emailType);
    }

    public EmailAttributes(String subject, String content, Collection<String> to, EmailType emailType) {
        this.subject = subject;
        this.content = content;
        this.emailType = emailType;

        StringBuilder stringBuilder = new StringBuilder();
        for (String email : to) {
            stringBuilder.append(email.trim()).append(SendEmail.ADDRESS_LIST_DELIMITER);
        }
        this.to = stringBuilder.substring(0, stringBuilder.length() - SendEmail.ADDRESS_LIST_DELIMITER.length());
    }

    public SendEmail getSendEmail() {
        SendEmail sendEmail = new SendEmail();
        sendEmail.setFromName(fromName);
        sendEmail.setSubject(subject);
        sendEmail.setContent(content.getBytes());
        sendEmail.setEmailType(emailType);
        sendEmail.setToAddress(to);
        sendEmail.setCc(cc);
        sendEmail.setBcc(bcc);
        sendEmail.setStatus(EmailStatus.GENERATED);
        sendEmail.setReplyTo(replyToEmail);
        return sendEmail;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public EmailAttributes setEmailType(EmailType emailType) {
        this.emailType = emailType;
        return this;
    }

    public String getFromName() {
        return fromName;
    }

    public EmailAttributes setFromName(String fromName) {
        this.fromName = fromName;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public EmailAttributes setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getContent() {
        return content;
    }

    public EmailAttributes setContent(String content) {
        this.content = content;
        return this;
    }

    public String getTo() {
        return to;
    }

    public EmailAttributes setTo(String to) {
        this.to = to;
        return this;
    }

    public String getReplyToEmail() {
        return replyToEmail;
    }

    public EmailAttributes setReplyToEmail(String replyToEmail) {
        this.replyToEmail = replyToEmail;
        return this;
    }

    public String getReplyToName() {
        return replyToName;
    }

    public EmailAttributes setReplyToName(String replyToName) {
        this.replyToName = replyToName;
        return this;
    }

    public String getBcc() {
        return bcc;
    }

    public EmailAttributes setBcc(String bcc) {
        this.bcc = bcc;
        return this;
    }

    public String getCc() {
        return cc;
    }

    public EmailAttributes setCc(String cc) {
        this.cc = cc;
        return this;
    }

    public PublishType getPublishType() {
        return publishType;
    }

    public EmailAttributes setPublishType(PublishType publishType) {
        this.publishType = publishType;
        return this;
    }
}
