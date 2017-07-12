package com.namodu.pustakam.service;

import com.namodu.pustakam.exception.NoPrivilegeException;
import com.namodu.pustakam.model.*;
import com.namodu.pustakam.repository.InvitationRepository;
import com.namodu.pustakam.repository.UserRepository;
import com.namodu.pustakam.repository.UserRoleRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by sanemdeepak on 11/20/16.
 */
@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final String CREATE_PERMISSION = "permission.efsr.AD.create";

    private final String EDIT_PERMISSION = "permission.efsr.AD.edit";

    private final String DELETE_PERMISSION = "permission.efsr.AD.delete";


    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final InvitationRepository invitationRepository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Autowired
    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, InvitationRepository invitationRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.invitationRepository = invitationRepository;
    }

    public Invitation createNewUser(RequestContext context, User user) {

        String requiredPermission = buildFullPermissionWithRole(CREATE_PERMISSION, user.getRole());
        String currentUserUserLinkId = context.getUserLinkId();
        String currentUserOrg = userRepository.findOrgIdByUserLinkId(currentUserUserLinkId);
        if (!userRoleRepository.hasPermission(requiredPermission, context.getUserLinkId())) {
            throw new NoPrivilegeException("Do no have required permission: " + requiredPermission);
        }
        //create all details required by user
        String tempHexPwd = passwordEncoder.encode(DigestUtils.md5Hex(UUID.randomUUID().toString()));
        String tempLogin = DigestUtils.md5Hex(UUID.randomUUID().toString());
        String userLinkId = UUID.randomUUID().toString();
        logger.info("Generated user-link-id: " + userLinkId);

        try {
            //new user attributes
            user.setActive(0);
            user.setNewUser(1);
            user.setLogin(tempLogin);
            user.setPassword(tempHexPwd);
            user.setUserLinkId(userLinkId);
            user.setLastUpdatedBy(currentUserUserLinkId);
            user.setAddedBy(currentUserUserLinkId);
            user.setUserOrg(currentUserOrg);

            //create user
            if (!userRepository.create(user)) {
                return null;
            }

            //if request has role
            Optional<Role> userRole = Optional.ofNullable(user.getRole());
            Optional<List<Permission>> permissions = Optional.ofNullable(user.getPermissions());

            if (userRole.isPresent()) {
                logger.info("role: " + userRole.get().toString());
                Role role = new Role(userRole.get().getName());
                if (!userRoleRepository.createRoleForUser(user, role)) {
                    return null;
                }
            }

            //if request has permissions
            if (permissions.isPresent()) {
                if (!userRoleRepository.hasAllPermission(user.getPermissions(), context.getUserLinkId())) {
                    throw new NoPrivilegeException("No permission to grant certain permissions");
                }
                if (!userRoleRepository.createPermissionForUser(user.getUserLinkId(), user.getPermissions())) {
                    return null;
                }
            }

            //Generate invitation
            if (!invitationRepository.createNewInvitation(user.getUserLinkId())) {
                return null;
            }

            return invitationRepository.findActiveInvitationByUserLinkId(user.getUserLinkId());
        } catch (Exception exp) {
            //delete all data if something goes wrong
            logger.error("Error creating user: " + exp.getMessage());
            userRepository.hardDeleteUserByUserLinkId(userLinkId);
            userRoleRepository.deleteAllRolesForUser(userLinkId);
            userRoleRepository.deleteAllPermissionsForUser(userLinkId);
            invitationRepository.deleteInvitation(userLinkId);
            throw exp;
        }
    }

    public User findUserByEmail(RequestContext context, String email) {
        return userRepository.findUserByEmail(email);
    }

    public List<User> findUserByLastName(RequestContext context, String lastName) {
        return userRepository.findUserByLastName(lastName);
    }

    public User findUserByMobile(RequestContext context, String mobile) {
        return userRepository.findUserByMobile(mobile);
    }

    public User findUserByUserLinkId(RequestContext context, String userLinkId) {
        userRepository.findUserByUserLinkId(userLinkId);
        User user = userRepository.findUserByUserLinkId(userLinkId);
        Role r = userRoleRepository.getRoleByUserLinkId(userLinkId);
        if (user != null) {
            user.setRole(r);
        }
        return user;
    }

    public List<User> findUsersAddedByUserLinkId(RequestContext context, String userLinkId) {
        List<User> user = userRepository.findUsersAddedByUserLinkId(userLinkId);
        return user;
    }

    public String findUserLinkIdByUsername(RequestContext context, String username) {
        return userRepository.findUserLinkIdByUsername(username);
    }

    public String findOrgIdByUserLinkId(RequestContext context, String userLinkId) {
        return userRepository.findOrgIdByUserLinkId(userLinkId);
    }

    public String getPasswordHashByUsername(RequestContext context, String username) {
        return userRepository.getPasswordHashByUsername(username);
    }

    public User updateUserDetails(RequestContext context, User user, String userLinkId) {
        if (!context.getUserLinkId().equalsIgnoreCase(userLinkId)) { // no restriction to update self details
            Role role = userRoleRepository.getRoleByUserLinkId(userLinkId);
            String requiredPermission = buildFullPermissionWithRole(EDIT_PERMISSION, role);
            if (!userRoleRepository.hasPermission(requiredPermission, context.getUserLinkId())) {
                throw new NoPrivilegeException("Do not have required permission: " + requiredPermission);
            }
        }
        user.setLastUpdatedBy(context.getUserLinkId());
        return userRepository.updateUserDetails(user, userLinkId);
    }

    public boolean updateUserUsername(RequestContext context, String username, String userLinkId) {
        return userRepository.updateUserUsername(username, userLinkId);
    }

    public boolean updateUserPassword(RequestContext context, String password, String userLinkId) {
        return userRepository.updateUserPassword(password, userLinkId);
    }

    public boolean activateUser(RequestContext context, String userLinkId) {
        Role role = userRoleRepository.getRoleByUserLinkId(userLinkId);
        String requiredPermission = buildFullPermissionWithRole(EDIT_PERMISSION, role);
        if (!userRoleRepository.hasPermission(requiredPermission, context.getUserLinkId())) {
            throw new NoPrivilegeException("Do not have required permission: " + requiredPermission);
        }
        return userRepository.activateUser(userLinkId, context.getUserLinkId());
    }

    public boolean deActivateUser(RequestContext context, String userLinkId) {
        Role role = userRoleRepository.getRoleByUserLinkId(userLinkId);
        String requiredPermission = buildFullPermissionWithRole(EDIT_PERMISSION, role);
        if (!userRoleRepository.hasPermission(requiredPermission, context.getUserLinkId())) {
            throw new NoPrivilegeException("Do not have required permission: " + requiredPermission);
        }
        return userRepository.deactivateUser(userLinkId, context.getUserLinkId());
    }

    public boolean deleteUserByUserLinkId(RequestContext context, String userLinkId) {
        Role role = userRoleRepository.getRoleByUserLinkId(userLinkId);
        String requiredPermission = buildFullPermissionWithRole(DELETE_PERMISSION, role);
        if (!userRoleRepository.hasPermission(requiredPermission, context.getUserLinkId())) {
            throw new NoPrivilegeException("Do not have required permission: " + requiredPermission);
        }
        return userRepository.softDeleteUserByUserLinkId(userLinkId, context.getUserLinkId());
    }

    public boolean signUpUser(RequestContext context, String userLinkId, String login, String password) {
        return userRepository.signUpUser(userLinkId, login, password);
    }

    //helpers

    protected String buildFullPermissionWithRole(String basePermission, Role role) {
        if (role.getName() == null) {
            return null;
        }
        String requiresPermission = basePermission;

        String[] strings = role.getName().split("_");
        for (String s : strings) {
            requiresPermission = requiresPermission + "." + s;

        }
        return requiresPermission.toLowerCase();
    }

}
