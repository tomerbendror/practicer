package com.practice.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by liorm on 18/10/2015.
 */
@Entity
@Table(name="event")
public class Event extends BaseEntity{

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 100)
    private EventType type;

    @Column(name = "child_id")
    private long childId;

    @Column(name = "parent_id")
    private long parentId;

    @Column(name = "group_id")
    private long groupId;

    @Column(name = "practice_id")
    private long practiceId;

    @Column(name = "question_id")
    private long questionId;

    @Column(name = "event_time")
    private Date eventTime;

    @Column(name = "value",  length = 500)
    private String value;

    public Event() {
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public long getChildId() {
        return childId;
    }

    public void setChildId(long childId) {
        this.childId = childId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(long practiceId) {
        this.practiceId = practiceId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
