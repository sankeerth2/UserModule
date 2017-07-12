package com.namodu.pustakam.utilities.rowMappers;

import com.namodu.pustakam.model.UserRole;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by sanemdeepak on 10/12/16.
 */
public class UserRoleMapper implements RowMapper<UserRole> {
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
    public UserRole mapRow(ResultSet rs, int rowNum) throws SQLException {

        int id = rs.getInt("id");
        String userLinkId = rs.getString("user_link_id");
        String role = rs.getString("role");
        int roleId = rs.getInt("role_id");

        UserRole userRole = new UserRole();
        userRole.setId(id);
        userRole.setUserLinkId(userLinkId);
        userRole.setRole(role);
        userRole.setRoleId(id);
        return null;
    }
}
