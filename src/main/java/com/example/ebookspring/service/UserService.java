package com.example.ebookspring.service;

import com.example.ebookspring.exception.UserException;
import com.example.ebookspring.model.User;

import java.util.List;

public interface UserService {

    public User findUserById(Long userId) throws UserException;

    public User findUserProfileByJwt(String jwt) throws UserException;

    public List<User> findAllUsers();

}
