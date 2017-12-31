package com.practice.dto;

import com.google.common.collect.LinkedListMultimap;
import com.practice.model.ChildUser;
import com.practice.model.ParentsGroup;
import com.practice.model.Practice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Tomer
 */
public class ChildPracticesSummaryDto {
    private Long childId;
    private List<PracticeSummaryDto> practicesSummary;

    public ChildPracticesSummaryDto() {
    }

    public ChildPracticesSummaryDto(ChildUser child, List<Practice> directPractices, LinkedListMultimap<ParentsGroup, Practice> groupToPracticesMap) {
        childId = child.getId();

        practicesSummary = new ArrayList<>(directPractices.size());
        for (Practice practice : directPractices) { // a practice that one of his parent create just for him, no group sharing
            practicesSummary.add(new PracticeSummaryDto(practice));
        }

        for (Map.Entry<ParentsGroup, Collection<Practice>> groupToPractices : groupToPracticesMap.asMap().entrySet()) {
            ParentsGroup group = groupToPractices.getKey();
            for (Practice practice : groupToPractices.getValue()) {
                practicesSummary.add(new PracticeSummaryDto(practice, group));
            }
        }
    }

    public Long getChildId() {
        return childId;
    }

    public void setChildId(Long childId) {
        this.childId = childId;
    }

    public List<PracticeSummaryDto> getPracticesSummary() {
        return practicesSummary;
    }

    public void setPracticesSummary(List<PracticeSummaryDto> practicesSummary) {
        this.practicesSummary = practicesSummary;
    }
}
