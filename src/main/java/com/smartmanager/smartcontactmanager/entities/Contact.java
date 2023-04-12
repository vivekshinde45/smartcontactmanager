package com.smartmanager.smartcontactmanager.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cId;
    private String name;
    private String secondName;
    private String email;
    private String work;
    private String imageUrl;
    private String phone;
    @Column(length = 50000)
    private String description;

    @ManyToOne
    private User user;

    public Contact() {
    }

    public Contact(int cId, String name, String secondName, String email, String work, String imageUrl, String phone,
            String description, User user) {
        this.cId = cId;
        this.name = name;
        this.secondName = secondName;
        this.email = email;
        this.work = work;
        this.imageUrl = imageUrl;
        this.phone = phone;
        this.description = description;
        this.user = user;
    }

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // @Override
    // public String toString() {
    // return "Contact [cId=" + cId + ", name=" + name + ", secondName=" +
    // secondName + ", email=" + email + ", work="
    // + work + ", imageUrl=" + imageUrl + ", phone=" + phone + ", description=" +
    // description + ", user="
    // + user + "]";
    // }

}
