package com.stackit.repositories;

import com.stackit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Method to find a user by username, excluding soft-deleted users
    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    // Method to find a user by email, excluding soft-deleted users
    Optional<User> findByEmailAndIsDeletedFalse(String email);
}