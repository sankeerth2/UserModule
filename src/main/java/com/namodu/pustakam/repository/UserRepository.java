package com.namodu.pustakam.repository;

import com.namodu.pustakam.model.Invitation;
import com.namodu.pustakam.model.Permission;
import com.namodu.pustakam.model.RequestContext;
import com.namodu.pustakam.model.User;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.List;

/**
 * Created by sanemdeepak on 9/26/16.
 */
public interface UserRepository {

    public Boolean create(User user);

    public User findUserByEmail(String email);

    public User findUserByUsername(String username);

    public List<User> findUserByLastName(String lastName);

    public User findUserByUserLinkId(String userLinkId);

    public User findUserByMobile(String mobile);

    public List<User> findUsersAddedByUserLinkId(String userLinkId);

    public String findUserLinkIdByUsername(String username);

    public String findOrgIdByUserLinkId(String userLinkId);

    public String getPasswordHashByUsername(String username) throws IncorrectResultSizeDataAccessException;

    public User updateUserDetails(User user, String userLinkId) throws IncorrectResultSizeDataAccessException;

    public boolean updateUserUsername(String username, String userLinkId) throws IncorrectResultSizeDataAccessException;

    public boolean updateUserPassword(String password, String userLinkId) throws IncorrectResultSizeDataAccessException;

    public boolean softDeleteUserByUserLinkId(String userLinkId, String updatedBy);

    public boolean hardDeleteUserByUserLinkId(String userLinkId);

    public boolean signUpUser(String userLinkId, String login, String password);

    public boolean activateUser(String userLinkId, String updatedBy);

    public boolean deactivateUser(String userLinkId, String updatedBy);
}
