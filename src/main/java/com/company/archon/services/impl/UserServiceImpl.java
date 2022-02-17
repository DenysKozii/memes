package com.company.archon.services.impl;

import com.company.archon.entity.Role;
import com.company.archon.entity.User;
import com.company.archon.repositories.UserRepository;
import com.company.archon.services.AuthorizationService;
import com.company.archon.services.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthorizationService authorizationService;
    private final UserRepository       userRepository;
    private final PasswordEncoder      passwordEncoder;

    public Long addUser(String username) {
        User user = new User();
        user.setRole(Role.USER);
        user.setName(username);
        user.setPassword(passwordEncoder.encode(username));
        userRepository.save(user);
        authorizationService.authorizeUser(user);
        return user.getId();
    }

}
