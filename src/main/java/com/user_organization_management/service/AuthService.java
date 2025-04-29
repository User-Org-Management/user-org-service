package com.user_organization_management.service;

import com.user_organization_management.model.AuthRequest;
import com.user_organization_management.security.CustomUserDetailsService;
import com.user_organization_management.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserService  userService;

    @Autowired
    JwtUtil jwtUtil;

    public Map<String, String> login(AuthRequest authRequest)  {
        try {
            userService.disableLogin(authRequest.getEmail());

            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            userService.resetFailedLoginAttempts(authRequest.getEmail());

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(authRequest.getEmail());
            String token = jwtUtil.generateToken(userDetails.getUsername());

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return response;

        } catch (BadCredentialsException e) {
            userService.increaseFailedLoginAttempts(authRequest.getEmail());
            throw new BadCredentialsException("Invalid username or password", e);
        }
    }}
