package com.practice.repository;

import com.practice.model.SendEmail;
import com.practice.model.SendEmail.EmailStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * User: tomer
 */
@Repository("emailRepository")
@Transactional
public class EmailRepository extends BaseRepository {
    public void createMailToSend(SendEmail sendEmail) {
        genericDao.persist(sendEmail);
    }

    public List<SendEmail> getMailsToSend() {
        TypedQuery<SendEmail> query = genericDao.getEm().createQuery("from SendEmail where status=:status", SendEmail.class);
        query.setParameter("status", EmailStatus.GENERATED);
        return query.getResultList();
    }
}
