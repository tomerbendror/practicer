package com.practice.mail;

import com.practice.model.SendEmail;
import com.practice.model.SendEmail.EmailStatus;
import com.practice.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * User: tomer
 */
public class MailSendingJob {
    public static final String ENCODING = "UTF-8";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailRepository emailRepository;

    @Scheduled(fixedDelay = 5000)   // the period will be measured from the completion time of each preceding invocation
    @Transactional
    public void sendIfNeeded() throws UnsupportedEncodingException, MessagingException {
        List<SendEmail> emails = emailRepository.getMailsToSend();
        List<MimeMessage> mailsToSend = new ArrayList<>(emails.size());
        for (SendEmail email : emails) {
            MimeMessage message = mailSender.createMimeMessage();
//            // todo here we need to duplicate the mail in case of emailPerRecipient
            message.setSubject(email.getSubject(), ENCODING);
            MimeMessageHelper helper = new MimeMessageHelper(message, true, ENCODING);
            helper.setFrom("Practicer", isNotBlank(email.getFromName()) ? email.getFromName() : "Practicer");
            helper.setTo(toAddressesAry(email.getToAddress()));
            helper.setCc(toAddressesAry(email.getCc()));
            helper.setBcc(toAddressesAry(email.getBcc()));
            if (isNotBlank(email.getReplyTo())) {
                helper.setReplyTo(toAddressesAry(email.getReplyTo())[0]);
            }
            helper.setText(new String(email.getContent(), ENCODING), true);
            mailsToSend.add(message);

            email.setStatus(EmailStatus.SENT);
            email.setSendTime(new Date());
        }

        if (!mailsToSend.isEmpty()) {
            // the mails will be send async right after success commit
            mailSender.send(mailsToSend.toArray(new MimeMessage[mailsToSend.size()]));
        }
    }

    private String[] toAddressesAry(String addresses) {
        if (isBlank(addresses)) {
            return new String[]{};
        }
        return addresses.split(SendEmail.ADDRESS_LIST_DELIMITER);
    }
}
