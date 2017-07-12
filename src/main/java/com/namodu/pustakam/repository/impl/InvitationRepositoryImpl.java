package com.namodu.pustakam.repository.impl;

import com.namodu.pustakam.model.Invitation;
import com.namodu.pustakam.repository.InvitationRepository;
import com.namodu.pustakam.utilities.rowMappers.InvitationRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by sanemdeepak on 10/13/16.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Service
public class InvitationRepositoryImpl implements InvitationRepository {

    private final Logger logger = LoggerFactory.getLogger(InvitationRepositoryImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String invitationTable = "EFSR_User_Invitation";

    @Autowired
    InvitationRowMapper invitationRowMapper;

    //create
    private final String createInvitationQuery = "INSERT INTO " + invitationTable + " (`invitation_id`,`user_link_id`) " +
            "VALUES(?,?)";

    //gets
    private final String findInvitationByInvitationId = "SELECT * FROM " + invitationTable + " WHERE invitation_id = ? AND is_usable = 1";
    private final String findInvitationByUserLinkId = "SELECT * FROM " + invitationTable + " WHERE user_link_id = ?";

    //updates
    private final String updateClaimedStatus = "UPDATE " + invitationTable + " SET claimed_at = UTC_TIMESTAMP(), is_claimed = 1 WHERE invitation_id = ?";
    private final String updateUsabilityByInvitationId = "UPDATE " + invitationTable + " SET is_usable = ? WHERE invitation_id = ?";
    private final String updateUsabilityByUserLinkId = "UPDATE " + invitationTable + " SET is_usable = ? WHERE user_link_id = ?";

    //deletes
    private final String deleteInvitationByUserLink = "DELETE FROM " + invitationTable + " WHERE user_link_id = ? and is_usable = 1";


    @Override
    public boolean createNewInvitation(String userLinkId) {
        String invitationId = generateRandomId();
        logger.info("Generated new Invitation ID: " + invitationId + "for user: " + userLinkId);
        return jdbcTemplate.update(createInvitationQuery, invitationId, userLinkId) > 0;
    }

    @Override
    public Invitation findInvitationByInvitationId(String invitationId) {
        try {
            return this.jdbcTemplate.queryForObject(findInvitationByInvitationId, new Object[]{invitationId}, invitationRowMapper);
        } catch (EmptyResultDataAccessException exp) {
            return null;

        }
    }

    @Override
    public Invitation findActiveInvitationByUserLinkId(String userLinkId) {
        try {
            return this.jdbcTemplate.queryForObject(findInvitationByUserLinkId, new Object[]{userLinkId}, invitationRowMapper);
        } catch (EmptyResultDataAccessException exp) {
            return null;

        }
    }

    @Override
    public boolean updateClaimedStatus(String invitationId) {
        return this.jdbcTemplate.update(updateClaimedStatus, invitationId) > 0;

    }

    @Override
    public boolean makeInvitationUnusable(String invitationId) {
        return this.jdbcTemplate.update(updateUsabilityByInvitationId, new Object[]{0, invitationId}) > 0;
    }

    @Override
    public boolean deleteInvitation(String userLinkId) {
        return this.jdbcTemplate.update(deleteInvitationByUserLink, userLinkId) > 0;
    }

    //helpers
    private String generateRandomId() {
        return UUID.randomUUID().toString();
    }
}
