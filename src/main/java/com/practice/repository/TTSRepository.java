package com.practice.repository;

import com.practice.controller.EmailController;
import com.practice.model.Practice;
import com.practice.model.Question;
import com.practice.model.TTSRecord;
import com.practice.model.TTSRecord.TTSRecordKey;
import com.practice.tts.AbsTTSEngine;
import com.practice.tts.TTSRecordAsyncCreator;
import com.practice.tts.TtsEnginesMapping;
import com.practice.type.Language;
import com.practice.type.TTSGenerationState;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.*;

/**
 * Created by Tomer
 */
@Repository("ttsRepository")
@Transactional
public class TTSRepository extends BaseRepository {

    private static final Logger logger = Logger.getLogger(TTSRepository.class);

    @Autowired
    private TtsEnginesMapping ttsEnginesMapping;

    @Autowired
    private TaskScheduler practicerScheduler;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private EmailController emailController;

    public TTSRecord findAudio(Long recordId) {
        return genericDao.find(TTSRecord.class, recordId);
    }

    public TTSGenerationState generateTTS(Practice practice) {
        List<Question> questions = practice.getQuestions();
        if (questions == null || questions.isEmpty()) {
            return TTSGenerationState.DONE;
        }

        Set<String> texts = new HashSet<>();
        for (Question question : questions) {
            String questionStr = question.getQuestionStr();
            texts.add(questionStr);
        }

        // query all the question without language and only in the memory I will check if the question exist according to text and lang
        TypedQuery<TTSRecord> query = getEm().createQuery("from TTSRecord where text in (:texts)", TTSRecord.class);
        query.setParameter("texts", texts);
        List<TTSRecord> resultList = query.getResultList();
        Map<TTSRecordKey, TTSRecord> textToRecordMap = new HashMap<>(resultList.size());
        for (TTSRecord ttsRecord : resultList) {
            textToRecordMap.put(new TTSRecordKey(ttsRecord), ttsRecord);
        }

        List<String> unSupportedTextList = new LinkedList<>();
        TTSGenerationState ttsGenerationState = TTSGenerationState.DONE;
        for (Question question : questions) {
            String questionStr = question.getQuestionStr();
            Language detectedLang = Language.detect(questionStr);
            if (!detectedLang.isSupported()) {
                Language ttsLang = practice.getTtsLang();
                if (ttsLang == null || !ttsLang.isSupported()) {
                    ttsLang = Language.EN;  // put a default if we could not detect the lang
                }
                if (detectedLang == Language.UNIVERSAL && ttsLang.isSupported()) {
                    detectedLang = ttsLang;
                } else {
                    question.setTtsGenerationState(TTSGenerationState.N_A);
                    unSupportedTextList.add(questionStr);
                    continue;
                }
            }
            TTSRecord ttsRecord = textToRecordMap.get(new TTSRecordKey(questionStr, detectedLang));
            if (ttsRecord == null) {
                logger.debug("could not find TTS for '" + questionStr + "', create new TTSRecordAsyncCreator");
                question.setTtsGenerationState(TTSGenerationState.IN_PROGRESS);
                AbsTTSEngine ttsEngine = ttsEnginesMapping.get(detectedLang);
                TTSRecordAsyncCreator ttsRecordAsyncCreator = new TTSRecordAsyncCreator(question, ttsEngine, this, questionRepository, practicerScheduler, emailController);
                ttsRecordAsyncCreator.start();
                ttsGenerationState = TTSGenerationState.IN_PROGRESS;
            } else {
                question.setTtsUrl("/tts/" + ttsRecord.getId());
            }
        }

        if (!unSupportedTextList.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder("Could not generate TTS for the words:<br>");
            for (String text : unSupportedTextList) {
                stringBuilder.append(text).append("<br>");
            }
            emailController.sendDebugMail(stringBuilder.toString());
        }
        return ttsGenerationState;
    }

    public void saveTTSRecord(TTSRecord ttsRecord) {
        getEm().persist(ttsRecord);
    }
}
