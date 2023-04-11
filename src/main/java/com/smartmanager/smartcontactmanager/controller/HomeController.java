package com.smartmanager.smartcontactmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartmanager.smartcontactmanager.dao.UserRepository;
import com.smartmanager.smartcontactmanager.entities.User;
import com.smartmanager.smartcontactmanager.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder _passwordEncoder;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About - Smart contact manager");
        return "about";
    }

    @RequestMapping("/signup")
    public String signUp(Model model) {
        model.addAttribute("title", "SignUp - Smart contact manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
            HttpSession session) {

        try {
            if (!agreement) {
                System.out.println("You must agreed with terms and conditions");
                throw new Exception("You must agreed with terms and conditions");
            }

            if (result.hasErrors()) {
                model.addAttribute("user", user);
                return "signup";
            }

            user.setRole("ROLE_USER");
            user.setActive(true);
            user.setPassword(_passwordEncoder.encode(user.getPassword()));

            User _user = userRepository.save(user);

            System.out.println("Agreemant = " + agreement);
            System.out.println("USER => " + _user);

            session.setAttribute("message", new Message("Successfully registered !!", "alert-success"));
            model.addAttribute("user", new User());
            return "signup";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something Went Wrong !! " + e.getMessage(), "alert-danger"));
            return "signup";
        }
    }
}
