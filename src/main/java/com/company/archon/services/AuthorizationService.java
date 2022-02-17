package com.company.archon.services;

import com.company.archon.dto.user.UserProfileDto;
import com.company.archon.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthorizationService {

    void authorizeUser(User user);

    UserProfileDto getProfileOfCurrent();

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

}
