package com.practice.model.statistics;

import com.practice.model.BaseEntity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Tomer
 */
@Entity
@Table(name="practice_statistics_for_child")
public class PracticeStatisticsForChild extends BaseEntity {

    @Column(name = "TIME_PERIOD")
    @Enumerated(value = EnumType.STRING)
    private TimePeriod timePeriod;

    @Column(name = "AVG_SCORE")
    private Integer avgScore;

    @Column(name = "AVG_TIME_SECOND")
    private Integer avgTimeSecond;

    @Column(name = "CREATED_DATE", insertable = false, updatable = false)
    private Timestamp createDate;

    @Column(name = "PRACTICE_ID")
    private Long practiceId;

    @Column(name = "CHILD_ID")
    private Long childId;

    public PracticeStatisticsForChild(TimePeriod timePeriod, Integer avgScore, Integer avgTimeSecond, long practiceId, long childId) {
        this.timePeriod = timePeriod;
        this.avgScore = avgScore;
        this.avgTimeSecond = avgTimeSecond;
        this.practiceId = practiceId;
        this.childId = childId;
    }

    public PracticeStatisticsForChild() {
    }

    public TimePeriod getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
    }

    public Integer getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(Integer avgScore) {
        this.avgScore = avgScore;
    }

    public Long getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(Long practiceId) {
        this.practiceId = practiceId;
    }

    public Long getChildId() {
        return childId;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public Integer getAvgTimeSecond() {
        return avgTimeSecond;
    }

    public void setAvgTimeSecond(Integer avgTimeSecond) {
        this.avgTimeSecond = avgTimeSecond;
    }
}
