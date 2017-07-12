package com.namodu.pustakam.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by sanemdeepak on 10/3/16.
 */
public class Permission {

    @JsonIgnore
    private int id;
    @NotBlank
    private String permission;
    @JsonIgnore
    private int role_id;

    private Permission() {
    }

    public Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    @Override
    public String toString() {
        return "Permission: " + permission;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Permission)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return this.permission.equalsIgnoreCase(((Permission) obj).permission);
    }
}
