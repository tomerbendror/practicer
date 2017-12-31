package com.practice.jira.report;

import com.practice.jira.JiraIssue;
import com.practice.jira.JiraUtils;
import com.practice.jira.enums.StatusName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Tomer.Ben-Dror on 2/14/2017
 */
public class PersonTasksSummary {

    private final static Logger logger = LoggerFactory.getLogger(PersonTasksSummary.class);

    private String assigneeName;
    private int totalWorkInSeconds;
    private Set<String> countedIssues = new HashSet<>();   // keep the stories we have already count their work
    private List<PersonIssue> personRelevantStories = new ArrayList<>();
    private Set<String> addedStories = new HashSet<>();

    public PersonTasksSummary(String assigneeName, Collection<JiraIssue> issues, final List<Integer> allStoriesIdsByPriority) {
        this.assigneeName = assigneeName;

        // sort subtask before stories
        List<JiraIssue> issuesList = new ArrayList<>(issues.size());
        Collections.sort(issuesList, new Comparator<JiraIssue>() {
            @Override
            public int compare(JiraIssue o1, JiraIssue o2) {
                return (o1.getStory() == null) ? 1 : -1;
            }
        });
        for (JiraIssue jiraIssue : issuesList) {
            addIssue(jiraIssue);
        }
//        issues.stream().sorted((o1, o2) -> (o1.getStory() == null) ? 1 : -1).forEach(this::addIssue);

        // go over all stories
//        issues.stream().forEach(issue -> addStory(issue.getStory() == null ? issue : issue.getStory()));
        for (JiraIssue issue : issuesList) {
            addStory(issue.getStory() == null ? issue : issue.getStory());
        }

        Collections.sort(personRelevantStories, new Comparator<PersonIssue>() {
            @Override
            public int compare(PersonIssue o1, PersonIssue o2) {
                return allStoriesIdsByPriority.indexOf(o1.issue.getId()) - allStoriesIdsByPriority.indexOf(o2.issue.getId());
            }
        });
//        personRelevantStories.sort((o1, o2) -> allStoriesIdsByPriority.indexOf(o1.issue.getId()) - allStoriesIdsByPriority.indexOf(o2.issue.getId()));
    }

    private void addStory(JiraIssue story) {
        if (!addedStories.contains(story.getKey())) {
            addedStories.add(story.getKey());
            boolean wasCountToWork = countedIssues.contains(story.getKey());
            PersonIssue personStory = new PersonIssue(story, wasCountToWork);
            personRelevantStories.add(personStory);
//            story.getSubtasks().stream().filter(subtask -> subtask.getAssigneeName().equals(assigneeName)).forEach(issueIn -> personStory.addSubtask(issueIn, countedIssues.contains(issueIn.getKey())));

            for (JiraIssue subtask : story.getSubtasks()) {
                if (subtask.getAssigneeName().equals(assigneeName)) {
                    personStory.addSubtask(subtask, countedIssues.contains(subtask.getKey()));
                }
            }
        }
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public List<PersonIssue> getPersonRelevantStories() {
        return personRelevantStories;
    }

    public void addIssue(JiraIssue issue) {
        JiraIssue parentStory = issue.getStory();
        if (parentStory == null) { // it's a story
            if (!hasSubtaskFor(assigneeName, issue.getSubtasks())) {    // if user own the story but doesn't have a subtask
                countWorkIfNeeded(issue);
            }
        } else {    // sub-task
            countWorkIfNeeded(issue);
        }
    }

    private boolean hasSubtaskFor(String assigneeName, Collection<JiraIssue> issues) {
        for (JiraIssue subtask : issues) {
            if (subtask.getAssigneeName().equals(assigneeName)) {
                return true;
            }
        }
        return false;
    }

    private void countWorkIfNeeded(JiraIssue issue) {
        if (issue.getStatusName() != StatusName.CLOSED && issue.getStatusName() != StatusName.RESOLVED && !countedIssues.contains(issue.getKey())) {
            totalWorkInSeconds += issue.getEstimateStatistic().getStatFieldValue().getValue();
            countedIssues.add(issue.getKey());
            logger.info(issue.getAssigneeName() + ", issue:" + issue.getKey() + ", " + JiraUtils.getTotalTimeStr(issue.getEstimateStatistic().getStatFieldValue().getValue()));
        }
    }

    public String getTotalTimeStr() {
        return JiraUtils.getTotalTimeStr(totalWorkInSeconds);
    }

    public int getTotalWorkInSeconds() {
        return totalWorkInSeconds;
    }

    public static class PersonIssue {
        private JiraIssue issue;
        private boolean countToWork;
        private boolean noEstimation;
        private List<PersonIssue> personSubtasks = new ArrayList<>();

        public PersonIssue(JiraIssue issue, boolean countToWork) {
            this.issue = issue;
            this.countToWork = countToWork;
            noEstimation = issue.getStatusName() != StatusName.RESOLVED && issue.getStatusName() != StatusName.CLOSED && issue.getTotalTimeStr().equals("");
        }

        public JiraIssue getIssue() {
            return issue;
        }

        public void setIssue(JiraIssue issue) {
            this.issue = issue;
        }

        public boolean isCountToWork() {
            return countToWork;
        }

        public void setCountToWork(boolean countToWork) {
            this.countToWork = countToWork;
        }

        public void addSubtask(JiraIssue subtask, boolean countToWork) {
            personSubtasks.add(new PersonIssue(subtask, countToWork));
        }

        public List<PersonIssue> getPersonSubtasks() {
            return personSubtasks;
        }

        public void setPersonSubtasks(List<PersonIssue> personSubtasks) {
            this.personSubtasks = personSubtasks;
        }

        public boolean isNoEstimation() {
            return noEstimation;
        }
    }
}
