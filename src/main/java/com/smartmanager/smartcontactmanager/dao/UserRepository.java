package com.smartmanager.smartcontactmanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartmanager.smartcontactmanager.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
