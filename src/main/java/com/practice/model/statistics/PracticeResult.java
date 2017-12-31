package com.practice.model.statistics;

import com.practice.model.BaseEntity;
import com.practice.model.ChildUser;
import com.practice.model.Practice;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomer
 */
@Entity
@Table(name="practice_result")
public class PracticeResult extends BaseEntity {

    @NotNull
    @Column(name = "SCORE")
    private Integer score;

    @Column(name = "TIME_SECOND")
    private Integer timeSecond;

    @Column(name = "CREATE_TIME", insertable = false, updatable = false)
    private Timestamp createdTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PRACTICE_ID")
    private Practice practice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CHILD_ID")
    private ChildUser childUser;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "practiceResult", cascade = CascadeType.ALL)
    private List<QuestionResult> questionResults = new ArrayList<>(0);

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public Practice getPractice() {
        return practice;
    }

    public void setPractice(Practice practice) {
        this.practice = practice;
    }

    public ChildUser getChildUser() {
        return childUser;
    }

    public void setChildUser(ChildUser childUser) {
        this.childUser = childUser;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTimeSecond() {
        return timeSecond;
    }

    public void setTimeSecond(Integer timeSecond) {
        this.timeSecond = timeSecond;
    }

    public List<QuestionResult> getQuestionResults() {
        return questionResults;
    }

    public void setQuestionResults(List<QuestionResult> questionResults) {
        this.questionResults = questionResults;
    }
}
