package com.smartmanager.smartcontactmanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartmanager.smartcontactmanager.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    // get user by username, the username is email in our case
    @Query("select u from User u where u.email = :email")
    public User getUserByUserName(@Param("email") String email);
}
