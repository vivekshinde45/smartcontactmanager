package com.smartmanager.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartmanager.smartcontactmanager.dao.ContactRepository;
import com.smartmanager.smartcontactmanager.dao.UserRepository;
import com.smartmanager.smartcontactmanager.entities.Contact;
import com.smartmanager.smartcontactmanager.entities.User;
import com.smartmanager.smartcontactmanager.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    private ContactRepository _contactRepository;

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
    public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
            Principal principal, HttpSession session) {
        try {
            if (contact == null) {
                throw new Exception("Add contact details");
            }
            String username = principal.getName();
            User _user = _userRepository.getUserByUserName(username);

            if (file.isEmpty()) {
                System.out.println("File not provided");
            } else {
                String fileName = file.getOriginalFilename();
                // save filename to DB
                contact.setImageUrl(fileName);

                // save file to static/img folder
                // get the folder path
                File folderPath = new ClassPathResource("static/image").getFile();

                // create filename => append the filename to folderpath
                Path filePath = Paths.get(folderPath.getAbsolutePath() + File.separator + fileName);

                // save file to location
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File is uploaded");
            }

            // map user wrt contact
            contact.setUser(_user);

            // map contact wrt user
            _user.getContacts().add(contact);

            User savedUser = this._userRepository.save(_user);
            System.out.println(savedUser);

            System.out.println("Added into DB");

            // Add message
            session.setAttribute("message", new Message("Your contact is added", "success"));

            return "normal/add_contact_form";

        } catch (Exception e) {
            session.setAttribute("message", new Message("Filed to update the contact try again", "danger"));
            return "normal/add_contact_form";
        }
    }

    @GetMapping("/show_contacts")
    public String showContacts(Model model, Principal principal) {
        model.addAttribute("title", "Contacts");

        // get user
        String userName = principal.getName();
        User user = this._userRepository.getUserByUserName(userName);

        // get contacts for specified user
        List<Contact> contacts = this._contactRepository.getAllContactByUser(user.getId());

        // map
        model.addAttribute("contacts", contacts);

        return "normal/show_contacts";
    }
}
