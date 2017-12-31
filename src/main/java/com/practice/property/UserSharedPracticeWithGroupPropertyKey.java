package com.practice.property;

import com.practice.model.UserProperty.PropertyKey;

/**
 * Created by Tomer
 */
public class UserSharedPracticeWithGroupPropertyKey extends PropertyKey<Boolean> {
    public UserSharedPracticeWithGroupPropertyKey(long groupId, long practiceId) {
        super("UserSharedPracticeWithGroupPropertyKey_groupId:" + groupId + "-practiceId:" + practiceId, Boolean.class, false);
    }
}
