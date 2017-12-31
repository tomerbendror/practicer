package com.practice.model.statistics;

import com.practice.model.BaseEntity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Tomer
 */
@Entity
@Table(name="practice_statistics_for_group")
public class PracticeStatisticsForGroup extends BaseEntity {

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

    @Column(name = "GROUP_ID")
    private Long groupId;

    public PracticeStatisticsForGroup(TimePeriod timePeriod, Integer avgScore, Integer avgTimeSecond, long practiceId, long groupId) {
        this.timePeriod = timePeriod;
        this.avgScore = avgScore;
        this.avgTimeSecond = avgTimeSecond;
        this.practiceId = practiceId;
        this.groupId = groupId;
    }

    public PracticeStatisticsForGroup() {
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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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
