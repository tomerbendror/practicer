package com.practice.job;

import com.practice.controller.EmailController;
import com.practice.model.*;
import com.practice.model.statistics.PracticeResult;
import com.practice.model.statistics.PracticeStatisticsForChild;
import com.practice.model.statistics.PracticeStatisticsForGroup;
import com.practice.model.statistics.TimePeriod;
import com.practice.property.UserProperties;
import com.practice.repository.BaseRepository;
import com.practice.util.PracticerEnvConfig;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Tomer
 */
@Component
public class StatisticsJob extends BaseRepository {

    private static final Logger logger = Logger.getLogger(StatisticsJob.class);

    @Autowired
    private EmailController emailController;

    @Autowired
    private PracticerEnvConfig practicerEnvConfig;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

//    @Scheduled(cron = "0 0 19 ? * FRI *")
    @Scheduled(cron = "0 0 4 1/1 * ?")    // 24:00 IL time
    @Transactional
    public void weeklyJob() {
//        if (practicerEnvConfig.isProduction()) {
//            emailController.sendDebugMail("start processing weekly job on: " + simpleDateFormat.format(new Date()));
//            calcGroupStatisticsData();
//            calcChildPracticesStatisticsData();
//            sendParentWeeklyReport();
//        }
    }

    @Scheduled(cron = "0 0 0/1 1/1 * ?")    // every single hour
    @Transactional
    public void dailyJob() {
        if (practicerEnvConfig.isProduction()) {
            Calendar calendar = Calendar.getInstance();
            Date time = calendar.getTime();
            String dateStr = simpleDateFormat.format(time);
            int parentThatGotMailCount = sendParentDailyReport();
            if (parentThatGotMailCount > 0) {
                String message = "Daily job finish, mail sent to " + parentThatGotMailCount + " parents, time: " + dateStr;
                emailController.sendDebugMail(message);
                logger.debug(message);
            }
        }
    }

