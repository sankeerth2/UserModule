package com.namodu.pustakam.service;

import com.namodu.pustakam.model.Invitation;
import com.namodu.pustakam.repository.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sanemdeepak on 11/20/16.
 */
@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;

    @Autowired
    public InvitationService(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    public boolean createNewInvitation(String userLinkId) {
        return invitationRepository.createNewInvitation(userLinkId);
    }

    public Invitation findInvitationByInvitationId(String invitationId) {
        return invitationRepository.findInvitationByInvitationId(invitationId);
    }

    public Invitation findActiveInvitationByUserLinkId(String userLinkId) {
        return invitationRepository.findActiveInvitationByUserLinkId(userLinkId);
    }

    public boolean updateClaimedStatus(String invitationId) {
        return invitationRepository.updateClaimedStatus(invitationId);
    }

    public boolean makeInvitationUnusable(String invitationId) {
        return invitationRepository.makeInvitationUnusable(invitationId);
    }

    public boolean deleteActiveInvitationOf(String userLinkId) {
        return invitationRepository.deleteInvitation(userLinkId);
    }


}
