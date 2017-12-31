package com.practice.jira;

import com.practice.jira.enums.IssueTypeName;
import com.practice.jira.enums.StatusName;

import java.util.Collections;
import java.util.List;

/**
 * Created by Tomer.Ben-Dror on 2/12/2017
 */
public class JiraIssue {
    private int id;
    private String key;
    private String parentKey;
    private Integer parentId;
    private IssueTypeName issueTypeName;
    private IssueTypeName typeName;
    private String summary;
    private boolean done;
    private String assigneeName;
    private StatusName statusName;
    private TimeEstimation estimateStatistic;
    private List<JiraIssue> subtasks = Collections.emptyList();
    private JiraIssue story;

    public JiraIssue() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public IssueTypeName getIssueTypeName() {
        return issueTypeName;
    }

    public void setIssueTypeName(IssueTypeName issueTypeName) {
        this.issueTypeName = issueTypeName;
    }

    public IssueTypeName getTypeName() {
        return typeName;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public void setTypeName(IssueTypeName typeName) {
        this.typeName = typeName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public StatusName getStatusName() {
        return statusName;
    }

    public void setStatusName(StatusName statusName) {
        this.statusName = statusName;
    }

    public TimeEstimation getEstimateStatistic() {
        return estimateStatistic;
    }

    public void setEstimateStatistic(TimeEstimation estimateStatistic) {
        this.estimateStatistic = estimateStatistic;
    }

    public String getTotalTimeStr() {
        return estimateStatistic.getStatFieldValue().getText();
    }

    public String browseUrl() {
        return /*JiraBrowser.BROWSER_ISSUE_PATH +*/ key;
    }

    public List<JiraIssue> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<JiraIssue> subtasks) {
        this.subtasks = subtasks;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public void setStory(JiraIssue story) {
        this.story = story;
    }

    public JiraIssue getStory() {
        return story;
    }

    public IssueTypeName getIssueType() {
        return issueTypeName != null ? issueTypeName : typeName;
    }
}
