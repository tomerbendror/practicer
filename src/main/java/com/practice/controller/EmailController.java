package com.practice.controller;

import com.practice.job.StatisticsJob.ChildPractices;
import com.practice.job.StatisticsJob.ChildWeeklyReportData;
import com.practice.job.StatisticsJob.PracticeInGroupStatistics;
import com.practice.mail.EmailAttributes;
import com.practice.mail.EmailSender;
import com.practice.model.*;
import com.practice.type.Language;
import com.practice.util.PracticerEnvConfig;
import com.practice.util.PracticerUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: tomer
 */
@Controller
public class EmailController extends BaseController {

    private static final Logger logger = Logger.getLogger(EmailController.class);

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private PracticerEnvConfig practicerEnvConfig;

    public void sendGroupInviteEmail(ParentUser inviter, Collection<GroupInvitation> needToInvite, ParentsGroup group) {
        PracticerModel model = new PracticerModel();
        model.put("group", group);
        model.put("userName", inviter.getDisplayName());

        for (GroupInvitation invitation : needToInvite) {
            model.put("email", invitation.getEmail());
            model.put("key", invitation.getInvitationKey());
            String messageText = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "groupInvitation.vm", "UTF-8", model);
            EmailAttributes emailAttributes = new EmailAttributes("קיבלת הזמנה מ-" + inviter.getDisplayName(), messageText, invitation.getEmail(), EmailType.GROUP_INVITATION);
            emailAttributes.setFromName(StringUtils.isNoneBlank(inviter.getFullName()) ? inviter.getFullName() : inviter.getEmail());
            emailSender.sendEmail(emailAttributes);
        }
    }

    public void sendPracticeSharedWithGroupMail(ParentUser parent, ParentsGroup group, Practice practice) {
        if (group.getGroupsToUsers().size() == 1) {
            return; // the single group member is the one that share the practice, no need to send him an email
        }
        PracticerModel model = new PracticerModel();
        model.put("group", group);
        model.put("parent", parent);
        model.put("practiceId", practice.getId());
        model.put("profileImgUrl", practicerEnvConfig.getAbsoluteProfileImageUrlOrDefault(parent));

        String groupName = group.getName();
        String parentDisplayName = parent.getDisplayName();
        String title;
        if (parent.getGender() == Gender.FEMALE) {
            title = "המשתמשת " + parentDisplayName + " שיתפה את התרגיל '" + practice.getName() + "' בקבוצה "  + groupName;
        } else {
            title = "המשתמש " + parentDisplayName + " שיתף את התרגיל '" + practice.getName() + "' בקבוצה " + groupName;
        }
        model.put("title", title);
        String messageText = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "practiceSharedWithGroup.vm", "UTF-8", model);

        Set<String> emails = new HashSet<>(group.getGroupsToUsers().size());
        for (ParentToGroup parentToGroup : group.getGroupsToUsers()) {
            String email = parentToGroup.getParentUser().getEmail();
            if (!email.equalsIgnoreCase(parent.getEmail())) {   // send for all the parent in the group but not to the sharing user
                emails.add(email);
            }
        }

        String subject = parentDisplayName + " shared a new practice in group " + groupName;
        String fromName = groupName + (StringUtils.isNotBlank(group.getDescription()) ? " " + group.getDescription() : "");
        EmailAttributes emailAttributes = new EmailAttributes(subject, messageText, emails, EmailType.PRACTICE_SHARED_WITH_GROUP).setFromName(fromName);
        emailSender.sendEmail(emailAttributes);
    }

    public void sendParentJoinToGroupMail(ParentUser parent, ParentsGroup group) {
        PracticerModel model = new PracticerModel();
        model.put("group", group);
        model.put("parent", parent);
        model.put("profileImgUrl", practicerEnvConfig.getAbsoluteProfileImageUrlOrDefault(parent));
        String parentDisplayName = parent.getDisplayName();
        String title;
        String groupName = group.getName();
        if (parent.getGender() == Gender.FEMALE) {
            title = "המשתמשת <b>" + parentDisplayName + "</b> הצטרפה לקבוצה <b>" + groupName + "</b>";
        } else {
            title = "המשתמש <b>" + parentDisplayName + "</b> הצטרף לקבוצה <b>" + groupName + "</b>";
        }
        model.put("title", title);
        String messageText = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "parentJoinToGroup.vm", "UTF-8", model);

        Set<String> emails = new HashSet<>(group.getGroupsToUsers().size());
        for (ParentToGroup parentToGroup : group.getGroupsToUsers()) {
            String email = parentToGroup.getParentUser().getEmail();
            if (!email.equalsIgnoreCase(parent.getEmail())) {   // send for all the parent in the group but not to the joined user
                emails.add(email);
            }
        }

        String subject = parentDisplayName + " joined the group " + groupName;
        String fromName = groupName + (StringUtils.isNotBlank(group.getDescription()) ? " " + group.getDescription() : "");
        EmailAttributes emailAttributes = new EmailAttributes(subject, messageText, emails, EmailType.PARENT_JOIN_TO_GROUP).setFromName(fromName);
        emailSender.sendEmail(emailAttributes);
    }

    public void sendDebugMail(String message) {
        emailSender.sendEmail(new EmailAttributes("Debug message", message, "tomerbd0910@gmail.com", EmailType.INTERNAL_ERROR));
    }

    public void sendInternalErrorMail(String message, Exception e) {
        try {
            String subject = practicerEnvConfig.getEnvName() + " Error - " + message;

            StringBuilder content = new StringBuilder();
            if (e != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
                try {
                    PrintStream ps = new PrintStream(bos, false, "UTF-8");
                    e.printStackTrace(ps);
                    String stackTrace = bos.toString("UTF-8");
                    content.append(stackTrace.replaceAll("[\n\r]+", "<br>"));
                } catch (UnsupportedEncodingException e1) {
                    logger.error(e1);
                }
            }

            EmailAttributes emailAttributes = new EmailAttributes(subject, content.toString(), "tomerbd0910@gmail.com", EmailType.INTERNAL_ERROR);
            emailSender.sendEmail(emailAttributes);
        } catch (Exception e1) {
            logger.error("fail to send internal error mail", e);
        }
    }

    public void sendParentDailyMail(ParentUser parent, Collection<ChildPractices> childPractices) {
        List<ChildPractices> childPracticesList = new ArrayList<>(childPractices);
        Collections.sort(childPracticesList, new Comparator<ChildPractices>() {
            @Override
            public int compare(ChildPractices o1, ChildPractices o2) {
                return Long.compare(o1.getChildId(), o2.getChildId());
            }
        });

        PracticerModel model = new PracticerModel();
        model.put("title", "דו''ח תרגול יומי");
        model.put("parent", parent);
        model.put("childsPractices", childPracticesList);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
        model.put("timeFormat", dateFormat);
        String messageText = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "parentDailyReport.vm", "UTF-8", model);

        String subject = "דו''ח תרגול יומי";
        String fromName = "Practicer";
        EmailAttributes emailAttributes = new EmailAttributes(subject, messageText, parent.getEmail(), EmailType.PARENT_DAILY_REPORT).setFromName(fromName);
        emailSender.sendEmail(emailAttributes);
    }

    public void sendTeacherDailyMail(ParentsGroup group, List<PracticeInGroupStatistics> practiceInGroupStatList) {
        PracticerModel model = new PracticerModel();
        model.put("title", "דו''ח תרגול יומי לקבוצה");
        model.put("practiceInGroupStatList", practiceInGroupStatList);
        model.put("group", group);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jerusalem"));
        model.put("timeFormat", dateFormat);
        String messageText = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "teacherDailyReport.vm", "UTF-8", model);

        Calendar cal = Calendar.getInstance();
        String subject = "דו''ח תרגול יומי למורה - " + group.getName();
        String fromName = "Practicer";
        List<String> recipients = new ArrayList<>(group.getTeachers().size());
        for (ParentUser parentUser : group.getTeachers()) {
            recipients.add(parentUser.getEmail());
        }
        EmailAttributes emailAttributes = new EmailAttributes(subject, messageText, /*recipients*/"tomerbd0910@gmail.com", EmailType.TEACHER_DAILY_REPORT).setFromName(fromName);
        emailSender.sendEmail(emailAttributes);
    }

    public void setResetPasswordEmail(ParentUser parent, String newPasswordOpen) {
        PracticerModel model = new PracticerModel();
        model.put("userDisplayName", parent.getDisplayName());
        model.put("newPasswordOpen", newPasswordOpen);
        String messageText = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "resetPassword.vm", "UTF-8", model);

        EmailAttributes emailAttributes = new EmailAttributes("Practicer password reset", messageText, parent.getEmail(), EmailType.RESET_PASSWORD).setFromName("Practicer");
        emailSender.sendEmail(emailAttributes);
    }

    public void sendParentWeeklyMail(List<ChildWeeklyReportData> childWeeklyReportDataList) {

    }

    private class PracticerModel extends HashMap<String, Object> {

        public PracticerModel() {
            put("baseUrl", practicerEnvConfig.getBaseUrl());
            put("PracticerUtils", PracticerUtils.class);
            put("lang", Language.HE);
        }
    }
}
