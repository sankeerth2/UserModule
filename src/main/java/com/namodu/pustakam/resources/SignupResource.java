package com.namodu.pustakam.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namodu.pustakam.model.Invitation;
import com.namodu.pustakam.model.RequestContext;
import com.namodu.pustakam.model.User;
import com.namodu.pustakam.model.UserCredentials;
import com.namodu.pustakam.security.transfer.JwtUserDto;
import com.namodu.pustakam.security.util.JwtTokenGenerator;
import com.namodu.pustakam.service.InvitationService;
import com.namodu.pustakam.service.UserService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by sanemdeepak on 10/9/16.
 */
@RestController
@RequestMapping("/api")
public class SignupResource {

    private final Logger logger = LoggerFactory.getLogger(SignupResource.class);

    @Autowired
    private UserService userService;

    @Autowired
    private InvitationService invitationService;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    ObjectMapper mapper;

    @RequestMapping(
            value = "/signup/{invitation_id}",
            method = RequestMethod.POST, consumes = "application/json",
            produces = "application/json"
    )
    @ResponseBody
    public ResponseEntity<String> signUpUser(@RequestBody @Valid UserCredentials maybeUserCredentials,
                                             @PathVariable("invitation_id") String maybeInvitation_id) throws IOException {

        RequestContext context = new RequestContext();
        context.setCorrelationId(UUID.randomUUID().toString());
        context.setCorrelationId("EFSR-SERVICE");
        Invitation invitation = invitationService.findInvitationByInvitationId(maybeInvitation_id);
        Optional<Invitation> mayBeInvitation = Optional.ofNullable(invitation);

        if (mayBeInvitation.isPresent()) {
            invitation = mayBeInvitation.get();
        } else {
            return new ResponseEntity<>("Invalid invitation", HttpStatus.NOT_FOUND);
        }
        if (invitation.isUsable()) {
            Optional<User> user = Optional.ofNullable(userService.findUserByUserLinkId(context, invitation.getUserLinkId()));
            if (user.isPresent() && user.get().getDeleted() != 1) {
                if (userService.signUpUser(context, invitation.getUserLinkId(), maybeUserCredentials.getUsername(),
                        maybeUserCredentials.getPassword()) &&
                        invitationService.updateClaimedStatus(invitation.getInvitationId()) &&
                        invitationService.makeInvitationUnusable(invitation.getInvitationId())) {
                    JwtUserDto jwtUserDto = new JwtUserDto();
                    jwtUserDto.setUsername(maybeUserCredentials.getUsername());
                    jwtUserDto.setPassword(maybeUserCredentials.getPassword());
                    String encodedToken = Base64.encodeBase64String(JwtTokenGenerator.generateToken(jwtUserDto, secret).getBytes());
                    return new ResponseEntity<>("{\"token\":\"" + encodedToken + "\"}", HttpStatus.OK);
                } else {
                    //roll back
                    userService.updateUserUsername(context, generateRandomId(), mayBeInvitation.get().getUserLinkId());
                    userService.updateUserPassword(context, generateRandomId(), mayBeInvitation.get().getUserLinkId());
                    invitationService.makeInvitationUnusable(invitation.getInvitationId());
                    logger.warn("BOOM");
                    return new ResponseEntity<>("Invalid Data", HttpStatus.INTERNAL_SERVER_ERROR);
                }

            } else {
                logger.warn("Invitation Id: " + maybeInvitation_id + "Does not have user profile");
                return new ResponseEntity<>("Invalid Data", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Invitation Expired", HttpStatus.BAD_REQUEST);
        }
    }

    //helpers
    private String generateRandomId() {
        return UUID.randomUUID().toString();
    }

}
