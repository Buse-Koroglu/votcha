package com.example.votify_meet.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
 * Check if you have an ID (token).
 * If you do, they'll let you in otherwise call the police!
 * OncePerRequest means 'Work only once per request'.
*/
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Is the request have a (Header) "Authorization"
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // If there is no authorization
        if(isInvalidAuthHeader(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Remove the word "Bearer" and get the pure token
        jwt = authHeader.substring(7);

        try{
            // Go to the next filter (Controller)
            // Extract the username from the token
            userEmail = jwtService.extractUsername(jwt);

            if(isAuthenticationRequired(userEmail)) {
                // Retrieve the user from the DB
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                if(jwtService.isTokenValid(jwt, userDetails)){
                    // Now we tell Spring Security, "This guy is trustworthy, let him in."
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // ENTER IDENTITY INTO THE SYSTEM
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            }
        }catch (ExpiredJwtException e){
            if (request.getServletPath().startsWith("/api/auth")) {
                filterChain.doFilter(request, response);
            } else {
                // Ama korumalı bir sayfaya gidiyorsa senin o meşhur metodunla 401 dön!
                logger.error("JWT expired: " + e.getMessage());
                handleAuthenticationException(response, "Token is expired, please login!");
            }
        }catch (Exception e){
            handleAuthenticationException(response, "Invalid token!");
        }
    }
    private void handleAuthenticationException(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", message);
        response.getWriter().write(jsonResponse);
    }


    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/api/auth");
    }

    /*
    * Check authHeader if it is invalid
    */
    private boolean isInvalidAuthHeader(String authHeader) {
        return authHeader == null || !authHeader.startsWith("Bearer ");
    }

    /*
    * Check authentication if it is required
    */
    private boolean isAuthenticationRequired(String userEmail) {
        return userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }
}
