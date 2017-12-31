package com.practice.dto;

/**
 * Created by Tomer
 */
public class QuestionResultDto {
    private String questionStr;
    private Boolean questionSkipped;
    private Float questionTimeSecond;
    private String wrongAnswersStr;
    private Boolean success;

    public QuestionResultDto() {
    }

    public String getQuestionStr() {
        return questionStr;
    }

    public void setQuestionStr(String questionStr) {
        this.questionStr = questionStr;
    }

    public Boolean getQuestionSkipped() {
        return questionSkipped;
    }

    public void setQuestionSkipped(Boolean questionSkipped) {
        this.questionSkipped = questionSkipped;
    }

    public Float getQuestionTimeSecond() {
        return questionTimeSecond;
    }

    public void setQuestionTimeSecond(Float questionTimeSecond) {
        this.questionTimeSecond = questionTimeSecond;
    }

    public String getWrongAnswersStr() {
        return wrongAnswersStr;
    }

    public void setWrongAnswersStr(String wrongAnswersStr) {
        this.wrongAnswersStr = wrongAnswersStr;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
