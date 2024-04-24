package com.example.ebookspring.service;

import com.example.ebookspring.config.JwtProvider;
import com.example.ebookspring.exception.UserException;
import com.example.ebookspring.model.User;
import com.example.ebookspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public User findUserById(Long userId) throws UserException {

        Optional<User> user = userRepository.findById(userId);

        if(user.isPresent()) {
            return user.get();
        }

        throw new UserException("User not found with ID - " + userId);
    }

    @Override
    public User findUserProfileByJwt(String jwt) throws UserException {

        String email = jwtProvider.getEmailFromToken(jwt);

        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new UserException("User not found with email - " + email);
        }

        return user;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public User updateUser(Long userId, User req) throws UserException {
        User user = findUserById(userId);

        if(req.getFirstName() != null) {
            user.setFirstName(req.getFirstName());
        }

        if(req.getLastName() != null) {
            user.setLastName(req.getLastName());
        }

        if(req.getImage() != null) {
            user.setImage(req.getImage());
        }

        if(req.getBackgroundImage() != null) {
            user.setBackgroundImage(req.getBackgroundImage());
        }

        if(req.getBirthDate() != null) {
            user.setBirthDate(req.getBirthDate());
        }

        if(req.getLocation() != null) {
            user.setLocation(req.getLocation());
        }

        if(req.getBio() != null) {
            user.setBio(req.getBio());
        }

        if(req.getWebsite() != null) {
            user.setWebsite(req.getWebsite());
        }

        return userRepository.save(user);
    }

    @Override
    public User followUser(Long userId, User user) throws UserException {

        User followToUser = findUserById(userId);

        if(user.getFollowings().contains(followToUser) && followToUser.getFollowers().contains(user)) {
            user.getFollowings().remove(followToUser);
            followToUser.getFollowers().remove(user);
        } else {
            user.getFollowings().add(followToUser);
            followToUser.getFollowers().add(user);
        }

        userRepository.save(followToUser);
        userRepository.save(user);
        return followToUser;
    }

    @Override
    public List<User> searchUser(String query) {
        return userRepository.searchUser(query);
    }
}
