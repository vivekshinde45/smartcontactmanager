package com.smartmanager.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
                contact.setImageUrl("contact.png");
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

    @GetMapping("/show_contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
        model.addAttribute("title", "Contacts");

        // get user
        String userName = principal.getName();
        User user = this._userRepository.getUserByUserName(userName);

        // get contacts for specified user
        // create pagable for pagination of show contacts where it takes 2 ele
        // 1:current_page and 2:#ofContacts/page
        Pageable pageable = PageRequest.of(page, 5);
        Page<Contact> contacts = this._contactRepository.getAllContactByUser(user.getId(), pageable);

        // map
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());

        return "normal/show_contacts";
    }

    @GetMapping("/contact/{id}")
    public String contact(@PathVariable("id") int id, Model model, Principal principal) {
        try {
            Optional<Contact> contactOptional = this._contactRepository.findById(id);
            Contact contact = contactOptional.get();

            String userName = principal.getName();
            User user = this._userRepository.getUserByUserName(userName);

            if (user.getId() == contact.getUser().getId()) {
                model.addAttribute("contact", contact);
                model.addAttribute("title", contact.getName());
            }
            return "normal/contact_page";
        } catch (Exception ex) {
            return "normal/contact_page";
        }
    }

    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") int cId, Model model, Principal principal) {
        try {
            String userName = principal.getName();
            User user = this._userRepository.getUserByUserName(userName);

            Contact contact = this._contactRepository.findById(cId).get();

            if (user.getId() == contact.getUser().getId()) {
                // remove image from static/image folder
                String imageName = contact.getImageUrl();
                if (!imageName.equals("contact.png")) {
                    // folder path
                    File folderPath = new ClassPathResource("static/image").getFile();
                    // make file path for delete
                    Path filePath = Paths.get(folderPath.getAbsolutePath() + File.separator + imageName);
                    // delete
                    Files.delete(filePath);
                }
                // un-sync contact from user
                contact.setUser(null);
                // delete contact
                this._contactRepository.delete(contact);
            }

        } catch (Exception e) {

        }

        return "redirect:/user/show_contacts/0";
    }
}
