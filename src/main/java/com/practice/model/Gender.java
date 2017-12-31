package com.practice.model;

/**
 * User: tomer
 */
public enum Gender {
    MALE,
    FEMALE,
    UNKNOWN;

    public static Gender fromString(String genderStr) {
        for (Gender gender : values()) {
            if (gender.name().equalsIgnoreCase(genderStr)) {
                return gender;
            }
        }
        return UNKNOWN;
    }
}
