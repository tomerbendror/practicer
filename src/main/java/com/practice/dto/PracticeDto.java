package com.practice.dto;

import com.practice.model.*;
import com.practice.type.Language;
import com.practice.type.TTSGenerationState;
import org.hibernate.Hibernate;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by Tomer
 */
@SuppressWarnings("unused")
public class PracticeDto {
    private Long id;
    private String name;
    private String description;
    private Integer maxSecondsForQuestion;
    private Integer maxMistakesNum;
    private Integer questionsPerPractice;
    private Boolean randomOrder;
    private QuestionType questionsType;
    private Boolean useTTS;
    private Timestamp createdTime;
    private Language ttsLang;
    private TTSGenerationState ttsGenerationState;

    private Long creatorId;
    private String creatorFullName;

    private List<Question> questions = new ArrayList<>(0);
    private List<Long> deletedQuestionsIds = new ArrayList<>(0);   // the client will not always send all the questions since they did not all loaded to view,
                                                                // so we need a way to mark the server which questions were deleted
    private List<PracticeShareGroupDto> practiceSharedGroupData;
    private List<PracticeShareChildDto> practiceSharedChildData;

    public PracticeDto() {
    }

    public PracticeDto(Practice practice, Long parentId, TTSGenerationState ttsGenerationState) {
        this(practice, parentId);
        this.ttsGenerationState = ttsGenerationState;
    }

    public PracticeDto(Practice practice, Long parentId) {
        id = practice.getId();
        name = practice.getName();
        description = practice.getDescription();
        maxSecondsForQuestion = practice.getMaxSecondsForQuestion();
        maxMistakesNum = practice.getMaxMistakesNum();
        questionsPerPractice = practice.getQuestionsPerPractice();
        randomOrder = practice.getRandomOrder();
        questionsType = practice.getQuestionType();
        useTTS = practice.getUseTTS();
        createdTime = practice.getCreatedTime();
        ttsLang = practice.getTtsLang();

        questions = practice.getQuestions();
        Hibernate.initialize(questions);
        for (Question question : questions) {
            Hibernate.initialize(question.getAnswers());
        }
        Collections.sort(questions, new Comparator<Question>() {
            @Override
            public int compare(Question question1, Question question2) {
                return Integer.compare(question1.getOrderIndex(), question2.getOrderIndex());
            }
        });


        // creator data
        ParentUser creator = practice.getCreator();
        if (creator != null) {  // user practice and not a system one
            creatorId = creator.getId();
            creatorFullName = creator.getFullName();

            if (creatorId.equals(parentId)){
                // child sharing - the parent that created this practice may share this practice among its own childs
                Map<Long, PracticeShareChildDto> childIdToShareData = new HashMap<>();

                // first fill all creator childs assume they doesn't share this practice, later we will add the practice.childs that actually sharing this practice,
                // in case of the second loop override the values, so if the group is actually shared, the second loop will override the first loop 'isShare=false'
                for (ChildUser parentChild : creator.getChilds()) {
                    childIdToShareData.put(parentChild.getId(), new PracticeShareChildDto(parentChild, false));
                }

                for (ChildUser shareChild : practice.getChilds()) {
                    childIdToShareData.put(shareChild.getId(), new PracticeShareChildDto(shareChild, true));
                }

                this.practiceSharedChildData = new ArrayList<>(childIdToShareData.values());


                // groups data - the creator of this group can decide to share this practice with different group he is member of
                Map<Long, PracticeShareGroupDto> groupIdToShareData = new HashMap<>();

                // first fill all creator groups assume he didn't shared those groups, later we will add the practice.groups that actually sharing the groups,
                // in case of the second loop override the values, so if the group is actually shared the second loop will override the first loop 'isShare=false'
                for (ParentToGroup parentToGroup : creator.getParentToGroups()) {
                    ParentsGroup parentsGroup = parentToGroup.getParentsGroup();
                    groupIdToShareData.put(parentsGroup.getId(), new PracticeShareGroupDto(parentsGroup, false));
                }

                for (PracticeToGroup practiceToGroup : practice.getPracticeToGroups()) {
                    ParentsGroup parentsGroup = practiceToGroup.getParentsGroup();
                    groupIdToShareData.put(parentsGroup.getId(), new PracticeShareGroupDto(parentsGroup, true));
                }

                this.practiceSharedGroupData = new ArrayList<>(groupIdToShareData.values());
            } else {
                Map<Long, PracticeShareGroupDto> groupIdToShareData = new HashMap<>();
                for (PracticeToGroup practiceToGroup : practice.getPracticeToGroups()) {
                    ParentsGroup parentsGroup = practiceToGroup.getParentsGroup();
                    Set<ParentToGroup> groupsToUsers = parentsGroup.getGroupsToUsers();
                    for (ParentToGroup groupsToUser : groupsToUsers) {
                        if (groupsToUser.getParentUser().getId().equals(parentId)) {
                            groupIdToShareData.put(parentsGroup.getId(), new PracticeShareGroupDto(parentsGroup, true));
                            break;
                        }
                    }
                }
                this.practiceSharedGroupData = new ArrayList<>(groupIdToShareData.values());
            }
        } else {                // system practice
            creatorId = -1L;
        }
    }

