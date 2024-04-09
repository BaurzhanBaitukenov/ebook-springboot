package com.example.ebookspring.controller;

import com.example.ebookspring.config.JwtProvider;
import com.example.ebookspring.exception.UserException;
import com.example.ebookspring.model.User;
import com.example.ebookspring.repository.UserRepository;
import com.example.ebookspring.request.LoginRequest;
import com.example.ebookspring.response.AuthResponse;
import com.example.ebookspring.service.CartService;
import com.example.ebookspring.service.CustomUserServiceImplementation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserRepository userRepository;
    private JwtProvider jwtProvider;
    private PasswordEncoder passwordEncoder;
    private CustomUserServiceImplementation customUserServiceImplementation;
    private CartService cartService;

    public AuthController(UserRepository userRepository,
                          CustomUserServiceImplementation customUserServiceImplementation,
                          PasswordEncoder passwordEncoder,
                          JwtProvider jwtProvider,
                          CartService cartService) {
        this.userRepository = userRepository;
        this.customUserServiceImplementation = customUserServiceImplementation;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.cartService = cartService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler (@RequestBody User user) throws UserException {

        String email = user.getEmail();
        String password = user.getPassword();
        String firstString = user.getFirstName();
        String lastNString = user.getLastName();

        User isEmailExist = userRepository.findByEmail(email);

        if(isEmailExist != null) {
            throw new UserException("Email is Already Used With Another User");
        }

        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setPassword(passwordEncoder.encode(password));
        createdUser.setFirstName(firstString);
        createdUser.setLastName(lastNString);

        User savedUser = userRepository.save(createdUser);

        cartService.createCart(savedUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getPassword(), savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Sign-up Success");

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.CREATED);
    }


    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> loginUserHandler(@RequestBody LoginRequest loginRequest) {

        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Sign-in Success");

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.CREATED);
    }


    private Authentication authenticate(String username, String password) {

        UserDetails userDetails = customUserServiceImplementation.loadUserByUsername(username);

        if(userDetails == null) {
            throw new BadCredentialsException("Invalid Username");
        }

        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Password...");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
