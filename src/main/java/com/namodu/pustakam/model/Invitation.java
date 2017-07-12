package com.namodu.pustakam.model;

import com.fasterxml.jackson.annotation.JsonRootName;

import java.sql.Timestamp;

/**
 * Created by sanemdeepak on 10/9/16.
 */
@JsonRootName(value = "invite")
public class Invitation {

    private String invitationId;
    private String userLinkId;
    private int isClaimed;
    private Timestamp claimedAt;
    private int usable;

    private Invitation() {
    }

    public Invitation(String invitationId, String userLinkId) {
        this.invitationId = invitationId;
        this.userLinkId = userLinkId;
    }

    public String getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(String invitation_id) {
        this.invitationId = invitation_id;
    }

    public String getUserLinkId() {
        return userLinkId;
    }

    public void setUserLinkId(String userLinkId) {
        this.userLinkId = userLinkId;
    }

    public int getIsClaimed() {
        return isClaimed;
    }

    public void setIsClaimed(int isClaimed) {
        this.isClaimed = isClaimed;
    }

    public Timestamp getClaimedAt() {
        return claimedAt;
    }

    public void setClaimedAt(Timestamp claimedAt) {
        this.claimedAt = claimedAt;
    }

    public boolean isUsable() {
        return usable == 1;
    }

    public void setUsable(int usable) {
        this.usable = usable;
    }
}
