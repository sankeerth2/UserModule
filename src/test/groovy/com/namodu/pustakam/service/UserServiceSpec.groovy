package com.namodu.pustakam.service

import com.namodu.pustakam.exception.NoPrivilegeException
import com.namodu.pustakam.model.*
import com.namodu.pustakam.repository.InvitationRepository
import com.namodu.pustakam.repository.UserRepository
import com.namodu.pustakam.repository.UserRoleRepository
import spock.lang.Specification

/**
 * Created by sanemdeepak on 11/20/16.
 */
class UserServiceSpec extends Specification {

    private final String FIRST_NAME = "FIRST_NAME"
    private final String LAST_NAME = "LAST_NAME"
    private final String MOBILE = "99999999"
    private final String EMAIL = "EMAIL@EMAIL.ME"

    private final String RANDOM_ID = "643fcaa3-bb2a-43a6-b39f-49a1def7e19b"

    UserRepository userRepository
    UserRoleRepository userRoleRepository
    InvitationRepository invitationRepository
    UserService userService

    def setup() {
        userRepository = Mock(UserRepository)
        userRoleRepository = Mock(UserRoleRepository)
        invitationRepository = Mock(InvitationRepository)
    }


    def "when userRepository.create(_) returns false createNewUser returns null"() {
        setup:
        Role role = new Role("ADMIN")
        User user = new User(FIRST_NAME, LAST_NAME, EMAIL, MOBILE);
        user.setRole(role)
        RequestContext context = Mock(RequestContext)
        Invitation invitation;
        userRepository.create(_) >> false
        userRoleRepository.hasPermission(_, _) >> true
        userService = new UserService(userRepository, userRoleRepository, invitationRepository);

        when:
        invitation = userService.createNewUser(context, user)

        then:
        invitation == null
    }

    def "when userRoleRepository.createRoleForUsers(_, _) method return false createNewUser returns null"() {
        setup:
        Role role = new Role("ADMIN")
        User user = new User(FIRST_NAME, LAST_NAME, EMAIL, MOBILE);
        user.setRole(role)
        RequestContext context = Mock(RequestContext)
        Invitation invitation;
        userRoleRepository.createRoleForUser(_, _) >> false
        userRoleRepository.hasPermission(_, _) >> true
        userService = new UserService(userRepository, userRoleRepository, invitationRepository);

        when:
        invitation = userService.createNewUser(context, user)

        then:
        1 * userRepository.create(_) >> true
        invitation == null
    }

    def "if user do not have required permission to create a specific user type NoPrivilegeException is thrown"() {
        setup:
        Role role = new Role("ADMIN")
        User user = new User(FIRST_NAME, LAST_NAME, EMAIL, MOBILE);
        user.setRole(role)
        RequestContext context = Mock(RequestContext)
        Invitation invitation;
        userRoleRepository.hasPermission(_, _) >> false
        userService = new UserService(userRepository, userRoleRepository, invitationRepository);

        when:
        userService.createNewUser(context, user)

        then:
        thrown(NoPrivilegeException)
    }

    def "if user do not have all permissions he is trying to create user with NoPrivilegeException is thrown"() {
        setup:
        Role role = new Role("ADMIN")
        List<Permission> permissions = Mock(List)
        User user = new User(FIRST_NAME, LAST_NAME, EMAIL, MOBILE);
        user.setRole(role)
        user.setPermissions(permissions)
        RequestContext context = Mock(RequestContext)
        userRoleRepository.hasPermission(_, _) >> true
        userRoleRepository.createRoleForUser(_, _) >> true
        userRoleRepository.hasAllPermission(_, _) >> false
        userRepository.create(_) >> true
        userService = new UserService(userRepository, userRoleRepository, invitationRepository);

        when:
        userService.createNewUser(context, user)

        then:
        thrown(NoPrivilegeException)
    }

    def "if any of the repository call throws exception, exception is caught and rollbacks will be called"() {
        setup:
        Role role = new Role("ADMIN")
        User user = new User(FIRST_NAME, LAST_NAME, EMAIL, MOBILE);
        user.setRole(role)
        RequestContext context = Mock(RequestContext)
        Invitation invitation;
        userRoleRepository.hasPermission(_, _) >> true
        userRepository.create(_) >> { throw new Exception() }
        userService = new UserService(userRepository, userRoleRepository, invitationRepository);

        when:
        userService.createNewUser(context, user)

        then:
        thrown(Exception)
        1 * userRepository.hardDeleteUserByUserLinkId(_)
    }

    def "if all repository calls are successful invitation is created"() {
        setup:
        Role role = new Role("ADMIN")
        User user = new User(FIRST_NAME, LAST_NAME, EMAIL, MOBILE);
        user.setRole(role)
        RequestContext context = Mock(RequestContext)
        Invitation invitation = Mock(Invitation)
        invitation.getInvitationId() >> RANDOM_ID
        userRoleRepository.hasPermission(_, _) >> true
        userRepository.create(_) >> true
        userRoleRepository.createRoleForUser(_, _) >> true
        invitationRepository.createNewInvitation(_) >> true
        invitationRepository.findActiveInvitationByUserLinkId(_) >> invitation
        userService = new UserService(userRepository, userRoleRepository, invitationRepository);

        when:
        invitation = userService.createNewUser(context, user)

        then:
        invitation.getInvitationId() == RANDOM_ID
    }
}
