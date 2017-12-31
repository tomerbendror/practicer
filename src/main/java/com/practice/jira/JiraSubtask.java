package com.practice.jira;


import com.practice.jira.enums.IssueTypeName;

/**
 * Created by Tomer.Ben-Dror on 2/13/2017
 */
public class JiraSubtask {
    private String key;
    private String summary;
    private IssueTypeName typeName;
    private TimeEstimation estimateStatistic;

    public JiraSubtask() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public IssueTypeName getTypeName() {
        return typeName;
    }

    public void setTypeName(IssueTypeName typeName) {
        this.typeName = typeName;
    }

    public TimeEstimation getEstimateStatistic() {
        return estimateStatistic;
    }

    public void setEstimateStatistic(TimeEstimation estimateStatistic) {
        this.estimateStatistic = estimateStatistic;
    }
}
