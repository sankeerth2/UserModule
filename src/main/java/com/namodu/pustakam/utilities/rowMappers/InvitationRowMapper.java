package com.namodu.pustakam.utilities.rowMappers;

import com.namodu.pustakam.model.Invitation;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by sanemdeepak on 10/9/16.
 */
public class InvitationRowMapper implements RowMapper<Invitation> {
    /**
     * Implementations must implement this method to map each row of data
     * in the ResultSet. This method should not call {@code next()} on
     * the ResultSet; it is only supposed to map values of the current row.
     *
     * @param rs     the ResultSet to map (pre-initialized for the current row)
     * @param rowNum the number of the current row
     * @return the result object for the current row
     * @throws SQLException if a SQLException is encountered getting
     *                      column values (that is, there's no need to catch SQLException)
     */
    @Override
    public Invitation mapRow(ResultSet rs, int rowNum) throws SQLException {

        String invitationId = rs.getString("invitation_id");
        String userLinkId = rs.getString("user_link_id");
        int isClaimed = rs.getInt("is_claimed");
        Timestamp claimedAt = rs.getTimestamp("claimed_at");
        int isUsable = rs.getInt("is_usable");

        Invitation invitation = new Invitation(invitationId, userLinkId);
        invitation.setIsClaimed(isClaimed);
        invitation.setClaimedAt(claimedAt);
        invitation.setUsable(isUsable);
        return invitation;
    }
}
