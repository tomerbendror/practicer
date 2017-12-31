package com.practice.mail;

import com.practice.repository.BaseRepository;
import com.practice.repository.EmailRepository;
import com.practice.util.PracticerEnvConfig;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: tomer
 */
public class EmailSenderImpl extends BaseRepository implements EmailSender {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private PracticerEnvConfig practicerEnvConfig;

    @Override
    public void sendEmail(EmailAttributes emailAttributes) {
        if (!practicerEnvConfig.isProduction()) {
            emailAttributes.setSubject(practicerEnvConfig.getEnvName() + " - " + emailAttributes.getSubject());
        }
        emailRepository.createMailToSend(emailAttributes.getSendEmail());
    }
}
