package com.namodu.pustakam.repository.impl;

import com.namodu.pustakam.model.User;
import com.namodu.pustakam.repository.UserRepository;
import com.namodu.pustakam.utilities.rowMappers.UserRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sanemdeepak on 9/26/16.
 */

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final String userTable = "EFSR_User";
    private final String userInvitationTable = "EFSR_User_Invitation";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRowMapper rowMapper;


    //new records
    private final String createUserQuery = "INSERT INTO " + "`" + userTable + "`" + "(`login`,`password`,`first_name`, " +
            "`last_name`,`user_org`, `email`,`mobile`,`active`,`new_user`, `user_link_id`, `added_by`, `last_updated_by`,`created_on`,`last_updated_on`) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, UTC_TIMESTAMP(), UTC_TIMESTAMP())";

    private final String createNewInvitationQuery = "INSERT INTO " + userInvitationTable + "(`invitation_id`, `user_link_id`, `is_claimed`, `is_usable`)" + "VALUES(?,?,?,?)";


    //update existing records

    private final String updateUserDetailsQuery = "UPDATE " + userTable + " SET first_name = ?, last_name = ?, email = ?, " +
            "mobile = ?, `last_updated_on` = UTC_TIMESTAMP(), `last_updated_by` = ?  WHERE user_link_id = ?";

    private final String updateUserUsernameQuery = "UPDATE " + userTable + " SET login = ?, last_updated_on = UTC_TIMESTAMP() WHERE user_link_id = ?";

    private final String updatePasswordQuery = "UPDATE " + userTable + " SET password = ?,last_updated_on = UTC_TIMESTAMP() WHERE user_link_id = ?";

    private final String enableUserQuery = "UPDATE " + userTable + " SET active = ?, last_updated_on = UTC_TIMESTAMP(), last_updated_by = ? WHERE user_link_id = ?";

    private final String disableUserQuery = "UPDATE " + userTable + " SET active = ?, last_updated_on = UTC_TIMESTAMP(), last_updated_by = ? WHERE user_link_id = ?";

    private final String enableNewUserQuery = "UPDATE " + userTable + " SET new_user = ? WHERE user_link_id = ?";
    private final String disableNewUserQuery = "UPDATE " + userTable + " SET new_user = ? WHERE user_link_id = ?";


    //retrive records

    private final String findUserByUserLinkId = "select * from " + userTable + " WHERE user_link_id = ?";

    private final String findUserByEmail = "select * from " + userTable + " WHERE email = ?";

    private final String findUserByMobile = "select * from " + userTable + " WHERE mobile = ?";

    private final String findUserByLastname = "select * from " + userTable + " WHERE last_name like ?";

    private final String findUserByUsername = "select * from " + userTable + " WHERE login = ?";

    private final String findAllUsersByUserLinkId = "select * from " + userTable + " WHERE user_org = ? and added_by = ? and deleted != 1";

    private final String findUserLinkIdByUsernameQuery = "SELECT user_link_id  FROM " + userTable + " WHERE login = ?";

    private final String getPasswordHashByUsernameQuery = "SELECT password FROM " + userTable + " WHERE login = ?";

    private final String findOrganizationIdByUserLinkId = "SELECT user_org FROM " + userTable + " WHERE user_link_id = ?";

    private final String findUsersByOrgId = "SELECT * FROM " + userTable + " WHERE user_org = ?";

    //delete records
    private final String softDeleteUserByUserLinkIdQuery = "UPDATE " + userTable + " SET deleted = 1, deleted_on = UTC_TIMESTAMP(), deleted_by = ? WHERE user_link_id = ?";
    private final String hardDeleteUserByUserLinkIdQuery = "DELETE FROM " + userTable + " WHERE user_link_id = ?";


    @Override
    public Boolean create(User user) {

        Object[] userDetails = new Object[]{
                user.getLogin().toLowerCase(),
                user.getPassword(),
                user.getFirstName().toLowerCase(),
                user.getLastName().toLowerCase(),
                user.getUserOrg(),
                user.getEmail().toLowerCase(),
                user.getMobile(),
                user.getActive(),
                user.getNewUser(),
                user.getUserLinkId(),
                user.getAddedBy(),
                user.getLastUpdatedBy()
        };

        return this.jdbcTemplate.update(createUserQuery, userDetails) > 0;
    }

    @Override
    public User findUserByEmail(String email) {
        try {
            return this.jdbcTemplate.queryForObject(findUserByEmail, new Object[]{email}, rowMapper);
        } catch (EmptyResultDataAccessException exp) {
            return null;
        }
    }

    @Override
    public User findUserByUsername(String username) throws IncorrectResultSizeDataAccessException {
        try {
            return this.jdbcTemplate.queryForObject(findUserByUsername, new Object[]{username}, rowMapper);
        } catch (EmptyResultDataAccessException exp) {
            return null;
        }
    }

    @Override
    public List<User> findUserByLastName(String lastName) {
        lastName = "%" + lastName.toLowerCase().trim() + "%";
        try {

            return this.jdbcTemplate.query(findUserByLastname, new Object[]{lastName}, rowMapper);
        } catch (EmptyResultDataAccessException exp) {
            return null;
        }
    }

    @Override
    public User findUserByUserLinkId(String userLinkId) throws IncorrectResultSizeDataAccessException {
        try {
            return this.jdbcTemplate.queryForObject(findUserByUserLinkId, new Object[]{userLinkId}, rowMapper);
        } catch (EmptyResultDataAccessException exp) {
            return null;
        }
    }

    @Override
    public User findUserByMobile(String mobile) {
        try {
            return this.jdbcTemplate.queryForObject(findUserByMobile, new Object[]{mobile}, rowMapper);
        } catch (EmptyResultDataAccessException exp) {
            return null;
        }
    }

    @Override
    public List<User> findUsersAddedByUserLinkId(String userLinkId) {
        try {
            String orgId = findOrgIdByUserLinkId(userLinkId);
            return this.jdbcTemplate.query(findAllUsersByUserLinkId, new Object[]{orgId, userLinkId}, rowMapper);
        } catch (EmptyResultDataAccessException exp) {
            return null;
        }
    }

    @Override
    public String findUserLinkIdByUsername(String username) {
        try {
            return this.jdbcTemplate.queryForObject(findUserLinkIdByUsernameQuery, new Object[]{username}, String.class);
        } catch (EmptyResultDataAccessException exp) {
            return null;
        }
    }

    @Override
    public String findOrgIdByUserLinkId(String userLinkId) {
        try {
            return this.jdbcTemplate.queryForObject(findOrganizationIdByUserLinkId, new Object[]{userLinkId}, String.class);
        } catch (EmptyResultDataAccessException exp) {
            return null;
        }
    }

    @Override
    public String getPasswordHashByUsername(String username) throws IncorrectResultSizeDataAccessException {
        Object[] queryParams = new Object[]{
                username
        };
        return this.jdbcTemplate.queryForObject(getPasswordHashByUsernameQuery, queryParams, String.class);
    }

    @Override
    public User updateUserDetails(User user, String userLinkId) throws IncorrectResultSizeDataAccessException {
        Object[] userDetails = new Object[]{
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getMobile(),
                user.getLastUpdatedBy(),
                userLinkId
        };
        this.jdbcTemplate.update(updateUserDetailsQuery, userDetails);
        return findUserByUserLinkId(userLinkId);
    }

    @Override
    public boolean updateUserUsername(String username, String userLinkId) {
        Object[] userDetails = new Object[]{
                username,
                userLinkId
        };
        return this.jdbcTemplate.update(updateUserUsernameQuery, userDetails) > 0;
    }

    @Override
    public boolean updateUserPassword(String password, String userLinkId) {
        String hashedPassword = passwordEncoder.encode(password);
        Object[] userDetails = new Object[]{
                hashedPassword,
                userLinkId
        };
        return this.jdbcTemplate.update(updatePasswordQuery, userDetails) > 0;
    }

    @Override
    public boolean softDeleteUserByUserLinkId(String userLinkId, String updatedBy) {
        Object[] userDetails = new Object[]{
                updatedBy,
                userLinkId
        };
        return this.jdbcTemplate.update(softDeleteUserByUserLinkIdQuery, userDetails) > 0;
    }

    @Override
    public boolean hardDeleteUserByUserLinkId(String userLinkId) {
        return this.jdbcTemplate.update(hardDeleteUserByUserLinkIdQuery, userLinkId) > 0;
    }

    @Override
    public boolean signUpUser(String userLinkId, String login, String password) {
        return updateUserPassword(password, userLinkId) &&
                updateUserUsername(login, userLinkId) &&
                activateUser(userLinkId, userLinkId) &&
                this.jdbcTemplate.update(disableNewUserQuery, 0, userLinkId) > 0;
    }

    @Override
    public boolean activateUser(String userLinkId, String updatedBy) {
        Object[] userDetails = new Object[]{
                1,
                updatedBy,
                userLinkId
        };
        return this.jdbcTemplate.update(enableUserQuery, userDetails) > 0;
    }

    @Override
    public boolean deactivateUser(String userLinkId, String updatedBy) {
        Object[] userDetails = new Object[]{
                0,
                updatedBy,
                userLinkId
        };
        return this.jdbcTemplate.update(disableUserQuery, userDetails) > 0;
    }
}
