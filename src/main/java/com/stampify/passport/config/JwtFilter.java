package com.stampify.passport.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = null;

        // ✅ Read JWT from cookie
        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null && jwtUtil.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);

            // ✅ Make available to controllers
            req.setAttribute("username", username);
            req.setAttribute("role", role);
        }

        filterChain.doFilter(req, res);
    }
}
