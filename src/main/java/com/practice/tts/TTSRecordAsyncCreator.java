package com.practice.tts;

import com.practice.controller.EmailController;
import com.practice.model.Question;
import com.practice.model.TTSRecord;
import com.practice.repository.QuestionRepository;
import com.practice.repository.TTSRepository;
import com.practice.type.FileMimeType;
import org.apache.log4j.Logger;
import org.springframework.scheduling.TaskScheduler;

import java.util.concurrent.ScheduledFuture;

/**
 * Created by Tomer
 */
public class TTSRecordAsyncCreator implements Runnable {

    private static final Logger logger = Logger.getLogger(NeoSpeechTTSEngine.class);
    public static final long REPEAT_DELAY_MS = 1000;
    public static final int MAX_SEC_TO_TRY = 240;
    public static final int MAX_TRIES = 240;

    private String text;
    private long questionId;
    private AbsTTSEngine ttsEngine;
    private TTSRepository ttsRepository;
    private QuestionRepository questionRepository;
    private TaskScheduler practicerScheduler;
    private EmailController emailController;

    private String audioFileUrl;
    private ScheduledFuture scheduledFuture;
    private long initialExecutionTime = -1;
    private int retries;

    public TTSRecordAsyncCreator(Question question, AbsTTSEngine ttsEngine, TTSRepository ttsRepository,
                                 QuestionRepository questionRepository, TaskScheduler practicerScheduler, EmailController emailController) {
        this.questionId = question.getId();
        this.text = question.getQuestionStr();
        this.ttsEngine = ttsEngine;
        this.ttsRepository = ttsRepository;
        this.questionRepository = questionRepository;
        this.practicerScheduler = practicerScheduler;
        this.emailController = emailController;
    }

    public void start() {
        scheduledFuture = practicerScheduler.scheduleWithFixedDelay(this, REPEAT_DELAY_MS);
    }

    @Override
    public void run() {
        try {
            if (initialExecutionTime == -1) {
                initialExecutionTime = System.currentTimeMillis();
            }

            if (audioFileUrl == null) {
                audioFileUrl = ttsEngine.getAudioFileUrl(text);
                if (audioFileUrl == null) {
                    cancelIfNeeded(false);
                    retries++;
                    return;
                }
            }

            byte[] bytes = ttsEngine.downloadFile(audioFileUrl);
            if (bytes == null || bytes.length == 0) {
                cancelIfNeeded(false);
                retries++;
                return;
            }

            bytes = ttsEngine.manipulateFile(bytes);
            TTSRecord ttsRecord = new TTSRecord(text, FileMimeType.MP3, ttsEngine.getLanguage(), ttsEngine.getSource(), bytes);
            ttsRepository.saveTTSRecord(ttsRecord);
            questionRepository.updateTTSGenerationFinished(questionId, ttsRecord);
            scheduledFuture.cancel(false);
            int secondsFromStart = secondsFromStart();
            String message = "generation of TTS record for '" + text + "' took " + secondsFromStart + " sec";
            logger.debug(message);
            if (secondsFromStart > 30) {
                emailController.sendDebugMail(message);
            }
        } catch (Exception e) {
            cancelIfNeeded(true, e);
        }
    }

    private boolean cancelIfNeeded(boolean forceCancellation) {
        return cancelIfNeeded(forceCancellation, null);
    }

    private boolean cancelIfNeeded(boolean forceCancellation, Exception e) {
        boolean shouldCancel = forceCancellation || (secondsFromStart() > MAX_SEC_TO_TRY && retries > MAX_TRIES);
        if (shouldCancel) {
            scheduledFuture.cancel(false);
            questionRepository.updateTTSGenerationFail(questionId);

            String message = "fail to get TTS record for text: '" + text + "' for " + secondsFromStart() + " sec";
            if (e != null) {
                logger.error(message, e);
            } else {
                logger.error(message);
            }
            emailController.sendDebugMail(message);
        }
        return shouldCancel;
    }

    private int secondsFromStart() {
        return (int) (System.currentTimeMillis() - initialExecutionTime) /1000;
    }
}