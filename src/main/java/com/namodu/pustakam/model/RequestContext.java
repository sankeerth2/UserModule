package com.namodu.pustakam.model;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Created by sanemdeepak on 11/15/16.
 */
public class RequestContext extends UsernamePasswordAuthenticationToken {

    private String correlationId;
    private String userLinkId;

    public RequestContext() {
        super(null, null);
    }

    public String getUserLinkId() {
        return userLinkId;
    }

    public void setUserLinkId(String userLinkId) {
        this.userLinkId = userLinkId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
