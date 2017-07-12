package com.namodu.pustakam.security;

import com.namodu.pustakam.repository.UserRepository;
import com.namodu.pustakam.security.exception.JwtTokenMalformedException;
import com.namodu.pustakam.security.exception.JwtTokenValidationException;
import com.namodu.pustakam.security.model.AuthenticatedUser;
import com.namodu.pustakam.security.model.JwtAuthenticationToken;
import com.namodu.pustakam.security.transfer.JwtUserDto;
import com.namodu.pustakam.security.util.JwtTokenValidator;
import com.namodu.pustakam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Used for checking the token from the request and supply the UserDetails if the token is valid
 *
 * @author pascal alma
 */
@Component
public class JwtAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private JwtTokenValidator jwtTokenValidator;

    @Autowired
    UserService userService;

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String token = jwtAuthenticationToken.getToken();

        JwtUserDto parsedUser = jwtTokenValidator.parseToken(token);

        if (parsedUser == null) {
            throw new JwtTokenMalformedException("JWT token is not valid");
        }
        try {
            String actualPassword = userService.getPasswordHashByUsername(null, parsedUser.getUsername());
            String providedPassword = parsedUser.getPassword();
            if (passwordEncoder.matches(providedPassword, actualPassword)) {
                return new AuthenticatedUser(parsedUser.getUsername(), token, parsedUser.getPassword());
            } else {
                throw new JwtTokenValidationException("Invalid credentials");
            }
        } catch (IncorrectResultSizeDataAccessException exp) {
            throw new JwtTokenValidationException("Invalid credentials");
        }
    }
}
