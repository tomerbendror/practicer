package com.practice.util;

import com.practice.model.ParentUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Tomer
 */
@Component
public class PracticerEnvConfig {

    @Value("#{systemProperties['envName'] ?: 'production'}")
    private String envName;

    public PracticerEnvConfig() {
    }

    public String getEnvName() {
        return envName;
    }

    public boolean isProduction() {
        return "production".equalsIgnoreCase(getEnvName());
    }

    public String getBaseUrl() {
        return "dev".equalsIgnoreCase(envName) ? "http://local.practicer-app.com" : "http://practicer-app.com";
    }

    public String getAbsoluteProfileImageUrlOrDefault(ParentUser parent) {
        String profileImageUrlOrDefault = parent.getProfileImageUrlOrDefault();
        if (StringUtils.isNotBlank(profileImageUrlOrDefault)) {
            String lowerCase = profileImageUrlOrDefault.toLowerCase();
            if (!lowerCase.contains("http://") && !lowerCase.contains("https://")) {
                return getBaseUrl() + "/" + profileImageUrlOrDefault;
            }
        }
        return profileImageUrlOrDefault;
    }
}
