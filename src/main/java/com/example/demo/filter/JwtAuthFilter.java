package com.example.demo.filter;

import com.example.demo.security.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        final String ipAddress = request.getRemoteAddr();
        final String endpoint = request.getRequestURI();
        final String method = request.getMethod();

        if (endpoint.startsWith("/api/auth/") ||
                endpoint.startsWith("/api/register/") ||
                endpoint.equals("/api/products") ||
                endpoint.startsWith("/api/products/") ||
                endpoint.startsWith("/api/public/")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("API_ACCESS_ATTEMPT - Endpoint: {}, Method: {}, IP: {}, HasAuth: {}, Time: {}",
                endpoint,
                method,
                ipAddress,
                authHeader != null,
                LocalDateTime.now()
        );

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("API_ACCESS_NO_TOKEN - Endpoint: {}, Method: {}, IP: {}, Time: {}",
                    endpoint,
                    method,
                    ipAddress,
                    LocalDateTime.now()
            );

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized - Authentication required\"}");

            return;
        }

        jwt = authHeader.substring(7);

        try {
            username = jwtUtil.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt)) {
                    if (!username.equals(userDetails.getUsername())) {
                        log.warn("API_ACCESS_USERNAME_MISMATCH - TokenUser: {}, DBUser: {}, Endpoint: {}, IP: {}, Time: {}",
                                username,
                                userDetails.getUsername(),
                                endpoint,
                                ipAddress,
                                LocalDateTime.now()
                        );
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Unauthorized - Invalid token\"}");
                        return;
                    }

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.info("API_ACCESS_AUTHORIZED - User: {}, Endpoint: {}, Method: {}, IP: {}, Time: {}",
                            username,
                            endpoint,
                            method,
                            ipAddress,
                            LocalDateTime.now()
                    );

                } else {
                    log.warn("API_ACCESS_INVALID_TOKEN - User: {}, Endpoint: {}, IP: {}, Time: {}",
                            username,
                            endpoint,
                            ipAddress,
                            LocalDateTime.now()
                    );
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Unauthorized - Invalid token\"}");
                    return;
                }
            }

        } catch (ExpiredJwtException e) {
            log.warn("API_ACCESS_TOKEN_EXPIRED - Endpoint: {}, IP: {}, Time: {}",
                    endpoint,
                    ipAddress,
                    LocalDateTime.now()
            );
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized - Token has expired\"}");
            return;

        } catch (SignatureException e) {
            log.warn("API_ACCESS_TOKEN_SIGNATURE_INVALID - Endpoint: {}, IP: {}, Time: {}",
                    endpoint,
                    ipAddress,
                    LocalDateTime.now()
            );
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized - Invalid token signature\"}");
            return;

        } catch (Exception e) {
            log.error("API_ACCESS_TOKEN_ERROR - Endpoint: {}, IP: {}, Error: {}, Time: {}",
                    endpoint,
                    ipAddress,
                    e.getMessage(),
                    LocalDateTime.now()
            );
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized - Token validation error\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}