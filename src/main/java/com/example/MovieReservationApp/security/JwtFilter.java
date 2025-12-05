package com.example.MovieReservationApp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();

        System.out.println("🔍 [JWT FILTER] ===== NEW REQUEST =====");
        System.out.println("🔍 [JWT FILTER] Method: " + method);
        System.out.println("🔍 [JWT FILTER] Path: " + path);

        if (path.startsWith("/api/auth/")) {
            System.out.println("✅ [JWT FILTER] Auth endpoint - skipping filter");
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            System.out.println("⚠️ [JWT FILTER] No Authorization header or invalid format");
            filterChain.doFilter(request, response);
            return;
        }
        
        System.out.println("✅ [JWT FILTER] Authorization header found");

        String token = header.substring(7);
        String email = jwtService.extractUsername(token);

        System.out.println("🔍 [JWT FILTER] Request path: " + request.getMethod() + " " + request.getRequestURI());
        System.out.println("🔍 [JWT FILTER] Email from token: " + email);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                System.out.println("🔍 [JWT FILTER] About to load user details...");
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                System.out.println("🔍 [JWT FILTER] User loaded: " + userDetails.getUsername());
                System.out.println("🔍 [JWT FILTER] About to get authorities...");
                var authorities = userDetails.getAuthorities();
                System.out.println("🔍 [JWT FILTER] User authorities: " + authorities);
                System.out.println("🔍 [JWT FILTER] About to validate token...");
                boolean tokenValid = jwtService.isTokenValid(token, userDetails.getUsername());
                System.out.println("🔍 [JWT FILTER] Token valid? " + tokenValid);

                if (tokenValid) {
                    System.out.println("🔍 [JWT FILTER] Creating authentication token...");
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println("✅ [JWT FILTER] Authentication set successfully!");
                    System.out.println("✅ [JWT FILTER] Final authorities in context: " + 
                        SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                } else {
                    System.out.println("❌ [JWT FILTER] Token invalid!");
                }
            } catch (Exception e) {
                System.out.println("❌ [JWT FILTER] ERROR loading user: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (email == null) {
            System.out.println("❌ [JWT FILTER] Email is null from token");
        } else {
            System.out.println("⚠️ [JWT FILTER] Authentication already exists: " + 
                SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        }

        filterChain.doFilter(request, response);
    }
}
