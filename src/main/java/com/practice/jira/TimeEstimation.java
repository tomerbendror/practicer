package com.practice.jira;

/**
 * Created by Tomer.Ben-Dror on 2/12/2017
 */
public class TimeEstimation {
    private EstimationValue statFieldValue;

    public TimeEstimation() {
    }

    public EstimationValue getStatFieldValue() {
        return statFieldValue;
    }

    public void setStatFieldValue(EstimationValue statFieldValue) {
        this.statFieldValue = statFieldValue;
    }

    public static class EstimationValue {
        private int value = 0;
        private String text = "";

        public EstimationValue() {
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
