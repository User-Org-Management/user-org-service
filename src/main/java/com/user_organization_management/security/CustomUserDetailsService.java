package com.user_organization_management.security;

import com.user_organization_management.entity.UserEntity;
import com.user_organization_management.exception.BadCredentialsAuthException;
import com.user_organization_management.exception.EntityNotFoundException;
import com.user_organization_management.mapper.UserMapper;
import com.user_organization_management.repository.UserRepository;
import com.user_organization_management.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username){
            UserEntity user = userMapper.toEntity(userService.getUserByName(username));
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new BadCredentialsAuthException("username or password invalid");
            }
            String role = "ROLE_USER";
            if (user.getRole().getName() != null) {
                role = "ROLE_" + user.getRole().getName();
            }

            log.info("User {} has role {}", username, role);

            return new User(
                    user.getName(),
                    user.getPassword(),
                    AuthorityUtils.createAuthorityList(role)
            );

    }
}