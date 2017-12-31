package com.practice.model;

import com.practice.model.statistics.PracticeResult;
import com.practice.type.Language;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Tomer
 */
@Entity
@Table(name="practice")
public class Practice extends BaseEntity {

    @NotNull
    @Length(min=1, max=100)
    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "MAX_SECONDS_FOR_QUESTION")
    @Range(min = 0, max = 30)
    private Integer maxSecondsForQuestion;

    @Column(name = "MAX_MISTAKES_NUM")
    @NotNull
    @Range(min = 0, max = 5)    // 0 means 'unlimited times'
    private Integer maxMistakesNum;

    @Column(name = "QUESTIONS_PER_PRACTICE")
    @NotNull
    @Min(value = 5)
    private Integer questionsPerPractice;

    @Column(name = "RANDOM_ORDER")
    @NotNull
    private Boolean randomOrder;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "QUESTION_TYPE")
    private QuestionType questionType;

    @Column(name = "USE_TTS")
    @NotNull
    private Boolean useTTS;

    @Enumerated(EnumType.STRING)
    @Column(name = "TTS_LANG")
    private Language ttsLang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATOR_ID")
    private ParentUser creator;    // in case this null -> the practice is system practice

    @Column(name = "CREATE_TIME", insertable = false, updatable = false)
    private Timestamp createdTime = new Timestamp(System.currentTimeMillis());

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "practice", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PracticeToGroup> practiceToGroups = new HashSet<>();

    @ManyToMany(cascade={CascadeType.ALL})
    @JoinTable(name="practice_to_child",
            joinColumns={@JoinColumn(name="PRACTICE_ID")},
            inverseJoinColumns={@JoinColumn(name="CHILD_ID")})
    private List<ChildUser> childs = new ArrayList<>(0);    // currently using for case where a parent want to assign to his childs a practice without using a group

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "practice", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Question> questions = new ArrayList<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "practice", cascade = CascadeType.ALL)
    private List<PracticeResult> practiceResults = new ArrayList<>(0);

    public Practice() {
    }

    public Practice duplicate() {
        Practice practice = new Practice();
        practice.setName(getName());
        practice.setDescription(getDescription());
        practice.setMaxSecondsForQuestion(getMaxSecondsForQuestion());
        practice.setMaxMistakesNum(getMaxMistakesNum());
        practice.setQuestionsPerPractice(getQuestionsPerPractice());
        practice.setRandomOrder(getRandomOrder());
        practice.setQuestionType(getQuestionType());
        practice.setUseTTS(getUseTTS());
        practice.setTtsLang(getTtsLang());
        practice.setCreator(getCreator());
        return practice;
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

    public Boolean getRandomOrder() {
        return randomOrder;
    }

    public void setRandomOrder(Boolean randomOrder) {
        this.randomOrder = randomOrder;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public ParentUser getCreator() {
        return creator;
    }

    public void setCreator(ParentUser creator) {
        this.creator = creator;
    }

    public Set<PracticeToGroup> getPracticeToGroups() {
        return practiceToGroups;
    }

    public void setPracticeToGroups(Set<PracticeToGroup> practiceToGroups) {
        this.practiceToGroups = practiceToGroups;
    }

    public List<ChildUser> getChilds() {
        return childs;
    }

    public void setChilds(List<ChildUser> childs) {
        this.childs = childs;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Boolean getUseTTS() {
        return useTTS;
    }

    public void setUseTTS(Boolean useTTS) {
        this.useTTS = useTTS;
    }

    public Language getTtsLang() {
        return ttsLang;
    }

    public void setTtsLang(Language ttsLang) {
        this.ttsLang = ttsLang;
    }

    public Integer getQuestionsPerPractice() {
        return questionsPerPractice;
    }

    public void setQuestionsPerPractice(Integer questionsPerPractice) {
        this.questionsPerPractice = questionsPerPractice;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }
}
