package com.namodu.pustakam.repository;

import com.namodu.pustakam.model.Permission;
import com.namodu.pustakam.model.Role;
import com.namodu.pustakam.model.User;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.List;
import java.util.Set;

/**
 * Created by sanemdeepak on 10/11/16.
 */
public interface UserRoleRepository {

    public List<Permission> getPermissionsByRoleId(int roleId);

    public Integer getRoleIdByRole(String roleName);

    public List<Permission> getPermissionsByUserLinkId(String userLinkId);

    public Boolean hasPermission(String permission, String userLinkId);

    public Boolean hasAllPermission(List<Permission> permissions, String userLinkId);

    public List<User> getUsersByRole(String role);

    public Set<User> getUsersByRoleId(Integer roleId);

    public Role getRoleByUserLinkId(String userLinkId);

    public boolean createRoleForUser(User user, Role role);

    public boolean createPermissionForUser(String userLinkId, List<Permission> permissions);

    public User updateUserRole(List<Role> roles);

    public boolean addPermissiontoUser(String userLinkId, List<Permission> permissions) throws IncorrectResultSizeDataAccessException;

    public boolean updateUserPermissions(String userLinkId, List<Permission> permissions) throws IncorrectResultSizeDataAccessException;

    public boolean deleteAllRolesForUser(String userLinkId);

    public boolean deleteRoleForUser(String userLinkId, String role);

    public boolean deleteAllPermissionsForUser(String userLinkId);

    public boolean deletePermissionForUser(String userLinkId, Permission permission);

    public boolean isPermissionExists(Permission permission);
}
