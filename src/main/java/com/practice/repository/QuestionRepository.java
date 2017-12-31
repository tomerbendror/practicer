package com.practice.repository;

import com.practice.model.Question;
import com.practice.model.TTSRecord;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Tomer
 */
@Repository("questionRepository")
@Transactional
public class QuestionRepository extends BaseRepository {

    public void updateTTSGenerationFinished(long questionId, TTSRecord ttsRecord) {
        Question question = getEm().find(Question.class, questionId);
        question.setTtsUrl("/tts/" + ttsRecord.getId());
    }

    public void updateTTSGenerationFail(long questionId) {
        Question question = getEm().find(Question.class, questionId);
        question.setTtsUrl(null);
    }
}
