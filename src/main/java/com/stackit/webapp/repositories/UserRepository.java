package com.stackit.webapp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stackit.webapp.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /** 
     * Lookup by username, only non-deleted users. 
     * Matches schema’s WHERE is_deleted = false.
     */
    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    /** 
     * Lookup by email, only non-deleted users. 
     */
    Optional<User> findByEmailAndIsDeletedFalse(String email);
}
