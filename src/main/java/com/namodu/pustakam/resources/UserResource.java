package com.namodu.pustakam.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namodu.pustakam.exception.NoPrivilegeException;
import com.namodu.pustakam.model.Invitation;
import com.namodu.pustakam.model.Permission;
import com.namodu.pustakam.model.RequestContext;
import com.namodu.pustakam.model.User;
import com.namodu.pustakam.service.UserRoleService;
import com.namodu.pustakam.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

/**
 * Created by sanemdeepak on 9/27/16.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger logger = LoggerFactory.getLogger(UserResource.class);


    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    ObjectMapper mapper;


    @RequestMapping(value = "/current/profile", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> getCurrentlyLoggedInUser() throws IOException {
        RequestContext context = (RequestContext) SecurityContextHolder.getContext().getAuthentication();
        Optional<User> maybeUser = Optional.ofNullable(userService.findUserByUserLinkId(context, context.getUserLinkId()));
        List<Permission> permissions = userRoleService.getPermissionsByUserLinkId(context, context.getUserLinkId());
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            user.setPermissions(permissions);
            String newUser = mapper.writeValueAsString(maybeUser.get());
            return new ResponseEntity<>(newUser, HttpStatus.OK);
        } else {
            logger.error("correlation-id: " + context.getCorrelationId() + " Authenticated users' user_link_id not found: " + context.getUserLinkId());
            return new ResponseEntity<>("BOOM", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/current/users", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> usersCreatedByLoggedInUser() throws IOException {
        RequestContext context = (RequestContext) SecurityContextHolder.getContext().getAuthentication();
        String userLinkId = context.getUserLinkId();
        List<User> users = userService.findUsersAddedByUserLinkId(context, userLinkId);
        final StringWriter sw = new StringWriter();
        mapper.writer().withRootName("users").writeValue(sw, users);
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/new/user", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> createNewUser(@RequestBody @Valid User maybeUser) throws JsonProcessingException {
        RequestContext context = (RequestContext) SecurityContextHolder.getContext().getAuthentication();
        ResponseEntity<String> responseEntity = null;
        try {
            String newUser;
            Invitation newUserInvitation = userService.createNewUser(context, maybeUser);
            newUser = mapper.writeValueAsString(newUserInvitation);
            if (newUserInvitation != null) {
                responseEntity = new ResponseEntity<>(newUser, HttpStatus.CREATED);
            } else {
                responseEntity = new ResponseEntity<>("Invalid Data", HttpStatus.BAD_REQUEST);
            }
        } catch (NoPrivilegeException npe) {
            responseEntity = new ResponseEntity<>(npe.getMessage(), HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/users/{userlinkid}/userlinkid", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> findUsersAddedByUserLinkId(@PathVariable("userlinkid") String userLinkId) throws IOException {

        RequestContext context = (RequestContext) SecurityContextHolder.getContext().getAuthentication();

        List<User> users = userService.findUsersAddedByUserLinkId(context, userLinkId);
        final StringWriter sw = new StringWriter();
        mapper.writeValue(sw, users);
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{userlinkid}/userlinkid", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> updateUserDetails(@RequestBody @Valid User maybeUserArg,
                                                    @PathVariable("userlinkid") String userLinkId)
            throws JsonProcessingException {
        RequestContext context = (RequestContext) SecurityContextHolder.getContext().getAuthentication();

        try {
            Optional<User> maybeUser = Optional.ofNullable(userService.updateUserDetails(context, maybeUserArg, userLinkId));
            if (maybeUser.isPresent()) {
                String updatedUser = mapper.writeValueAsString(maybeUser.get());
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("{\"message\":\"Cannot update user - user does not exist\"}", HttpStatus.NOT_FOUND);
            }
        } catch (IncorrectResultSizeDataAccessException ex) {
            logger.error("Duplicate user_link_id found, user_link_id: " + userLinkId);
            return new ResponseEntity<>("{\"message\":\"Cannot update user - corrupt data\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoPrivilegeException npe) {
            return new ResponseEntity<>("{\"message\":\"action not allowed\"}", HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(value = "/permissions/{userlinkid}/userlinkid", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> updateUserPermissions(@RequestBody @Valid List<Permission> maybePermissionses,
                                                        @PathVariable("userlinkid") String userLinkId) {

        RequestContext context = (RequestContext) SecurityContextHolder.getContext().getAuthentication();

        try {
            userRoleService.deleteAllPermissionsForUser(context, userLinkId);
            userRoleService.addPermissiontoUser(context, userLinkId, maybePermissionses);
            return new ResponseEntity<>("{\"message\":\"action successful\"}", HttpStatus.OK);
        } catch (EmptyResultDataAccessException ex) {
            return new ResponseEntity<>("{\"message\":\"Cannot update user - user does not exist\"}", HttpStatus.NOT_FOUND);
        } catch (IncorrectResultSizeDataAccessException ex) {
            logger.error("Duplicate user_link_id found, user_link_id: " + userLinkId);
            return new ResponseEntity<>("{\"message\":\"Cannot update user - corrupt data\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/users/{userlinkid}/userlinkid/activate", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> activateUser(@PathVariable("userlinkid") String userLinkId) {
        RequestContext context = (RequestContext) SecurityContextHolder.getContext().getAuthentication();
        if (userService.activateUser(context, userLinkId)) {
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/users/{userlinkid}/userlinkid/deactivate", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> deActivateUser(@PathVariable("userlinkid") String userLinkId) {
        RequestContext context = (RequestContext) SecurityContextHolder.getContext().getAuthentication();
        if (userService.deActivateUser(context, userLinkId)) {
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/profile/{userlinkid}/userlinkid", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> findUserByUserLinkId(@PathVariable("userlinkid") String userLinkId) throws IOException {

        RequestContext context = (RequestContext) SecurityContextHolder.getContext().getAuthentication();
        ResponseEntity<String> responseEntity = null;
        Optional<User> maybeUser = Optional.ofNullable(userService.findUserByUserLinkId(context, userLinkId));
        if (maybeUser.isPresent()) {
            String user = mapper.writeValueAsString(maybeUser.get());
            responseEntity = new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/search/{email}/email", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> findUserByEmail(@PathVariable("email") String email) throws IOException {
        RequestContext context = (RequestContext) SecurityContextHolder.getContext().getAuthentication();
        String basePermission = "permission.efsr.view";
        ResponseEntity<String> responseEntity = null;
        Optional<User> maybeUser = Optional.ofNullable(userService.findUserByEmail(context, email));
        if (maybeUser.isPresent()) {
            String user = mapper.writeValueAsString(maybeUser.get());
            responseEntity = new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>("No user found with email " + email, HttpStatus.OK);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/search/{mobile}/mobile", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> findUserByMobile(@PathVariable("mobile") String mobile) throws IOException {
        RequestContext context = (RequestContext) SecurityContextHolder.getContext().getAuthentication();
        String basePermission = "permission.efsr.view";
        ResponseEntity<String> responseEntity = null;
        Optional<User> maybeUser = Optional.ofNullable(userService.findUserByMobile(context, mobile));
        if (maybeUser.isPresent()) {
            final StringWriter sw = new StringWriter();
            mapper.writer().withRootName("users").writeValue(sw, maybeUser.get());
            responseEntity = new ResponseEntity<>(sw.toString(), HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>("No user found with mobile number " + mobile, HttpStatus.OK);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/search/{lastname}/lastname", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> findUsersByLastname(@PathVariable("lastname") String lastname) throws IOException {
        RequestContext context = (RequestContext) SecurityContextHolder.getContext().getAuthentication();
        String basePermission = "permission.efsr.view";
        List<User> users = userService.findUserByLastName(context, lastname);
        final StringWriter sw = new StringWriter();
        mapper.writer().withRootName("users").writeValue(sw, users);
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/search/permissions/{userlinkid}/userlinkid", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> findPermissionsByUserLinkId(@PathVariable("userlinkid") String userLinkId) throws IOException {
        RequestContext context = (RequestContext) SecurityContextHolder.getContext().getAuthentication();
        List<Permission> permissions = userRoleService.getPermissionsByUserLinkId(context, userLinkId);
        final StringWriter sw = new StringWriter();
        mapper.writer().withRootName("permissions").writeValue(sw, permissions);
        return new ResponseEntity<>(sw.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{userlinkid}/userlinkid/delete", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> deleteUserByUserLinkId(@PathVariable("userlinkid") String userLinkId) throws IOException {
        RequestContext context = (RequestContext) SecurityContextHolder.getContext().getAuthentication();
        if (userService.deleteUserByUserLinkId(context, userLinkId)) {
            return new ResponseEntity<>("{\"message\":\"User deletion successful\"}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{\"message\":\"User deletion failure\"}", HttpStatus.BAD_REQUEST);
        }
    }
}
