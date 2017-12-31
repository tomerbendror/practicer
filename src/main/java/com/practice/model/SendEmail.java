package com.practice.model;

import javax.persistence.*;
import java.util.Date;

/**
 * User: tomer
 */
@Entity
@Table(name = "send_email")
public class SendEmail extends BaseEntity {
    public static final String ADDRESS_LIST_DELIMITER = ";";

    @Column(name = "FROM_NAME")
    private String fromName;

    @Column(name = "TO_ADDRESS")
    private String toAddress;

    @Column(name = "CC")
    private String cc;

    @Column(name = "BCC")
    private String bcc;

    @Column(name = "REPLY_TO")
    private String replyTo;

    @Column(name = "SUBJECT")
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "EMAIL_TYPE")
    private EmailType emailType;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private EmailStatus status;

    @Column(name = "ERROR_DETAILS")
    private String errorDetails;

    @Column(name = "CREATE_TIME", insertable = false, updatable = false)
    private Date createTime;

    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    @Column(name = "SEND_TIME", insertable = false)
    private Date sendTime;

    @Column(name = "RANDOM_KEY")
    private String randomKey;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "CONTENT")
    private byte[] content;

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    public EmailStatus getStatus() {
        return status;
    }

    public void setStatus(EmailStatus status) {
        this.status = status;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getRandomKey() {
        return randomKey;
    }

    public void setRandomKey(String randomKey) {
        this.randomKey = randomKey;
    }

    public enum EmailStatus {
        GENERATED,
        SENT,
        ERROR
    }

    public enum PublishType {
        MAIL_PER_RECIPIENT,
        SINGLE_MAIL_FOR_ALL
    }
}
