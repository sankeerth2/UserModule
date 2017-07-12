package com.namodu.pustakam.repository.impl;

import com.namodu.pustakam.model.Permission;
import com.namodu.pustakam.model.Role;
import com.namodu.pustakam.model.User;
import com.namodu.pustakam.repository.UserRepository;
import com.namodu.pustakam.repository.UserRoleRepository;
import com.namodu.pustakam.utilities.rowMappers.PermissionMapper;
import com.namodu.pustakam.utilities.rowMappers.RoleMapper;
import com.namodu.pustakam.utilities.rowMappers.UserPermissionMapper;
import com.namodu.pustakam.utilities.rowMappers.UserRoleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by sanemdeepak on 10/11/16.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Service
public class UserRoleRepositoryImpl implements UserRoleRepository {

    private final Logger logger = LoggerFactory.getLogger(UserRoleRepositoryImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PermissionMapper permissionMapper;


    @Autowired
    UserRepository userRepository;

    private final String roleTable = "EFSR_Roles";
    private final String permissionsTable = "EFSR_Role_Permissions";
    private final String userRoleTable = "EFSR_User_Role";
    private final String userPermissionTable = "EFSR_User_Permissions";

    //gets
    private final String findRoleIdByRoleName = "SELECT * FROM " + roleTable + " WHERE name = ?";
    private final String findPermissionsByRoleId = "SELECT * FROM " + permissionsTable + " WHERE role_id = ?";
    private final String findUserLinkIdsByRoleId = "SELECT user_link_id FROM " + userRoleTable + " WHERE role_id = ?";
    private final String findRoleByUserLinkId = "SELECT role FROM " + userRoleTable + " WHERE user_link_id = ?";

    private final String findPermissionsByUserLinkId = "SELECT * FROM " + userPermissionTable + " WHERE user_link_id = ? group by permission";

    private final String isPermissionExist = "SELECT count(*) count FROM " + permissionsTable + " WHERE permission = ?";

    //creates
    private final String createRoleForUser = "INSERT INTO " + userRoleTable + "(`user_link_id`, `role`, `role_id`) VALUES(?,?,?)";
    private final String createPermissionForUser = "INSERT INTO " + userPermissionTable + "(`user_link_id`, `permission`, `allowed`) VALUES(?,?,?)";


    //delets
    private final String deleteAllRolesForUser = "DELETE FROM " + userRoleTable + " WHERE user_link_id = ?";
    private final String deleteRoleForUser = "DELETE FROM " + userRoleTable + " WHERE = user_link_id = ? AND role = ?";
    private final String deleteAllPermissionsForUser = "DELETE FROM " + userPermissionTable + " WHERE user_link_id = ?";
    private final String deletePermissionForUser = "DELETE FROM " + userPermissionTable + "WHERE user_link_id = ? AND permission =?";

    private final String checkUserForPermissionQuery = "SELECT count(*) count from " + userPermissionTable + " WHERE permission = ? and user_link_id = ?";

    //other quries
    private final String enablePermissionForUser = "UPDATE TABLE " + userPermissionTable + " SET allowed = 1 WHERE user_link_id = ?";
    private final String disablePermissionForUser = "UPDATE TABLE " + userPermissionTable + " SET allowed = 0 WHERE user_link_id = ?";

    @Override
    public List<Permission> getPermissionsByRoleId(int roleId) {
        List<Permission> permissions = this.jdbcTemplate.query(findPermissionsByRoleId, new Object[]{roleId}, permissionMapper);
        return permissions;
    }

    @Override
    public Integer getRoleIdByRole(String roleName) {
        Role role = this.jdbcTemplate.queryForObject(findRoleIdByRoleName, new Object[]{roleName}, roleMapper);
        return role.getId();
    }

    @Override
    public List<Permission> getPermissionsByUserLinkId(String userLinkId) {
        List<Permission> permissions = this.jdbcTemplate.query(findPermissionsByUserLinkId, new Object[]{userLinkId}, new UserPermissionMapper());
        return permissions;
    }

    @Override
    public Boolean hasPermission(String permission, String userLinkId) {
        List<Long> count = this.jdbcTemplate.query(checkUserForPermissionQuery, new Object[]{permission, userLinkId},
                (rs, rowNum) -> rs.getLong("count"));
        return count.get(0) > 0;
    }

    @Override
    public Boolean hasAllPermission(List<Permission> permissions, String userLinkId) {
        List<Permission> currentUserPermissions = getPermissionsByUserLinkId(userLinkId);
        return currentUserPermissions.containsAll(permissions);

    }

    @Override
    public List<User> getUsersByRole(String role) {
        return null;
    }

    @Override
    public Set<User> getUsersByRoleId(Integer roleId) {
//        List<UserRole> userRoles = jdbcTemplate.query(findUserLinkIdsByRoleId, new Object[]{roleId}, userRoleMapper);
//        //Because we don't want to return duplicate values
//        Set<User> users = userRoles.stream().map(userRole -> userRepository.findUserByUserLinkId(userRole.getUserLinkId()))
//                .collect(Collectors.toSet());
        return null;
    }

    @Override
    public Role getRoleByUserLinkId(String userLinkId) {
        try {
            return jdbcTemplate.queryForObject(findRoleByUserLinkId, new Object[]{userLinkId}, (rs, rowNum) -> new Role(rs.getString("role")));
        }
        catch(EmptyResultDataAccessException exp){
            return null;
        }
    }

    @Override
    public boolean createRoleForUser(User user, Role role) {
        String userLinkId = user.getUserLinkId();
        String userRoleName = role.getName();
        int userRoleId = getRoleIdByRole(userRoleName);
        List<Permission> permissions = getPermissionsByRoleId(userRoleId);
        return this.jdbcTemplate.update(createRoleForUser, userLinkId, userRoleName, userRoleId) > 0
                && createPermissionForUser(userLinkId, permissions);
    }

    @Override
    public boolean createPermissionForUser(String userLinkId, List<Permission> permissions) {
        try {
            for (Permission permission : permissions) {
                if (isPermissionExists(permission)) {
                    this.jdbcTemplate.update(createPermissionForUser, userLinkId, permission.getPermission(), 1);
                } else {
                    logger.warn("Unexpected Permission: " + permission.getPermission());
                    return false;
                }
            }
            return true;
        } catch (Exception exp) {
            logger.warn("Failed To create permissions for user: " + exp.getMessage());
            return false;
        }
    }

    @Override
    public User updateUserRole(List<Role> roles) {
        return null;
    }

    @Override
    public boolean deleteAllRolesForUser(String userLinkId) {
        try {
            return this.jdbcTemplate.update(deleteAllRolesForUser, userLinkId) > 0;
        } catch (Exception exp) {
            return false;
        }

    }

    @Override
    public boolean addPermissiontoUser(String userLinkId, List<Permission> permissions) throws IncorrectResultSizeDataAccessException {
        try {
            return this.createPermissionForUser(userLinkId, permissions);
        } catch (EmptyResultDataAccessException exp) {
            logger.error("Error Updating permissions for user:" + userLinkId);
            return false;
        }
    }

    @Override
    public boolean updateUserPermissions(String userLinkId, List<Permission> permissions) throws IncorrectResultSizeDataAccessException {
        try {
            return this.createPermissionForUser(userLinkId, permissions);
        } catch (EmptyResultDataAccessException exp) {
            logger.error("Error Updating permissions for user:" + userLinkId);
            return false;
        }
    }

    @Override
    public boolean deleteRoleForUser(String userLinkId, String role) {
        try {
            return this.jdbcTemplate.update(deleteRoleForUser, userLinkId, role) > 0;
        } catch (Exception exp) {
            return false;
        }
    }

    @Override
    public boolean deleteAllPermissionsForUser(String userLinkId) {
        try {
            return this.jdbcTemplate.update(deleteAllPermissionsForUser, userLinkId) > 0;
        } catch (Exception exp) {
            return false;
        }
    }

    @Override
    public boolean deletePermissionForUser(String userLinkId, Permission permission) {
        try {
            return this.jdbcTemplate.update(deletePermissionForUser, userLinkId, permission.getPermission()) > 0;
        } catch (Exception exp) {
            return false;
        }
    }

    @Override
    public boolean isPermissionExists(Permission permission) {
        List<Long> count = this.jdbcTemplate.query(isPermissionExist, new Object[]{permission.getPermission()},
                (rs, rowNum) -> rs.getLong("count"));
        return count.get(0) > 0;
    }
}
