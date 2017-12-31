package com.practice.jira.report;

import com.practice.jira.JiraIssue;
import com.practice.jira.JiraUtils;
import com.practice.jira.enums.StatusName;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tomer.Ben-Dror on 2/13/2017
 */
public class StoriesForUserSummary {
    public static final int HOUR_IN_SECONDS = 60*60;
    public static final int DAY_CAPACITY_IN_HOURS = 8;

    private String user;
    private int totalTimeSecond;
    private List<JiraIssue> stories = new LinkedList<>();

    public StoriesForUserSummary(String user) {
        this.user = user;
    }

    public void addStory(JiraIssue jiraIssue) {
        if (jiraIssue.getStatusName() == StatusName.OPEN || jiraIssue.getStatusName() == StatusName.IN_PROGRESS) {
            totalTimeSecond += jiraIssue.getEstimateStatistic().getStatFieldValue().getValue();
        }
        stories.add(jiraIssue);
    }

    public String getUser() {
        return user;
    }

    public int getTotalTimeSecond() {
        return totalTimeSecond;
    }

    public List<JiraIssue> getStories() {
        return stories;
    }

    public String getTotalTimeStr() {
        return JiraUtils.getTotalTimeStr(totalTimeSecond);
    }
}
