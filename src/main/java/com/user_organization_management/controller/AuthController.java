package com.user_organization_management.controller;
import com.user_organization_management.dto.UserDTO;
import com.user_organization_management.model.AuthRequest;
import com.user_organization_management.security.CustomUserDetailsService;
import com.user_organization_management.security.JwtUtil;
import com.user_organization_management.service.AuthService;
import com.user_organization_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    AuthService authService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.login(authRequest));
    }


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid  UserDTO userDTO) {
        return ResponseEntity.ok(userService.create(userDTO));
    }
}