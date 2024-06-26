package com.example.ebookspring.repository;

import com.example.ebookspring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByEmail(String email);
    public List<User> findAllByOrderByCreatedAtDesc();

    @Query("SELECT DISTINCT u FROM User u WHERE u.firstName LIKE %:query% OR u.email LIKE %:query%")
    List<User> searchUser(@Param("query") String query);
}
