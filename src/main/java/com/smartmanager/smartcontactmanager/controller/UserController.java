package com.smartmanager.smartcontactmanager.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smartmanager.smartcontactmanager.dao.UserRepository;
import com.smartmanager.smartcontactmanager.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    public UserRepository _userRepository;

    @RequestMapping("/index")
    public String index(Model model, Principal principal) {
        String username = principal.getName();

        // get user by username
        User user = _userRepository.getUserByUserName(username);
        model.addAttribute("user", user);

        return "normal/user_dashboard";
    }
}
