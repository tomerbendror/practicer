package com.practice.etc;

import org.brickred.socialauth.SocialAuthManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by Tomer
 */
@Component
@Scope(value = "session")
public class OAuthSession implements Serializable {
    private String redirectUri; // the url to be redirect after success login, this url is registered at oauth provider account
    private String providerId;
    private SocialAuthManager authManager;

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setAuthManager(SocialAuthManager authManager) {
        this.authManager = authManager;
    }

    public SocialAuthManager getAuthManager() {
        return authManager;
    }
}
