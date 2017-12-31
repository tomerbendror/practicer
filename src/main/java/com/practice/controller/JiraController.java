package com.practice.controller;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import com.practice.jira.JiraIssue;
import com.practice.jira.JiraJsonObj;
import com.practice.jira.JiraJsonWrapper;
import com.practice.jira.JiraSprint;
import com.practice.jira.enums.IssueTypeName;
import com.practice.jira.enums.SprintState;
import com.practice.jira.enums.StatusName;
import com.practice.jira.freemarker.FreeMarkerConfig;
import com.practice.jira.full.AllDataJson;
import com.practice.jira.report.PersonTasksSummary;
import com.practice.jira.report.StoriesForUserSummary;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by Tomer
 */
@Controller
@RequestMapping("/jira")
public class JiraController extends BaseController {
    private static final Logger logger = Logger.getLogger(JiraController.class);
    private static final String sprintNamePrefix = "CSSC Sprint ";

    public static final String BROWSER_ISSUE_PATH = "https://jira.clearforest.com/browse/";
    public static final String BROWSE_SINGLE_SPRINT_PATH = "https://jira.clearforest.com/secure/RapidBoard.jspa?rapidView=161&view=planning.nodetail&selectedIssue=%s&epics=hidden";

    private static Gson gson;

    private ConcurrentHashMap<String, String> reportsMap = new ConcurrentHashMap<>(10);

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(IssueTypeName.class, new IssueTypeDeserializer());
        gsonBuilder.registerTypeAdapter(StatusName.class, new StatusNameDeserializer());
        gson = gsonBuilder.create();
    }

    @RequestMapping(value = "/sprintReport", produces = MediaType.TEXT_HTML_VALUE, method= RequestMethod.GET)
    @ResponseBody
    public String getReport(HttpServletResponse response) {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String reportHtml = reportsMap.get("key");
        if (reportHtml == null) {
            return "<h1>No report found, please use the Jira plugin to generate the report</h1>";
        }
        return reportHtml;
    }


    @RequestMapping(value = "/submitSprintReportData", method = RequestMethod.POST)
    public void downloadReport(@RequestParam(value="jsonContent", required=true) String jsonContent, HttpServletResponse response) {

        try {
            while(jsonContent.contains("\"avatarUrl\":")) {
                int begin = jsonContent.indexOf("\"avatarUrl\":");
                int end = jsonContent.indexOf("\"", begin + 20);
                String toReplace = jsonContent.substring(begin, end);
                jsonContent = jsonContent.replace(toReplace, "");
            }
            JiraJsonWrapper jiraJsonWrapper = gson.fromJson(jsonContent, JiraJsonWrapper.class);
            logger.info(jiraJsonWrapper);

            String report = getReport(jiraJsonWrapper);
            reportsMap.put("key", report);
            // get your file as InputStream
            InputStream is = new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8)); ;
            // copy it to response's OutputStream
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            response.setContentType("text/html");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + "tomer" + ".html\"");
            response.flushBuffer();
        } catch (IOException ex) {
            logger.info("Error writing file to output stream. Filename was '{}'", ex);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

    private String getReport(JiraJsonWrapper jiraJsonWrapper) {
        try {
            Multimap<String, JiraIssue> assigneeToIssuesMap = LinkedListMultimap.create();
            Map<String, StoriesForUserSummary> assigneeToStoriesSummary = new HashMap<>();

            JiraJsonObj jiraJsonObj = jiraJsonWrapper.getJiraJsonObj();

            Map<Integer, JiraSprint> sprintsMap = new HashMap<>(jiraJsonObj.getSprints().size());
            for (JiraSprint sprint : jiraJsonObj.getSprints()) {
                sprintsMap.put(sprint.getId(), sprint);
            }

            JiraSprint sprint = null;
            int sprintNum = Integer.valueOf("142");//todo
            for (Map.Entry<Integer, JiraSprint> entry : sprintsMap.entrySet()) {
                if (entry.getValue().getName().startsWith(sprintNamePrefix + sprintNum)) {
                    sprint = entry.getValue();
                    break;
                }
            }

            Map<Integer, JiraIssue> sprintIssuesMap = new HashMap<>(jiraJsonObj.getIssues().size());
            for (JiraIssue issue : jiraJsonObj.getIssues()) {
                sprintIssuesMap.put(issue.getId(), issue);
            }

            List<Integer> issuesIds = sprint.getIssuesIds();
            for (Integer issueId : issuesIds) {
                JiraIssue jiraIssue = sprintIssuesMap.get(issueId);
                assigneeToIssuesMap.put(jiraIssue.getAssigneeName(), jiraIssue);
            }

            Integer focusIssueForSprint = issuesIds.get(min(max(issuesIds.size() - 1, 0), 12));
            JiraIssue focusIssue = sprintIssuesMap.get(focusIssueForSprint);


            // now get the full data in order to get the subtasks, qa tasks, and devOps tasks
            AllDataJson allJsonObj = jiraJsonWrapper.getAllDataJson();
            Multimap<String, JiraIssue> parentStoryToSubtask = LinkedListMultimap.create();

            for (JiraIssue jiraIssue : allJsonObj.getIssuesData().getIssues()) {
                if (jiraIssue.getParentKey() != null) {
                    parentStoryToSubtask.put(jiraIssue.getParentKey(), jiraIssue);
                }
            }

            // aggregate stories time estimation by person
            for (JiraIssue story : assigneeToIssuesMap.values()) {
                StoriesForUserSummary storiesForUserSummary = assigneeToStoriesSummary.get(story.getAssigneeName());
                if (storiesForUserSummary == null) {
                    storiesForUserSummary = new StoriesForUserSummary(story.getAssigneeName());
                    assigneeToStoriesSummary.put(story.getAssigneeName(), storiesForUserSummary);
                }
                storiesForUserSummary.addStory(story);

                // add the subtasks
                Collection<JiraIssue> subtasks = parentStoryToSubtask.get(story.getKey());
                LinkedList<JiraIssue> subtasksList = new LinkedList<>(subtasks);
                story.setSubtasks(subtasksList);

                for (JiraIssue subtask : subtasksList) {
                    subtask.setStory(story);
                }
            }

            List<JiraIssue> storiesList = new LinkedList<>();
            for (Integer issuesId : issuesIds) {
                storiesList.add(sprintIssuesMap.get(issuesId));
            }

            // aggregate sub-task info by user
            Multimap<String, JiraIssue> userToStoriesAndSubtasks = LinkedListMultimap.create();
            for (StoriesForUserSummary storySummary : assigneeToStoriesSummary.values()) {
                for (JiraIssue jiraIssue : storySummary.getStories()) {
                    // add the story
                    userToStoriesAndSubtasks.put(jiraIssue.getAssigneeName(), jiraIssue);

                    // add the subtask
                    for (JiraIssue subtask : jiraIssue.getSubtasks()) {
                        userToStoriesAndSubtasks.put(subtask.getAssigneeName(), subtask);
                    }
                }
            }

            List<PersonTasksSummary> personTasksSummaries = new LinkedList<>();
            for (Map.Entry<String, Collection<JiraIssue>> entry : userToStoriesAndSubtasks.asMap().entrySet()) {
                personTasksSummaries.add(new PersonTasksSummary(entry.getKey(), entry.getValue(), issuesIds));
            }

            Map<String, Object> input = new HashMap<>();
            input.put("sprintNum", sprintNum);
            input.put("storySummaries", assigneeToStoriesSummary.values());
            input.put("storiesList", storiesList);
            input.put("personTasksSummaries", personTasksSummaries);
            input.put("sprintUrl", String.format(BROWSE_SINGLE_SPRINT_PATH, focusIssue.getKey()));
            input.put("isFutureSprint", sprint.getState() == SprintState.FUTURE);
            input.put("timeRemaining", sprint.getTimeRemaining() != null ? sprint.getWorkingDaysRemaining() : "");
            input.put("totalWorkInSeconds", sprint.getTimeRemaining() != null ? sprint.getDaysRemainingSeconds() : 0);
            String reportHtml = FreeMarkerConfig.processTemplate("sprint-report.ftl", input);
            return reportHtml;
//                String filePath = "reports/sprint-" + sprintNum + "-report.html";
//                File file = new File(filePath);
//                file.getParentFile().mkdirs(); // Will create parent directories if not exists
//                file.createNewFile();
//                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
//                    writer.write(reportHtml);
//                }
//                logger.info("Report file was saved at: " + file.getAbsolutePath());
        } catch (Exception e) {
            return "fail to process";
        }
    }

    private static class IssueTypeDeserializer implements JsonDeserializer<IssueTypeName> {
        @Override
        public IssueTypeName deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException
        {
            IssueTypeName[] scopes = IssueTypeName.values();
            for (IssueTypeName issueType : scopes){
                if (issueType.getStrValue().equals(json.getAsString()))
                    return issueType;
            }
            return null;
        }
    }

    private static class StatusNameDeserializer implements JsonDeserializer<StatusName> {
        @Override
        public StatusName deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException
        {
            StatusName[] scopes = StatusName.values();
            for (StatusName statusName : scopes){
                if (statusName.getStrValue().equals(json.getAsString()))
                    return statusName;
            }
            return null;
        }
    }
}
