package com.practice.dto;

import java.util.List;

/**
 * Created by Tomer
 */
public class PracticeResultDto {
    private Long practiceId;
    private Integer score;
    private Boolean useTranslationAsDictation;
    private List<QuestionResultDto> questionResults;

    public PracticeResultDto() {
    }

    public Long getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(Long practiceId) {
        this.practiceId = practiceId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getUseTranslationAsDictation() {
        return useTranslationAsDictation;
    }

    public void setUseTranslationAsDictation(Boolean useTranslationAsDictation) {
        this.useTranslationAsDictation = useTranslationAsDictation;
    }

    public List<QuestionResultDto> getQuestionResults() {
        return questionResults;
    }

    public void setQuestionResults(List<QuestionResultDto> questionResults) {
        this.questionResults = questionResults;
    }
}
