package com.example.ebookspring.service;

import com.example.ebookspring.config.JwtProvider;
import com.example.ebookspring.exception.UserException;
import com.example.ebookspring.model.User;
import com.example.ebookspring.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService{

    private UserRepository userRepository;
    private JwtProvider jwtProvider;

    public UserServiceImplementation(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

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
        // TODO Auto-generated method stub
        return userRepository.findAllByOrderByCreatedAtDesc();
    }
}
