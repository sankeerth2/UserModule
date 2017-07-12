package com.namodu.pustakam.repository;

import com.namodu.pustakam.model.Invitation;

/**
 * Created by sanemdeepak on 10/13/16.
 */
public interface InvitationRepository {

    //TODO all specifications of invitation service
    public boolean createNewInvitation(String userLinkId);

    public Invitation findInvitationByInvitationId(String invitationId);

    public Invitation findActiveInvitationByUserLinkId(String userLinkId);

    public boolean updateClaimedStatus(String invitationId);

    public boolean makeInvitationUnusable(String invitationId);

    public boolean deleteInvitation(String userLinkId);

}
