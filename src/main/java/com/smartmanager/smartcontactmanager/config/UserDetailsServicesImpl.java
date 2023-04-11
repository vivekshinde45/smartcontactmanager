package com.smartmanager.smartcontactmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smartmanager.smartcontactmanager.dao.UserRepository;
import com.smartmanager.smartcontactmanager.entities.User;

public class UserDetailsServicesImpl implements UserDetailsService {

    @Autowired
    private UserRepository _userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // get user by username from DB
        User _user = _userRepository.getUserByUserName(username);
        if (_user == null) {
            throw new UsernameNotFoundException("Could not found user with username " + username);
        }
        CustomUserDetails customUserDetails = new CustomUserDetails(_user);
        return customUserDetails;
    }

}
