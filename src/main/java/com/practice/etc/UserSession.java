package com.practice.etc;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by Tomer
 */
@Component
@Scope(value = "session")
public class UserSession implements Serializable {
    private String redirectUrl;

    // we allow to parent to view the child appliation as one of his child, in order to get back to the parent app we keep the parentId as there may be more than single parent for child
    private Long viewAsChildParentId;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        if (StringUtils.isNotBlank(redirectUrl)) {
            this.redirectUrl = redirectUrl;
        }
    }

    public Long getViewAsChildParentId() {
        return viewAsChildParentId;
    }

    public void setViewAsChildParentId(Long viewAsChildParentId) {
        this.viewAsChildParentId = viewAsChildParentId;
    }
}
