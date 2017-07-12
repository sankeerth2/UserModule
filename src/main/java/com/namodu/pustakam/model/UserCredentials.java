package com.namodu.pustakam.model;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by dsanem on 11/17/16.
 */
public class UserCredentials {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
