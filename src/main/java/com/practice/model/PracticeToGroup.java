package com.practice.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Tomer
 */
@Entity
@Table(name="practice_to_group")
public class PracticeToGroup extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRACTICE_ID", nullable = false)
    private Practice practice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID", nullable = false)
    private ParentsGroup parentsGroup;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_DATE")
    private Date createDate;

    public PracticeToGroup() {
    }

    public PracticeToGroup(Practice practice, ParentsGroup parentsGroup) {
        this.practice = practice;
        this.parentsGroup = parentsGroup;
        createDate = new Date();
    }

    public Practice getPractice() {
        return practice;
    }

    public void setPractice(Practice practice) {
        this.practice = practice;
    }

    public ParentsGroup getParentsGroup() {
        return parentsGroup;
    }

    public void setParentsGroup(ParentsGroup parentsGroup) {
        this.parentsGroup = parentsGroup;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date joinDate) {
        this.createDate = joinDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PracticeToGroup)) return false;
        if (!super.equals(o)) return false;
        PracticeToGroup that = (PracticeToGroup) o;
        return Objects.equals(practice, that.practice) &&
                Objects.equals(parentsGroup, that.parentsGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), practice, parentsGroup);
    }

    public boolean equals(long practiceId, long groupId) {
        return practiceId == practice.getId() && groupId == parentsGroup.getId();
    }
}
