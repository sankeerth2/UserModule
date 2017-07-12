package com.namodu.pustakam.model;

/**
 * Created by sanemdeepak on 10/12/16.
 */
public class UserRole {

    private int id;
    private String userLinkId;
    private String role;
    private int roleId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserLinkId() {
        return userLinkId;
    }

    public void setUserLinkId(String userLinkId) {
        this.userLinkId = userLinkId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}
