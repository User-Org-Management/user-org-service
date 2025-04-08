package com.user_organization_management.security;

import com.user_organization_management.entity.UserEntity;
import com.user_organization_management.exception.BadCredentialsAuthException;
import com.user_organization_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByName(username)
                .orElseThrow(() -> new BadCredentialsAuthException("username or password invalid"));
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new BadCredentialsAuthException("username or password invalid");
        }
        return new User(
                user.getName(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
}