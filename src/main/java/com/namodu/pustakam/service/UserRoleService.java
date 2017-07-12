package com.namodu.pustakam.service;

import com.namodu.pustakam.exception.NoPrivilegeException;
import com.namodu.pustakam.model.Permission;
import com.namodu.pustakam.model.RequestContext;
import com.namodu.pustakam.model.Role;
import com.namodu.pustakam.model.User;
import com.namodu.pustakam.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by sanemdeepak on 11/20/16.
 */
@Service
public class UserRoleService {
    private final Logger logger = LoggerFactory.getLogger(UserRoleService.class);


    private final String EDIT_PERMISSION = "permission.efsr.AD.edit";

    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }


    public List<Permission> getPermissionsByRoleId(RequestContext context, int roleId) {
        return userRoleRepository.getPermissionsByRoleId(roleId);
    }

    public Integer getRoleIdByRole(RequestContext context, String roleName) {
        return userRoleRepository.getRoleIdByRole(roleName);
    }

    public List<Permission> getPermissionsByUserLinkId(RequestContext context, String userLinkId) {
        return userRoleRepository.getPermissionsByUserLinkId(userLinkId);
    }

    public Boolean hasPermission(RequestContext context, String permission, String userLinkId) {
        return userRoleRepository.hasPermission(permission, userLinkId);
    }

    public Boolean hasAllPermission(RequestContext context, List<Permission> permissions, String userLinkId) {
        return userRoleRepository.hasAllPermission(permissions, userLinkId);
    }

    public List<User> getUsersByRole(RequestContext context, String role) {
        return userRoleRepository.getUsersByRole(role);
    }

    public Set<User> getUsersByRoleId(RequestContext context, Integer roleId) {
        return userRoleRepository.getUsersByRoleId(roleId);
    }

    public Role getRoleByUserLinkId(RequestContext context, String userLinkId) {
        return userRoleRepository.getRoleByUserLinkId(userLinkId);
    }

    public boolean createRoleForUser(RequestContext context, User user, Role role) {
        return userRoleRepository.createRoleForUser(user, role);
    }

    public boolean createPermissionForUser(RequestContext context, String userLinkId, List<Permission> permissions) {
        return userRoleRepository.createPermissionForUser(userLinkId, permissions);
    }

    public User updateUserRole(RequestContext context, List<Role> roles) {
        return userRoleRepository.updateUserRole(roles);
    }

    public void addPermissiontoUser(RequestContext context, String userLinkId, List<Permission> permissions)
            throws IncorrectResultSizeDataAccessException {
        Optional<Role> maybeRole = Optional.ofNullable(userRoleRepository.getRoleByUserLinkId(userLinkId));
        if (maybeRole.isPresent()) {
            String requiredPermission = buildFullPermissionWithRole(EDIT_PERMISSION, maybeRole.get().getName());

            if (!userRoleRepository.hasPermission(requiredPermission, context.getUserLinkId()) ||
                    !userRoleRepository.hasAllPermission(permissions, context.getUserLinkId())) {

                throw new NoPrivilegeException("No Privileges");
            }
            userRoleRepository.addPermissiontoUser(userLinkId, permissions);
        }
    }


    public boolean deleteAllRolesForUser(RequestContext context, String userLinkId) {
        return userRoleRepository.deleteAllRolesForUser(userLinkId);
    }

    public boolean deleteRoleForUser(RequestContext context, String userLinkId, String role) {
        return userRoleRepository.deleteRoleForUser(userLinkId, role);
    }

    public boolean deleteAllPermissionsForUser(RequestContext context, String userLinkId) {
        return userRoleRepository.deleteAllPermissionsForUser(userLinkId);
    }

    public boolean deletePermissionForUser(RequestContext context, String userLinkId, Permission permission) {
        return userRoleRepository.deletePermissionForUser(userLinkId, permission);

    }

    public boolean isPermissionExists(RequestContext context, Permission permission) {
        return userRoleRepository.isPermissionExists(permission);
    }

    private String buildFullPermissionWithRole(String basePermission, String roleName) {
        String requiresPermission = basePermission;

        String[] strings = roleName.split("_");
        for (String s : strings) {
            requiresPermission = requiresPermission + "." + s;

        }
        return requiresPermission;
    }
}
