package com.namodu.pustakam.utilities.rowMappers;

import com.namodu.pustakam.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by sanemdeepak on 9/28/16.
 */
public class UserRowMapper implements RowMapper<User> {
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
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {

        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String email = rs.getString("email");
        String mobile = rs.getString("mobile");
        String userOrg = rs.getString("user_org");
        String addedBy = rs.getString("added_by");
        String lastUpdatedBy = rs.getString("last_updated_by");
        String userLinkId = rs.getString("user_link_id");


        User user = new User(firstName, lastName, email, mobile);
        user.setLogin(rs.getString("login"));
        user.setPassword(rs.getString("password"));
        user.setActive(rs.getInt("active"));
        user.setNewUser(rs.getInt("new_user"));
        user.setCreatedOn(rs.getDate("created_on"));
        user.setLastUpdatedOn(rs.getDate("last_updated_on"));
        user.setAddedBy(addedBy);
        user.setUserOrg(userOrg);
        user.setLastUpdatedBy(lastUpdatedBy);
        user.setUserLinkId(userLinkId);
        user.setDeleted(rs.getInt("deleted"));

        return user;
    }
}
