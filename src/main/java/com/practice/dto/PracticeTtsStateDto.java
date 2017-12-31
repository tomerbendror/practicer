package com.practice.dto;

import com.practice.type.TTSGenerationState;

import java.util.Map;

/**
 * Created by Tomer
 */
public class PracticeTtsStateDto {
    private Long practiceId;
    private Map<TTSGenerationState, Integer> stateToCount;
    private TTSGenerationState summaryState;
    private Map<Long, String> questionIdToTtsUrl;

    public PracticeTtsStateDto(long practiceId, Map<TTSGenerationState, Integer> stateToCount) {
        this.practiceId = practiceId;
        this.stateToCount = stateToCount;
        for (TTSGenerationState ttsGenerationState : stateToCount.keySet()) {
            if (ttsGenerationState == TTSGenerationState.IN_PROGRESS) {
                summaryState = TTSGenerationState.IN_PROGRESS;
                break;
            }
        }
        if (summaryState == null) {
            summaryState = TTSGenerationState.DONE;
        }
    }

    public PracticeTtsStateDto() {
    }

    public Long getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(Long practiceId) {
        this.practiceId = practiceId;
    }

    public Map<TTSGenerationState, Integer> getStateToCount() {
        return stateToCount;
    }

    public void setStateToCount(Map<TTSGenerationState, Integer> stateToCount) {
        this.stateToCount = stateToCount;
    }

    public TTSGenerationState getSummaryState() {
        return summaryState;
    }

    public void setSummaryState(TTSGenerationState summaryState) {
        this.summaryState = summaryState;
    }

    public Map<Long, String> getQuestionIdToTtsUrl() {
        return questionIdToTtsUrl;
    }

    public void setQuestionIdToTtsUrl(Map<Long, String> questionIdToTtsUrl) {
        this.questionIdToTtsUrl = questionIdToTtsUrl;
    }
}