    @Scheduled(cron = "0 0 15 1/1 * ?")    // 21:00 IL time
    @Transactional
    public void teacherDailyJob() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -24);

        EntityManager em = getEm();
        Query groupIdsQuery = em.createNativeQuery("SELECT ptg.GROUP_ID FROM practice_result pr JOIN practice_to_group ptg ON ptg.PRACTICE_ID=pr.PRACTICE_ID " +
                " JOIN parent_to_group partg ON partg.GROUP_ID=ptg.GROUP_ID WHERE partg.USER_IN_GROUP_ROLE=:userRole AND pr.CREATE_TIME>=:minTime " +
                "GROUP BY ptg.GROUP_ID");
        groupIdsQuery.setParameter("userRole", UserInGroupRole.TEACHER.name());
        groupIdsQuery.setParameter("minTime", calendar.getTime());
        @SuppressWarnings("unchecked")
        List<Number> groupIds = (List<Number>) groupIdsQuery.getResultList();

        for (Number groupId : groupIds) {
            ParentsGroup group = getEm().find(ParentsGroup.class, groupId.longValue());
            Query groupPracticesQuery = getEm().createNativeQuery("SELECT pr.* FROM practice_result pr JOIN practice_to_group ptg ON ptg.PRACTICE_ID=pr.PRACTICE_ID " +
                    "WHERE ptg.GROUP_ID=:groupId AND pr.CREATE_TIME>=:minTime", PracticeResult.class);
            groupPracticesQuery.setParameter("minTime", calendar.getTime());
            groupPracticesQuery.setParameter("groupId", group.getId());
            @SuppressWarnings("unchecked")
            List<PracticeResult> groupPractices = groupPracticesQuery.getResultList();

            Map<Long, PracticeInGroupStatistics> practiceIdToGroupStat = new HashMap<>();
            for (PracticeResult practiceResult : groupPractices) {
                Long practiceId = practiceResult.getPractice().getId();
                PracticeInGroupStatistics practiceInGroupStat = practiceIdToGroupStat.get(practiceId);
                if (practiceInGroupStat == null) {
                    practiceInGroupStat = new PracticeInGroupStatistics(practiceResult.getPractice().getName());
                    practiceIdToGroupStat.put(practiceResult.getPractice().getId(), practiceInGroupStat);
                }
                practiceInGroupStat.addPracticeResult(practiceResult);
            }

            List<PracticeInGroupStatistics> practiceInGroupStatList = new ArrayList<>(practiceIdToGroupStat.values());
            Collections.sort(practiceInGroupStatList, new Comparator<PracticeInGroupStatistics>() {
                @Override
                public int compare(PracticeInGroupStatistics o1, PracticeInGroupStatistics o2) {
                    return o1.practiceName.compareTo(o2.practiceName);
                }
            });
            emailController.sendTeacherDailyMail(group, practiceInGroupStatList);
        }
    }

    public static void main(String[] args) {
        Calendar calIL = Calendar.getInstance(TimeZone.getTimeZone("Europe/Athens"));
        int hourInIL = calIL.get(Calendar.HOUR_OF_DAY);
        System.out.println(hourInIL);
    }


    private int sendParentDailyReport() {
        EntityManager em = getEm();

        Calendar calIL = Calendar.getInstance(TimeZone.getTimeZone("Europe/Athens")); // IL stable time
        int hourInIL = calIL.get(Calendar.HOUR_OF_DAY);
        Query query;
        if (hourInIL == UserProperties.DAILY_MAIL_HOUR_KEY.defaultVal) {
            query = em.createNativeQuery("SELECT pu.ID FROM parent_user pu LEFT JOIN user_property up ON pu.ID=up.USER_ID " +
                    "WHERE (up.ID IS NULL OR (NOT exists(SELECT 1 FROM user_property up1 WHERE up1.USER_ID=pu.id AND up1.PROPERTY_NAME=:receiveDailyMailKey) " +
                    "AND NOT exists(SELECT 1 FROM user_property up2 WHERE up2.USER_ID=pu.id AND up2.PROPERTY_NAME=:mailHourKey))) " +
                    "AND exists(SELECT 1 FROM parent_to_child ptc WHERE ptc.PARENT_ID=pu.ID GROUP BY ptc.PARENT_ID HAVING count(ptc.PARENT_ID)>0) GROUP BY pu.ID");
        } else {
            query = em.createNativeQuery("SELECT pu.ID FROM parent_user pu LEFT JOIN user_property up ON pu.ID=up.USER_ID " +
                    "WHERE (NOT exists(SELECT 1 FROM user_property up1 WHERE up1.USER_ID=pu.id AND up1.PROPERTY_NAME=:receiveDailyMailKey) " +
                    "AND exists(SELECT 1 FROM user_property up2 WHERE up2.USER_ID=pu.id AND up2.PROPERTY_NAME=:mailHourKey AND up2.VALUE_STR=:hourStr)) " +
                    "AND exists(SELECT 1 FROM parent_to_child ptc WHERE ptc.PARENT_ID=pu.ID GROUP BY ptc.PARENT_ID HAVING count(ptc.PARENT_ID)>0) GROUP BY pu.ID");
            query.setParameter("hourStr", String.valueOf(hourInIL));
        }
        query.setParameter("mailHourKey", UserProperties.DAILY_MAIL_HOUR_KEY.name);
        query.setParameter("receiveDailyMailKey", UserProperties.RECEIVE_DAILY_MAIL_KEY.name);
        @SuppressWarnings("unchecked")
        List<Number> parentIds = (List<Number>) query.getResultList();
        int parentToSendMailCount = 0;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -24);
        for (Number parentId : parentIds) {
            ParentUser parent = em.find(ParentUser.class, parentId.longValue());
            TypedQuery<PracticeResult> practiceResultsQuery = em.createQuery("from PracticeResult where childUser in (:childs) and createdTime>=:minTime", PracticeResult.class);
            practiceResultsQuery.setParameter("childs", parent.getChilds());
            practiceResultsQuery.setParameter("minTime", calendar.getTime());
            List<PracticeResult> practiceResults = practiceResultsQuery.getResultList();

            Map<Long, ChildPractices> childIdToPractices = new HashMap<>(parent.getChilds().size());
            for (PracticeResult practiceResult : practiceResults) {
                ChildUser child = practiceResult.getChildUser();
                Practice practice = practiceResult.getPractice();
                if (child != null && practice != null) {
                    ChildPractices childPractices = childIdToPractices.get(child.getId());
                    if (childPractices == null) {
                        childPractices = new ChildPractices(child.getId(), child.getDisplayName());
                        childIdToPractices.put(child.getId(), childPractices);
                    }
                    childPractices.addPractice(practiceResult);
                }
            }
            if (!childIdToPractices.isEmpty()) {
                parentToSendMailCount++;
                emailController.sendParentDailyMail(parent, childIdToPractices.values());
            }
        }
        logger.debug("found " + parentToSendMailCount + " parents to send daily report");
        return parentToSendMailCount;
    }

    public static class ChildPractices {
        private long childId;
        private String childDisplayName;
        private List<PracticeResult> practices;

        public ChildPractices(long childId, String childDisplayName) {
            this.childId = childId;
            this.childDisplayName = childDisplayName;
            this.practices = new LinkedList<>();
        }

        public long getChildId() {
            return childId;
        }

        public String getChildDisplayName() {
            return childDisplayName;
        }

        public List<PracticeResult> getPractices() {
            return practices;
        }

        public void addPractice(PracticeResult practiceResult) {
            practices.add(practiceResult);
        }
    }

    private void calcGroupStatisticsData() {
        try {
            Date weekAgoDate = weekAgoDate();
            EntityManager em = getEm();
            TypedQuery<Long> query = em.createQuery("select grp.id from ParentsGroup grp where grp.childsToGroup.size > 0", Long.class);
            List<Long> groupsIds = query.getResultList();
            logger.debug("found " + groupsIds.size() + " group to calculate group practices statistics");

            for (Long groupId : groupsIds) {
                try {
                    String queryStr = "from PracticeResult " +
                            "where createdTime>:weekAgoDate " +
                            "and childUser.id in (select ctg.childUser.id from ChildToGroup ctg where ctg.parentsGroup.id=:groupId) " +
                            "and practice.id in (select ptg.practice.id from PracticeToGroup ptg where ptg.parentsGroup.id=:groupId)";
                    TypedQuery<PracticeResult> practicesResultsQuery = em.createQuery(queryStr, PracticeResult.class);
                    practicesResultsQuery.setParameter("groupId", groupId);
                    practicesResultsQuery.setParameter("weekAgoDate", weekAgoDate);
                    List<PracticeResult> groupPracticesResults = practicesResultsQuery.getResultList();

                    Map<Long, StatisticsData> practiceToDataMap = new HashMap<>();
                    for (PracticeResult practiceResult : groupPracticesResults) {
                        Long practiceId = practiceResult.getPractice().getId();
                        StatisticsData practiceData = practiceToDataMap.get(practiceId);
                        if (practiceData == null) {
                            practiceData = new StatisticsData();
                            practiceToDataMap.put(practiceId, practiceData);
                        }
                        practiceData.avgScore.addElement(practiceResult.getScore());
                        practiceData.avgTime.addElement(practiceResult.getTimeSecond());
                    }

                    for (Map.Entry<Long, StatisticsData> practiceDataEntry : practiceToDataMap.entrySet()) {
                        Long practiceId = practiceDataEntry.getKey();
                        StatisticsData practiceData = practiceDataEntry.getValue();
                        int avgScore = (int) Math.round(practiceData.avgScore.getAverage());
                        int avgTime = (int) Math.round(practiceData.avgTime.getAverage());
                        em.persist(new PracticeStatisticsForGroup(TimePeriod.WEEK, avgScore, avgTime, practiceId, groupId));
                        em.flush();
                        em.clear();
                    }
                } catch (Exception e) {
                    String message = "fail to calculate group practices statistics for groupId: " + groupId;
                    logger.error(message, e);
                    emailController.sendInternalErrorMail(message, e);
                }
            }
        } catch (Exception e) {
            String message = "fail to calculate group practices statistics";
            logger.error(message, e);
            emailController.sendInternalErrorMail(message, e);
        }
    }

    private void calcChildPracticesStatisticsData() {
        Date weekAgoDate = weekAgoDate();

        EntityManager em = getEm();
        TypedQuery<ChildUser> childsQuery = em.createQuery("from ChildUser c join PracticeResult pr on c=pr.childUser where pr.createdTime > :weekAgoDate", ChildUser.class);
        childsQuery.setParameter("weekAgoDate", weekAgoDate);
        for (ChildUser child : childsQuery.getResultList()) {
            Map<Long, StatisticsData> practiceToDataMap = new HashMap<>();
            TypedQuery<PracticeResult> query = em.createQuery("from PracticeResult where childUser=:childUser", PracticeResult.class);
            query.setParameter("childUser", child);
            for (PracticeResult practiceResult : query.getResultList()) {
                StatisticsData practiceData = practiceToDataMap.get(practiceResult.getId());
                if (practiceData == null) {
                    practiceData = new StatisticsData();
                    practiceToDataMap.put(practiceResult.getId(), practiceData);
                }
                practiceData.avgScore.addElement(practiceResult.getScore());
                practiceData.avgTime.addElement(practiceResult.getTimeSecond());
            }

            for (Map.Entry<Long, StatisticsData> practiceDataEntry : practiceToDataMap.entrySet()) {
                Long practiceId = practiceDataEntry.getKey();
                StatisticsData practiceData = practiceDataEntry.getValue();
                int avgScore = (int) Math.round(practiceData.avgScore.getAverage());
                int avgTime = (int) Math.round(practiceData.avgTime.getAverage());
                em.persist(new PracticeStatisticsForChild(TimePeriod.WEEK, avgScore, avgTime, practiceId, child.getId()));
                em.flush();
                em.clear();
            }
        }
    }

    private void sendParentWeeklyReport() {
        EntityManager em = getEm();
        Date weekAgoDate = weekAgoDate();
        //todo need to get parent which have a child practice result
        Query query = em.createNativeQuery("SELECT pu.ID FROM parent_user pu JOIN parent_to_child ptc ON pu.ID=ptc.PARENT_ID GROUP BY pu.ID");
        @SuppressWarnings("unchecked")
        List<Number> parentIds = (List<Number>) query.getResultList();

        for (Number parentId : parentIds) {
            ParentUser parent = em.find(ParentUser.class, parentId.longValue());
            List<ChildUser> childs = parent.getChilds();
            List<ChildWeeklyReportData> childWeeklyReportDataList = new ArrayList<>(childs.size());
            for (ChildUser child : childs) {
                // get child practices statistic
                TypedQuery<PracticeStatisticsForChild> childPracticesStatQuery = em.createQuery("from PracticeStatisticsForChild where childId=:childId and createDate>:weekAgoDate", PracticeStatisticsForChild.class);
                childPracticesStatQuery.setParameter("weekAgoDate", weekAgoDate);
                childPracticesStatQuery.setParameter("childId", child.getId());
                List<PracticeStatisticsForChild> childPracticesStatList = childPracticesStatQuery.getResultList();

                // get the group statistic relevant to this child
                Set<Long> childGroupsIds = new HashSet<>();
                for (ChildToGroup childToGroup : child.getChildToGroups()) {
                    childGroupsIds.add(childToGroup.getParentsGroup().getId());
                }
                String sqlStr = "from PracticeStatisticsForGroup where groupId in (:groupsIds) and createDate>:weekAgoDate";
                TypedQuery<PracticeStatisticsForGroup> groupPracticesStatQuery = em.createQuery(sqlStr, PracticeStatisticsForGroup.class);
                groupPracticesStatQuery.setParameter("groupsIds", childGroupsIds);
                List<PracticeStatisticsForGroup> groupPracStatList = groupPracticesStatQuery.getResultList();
                Map<Long, PracticeStatisticsForGroup> pracIdToGroupStatMap = new HashMap<>(groupPracStatList.size());
                for (PracticeStatisticsForGroup practiceStatisticsForGroup : groupPracStatList) {
                    pracIdToGroupStatMap.put(practiceStatisticsForGroup.getPracticeId(), practiceStatisticsForGroup);
                }

                List<PracticeStatisticsForChild> practicesWithoutGroupStat = new LinkedList<>();
                List<Pair<PracticeStatisticsForChild, PracticeStatisticsForGroup>> childAndGroupStatList = new LinkedList<>();
                for (PracticeStatisticsForChild childPracticeStat : childPracticesStatList) {
                    Long practiceId = childPracticeStat.getPracticeId();
                    PracticeStatisticsForGroup practiceStatisticsForGroup = pracIdToGroupStatMap.get(practiceId);
                    if (practiceStatisticsForGroup == null) {   // no group statistics, so we will just specify the child avg
                        practicesWithoutGroupStat.add(childPracticeStat);
                    } else {
                        childAndGroupStatList.add(new ImmutablePair<>(childPracticeStat, practiceStatisticsForGroup));
                    }
                }
                childWeeklyReportDataList.add(new ChildWeeklyReportData(child, practicesWithoutGroupStat, childAndGroupStatList));
            }
            emailController.sendParentWeeklyMail(childWeeklyReportDataList);
        }
    }

    private Date weekAgoDate() {
        Calendar weekAgo = Calendar.getInstance();
        weekAgo.add(Calendar.DATE, -7);
        weekAgo.add(Calendar.HOUR_OF_DAY, -4);  // since the calculation will take some time
        return weekAgo.getTime();
    }

    private static class StatisticsData {
        public Average avgScore = new Average();
        public Average avgTime = new Average();

        public Average getAvgScore() {
            return avgScore;
        }

        public Average getAvgTime() {
            return avgTime;
        }
    }

    public static class ChildStatisticsData extends StatisticsData {
        private String childName;

        public ChildStatisticsData(String childName) {
            this.childName = childName;
        }

        public String getChildName() {
            return childName;
        }

        public int getAverageScore() {
            return (int) avgScore.getAverage();
        }

        public double getAverageTime() {
            return avgTime.getAverage();
        }

        public int getElementsCount() {
            return avgTime.getElementCount();
        }
    }

    public static class PracticeInGroupStatistics {
        private String practiceName;
        private Map<Long, ChildStatisticsData> childIdToStat = new HashMap<>();

        PracticeInGroupStatistics(String practiceName) {
            this.practiceName = practiceName;
        }

        public List<ChildStatisticsData> getChildStatList() {
            List<ChildStatisticsData> childStatList = new ArrayList<>(childIdToStat.values());
            Collections.sort(childStatList, new Comparator<ChildStatisticsData>() {
                @Override
                public int compare(ChildStatisticsData o1, ChildStatisticsData o2) {
                    return o1.childName.compareTo(o2.childName);
                }
            });
            return childStatList;
        }

        public void addPracticeResult(PracticeResult practiceResult) {
            Long childId = practiceResult.getChildUser().getId();
            ChildStatisticsData childStatisticsData = childIdToStat.get(childId);
            if (childStatisticsData == null) {
                childStatisticsData = new ChildStatisticsData(practiceResult.getChildUser().getFullName());
                childIdToStat.put(childId, childStatisticsData);
            }
            childStatisticsData.avgScore.addElement(practiceResult.getScore());
            childStatisticsData.avgTime.addElement(practiceResult.getTimeSecond());
        }

        public String getPracticeName() {
            return practiceName;
        }
    }

    private static class Average {
        private double totalValues;
        private int elementCount;

        public void addElement(double element) {
            totalValues += element;
            elementCount++;
        }

        public double getAverage() {
            return totalValues/elementCount;
        }

        public int getElementCount() {
            return elementCount;
        }
    }

    public static class ChildWeeklyReportData {

        private ChildUser child;
        private List<PracticeStatisticsForChild> practicesWithoutGroupStat;
        private List<Pair<PracticeStatisticsForChild, PracticeStatisticsForGroup>> childAndGroupStatList;

        public ChildWeeklyReportData(ChildUser child, List<PracticeStatisticsForChild> practicesWithoutGroupStat,
                                     List<Pair<PracticeStatisticsForChild, PracticeStatisticsForGroup>> childAndGroupStatList) {
            this.child = child;
            this.practicesWithoutGroupStat = practicesWithoutGroupStat;
            this.childAndGroupStatList = childAndGroupStatList;
        }

        public ChildUser getChild() {
            return child;
        }

        public List<PracticeStatisticsForChild> getPracticesWithoutGroupStat() {
            return practicesWithoutGroupStat;
        }

        public List<Pair<PracticeStatisticsForChild, PracticeStatisticsForGroup>> getChildAndGroupStatList() {
            return childAndGroupStatList;
        }
    }
}
