package com.dcp.middlewares;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.dcp.iam.domain.AuthenticatedCompany;
import com.dcp.iam.domain.JwtService;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtHttpFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final Logger httpFilterLogger = LoggerFactory.getLogger(JwtHttpFilter.class);

    @Autowired
    public JwtHttpFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || header.isEmpty() || !header.substring(0, 7).equalsIgnoreCase("bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        final String accessToken = header.substring(7);

        AuthenticatedCompany authenticatedCompany;
        try {
            authenticatedCompany = this.jwtService.decodeAccessToken(accessToken);
            Authentication auth = new JwtAuth(authenticatedCompany);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (JWTVerificationException e) {
            httpFilterLogger.debug(() -> "Error in decoding token");
            response.setStatus(401);
            return;
        }

        chain.doFilter(request, response);
    }
}