    public Long getId() {
        return id;
    }

    public void setPracticeId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxSecondsForQuestion() {
        return maxSecondsForQuestion;
    }

    public void setMaxSecondsForQuestion(Integer maxSecondsForQuestion) {
        this.maxSecondsForQuestion = maxSecondsForQuestion;
    }

    public Integer getMaxMistakesNum() {
        return maxMistakesNum;
    }

    public void setMaxMistakesNum(Integer maxMistakesNum) {
        this.maxMistakesNum = maxMistakesNum;
    }

    public Integer getQuestionsPerPractice() {
        return questionsPerPractice;
    }

    public void setQuestionsPerPractice(Integer questionsPerPractice) {
        this.questionsPerPractice = questionsPerPractice;
    }

    public void setDeletedQuestionsIds(List<Long> deletedQuestionsIds) {
        this.deletedQuestionsIds = deletedQuestionsIds;
    }

    public List<Long> getDeletedQuestionsIds() {
        return deletedQuestionsIds;
    }

    public Boolean getRandomOrder() {
        return randomOrder;
    }

    public void setRandomOrder(Boolean randomOrder) {
        this.randomOrder = randomOrder;
    }

    public QuestionType getQuestionsType() {
        return questionsType;
    }

    public void setQuestionType(QuestionType questionsType) {
        this.questionsType = questionsType;
    }

    public Boolean getUseTTS() {
        return useTTS;
    }

    public void setUseTTS(Boolean useTTS) {
        this.useTTS = useTTS;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorFullName() {
        return creatorFullName;
    }

    public void setCreatorFullName(String creatorFullName) {
        this.creatorFullName = creatorFullName;
    }

    public List<PracticeShareGroupDto> getPracticeSharedGroupData() {
        return practiceSharedGroupData;
    }

    public void setPracticeSharedGroupData(List<PracticeShareGroupDto> practiceSharedGroupData) {
        this.practiceSharedGroupData = practiceSharedGroupData;
    }

    public void setPracticeSharedChildData(List<PracticeShareChildDto> practiceSharedChildData) {
        this.practiceSharedChildData = practiceSharedChildData;
    }

    public List<PracticeShareChildDto> getPracticeSharedChildData() {
        return practiceSharedChildData;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public Language getTtsLang() {
        return ttsLang;
    }

    public void setTtsLang(Language ttsLang) {
        this.ttsLang = ttsLang;
    }

    public TTSGenerationState getTtsGenerationState() {
        return ttsGenerationState;
    }

    public void setTtsGenerationState(TTSGenerationState ttsGenerationState) {
        this.ttsGenerationState = ttsGenerationState;
    }

    // this class represent a single group the user is a member of, the 'isShare' represent whether the practice is share within the specify group
    public static class PracticeShareGroupDto {
        private Long groupId;
        private String groupName;
        private Boolean isShare;

        public PracticeShareGroupDto() {
        }

        public PracticeShareGroupDto(ParentsGroup group, Boolean isShare) {
            groupId = group.getId();
            groupName = group.getName();
            this.isShare = isShare;
        }

        public Long getGroupId() {
            return groupId;
        }

        public void setGroupId(Long groupId) {
            this.groupId = groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public Boolean getIsShare() {
            return isShare;
        }

        public void setIsShare(Boolean isShare) {
            this.isShare = isShare;
        }
    }

    // this class represent a single child the parent decided to share the practice with, the 'isShare' represent whether the practice is share for the specific child
    public static class PracticeShareChildDto {
        private Long childId;
        private String childName;
        private Boolean isShare;
        private Gender gender;

        public PracticeShareChildDto() {
        }

        public PracticeShareChildDto(ChildUser childUser, Boolean isShare) {
            childId = childUser.getId();
            childName = childUser.getFirstName();
            gender = childUser.getGender();
            this.isShare = isShare;
        }

        public Long getChildId() {
            return childId;
        }

        public void setChildId(Long childId) {
            this.childId = childId;
        }

        public String getChildName() {
            return childName;
        }

        public void setChildName(String childName) {
            this.childName = childName;
        }

        public Boolean getIsShare() {
            return isShare;
        }

        public void setIsShare(Boolean isShare) {
            this.isShare = isShare;
        }

        public Gender getGender() {
            return gender;
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }
    }
}
