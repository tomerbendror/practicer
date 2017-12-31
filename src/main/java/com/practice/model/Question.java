package com.practice.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.practice.type.TTSGenerationState;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

import static com.practice.util.PracticerUtils.stripHebrewVowels;

/**
 * Created by Tomer
 */
@Entity
@Table(name="question")
public class Question extends BaseEntity {

    @NotNull
    @Column(name = "QUESTION_STR")
    private String questionStr;

    @Column(name = "HINT")
    private String hint;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "QUESTION_TYPE")
    private QuestionType questionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "TTS_GENERATION_STATE")
    private TTSGenerationState ttsGenerationState = TTSGenerationState.N_A;

    @Column(name = "ORDER_INDEX")
    private int orderIndex;

    @Column(name = "TTS_URL")
    private String ttsUrl;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>(0);

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRACTICE_ID")
    @JsonIdentityReference(alwaysAsId=true)
    private Practice practice;

    public Question() {
    }

    public static Question newQuestion(Question updateQuestion) {
        Question newQuestion = new Question();
        newQuestion.updateFrom(updateQuestion);
        newQuestion.setId(null); // clean the client tmp id
        return newQuestion;
    }

    public void updateFrom(Question updateQuestion) {
        if (!StringUtils.equals(getQuestionStr(), updateQuestion.getQuestionStr())) {
            ttsUrl = null;  // indicating we need to re-validate the TTS \
        }
        questionStr = updateQuestion.questionStr;
        hint = updateQuestion.hint;
        questionType = updateQuestion.questionType; // for now this option is not allow by the client

        // create or update the answers
        Set<Answer> existAnswerSet = new HashSet<>(getAnswers());    // help to calc the deleted answers
        for (Answer updateAnswer : updateQuestion.getAnswers()) {
            if (updateAnswer.getId() == null || updateAnswer.getId() < 0) {   // new question
                Answer newAnswer = Answer.newAnswer(updateAnswer);
                newAnswer.setQuestion(this);
                getAnswers().add(newAnswer);
            } else {    // update or delete answer
                for (Answer existAnswer : getAnswers()) {
                    if (existAnswer.getId().equals(updateAnswer.getId())) {
                        existAnswer.updateFrom(updateAnswer);
                        existAnswerSet.remove(existAnswer);
                    }
                }
            }
        }

        getAnswers().removeAll(existAnswerSet);
    }

    public Question duplicate() {
        Question duplicate = new Question();
        duplicate.questionStr = questionStr;
        duplicate.hint = hint;
        duplicate.questionStr = questionStr;
        duplicate.questionType = questionType;
        duplicate.ttsGenerationState = ttsGenerationState;
        duplicate.ttsUrl = ttsUrl;
        // orderIndex will be assign from the Practice context after we add all questions

        for (Answer answer : answers) {
            Answer duplicateAnswer = Answer.newAnswer(answer);
            duplicate.answers.add(duplicateAnswer);
            duplicateAnswer.setQuestion(duplicate);
        }
        return duplicate;
    }

    public void mergeWith(Question newQuestion) {
        if (!stripHebrewVowels(questionStr.toLowerCase().trim()).equals(stripHebrewVowels(newQuestion.questionStr).toLowerCase().trim())) {
            throw new RuntimeException("question mergeWith() must call with two identical questionStr, first: " + questionStr + ", second: " + newQuestion.questionStr);
        }

        if (questionType != newQuestion.questionType) {
            throw new RuntimeException("question mergeWith() must call with two identical questionTypes, firstId: " + getId() + ", secondId: " + newQuestion.getId());
        }

        // in case of Hebrew prefer the one with NIKKUD
        questionStr = questionStr.length() > newQuestion.questionStr.length() ? questionStr : newQuestion.questionStr;

        Map<String, Answer> answerTextToAnswerMap = new HashMap<>(getAnswers().size());
        for (Answer answer : getAnswers()) {
            answerTextToAnswerMap.put(answer.getAnswerStr(), answer);
        }

        for (Answer newAnswer : newQuestion.getAnswers()) {
            Answer existAnswer = answerTextToAnswerMap.get(newAnswer.getAnswerStr());
            if (existAnswer == null) {  // we could not find this answer, so add it also to possible answers
                Answer answer = Answer.newAnswer(newAnswer);
                answers.add(answer);
                answer.setQuestion(this);
            }
        }
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public TTSGenerationState getTtsGenerationState() {
        return ttsGenerationState;
    }

    public void setTtsGenerationState(TTSGenerationState ttsGenerationState) {
        this.ttsGenerationState = ttsGenerationState;
    }

    @JsonIgnore
    public Practice getPractice() {
        return practice;
    }

    public void setPractice(Practice practice) {
        this.practice = practice;
    }

    public String getQuestionStr() {
        return questionStr;
    }

    public void setQuestionStr(String questionStr) {
        this.questionStr = questionStr;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getTtsUrl() {
        return ttsUrl;
    }

    public void setTtsUrl(String ttsUrl) {
        this.ttsUrl = ttsUrl;
        if (StringUtils.isNoneBlank(ttsUrl)) {
            setTtsGenerationState(TTSGenerationState.DONE);
        } else {
            setTtsGenerationState(TTSGenerationState.FAIL);
        }
    }
}
