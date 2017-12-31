package com.practice.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Tomer
 */
@Entity
@Table(name="answer")
public class Answer extends BaseEntity {

    @NotNull
    @Column(name = "ANSWER_STR")
    private String answerStr;

    @NotNull
    @Column(name = "IS_CORRECT")
    private Boolean isCorrect;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QUESTION_ID")
    @JsonIdentityReference(alwaysAsId=true)
    private Question question;

    public Answer() {
    }

    public static Answer newAnswer(Answer updateAnswer) {
        Answer newAnswer = new Answer();
        newAnswer.updateFrom(updateAnswer);
        return newAnswer;
    }

    public void updateFrom(Answer updateAnswer) {
        answerStr = updateAnswer.answerStr;
        isCorrect = updateAnswer.isCorrect == null ? true : updateAnswer.isCorrect;
    }

    public String getAnswerStr() {
        return answerStr;
    }

    public void setAnswerStr(String answerStr) {
        this.answerStr = answerStr;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
