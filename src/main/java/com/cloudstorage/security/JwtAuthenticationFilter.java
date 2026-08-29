package com.cloudstorage.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.cloudstorage.security.JwtService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
    	
    	System.out.println("========== JWT FILTER ==========");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Method: " + request.getMethod());
        
            String authHeader = request.getHeader("Authorization");
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
        	
        	System.out.println("NO VALID BEARER TOKEN");

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtService.extractEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {                          

                UserDetails userDetails =userDetailsService.loadUserByUsername(email);
                System.out.println(
                        "User loaded: "
                        + userDetails.getUsername()
                );


                if (jwtService.isTokenValid(token,userDetails.getUsername())) { System.out.println("JWT VALID");
                	UsernamePasswordAuthenticationToken authentication =new UsernamePasswordAuthenticationToken(                            
                                    userDetails, null, userDetails.getAuthorities());                           

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);                                                       
                }else {

                    System.out.println("JWT INVALID");
                }
            }

        } catch (Exception e) {
            // Invalid JWT
            // Request will continue without authentication 
        	System.out.println("========== JWT ERROR ==========");
        	e.printStackTrace();
        }
        System.out.println("========== REQUEST DEBUG ==========");
        System.out.println("Content-Type: " + request.getContentType());
        System.out.println("Content-Length: " + request.getContentLengthLong());
        System.out.println("Content-Type Header: " + request.getHeader("Content-Type"));

        filterChain.doFilter(request, response);
    }

   /* private com.cloudstorage.entity.User createUserFromDetails(
            UserDetails userDetails) {

        com.cloudstorage.entity.User user =
                new com.cloudstorage.entity.User();

        user.setEmail(userDetails.getUsername());
        user.setPassword(userDetails.getPassword());

        return user;
    }*/
}