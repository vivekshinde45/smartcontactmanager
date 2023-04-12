package com.smartmanager.smartcontactmanager.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smartmanager.smartcontactmanager.dao.UserRepository;
import com.smartmanager.smartcontactmanager.entities.Contact;
import com.smartmanager.smartcontactmanager.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    public UserRepository _userRepository;

    // add common data
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String username = principal.getName();
        // System.out.println(username);

        // get user by username
        User user = _userRepository.getUserByUserName(username);
        System.out.println(user);
        model.addAttribute("user", user);
    }

    // mapping for user index page
    @RequestMapping("/index")
    public String index(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    // mapping for Add Contact
    @GetMapping("add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contacts");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    // process the added contact for given user
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, Principal principal) {
        try {
            String username = principal.getName();

            User _user = _userRepository.getUserByUserName(username);

            // map user wrt contact
            contact.setUser(_user);

            // map contact wrt user
            _user.getContacts().add(contact);

            User savedUser = this._userRepository.save(_user);
            System.out.println(savedUser);

            System.out.println("Added into DB");
            return "normal/add_contact_form";
        } catch (Exception e) {
            return "normal/add_contact_form";
        }
    }
}
