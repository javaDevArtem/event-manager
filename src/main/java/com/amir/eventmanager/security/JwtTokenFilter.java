package com.amir.eventmanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenFilter.class);


    private final JwtTokenManager tokenManager;

    public JwtTokenFilter(JwtTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || authorization.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
        }
        String jwtToken = authorization.substring(7);
        if (!tokenManager.isTokenValid(jwtToken)) {
            log.info("Jwt token not valid");
            filterChain.doFilter(request, response);
        }
        String login = tokenManager.getLoginFromToken(jwtToken);
        String role = tokenManager.getRoleFromToken(jwtToken);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                login,
                null,
                List.of(new SimpleGrantedAuthority(role))
        );
        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(request, response);

    }
}
