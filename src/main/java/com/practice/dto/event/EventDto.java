package com.practice.dto.event;

import java.util.Date;

/**
 * Created by liorm on 18/10/2015.
 */
public class EventDto {

       private String type;
       private long childId;
       private long parentId;
       private long groupId;
       private long practiceId;
       private long questionId;
       private Date eventTime;
       private String value;

    public EventDto(String type, long childId, long parentId, long groupId, long practiceId, long questionId, Date eventTime, String value) {
        this.type = type;
        this.childId = childId;
        this.parentId = parentId;
        this.groupId = groupId;
        this.practiceId = practiceId;
        this.questionId = questionId;
        this.eventTime = eventTime;
        this.value = value;
    }

    public EventDto() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
