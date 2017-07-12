package com.namodu.pustakam.security;

import com.namodu.pustakam.model.RequestContext;
import com.namodu.pustakam.repository.UserRepository;
import com.namodu.pustakam.repository.UserRoleRepository;
import com.namodu.pustakam.security.exception.JwtTokenMissingException;
import com.namodu.pustakam.security.model.JwtAuthenticationToken;
import com.namodu.pustakam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Filter that orchestrates authentication by using supplied JWT token
 *
 * @author pascal alma
 */
public class JwtAuthenticationTokenFilter extends AbstractAuthenticationProcessingFilter {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${auth.ignore.urls}")
    private String ignoreUrls;

    @Autowired
    UserService userService;


    public JwtAuthenticationTokenFilter() {
        super("/**");
    }

    /**
     * Attempt to authenticate request - basically just pass over to another method to authenticate request headers
     */

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        Authentication authentication = null;
        RequestContext context = new RequestContext();

        if (!requiresAuthentication(request, response)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            authentication = attemptAuthentication(request, response);
            if (authentication.isAuthenticated()) {
                context.setCorrelationId(UUID.randomUUID().toString());
                String userLinkId = userService.findUserLinkIdByUsername(context, authentication.getName());
                context.setUserLinkId(userLinkId);
                successfulAuthentication(request, response, chain, context);
            }
        } catch (AuthenticationException exp) {
            unsuccessfulAuthentication(request, response, exp);
        }
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        Set<String> urls = ignoreUrlSet();
        for (String str : urls) {
            if (request.getRequestURI().contains(str)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String header = request.getHeader(this.tokenHeader);

        if (header == null) {
            throw new JwtTokenMissingException("No JWT token found in request headers");
        }

        JwtAuthenticationToken authRequest = new JwtAuthenticationToken(header);

        return getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * Make sure the rest of the filterchain is satisfied
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, exception);
    }

    private Set<String> ignoreUrlSet() {
        Set<String> urlSet = new HashSet<>();
        String[] urls = ignoreUrls.split(",");

        for (String str : urls) {
            urlSet.add(str.trim());
        }
        return urlSet;
    }
}