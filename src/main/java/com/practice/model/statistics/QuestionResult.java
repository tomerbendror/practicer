package com.practice.model.statistics;

import com.practice.dto.QuestionResultDto;
import com.practice.model.BaseEntity;
import com.practice.model.statistics.PracticeResult;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Tomer
 */
@Entity
@Table(name="question_result")
public class QuestionResult extends BaseEntity {

    @NotNull
    @Column(name = "QUESTION_STR")
    private String questionStr;

    @Column(name = "SUCCESS")
    private boolean success;

    @Column(name = "WRONG_ANSWERS_COUNT")
    private Integer wrongAnswersCount;

    @Column(name = "QUESTION_SKIPPED")
    private boolean questionSkipped;

    @Column(name = "QUESTION_TIME_SECOND")
    private Float questionTimeSecond;

    @NotNull
    @Column(name = "WRONG_ANSWERS_STR")
    private String wrongAnswersStr;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "PRACTICE_RESULT_ID")
    private PracticeResult practiceResult;

    public QuestionResult(QuestionResultDto questionResultDto) {
        questionSkipped = Boolean.TRUE.equals(questionResultDto.getQuestionSkipped());
        questionTimeSecond =questionResultDto.getQuestionTimeSecond();
        wrongAnswersStr = StringUtils.left(questionResultDto.getWrongAnswersStr(), 255);
        questionStr = questionResultDto.getQuestionStr();
        this.success = questionResultDto.getSuccess();

        if (StringUtils.isNotBlank(wrongAnswersStr)) {
            String[] split = wrongAnswersStr.split(";");
            setWrongAnswersCount(split.length);
        } else {
            setWrongAnswersCount(0);
        }
    }

    public QuestionResult() {
    }

    public String getQuestionStr() {
        return questionStr;
    }

    public void setQuestionStr(String questionStr) {
        this.questionStr = questionStr;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isQuestionSkipped() {
        return questionSkipped;
    }

    public void setQuestionSkipped(boolean questionSkipped) {
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

    public PracticeResult getPracticeResult() {
        return practiceResult;
    }

    public void setPracticeResult(PracticeResult practiceResult) {
        this.practiceResult = practiceResult;
    }

    public Integer getWrongAnswersCount() {
        return wrongAnswersCount;
    }

    public void setWrongAnswersCount(Integer wrongAnswersCount) {
        this.wrongAnswersCount = wrongAnswersCount;
    }
}
