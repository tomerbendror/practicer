package com.practice.mail;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.Executor;

/**
 * User: tomer
 */
public class MailSendingInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(MailSendingInterceptor.class);

    @Autowired
    private Executor practicerExecutor;

    public Object executeOnSuccessfulCommit(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        if(!TransactionSynchronizationManager.isSynchronizationActive()) {
            return proceedingJoinPoint.proceed();
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                practicerExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            proceedingJoinPoint.proceed();
                        } catch (Throwable throwable) {
                            logger.error("fail to send mail", throwable);
                        }
                    }
                });
            }
        });

        return null;
    }
}
