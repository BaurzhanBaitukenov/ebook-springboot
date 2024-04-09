package com.example.ebookspring.service;

import com.example.ebookspring.exception.UserException;
import com.example.ebookspring.model.User;

public interface UserService {

    public User findUserById(Long userId) throws UserException;

    public User findUserProfileByJwt(String jwt) throws UserException;

}
